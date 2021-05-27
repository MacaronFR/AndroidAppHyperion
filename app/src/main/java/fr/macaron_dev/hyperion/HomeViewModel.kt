package fr.macaron_dev.hyperion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel: ViewModel() {
    private val _text = MutableLiveData<String>().apply{
        value = "NIKKKKK"
    }
    val text: LiveData<String> = _text
}