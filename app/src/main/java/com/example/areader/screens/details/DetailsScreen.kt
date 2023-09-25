package com.example.areader.screens.details

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.areader.components.AppBar
import com.example.areader.components.RoundedButton
import com.example.areader.data.Resource
import com.example.areader.model.MBook
import com.example.areader.model.apiModel.Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@ExperimentalMaterial3Api
@Composable
fun DetailsScreen(
    navController: NavController,
    bookId: String,
    detailsViewModel: DetailsViewModel
) {
    Scaffold(topBar = {
        AppBar(
            title = "Book Details",
            navController = navController,
            icon = Icons.Default.ArrowBack,
            showProfile = false
        ) {
            navController.popBackStack()
        }
    }) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 50.dp)
        ) {
            Column(
                modifier = Modifier.padding(top = 12.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val bookInfo = produceState<Resource<Item>>(initialValue = Resource.Loading()) {
                    value = detailsViewModel.getBookInfo(bookId = bookId)
                }.value

                if (bookInfo.data == null) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

                } else {
                    ShowBookDetails(bookInfo = bookInfo, navController = navController)
                }

            }
        }
    }
}

@Composable
fun ShowBookDetails(bookInfo: Resource<Item>, navController: NavController) {
    Card(
        modifier = Modifier.padding(34.dp),
        shape = CircleShape,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = bookInfo.data?.volumeInfo?.imageLinks?.thumbnail),
            contentDescription = null,
            modifier = Modifier
                .width(90.dp)
                .height(90.dp)
                .padding(1.dp)
        )
    }
    Text(
        text = bookInfo.data?.volumeInfo?.title.toString(),
        style = MaterialTheme.typography.bodyLarge,
        overflow = TextOverflow.Ellipsis,
        maxLines = 19
    )
    Text(
        text = "Authors: " + bookInfo.data?.volumeInfo?.authors.toString(),
    )
    Text(
        text = "Page Count: " + bookInfo.data?.volumeInfo?.pageCount.toString(),
    )
    Text(
        text = "Categories: " + bookInfo.data?.volumeInfo?.categories.toString(),
        style = MaterialTheme.typography.titleMedium,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis
    )
    Text(
        text = "Published: " + bookInfo.data?.volumeInfo?.publishedDate.toString(),
        style = MaterialTheme.typography.titleMedium
    )

    Spacer(modifier = Modifier.height(5.dp))

    val cleanDescription = HtmlCompat.fromHtml(
        bookInfo.data?.volumeInfo?.description.toString(),
        HtmlCompat.FROM_HTML_MODE_LEGACY
    ).toString()
    val localDims = LocalContext.current.resources.displayMetrics
    Surface(
        modifier = Modifier
            .height(localDims.heightPixels.dp.times(0.09f))
            .fillMaxWidth()
            .padding(4.dp),
        shape = RectangleShape,
        border = BorderStroke(width = 1.dp, color = Color.DarkGray)
    ) {
        LazyColumn(modifier = Modifier.padding(3.dp)) {
            item {
                Text(text = cleanDescription)
            }
        }
    }


    Row(modifier = Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.SpaceAround) {
        RoundedButton(label = "Save", radius = 29) {

            // TODO save this book to firebase
            val book = MBook(
                title = bookInfo.data?.volumeInfo?.title.toString(),
                authors = bookInfo.data?.volumeInfo?.authors.toString(),
                description = bookInfo.data?.volumeInfo?.description.toString(),
                categories = bookInfo.data?.volumeInfo?.categories.toString(),
                notes = "",
                photoUrl = bookInfo.data?.volumeInfo?.imageLinks?.thumbnail.toString(),
                publishedDate = bookInfo.data?.volumeInfo?.publishedDate.toString(),
                pageCount = bookInfo.data?.volumeInfo?.pageCount.toString(),
                rating = 0.0,
                googleBookId = bookInfo.data?.id,
                userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
            )
            saveToFirebase(book = book, navController = navController)

        }
        Spacer(modifier = Modifier.width(25.dp))
        RoundedButton(label = "Cancel", radius = 29) {
            //TODO return to search screen
            navController.popBackStack()
        }
    }

}


fun saveToFirebase(book: MBook, navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val dbCollection = db.collection("books")

    if (book.toString().isNotEmpty()) {

        dbCollection.add(book)
            .addOnSuccessListener { documentRef ->
                val docId = documentRef.id
                dbCollection.document(docId)
                    .update(hashMapOf("id" to docId) as Map<String, Any>)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            navController.popBackStack()
                        }
                    }.addOnFailureListener {
                        Log.w("TAG", "SaveToFirebase: Error updating doc ", it)
                    }
            }
    } else {

    }
}
