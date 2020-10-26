package com.misiak.android.autoexpense.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignInViewModel() : ViewModel() {

    private var _name = MutableLiveData<String>()
    val name: LiveData<String>
        get() = _name

    private var _googleSignIn = MutableLiveData<Boolean>()
    val googleSignIn: LiveData<Boolean>
        get() = _googleSignIn

    fun onGoogleSignInClicked() {
        _googleSignIn.value = true
    }

    fun onGoogleSignedIn() {
        _googleSignIn.value = false
    }

    fun googleSignInComplete(name: String) {
        _name.value = name
    }
}