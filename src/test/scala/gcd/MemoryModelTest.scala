import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class MemoryModelTest extends AnyFlatSpec with ChiselScalatestTester {
    "MemoryModelTest1: Writing then Reading" should "pass" in {
        test(new MemoryModel(32, 5)) {dut =>
            dut.input.initSource()
            dut.input.setSourceClock(dut.clock)
            dut.output.initSink()
            dut.output.setSinkClock(dut.clock)

            
            // Input data [100, 101, ..., 131] into Memory
            var a = 0
            for (a <- 0 to 31) {
                // Set up values
                dut.input.bits.data_in.poke((a+100).S) 
                dut.input.bits.addr_in.poke(a.U)
                dut.input.bits.write_en.poke(true.B)
                // Data is then valid
                dut.input.valid.poke(true.B)
                dut.clock.step(5) // Simulate waiting until the output is ready
                dut.output.ready.poke(true.B) 
                dut.clock.step()
                dut.input.valid.poke(false.B) // Simulate waiting until input is valid
                dut.clock.step(5)
            }

            dut.input.valid.poke(false.B)
            // Read data here and ensure they are correct
            var b = 0
            for (b <- 0 to 31) {
                // Set up values
                dut.input.bits.addr_in.poke(b.U)
                dut.input.bits.write_en.poke(false.B)
                // Data is then valid
                dut.input.valid.poke(true.B)
                dut.clock.step(5) // Simulate waiting until output is ready
                dut.output.ready.poke(true.B)
                dut.clock.step()
                dut.output.bits.data_out.expect((b+100).S)
                dut.input.valid.poke(false.B) 
                dut.clock.step(5)
            }
        }
    }

    "MemoryModelTest2: Writing and overwriting when not ready" should "pass" in {
        test(new MemoryModel(32, 5)) {dut =>
            dut.input.initSource()
            dut.input.setSourceClock(dut.clock)
            dut.output.initSink()
            dut.output.setSinkClock(dut.clock)

            // Input data [100, 101, ..., 131] into Memory
            var a = 0
            for (a <- 0 to 31) {
                // Set up values
                dut.input.bits.data_in.poke((a+100).S) 
                dut.input.bits.addr_in.poke(a.U)
                dut.input.bits.write_en.poke(true.B)
                // Data is then valid
                dut.input.valid.poke(true.B)
                dut.clock.step(5) // Simulate waiting until the output is ready
                dut.output.ready.poke(true.B) 
                dut.clock.step()
                dut.input.valid.poke(false.B) // Simulate waiting until input is valid
                dut.clock.step(5)
            }

            // Input data [200, 201, ..., 231] into Memory but the input is never valid
            var c = 0
            dut.input.valid.poke(false.B)
            for (c <- 0 to 31) {
                // Set up values
                dut.input.bits.data_in.poke((c+200).S) 
                dut.input.bits.addr_in.poke(c.U)
                dut.input.bits.write_en.poke(true.B)
                // Data is then valid
                
                dut.clock.step(5) // Simulate waiting until the output is ready
                dut.output.ready.poke(true.B) 
                dut.clock.step()
                dut.clock.step(5)
            }
            

            // Now read normally, it should be [100, 101, ..., 131]
            dut.input.valid.poke(false.B)
            // Read data here and ensure they are correct
            var b = 0
            for (b <- 0 to 31) {
                // Set up values
                dut.input.bits.addr_in.poke(b.U)
                dut.input.bits.write_en.poke(false.B)
                // Data is then valid
                dut.input.valid.poke(true.B)
                dut.clock.step(5) // Simulate waiting until output is ready
                dut.output.ready.poke(true.B)
                dut.clock.step()
                dut.output.bits.data_out.expect((b+100).S)
                dut.input.valid.poke(false.B) 
                dut.clock.step(5)
            }
        }
    }
}