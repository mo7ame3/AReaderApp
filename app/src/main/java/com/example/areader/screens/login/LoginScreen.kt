package com.example.areader.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.areader.R
import com.example.areader.components.CircleProgress
import com.example.areader.components.EmailInput
import com.example.areader.components.PasswordInput
import com.example.areader.components.ReaderLogo
import com.example.areader.components.SubmitButton
import com.example.areader.navigation.AllScreens

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
fun LoginScreen(
    navController: NavController,
    loginScreenViewModel: LoginScreenViewModel = viewModel()
) {
    val showLoginForm = rememberSaveable {
        mutableStateOf(true)
    }
    val context = LocalContext.current
    val loading = rememberSaveable {
        mutableStateOf(false)
    }
    Surface(modifier = Modifier.fillMaxSize()) {
        if(!loading.value) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                ReaderLogo()
                if (showLoginForm.value) {
                    UserForm(loading = false, isCreateAccount = false) { email, password ->
                        //TODO login
                        loginScreenViewModel.signInWithEmailAndPassword(email, password, loading,  context) {
                            navController.navigate(route = AllScreens.HomeScreen.name) {
                                navController.popBackStack()
                                navController.popBackStack()
                                navController.popBackStack()
                            }
                        }
                    }
                } else {
                    UserForm(loading = false, isCreateAccount = true) { email, password ->
                        //TODO create account
                        loginScreenViewModel.createUserWithEmailAndPassword(
                            email,
                            password,
                            loading,
                            context
                        ) {
                            navController.navigate(route = AllScreens.HomeScreen.name) {
                                navController.popBackStack()
                                navController.popBackStack()
                                navController.popBackStack()
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                modifier = Modifier.padding(15.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = if (showLoginForm.value) "Don't have an account?" else "Already have an account?")
                Text(
                    text = if (showLoginForm.value) "Sign Up" else "Login",
                    modifier = Modifier
                        .clickable {
                            showLoginForm.value = !showLoginForm.value
                        }
                        .padding(start = 5.dp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )

            }
        }
        else{
            CircleProgress()
        }
    }
    }



@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
@Composable
fun UserForm(
    loading: Boolean = false, isCreateAccount: Boolean = false,
    onDone: (String, String) -> Unit = { _, _ -> }
) {
    val emailRegex = "^[A-Za-z](.*)(@)(.+)(\\.)(.+)"
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val passwordVisibility = rememberSaveable { mutableStateOf(false) }
    val (passwordFocusRequest) = FocusRequester.createRefs()
    val keyboardController = LocalSoftwareKeyboardController.current
    val valid = remember(email.value, password.value) {
        emailRegex.toRegex().matches(email.value.trim()) && password.value.trim().isNotEmpty()
    }
    val modifier = Modifier
        .height(250.dp)
        .background(color = MaterialTheme.colorScheme.background)
        .verticalScroll(rememberScrollState())

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {

        if (isCreateAccount) Text(
            text = stringResource(id = R.string.create_acct),
            modifier = Modifier.padding(4.dp)
        ) else Text(text = "")

        EmailInput(emailState = email, enabled = !loading, onAction = KeyboardActions {
            passwordFocusRequest.requestFocus()
        })
        PasswordInput(
            modifier = Modifier.focusRequester(passwordFocusRequest),
            passwordState = password,
            labelId = "Password",
            enabled = !loading,
            passwordVisibility = passwordVisibility,
            onAction = KeyboardActions {
                if (!valid) return@KeyboardActions
                onDone(email.value.trim(), password.value.trim())
                keyboardController?.hide()
            }
        )
        SubmitButton(
            textId = if (isCreateAccount) "Create Account" else "Login",
            loading = loading, validInputs = valid
        ) {
            onDone(email.value.trim(), password.value.trim())
            keyboardController?.hide()
        }
    }

}









