package com.example.areader.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.areader.components.AppBar
import com.example.areader.components.FABContent
import com.example.areader.components.ListCard
import com.example.areader.components.TitleSection
import com.example.areader.model.MBook
import com.example.areader.navigation.AllScreens
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@ExperimentalMaterial3Api
@Composable
fun HomeScreen(navController: NavController) {

    Scaffold(
        topBar = { AppBar(title = "A.Reader", navController = navController) },
        floatingActionButton = {
            FABContent {
                navController.navigate(route = AllScreens.SearchScreen.name)
            }
        }) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 50.dp)
        ) {
            HomeContent(navController = navController)
        }
    }


}


@Composable
fun HomeContent(navController: NavController) {

    val listOfBooks = listOf(
        MBook(id = "ded", title = "Hello Again", authors = "All of us", notes = null),
        MBook(id = "ded", title = "Again", authors = "All of us", notes = null),
        MBook(id = "ded", title = "Hello", authors = "All of us", notes = null),
        MBook(id = "ded", title = "Hello Again", authors = "All of us", notes = null),
        MBook(id = "ded", title = "Hello Again", authors = "All of us", notes = null),
    )

    val currentUserName =
        if (!FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty())
            FirebaseAuth.getInstance().currentUser?.email?.split("@")?.get(0) else "N/A"
    Column(modifier = Modifier.padding(2.dp), verticalArrangement = Arrangement.Top) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TitleSection(label = "Your reading \n" + "activity right now...")
            Column(modifier = Modifier.padding(end = 2.dp)) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier
                        .size(45.dp)
                        .clickable {
                            navController.navigate(route = AllScreens.StatsScreen.name)
                        })
                Text(
                    text = currentUserName.toString(),
                    modifier = Modifier.padding(2.dp),
                    maxLines = 1,
                    color = Color.Red,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 15.sp,
                    overflow = TextOverflow.Clip
                )
                Divider(modifier = Modifier.width(45.dp))
            }
        }
        ReadingRightNowArea(books = listOfBooks, navController = navController)

        TitleSection(label = "Reading List")
        BookListArea(listOfBooks = listOfBooks, navController = navController)
    }
}

@Composable
fun BookListArea(listOfBooks: List<MBook>, navController: NavController) {
    HorizontalScrollableComponent(listOfBooks) {

    }
}

@Composable
fun HorizontalScrollableComponent(listOfBooks: List<MBook>, onCardPressed: (String) -> Unit) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(280.dp)
            .horizontalScroll(scrollState)
    ) {
        for (book in listOfBooks) {
            ListCard(book = book) {
                onCardPressed(it)
            }
        }
    }

}


@Composable
fun ReadingRightNowArea(books: List<MBook>, navController: NavController) {
    ListCard(book = books[0]) {

    }

}


