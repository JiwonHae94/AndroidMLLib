package com.jiwon.lib_ai.support.core

interface Operator<IType, OType> {
    fun apply(var1 : IType) : OType
    fun apply(var1 : IType, rslt : (OType) -> Unit){
        rslt(apply(var1))
    }
}