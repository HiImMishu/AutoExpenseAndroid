package com.misiak.android.autoexpense.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.misiak.android.autoexpense.R
import com.misiak.android.autoexpense.database.getDatabase
import com.misiak.android.autoexpense.databinding.FragmentSignInBinding
import com.misiak.android.autoexpense.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val CLIENT_ID =
    "965189201935-vgv7761p1so3gkoojq99ereccv24stdc.apps.googleusercontent.com"
private const val GOOGLE_SIGN_IN = 9001

class SignInFragment : Fragment() {

    private lateinit var signInViewModel: SignInViewModel
    private val job = Job()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSignInBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_sign_in, container, false)
        val googleSignInClient = getGoogleSignInClient(requireContext())

        navigateWhenUserAlreadySigned()

        signInViewModel = ViewModelProvider(this).get(SignInViewModel::class.java)
        binding.viewModel = signInViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setGoogleSignInObserver(googleSignInClient)
        hideActionBar()

        return binding.root
    }

    private fun navigateWhenUserAlreadySigned() {
        GoogleSignIn.getLastSignedInAccount(context)?.let {
            navigateToMainScreen(it)
        }
    }

    private fun setGoogleSignInObserver(googleSignInClient: GoogleSignInClient) {
        signInViewModel.googleSignIn.observe(viewLifecycleOwner, Observer { googleSignIn ->
            if (googleSignIn) {
                val intent = googleSignInClient.signInIntent
                startActivityForResult(intent, GOOGLE_SIGN_IN)
                signInViewModel.onGoogleSignedIn()
            }
        })
    }

    private fun hideActionBar() {
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GOOGLE_SIGN_IN -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            signInUser(account!!)
            navigateToMainScreen(account)
        } catch (e: ApiException) {
            Toast.makeText(requireContext(), "Check your internet connection", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun signInUser(account: GoogleSignInAccount) {
        val coroutineScope = CoroutineScope(Dispatchers.Main + job)
        val database = getDatabase(requireContext().applicationContext)
        val repository = UserRepository(database, account)
        coroutineScope.launch {
            repository.signInUser()
        }
    }

    private fun navigateToMainScreen(account: GoogleSignInAccount) {
        findNavController().navigate(
            SignInFragmentDirections.actionSignInFragmentToMainScreenFragment2(
                account
            )
        )
    }

    companion object {
        fun getAccount(context: Context, action: (account: GoogleSignInAccount) -> Unit) {
            val googleSignInClient = getGoogleSignInClient(context)
            val task = googleSignInClient.silentSignIn()
            task.addOnCompleteListener { _task ->
                try {
                    action(_task.getResult(ApiException::class.java)!!)
                    println("--------------------Task success!-------------------")
                } catch (apiException: ApiException) {
                    //TODO("Handle exception")
                    println("Task error $apiException")
                }
            }
        }

        fun signOut(context: Context) {
            getGoogleSignInClient(context).signOut()
        }

        private fun getGoogleSignInClient(context: Context): GoogleSignInClient {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(CLIENT_ID)
                .requestEmail()
                .requestProfile()
                .build()

            return GoogleSignIn.getClient(context, gso)
        }
    }
}
