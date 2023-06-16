package com.example.mapsapp.ui.launch

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mapsapp.model.User

class UserViewModel : ViewModel() {

    val user = MutableLiveData<User?>()

    fun setUser(email: String){
        user.postValue(User(email))
    }

}