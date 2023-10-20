package com.example.areader.screens.update

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.areader.R
import com.example.areader.components.AppBar
import com.example.areader.components.InputField
import com.example.areader.components.RatingBar
import com.example.areader.components.RoundedButton
import com.example.areader.data.DataOrException
import com.example.areader.model.MBook
import com.example.areader.navigation.AllScreens
import com.example.areader.screens.home.HomeViewModel
import com.example.areader.utils.formatDate
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UpdateScreen(navController: NavController, bookItemId: String, homeViewModel: HomeViewModel) {
    Scaffold(topBar = {
        AppBar(
            title = "Update Book",
            navController = navController,
            icon = Icons.Default.ArrowBack,
            showProfile = false
        ) {
            navController.navigate(route = AllScreens.HomeScreen.name)
        }
    }) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 50.dp, bottom = 3.dp, start = 3.dp, end = 3.dp)
        ) {
            Column(
                modifier = Modifier.padding(top = 3.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = CenterHorizontally
            ) {
                if (homeViewModel.data.value.data.isNullOrEmpty()) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                } else {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(2.dp),
                        shadowElevation = 4.dp,
                        shape = CircleShape
                    ) {
                        ShowBookUpdate(bookInfo = homeViewModel.data.value, bookItemId = bookItemId)
                    }
                    ShowSimpleForm(book = homeViewModel.data.value.data?.first { mBook ->
                        mBook.googleBookId == bookItemId
                    }!!, navController = navController)
                }
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun ShowSimpleForm(book: MBook, navController: NavController) {
    val notesText = remember {
        mutableStateOf("")
    }
    val isStartedReading = remember {
        mutableStateOf(false)
    }
    val isFinishedReading = remember {
        mutableStateOf(false)
    }
    val ratingVal = remember {
        mutableStateOf(0)
    }
    SimpleForm(defaultValue = book.notes.toString()
        .ifEmpty { "No thoughts available :(" }) { note ->
        notesText.value = note
    }

    Row(
        modifier = Modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        TextButton(onClick = {
            isStartedReading.value = true
        }, enabled = book.startedReading == null) {
            if (book.startedReading == null) {
                if (!isStartedReading.value) {
                    Text(text = "Start Reading")
                } else {
                    Text(
                        text = "Started Reading!",
                        modifier = Modifier.alpha(0.6f),
                        color = Color.Red.copy(alpha = 0.5f)
                    )
                }
            } else {
                Text(text = "Started on: ${formatDate(book.startedReading!!)}")
            }

        }
        Spacer(modifier = Modifier.height(4.dp))
        TextButton(
            onClick = { isFinishedReading.value = true }, enabled = book.finishedReading == null
        ) {
            if (book.finishedReading == null) {
                if (!isFinishedReading.value) {
                    Text(text = "Mark ad Read")
                } else {
                    Text(text = "Finished Reading!")
                }
            } else {
                Text(text = "Finished on: ${formatDate(book.finishedReading!!)}")
            }
        }
    }

    Text(text = "Rating", modifier = Modifier.padding(bottom = 3.dp))
    book.rating?.toInt().let {
        RatingBar(rating = it!!) { rating ->
            ratingVal.value = rating
        }
    }

    Spacer(modifier = Modifier.padding(bottom = 15.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        val changedNotes = book.notes != notesText.value
        val changedRating = book.rating?.toInt() != ratingVal.value
        val isFinishedTimeStamp =
            if (isFinishedReading.value) Timestamp.now() else book.finishedReading
        val isStartedTimeStamp =
            if (isStartedReading.value) Timestamp.now() else book.startedReading
        val bookUpdate =
            changedNotes || changedRating || isStartedReading.value || isFinishedReading.value

        val bookToUpdate = hashMapOf(

            "finished_reading_at" to isFinishedTimeStamp,
            "started_reading_at" to isStartedTimeStamp,
            "rating" to ratingVal.value,
            "notes" to notesText.value
        ).toMap()

        RoundedButton(label = "Update", radius = 29) {

            if (bookUpdate) {
                FirebaseFirestore.getInstance().collection("books").document(book.id.toString())
                    .update(bookToUpdate)
                    .addOnCompleteListener {
                        navController.navigate(route = AllScreens.HomeScreen.name)

                    }.addOnFailureListener {
                        Log.w("TAG", "Error updating document", it)
                    }
            }
        }
        val openDialog = remember {
            mutableStateOf(false)
        }
        if (openDialog.value) {
            ShowAlertDialog(
                message = stringResource(id = R.string.sure)
                        + "\n"
                        + stringResource(id = R.string.action),
                openDialog = openDialog
            ) {
            FirebaseFirestore.getInstance().collection("books").document(book.id.toString())
                .delete()
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        openDialog.value = false
                        navController.navigate(route = AllScreens.HomeScreen.name)
                    }
                }
            }
        }
        RoundedButton(label = "Delete", radius = 29) {
        openDialog.value = true
        }
    }
}

@Composable
fun ShowAlertDialog(message: String, openDialog: MutableState<Boolean>, onYesPressed: () -> Unit) {

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { /*TODO*/ },
            title = { Text(text = "Delete Books") },
            text = { Text(text = message) },
            confirmButton = {
                TextButton(onClick = { onYesPressed.invoke() }) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { openDialog.value = false }) {
                    Text(text = "No")
                }
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalComposeUiApi
@Composable
fun SimpleForm(
    defaultValue: String,
    onSearch: (String) -> Unit
) {
    Column {
        val textFieldValue = rememberSaveable {
            mutableStateOf(defaultValue)
        }
        val keyboardController = LocalSoftwareKeyboardController.current
        val valid = remember(textFieldValue.value, textFieldValue.value.trim()::isNotEmpty)

        InputField(valueState = textFieldValue,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(3.dp)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            labelId = "Enter Your thoughts",
            imeAction = ImeAction.Done,
            enabled = true,
            onAction = KeyboardActions {
                if (!valid) return@KeyboardActions
                onSearch(textFieldValue.value.trim())
                keyboardController?.hide()
            })

    }
}

@Composable
fun ShowBookUpdate(bookInfo: DataOrException<List<MBook>, Boolean, Exception>, bookItemId: String) {
    Row {
        Spacer(modifier = Modifier.width(43.dp))
        if (bookInfo.data != null) {
            Column(modifier = Modifier.padding(4.dp), verticalArrangement = Arrangement.Center) {
                CardListItem(book = bookInfo.data!!.first { mBook ->
                    mBook.googleBookId == bookItemId
                }, onPressDetails = {})
            }
        }
    }
}

@Composable
fun CardListItem(book: MBook, onPressDetails: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .clip(
                RoundedCornerShape(20.dp)
            )
            .clickable { onPressDetails.invoke() },
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = book.photoUrl.toString()),
                contentDescription = null,
                modifier = Modifier
                    .height(120.dp)
                    .padding(4.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 120.dp, topEnd = 20.dp, bottomEnd = 0.dp, bottomStart = 0.dp
                        )
                    )
            )
            Column {
                Text(
                    text = book.title.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = book.authors.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(
                        start = 8.dp, end = 8.dp, top = 2.dp, bottom = 0.dp
                    ),
                )
                Text(
                    text = book.publishedDate.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(
                        start = 8.dp, end = 8.dp, top = 0.dp, bottom = 8.dp
                    ),
                )

            }
        }
    }
}
