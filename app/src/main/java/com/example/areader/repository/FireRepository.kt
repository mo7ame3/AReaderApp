package com.example.areader.repository

import android.util.Log
import com.example.areader.data.DataOrException
import com.example.areader.model.MBook
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FireRepository @Inject constructor(private val queryBook: Query) {

    suspend fun getAllBooksFromFirebase(): DataOrException<List<MBook>, Boolean, Exception> {
        val dataOrException = DataOrException<List<MBook>, Boolean, Exception>()

        try {
            dataOrException.loading = true
            dataOrException.data = queryBook.get().await().documents.map { documentSnapshot ->
                documentSnapshot.toObject(MBook::class.java)!!
            }
            if (!dataOrException.data.isNullOrEmpty()) dataOrException.loading = false
        } catch (e: FirebaseFirestoreException) {
            dataOrException.e = e
            Log.d("TAG", "getAllBooksFromFirebase: ${e.message}")
        }
        return dataOrException
    }
}