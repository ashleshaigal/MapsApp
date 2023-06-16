package com.example.mapsapp.ui.login

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.mapsapp.util.States
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.lang.Thread.State
import java.util.regex.Pattern

class LoginScreenViewModel : ViewModel() {
    val email = MutableLiveData<String>("a@a.com")
    val password = MutableLiveData<String>("Acc@1234")

    val states = MutableLiveData<States<Boolean>>(States.Idle())

    val TAG = "Login"

    private val isEmailValid: LiveData<Boolean> = email.map { email ->
        !email.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private val isPasswordValid: LiveData<Boolean> = password.map { password ->
        val pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[a-zA-Z]).{8,}\$")
        !password.isNullOrEmpty() && pattern.matcher(password).matches()
    }

    val isLoginButtonEnabled = MediatorLiveData<Boolean>().apply {
        addSource(isEmailValid) {
            postValue(it && isPasswordValid.value == true)
        }
        addSource(isPasswordValid) {
            postValue(it && isEmailValid.value == true)
        }

    }


    suspend fun login() {
        states.postValue(States.Loading())
        delay(3000)
        states.postValue(States.Success("JSON RESPONSE"))
    }


}