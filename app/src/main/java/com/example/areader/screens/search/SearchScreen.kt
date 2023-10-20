package com.example.areader.screens.search

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.areader.components.AppBar
import com.example.areader.components.InputField
import com.example.areader.model.apiModel.Item
import com.example.areader.navigation.AllScreens

@ExperimentalComposeUiApi
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@ExperimentalMaterial3Api
@Composable
fun SearchScreen(navController: NavController, searchViewModel: SearchViewModel) {
    Scaffold(topBar = {
        AppBar(
            title = "Search books",
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
                .padding(top = 50.dp)
        ) {
            Column {
                SearchForm(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) { searchQuery ->
                    searchViewModel.searchBooks(query = searchQuery)
                }
                Spacer(modifier = Modifier.height(13.dp))
                BookList(navController = navController, searchViewModel = searchViewModel)
            }
        }
    }
}

@Composable
fun BookList(navController: NavController, searchViewModel: SearchViewModel) {
//    val listOfBooks = listOf(
//        MBook(id = "ded", title = "Hello Again", authors = "All of us", notes = null),
//        MBook(id = "ded", title = "Again", authors = "All of us", notes = null),
//        MBook(id = "ded", title = "Hello", authors = "All of us", notes = null),
//        MBook(id = "ded", title = "Hello Again", authors = "All of us", notes = null),
//        MBook(id = "ded", title = "Hello Again", authors = "All of us", notes = null),
//    )
    val listOfBooks = searchViewModel.list
    if (searchViewModel.isLoading) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
            items(items = listOfBooks) { book ->
                BookRow(book = book, navController = navController)
            }
        }
    }
}

@Composable
fun BookRow(book: Item, navController: NavController) {
    Card(modifier = Modifier
        .clickable {
            navController.navigate(route = AllScreens.DetailsScreen.name + "/${book.id}")
        }
        .fillMaxWidth()
        .height(100.dp)
        .padding(3.dp),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(7.dp)) {
        Row(modifier = Modifier.padding(5.dp), verticalAlignment = Alignment.Top) {
            val imageUrl = book.volumeInfo.imageLinks.smallThumbnail
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .padding(end = 4.dp)
            )
            Column {
                Text(text = book.volumeInfo.title, overflow = TextOverflow.Ellipsis)
                Text(
                    text = "Authors: ${book.volumeInfo.authors}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "Date: ${book.volumeInfo.publishedDate}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "${book.volumeInfo.categories}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodyLarge
                )


            }
        }
    }
}


@SuppressLint("UnrememberedMutableState")
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
@Composable
fun SearchForm(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    hint: String = "",
    onSearch: (String) -> Unit
) {
    Column {
        val searchQueryState = rememberSaveable { mutableStateOf("") }
        val keyboardController = LocalSoftwareKeyboardController.current
        val valid = derivedStateOf {
            searchQueryState.value.trim().isNotEmpty()
        }

        InputField(
            valueState = searchQueryState,
            labelId = "Search",
            enabled = true,
            imeAction = ImeAction.Done,
            onAction = KeyboardActions {
                if (!valid.value) return@KeyboardActions
                onSearch(searchQueryState.value.trim())
                searchQueryState.value = ""
                keyboardController?.hide()
            })

    }
}