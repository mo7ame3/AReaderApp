package com.example.areader.screens.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.areader.data.DataOrException
import com.example.areader.model.MBook
import com.example.areader.repository.FireRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: FireRepository) :
    ViewModel() {

    val data: MutableState<DataOrException<List<MBook>, Boolean, Exception>> = mutableStateOf(
        DataOrException(listOf(), true, Exception(""))
    )

    init {
        getAllBooksFromFirebase()
    }

    private fun getAllBooksFromFirebase() {
        viewModelScope.launch {
            data.value.loading = true
            data.value = repository.getAllBooksFromFirebase()

            if (!data.value.data.isNullOrEmpty()){
                data.value.loading = false
                Log.d("TAG", "getAllBooksFromFirebase: ${data.value.data?.toList().toString()}")

            }
        }
    }

}