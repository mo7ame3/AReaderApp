package com.example.areader.repository

import android.util.Log
import com.example.areader.data.Resource
import com.example.areader.model.apiModel.Item
import com.example.areader.network.BooksApi
import javax.inject.Inject

class BookRepository @Inject constructor(private val api: BooksApi) {


    suspend fun getBooks(searchQuery: String):
            Resource<List<Item>> {
        return try {
            Resource.Loading(data = true)
            val itemList = api.getAllBooks(query = searchQuery).items
            if (itemList.isNotEmpty()) {
                Resource.Loading(data = false)
            }
            Resource.Success(data = itemList)

        } catch (e: Exception) {
            Log.d("TAG", "getBooks: ${e.message}")
            Resource.Error(message = e.message.toString())
        }

    }

    suspend fun getBookInfo(bookId: String):
            Resource<Item> {
        val response = try {
            Resource.Loading(data = true)
            api.getBookInfo(bookId = bookId)

        } catch (e: Exception) {
            return Resource.Error(message = e.message.toString())
        }
        Resource.Loading(data = false)
        return Resource.Success(data = response)
    }

}