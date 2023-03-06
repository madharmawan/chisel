import chisel3._
import chisel3.util.Decoupled
import chisel3.util._
import chisel3.util.Enum


class MemoryModelInputBundle(val dataWidth: Int, val addrWidth: Int) extends Bundle {
    val data_in = SInt(dataWidth.W)
    val addr_in = UInt(addrWidth.W)
    val write_en = Bool()
}

class MemoryModelOutputBundle(val dataWidth: Int) extends Bundle {
    val data_out = SInt(dataWidth.W)
    val state_out = UInt(2.W)
}

class MemoryModel(dataWidth: Int, addrWidth: Int) extends Module {
    // I/O
    val input = IO(Flipped(Decoupled(new MemoryModelInputBundle(dataWidth, addrWidth))))
    val output = IO(Decoupled(new MemoryModelOutputBundle(dataWidth)))

    // Declare Memory Array
    val mem = Mem(32, SInt())

    // Declare States via Enum and State Register
    val sIDLE::sREAD::sWRITE::Nil = Enum(3)
    val state = RegInit(sIDLE)

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
        is (sIDLE) {
            output.bits.state_out := 0.U
            when (input.valid) {
                val bundle = input.deq()
                busy := true.B
                dataIn := bundle.data_in
                dataAddr := bundle.addr_in
                when (input.bits.write_en) {
                    state := sWRITE
                } .otherwise {
                    state := sREAD
                }
            } .otherwise {
                busy := false.B
                state := sIDLE
            }
        }
        is (sREAD) {
            output.bits.state_out := 1.U
            resultValid := true.B
            when (output.ready && resultValid) {
                output.bits.data_out := mem(dataAddr)
                busy := false.B
                resultValid := false.B
                state := sIDLE
            } .otherwise {
                state := sREAD
            }
        }
        is (sWRITE) {
            output.bits.state_out := 2.U
            mem(dataAddr) := dataIn
            resultValid := true.B
            when (output.ready && resultValid) {
                
                busy := false.B
                resultValid := false.B
                state := sIDLE
            } .otherwise {
                state := sWRITE
            }
        }
    }
}


// class MemoryModel extends Module {
//     val io = IO(new Bundle {
//         val in = Flipped(Decoupled())
//         val data_in = Input(SInt(32.W))
//         val addr_in = Input(UInt(5.W))
//         val write_en = Input(Bool())
//         val out = Decoupled()
//         val data_out = Output(SInt(32.W))
//     })


    
//     // Memory Array of 32 32-bit elements
//     val mem = Mem(32, SInt())
    
//     val sIDLE::sREAD::sWRITE::Nil = Enum(3)
//     val state = RegInit(sIDLE)
    
//     // Define default outputs
//     io.data_out := WireDefault(io.data_in)
//     io.out.valid := WireDefault(false.B)
//     io.in.ready := WireDefault(false.B)
    
    
//     //
//     switch(state) {
//         is (sIDLE) {
//             when (io.in.valid) {
//                 when (io.write_en) {
//                     state := sWRITE
//                 } .otherwise {
//                     state := sREAD
//                 }
//             } .otherwise {
//                 state := sIDLE
//             }
//         }
//         is (sREAD) {
//             when (io.in.valid) {
//                 io.data_out := mem(io.addr_in)
//                 state := sIDLE
//             } .otherwise {
//                 state := sIDLE
//             }
            
//         }
//         is (sWRITE) {
//             when (io.in.valid) {
//                 mem(io.addr_in) := io.data_in
//                 state := sIDLE
//             } .otherwise {
//                 state := sIDLE
//             }
//         }
//     }
// }

