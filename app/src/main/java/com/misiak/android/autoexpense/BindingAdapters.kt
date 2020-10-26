package com.misiak.android.autoexpense

import androidx.databinding.BindingAdapter
import com.google.android.gms.common.SignInButton

@BindingAdapter("android:onClick")
fun bindSignInClick(button: SignInButton, method: () -> Unit) {
    button.setOnClickListener { method.invoke() }
}