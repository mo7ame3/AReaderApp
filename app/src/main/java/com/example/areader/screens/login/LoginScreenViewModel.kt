package com.example.areader.screens.login

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.areader.model.MUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class LoginScreenViewModel : ViewModel() {

    // val loadingState = MutableStateFlow(LoadingState.IDLE)
    private val auth: FirebaseAuth = Firebase.auth

    fun signInWithEmailAndPassword(
        email: String,
        password: String,
        loading: MutableState<Boolean>,
        context: Context,
        home: () -> Unit
    ) =
        viewModelScope.launch {
            loading.value = true
            try {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //TODO take To Home
                        home()
                    } else {
                        // Check if the error message indicates that the user doesn't exist
                        if (task.exception?.message?.contains("no user record") == true) {
                            // Handle the case where the user doesn't exist
                            loading.value = false
                            Log.d("TAG", "User does not exist: ${task.exception?.message}")
                            Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            // Handle other sign-in errors
                            loading.value = false

                            Log.d(
                                "TAG",
                                "signInWithEmailAndPassword failed: ${task.exception?.message}"
                            )
                            Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            } catch (ex: Exception) {
                loading.value = false
                Log.d("TAG", "signInWithEmailAndPassword: ${ex.message} ")
                Toast.makeText(context, ex.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }

    fun createUserWithEmailAndPassword(
        email: String, password: String,
        loading: MutableState<Boolean>,
        context: Context,
        home: () -> Unit
    ) {
        try {
            loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //TODO take To Home
                        val displayName = task.result.user?.email?.split('@')?.get(0)
                        createUser(displayName)
                        home()
                    } else {
                        if (task.exception?.message != null) {
                            loading.value = false
                            Log.d("TAG", "User already exist")
                            Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }

        } catch (ex: Exception) {
            loading.value = false
            Log.d("TAG", "createUserWithEmailAndPassword: ${ex.message}")
            Toast.makeText(context, ex.message, Toast.LENGTH_SHORT)
                .show()
        }
    }


    private fun createUser(displayName: String?) {
        val userId = auth.currentUser?.uid
        val user = MUser(
            userId = userId.toString(),
            displayName = displayName.toString(),
            avatarUrl = "",
            quote = "Life is great",
            profession = "Android Developer",
            id = null
        ).toMap()

        FirebaseFirestore.getInstance().collection("users").add(user)
    }


}


