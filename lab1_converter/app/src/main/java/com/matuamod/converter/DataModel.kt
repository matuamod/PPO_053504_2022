package com.matuamod.converter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class DataModel : ViewModel() {
    val digit: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}