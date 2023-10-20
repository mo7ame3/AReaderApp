package com.example.areader.screens.stats

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.sharp.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.areader.components.AppBar
import com.example.areader.model.MBook
import com.example.areader.screens.home.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@ExperimentalMaterial3Api
@Composable
fun StatsScreen(navController: NavController, homeViewModel: HomeViewModel) {
    var books: List<MBook>
    val currentUser = FirebaseAuth.getInstance().currentUser

    Scaffold(topBar = {
        AppBar(
            title = "Book Stats",
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
            books = if (!homeViewModel.data.value.data.isNullOrEmpty()) {
                homeViewModel.data.value.data!!.filter { mBook ->
                    (mBook.userId == currentUser?.uid)
                }
            } else {
                emptyList()
            }
            Column {
                Row {
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .padding(2.dp)
                    ) {
                        Icon(imageVector = Icons.Sharp.Person, contentDescription = null)
                    }
                    Text(
                        text = "Hi, ${
                            currentUser?.email.toString()
                                .split("@")[0].uppercase(Locale.getDefault())
                        }"
                    )
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    shape = CircleShape,
                    elevation = CardDefaults.cardElevation(5.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    val readBooksList: List<MBook> =
                        if (!homeViewModel.data.value.data.isNullOrEmpty()) {
                            books.filter { mBook ->
                                (mBook.userId == currentUser?.uid) && (mBook.finishedReading != null)
                            }
                        } else {
                            emptyList()
                        }
                    val readingBooks = books.filter { mBook ->
                        (mBook.startedReading != null && mBook.finishedReading == null)
                    }

                    Column(
                        modifier = Modifier.padding(start = 25.dp, top = 4.dp, bottom = 4.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(text = "Your Stats", style = MaterialTheme.typography.bodyLarge)
                        Divider(modifier = Modifier.fillMaxWidth())
                        Text(text = "You're reading: ${readingBooks.size} books")
                        Text(text = "You're read: ${readBooksList.size} books")
                    }

                }
                if (homeViewModel.data.value.loading == true) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                } else {
                    Divider(modifier = Modifier.fillMaxWidth())
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        val readBooks: List<MBook> =
                            if (!homeViewModel.data.value.data.isNullOrEmpty()) {
                                homeViewModel.data.value.data!!.filter { mBook ->
                                    (mBook.userId == currentUser?.uid) && (mBook.finishedReading != null)
                                }
                            } else {
                                emptyList()
                            }
                        items(items = readBooks) { book ->
                            BookRowStats(book = book)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookRowStats(book: MBook) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(3.dp),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(7.dp)
    ) {
        Row(modifier = Modifier.padding(5.dp), verticalAlignment = Alignment.Top) {
            val imageUrl = book.photoUrl
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .padding(end = 4.dp)
            )
            Column {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = book.title.toString(), overflow = TextOverflow.Ellipsis)
                    if (book.rating!! >= 4) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = null,
                            tint = Color.Green.copy(alpha = 0.5f)
                        )
                    } else {
                        Box {

                        }
                    }
                }
                Text(
                    text = "Authors: ${book.authors}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "Date: ${book.publishedDate}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "${book.categories}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodyLarge
                )


            }
        }
    }
}