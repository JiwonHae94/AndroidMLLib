package com.jiwon.lib_ai.model.support.core

interface Operator<IType, OType> {
    fun apply(var1 : IType) : OType
    fun apply(var1 : IType, rslt : (OType) -> Unit){
        rslt(apply(var1))
    }
}