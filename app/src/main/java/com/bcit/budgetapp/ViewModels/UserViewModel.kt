package com.bcit.budgetapp.ViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bcit.budgetapp.Models.User

class UserViewModel : ViewModel() {

    val user = User()

    fun loggedInUser(ID: String){
        user.username = ID
    }

}