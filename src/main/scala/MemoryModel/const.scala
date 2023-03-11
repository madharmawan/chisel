package MemoryModel

import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum


object MemoryModelDefines {
    val M = true.B
}


object State extends ChiselEnum {
        val sIDLE = Value 
        val sREAD, sWRITE = Value
    }