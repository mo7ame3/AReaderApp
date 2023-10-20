package com.example.areader.screens.search

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.areader.data.Resource
import com.example.areader.model.apiModel.Item
import com.example.areader.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: BookRepository) : ViewModel() {
    var list: List<Item> by mutableStateOf(emptyList())
    var isLoading: Boolean by mutableStateOf(true)

    init {
        loadBooks()
    }

    private fun loadBooks() {
        searchBooks("android")
    }

    fun searchBooks(query: String) {
        viewModelScope.launch(Dispatchers.Default) {
            if (query.isEmpty()) {
                return@launch
            }
            try {
                when (val response = repository.getBooks(searchQuery = query)) {
                    is Resource.Success -> {
                        list = response.data!!
                        if (list.isNotEmpty()) isLoading = false
                    }

                    is Resource.Error -> {
                        isLoading = false
                        Log.e("TAG", "searchBooks: Failed getting books")
                    }

                    else -> {
                        isLoading = false
                    }
                }
            } catch (e: Exception) {
                isLoading =false
                Log.d("TAG", "searchBooks: ${e.message}")
            }
        }
    }

}