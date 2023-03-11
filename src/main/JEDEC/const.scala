package LPDDR4

import chisel3._
import chisel3.util._j

object DecoderDefines {
    val MPC_lNOP = "b000000".Bits(6.W) 
    val MPC_lOP  = "b100000".Bits(6.W)
    val PRE_lNAB = "b010000".Bits(6.W)
    val PRE_lAB  = "1100000".Bits(6.W)
}


object DecoderStates {
    val sIdle = Value
    // Deselect state
    val sDES = Value
    
    /*
    Multi-Purpose Command (MPC)
    - 
    */
    val sMPC_lNOP, sMPC_lOP = Value
    val sMPC_lOP_hRDFIFO, sMPC_lOP_hRDDQCal, sMPC_lOP_hRFU,
        sMPC_lOP_hWRFIFO, sMPC_lOP_hStartDQSOsc, sMPC_lOP_hStopDQSOsc,
        sMPC_lOP_hStartZQCal, sMPC_lOP_hLatchZQCal = Value

    /*
    Precharge (PRE)
    -
    */
    val sPRE_lNAB, sPRE_lAB = Value
    val sPRE_lNAB_hB0, sPRE_lNAB_hB1, sPRE_lNAB_hB2, 
        sPRE_lNAB_hB3, sPRE_lNAB_hB4, sPRE_lNAB_hB5,
        sPRE_lNAB_hB6, sPRE_lNAB_hB7, sPRE_lAB = Value
}