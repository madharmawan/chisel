
package MemoryModel
import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum
import MemoryModelDefines._
import State._

class MemoryModelInputBundle(val dataWidth: Int, val addrWidth: Int) extends Bundle {
    val data_in = SInt(dataWidth.W)
    val addr_in = UInt(addrWidth.W)
    val write_en = Bool()
}

class MemoryModelOutputBundle(val dataWidth: Int) extends Bundle {
    val data_out = SInt(dataWidth.W)
}

class MemoryModel(dataWidth: Int, addrWidth: Int) extends Module {
    // I/O
    val input = IO(Flipped(Decoupled(new MemoryModelInputBundle(dataWidth, addrWidth))))
    val output = IO(Decoupled(new MemoryModelOutputBundle(dataWidth)))

    // Declare Memory Array
    val mem = Mem(32, SInt())

    // Declare States via Enum and State Register
    
    val state = RegInit(State.sIDLE)

    // Controls ready and valid handshake
    val busy = RegInit(false.B)
    val resultValid = RegInit(false.B)

    // Ready/Valid
    input.ready := !busy
    output.valid := resultValid
    output.bits := DontCare
    
    // More Register Declarations
    val dataIn = Reg(SInt())
    val dataAddr = Reg(UInt())

    
    switch (state) {
        is (State.sIDLE) {
            
            when (input.valid) {
                val bundle = input.deq()
                busy := M
                dataIn := bundle.data_in
                dataAddr := bundle.addr_in
                when (input.bits.write_en) {
                    state := State.sWRITE
                } .otherwise {
                    state := State.sREAD
                }
            } .otherwise {
                busy := false.B
                state := State.sIDLE
            }
        }
        is (State.sREAD) {
            
            resultValid := true.B
            when (output.ready && resultValid) {
                output.bits.data_out := mem(dataAddr)
                busy := false.B
                resultValid := false.B
                state := State.sIDLE
            } .otherwise {
                state := State.sREAD
            }
        }
        is (State.sWRITE) {
            
            mem(dataAddr) := dataIn
            resultValid := true.B
            when (output.ready && resultValid) {
                
                busy := false.B
                resultValid := false.B
                state := State.sIDLE
            } .otherwise {
                state := State.sWRITE
            }
        }
    }
}



