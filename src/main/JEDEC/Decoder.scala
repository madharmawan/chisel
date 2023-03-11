/*
LPDDR4Decoder decodes the command address based on the JEDEC Standard for LPDDR4.
This is a FSM that moves to each state.

Inputs (Info found on Table 2)
    - CS      : Chip Select
    - CA[5:0] : Command Address
    - CK      : Clock

Output:
    - 
*/

package LPDDR4

import chisel3._
import chisel3.util.Decoupled
import chisel3.util._
import chisel3.util.Enum
import chisel3.experimental.ChiselEnum
import DecoderDefines._
import Decoderstates._
class LPDDR4DecoderInputBundle extends Bundle {
    val ca = Bits(6.W)
    val cs = Bits(1.W)
}

class LPDDR4Decoder extends Module {

    val input = IO(new LPDDR4DecoderInputBundle)
    
    // Enums relating to MPC
    object CommandState extends ChiselEnum {
        
    }

    val state = RegInit(sIdle)

    switch (state) {
        is (sIdle) {
            // MPC related cases
            when (~input.cs) {
                switch (input.ca) {
                    is (MPC_lNOP) {
                        state := sMPC_lNOP
                    }
                    is (MPC_lNOP) {
                        state := sMPC_lOP
                    }
                    is (PRE_lNAB) {
                        state := sPRE_lNAB
                    }
                    is (PRE_lAB) {
                        state := sPRE_lAB
                    }
                }

            } .otherwise {
                
            }
            
        }
        is (sMPC_lNOP) {
            state := sIdle
        }
        is (sMPC_lOP) {
            switch (input.ca) {
                    is ("b1000001".Bits(6.W)) {
                        state := sMPC_lOP_hRDFIFO
                    }
                    is ("b1000011".Bits(6.W)) {
                        state := sMPC_lOP_hRDDQCal
                    }
                    is ("b1000101".Bits(6.W) || "b1001001".Bits(6.W)) {
                        state := sMPC_lOP_hRFU
                    }
                    is ("b".Bits(6.W)) {
                        state := sMPC_lOP_hWRFIFO
                    }
                    is ("b1001011".Bits(6.W)) {
                        state := sMPC_lOP_hStartDQSOsc
                    }
                    is ("b1001101".Bits(6.W)) {
                        state := sMPC_lOP_hStopDQSOsc
                    }
                    is ("b1001111".Bits(6.W)) {
                        state := sMPC_lOP_hStartZQCal
                    }
                    is ("b1010001".Bits(6.W)) {
                        state := sMPC_lOP_hLatchZQCal
                    }
                }
        }
        
        is (sPRE_lAB)

    }











}