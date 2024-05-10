package com.example.architec.ui.transform

import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.architec.data.ArchitectureStyle
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class TransformViewModel : ViewModel() {
    private val db = Firebase.firestore

    private val _styles = MutableLiveData<List<ArchitectureStyle>>()

    val styles: LiveData<List<ArchitectureStyle>>
        get() = _styles

    init {
        loadStyles()
    }

    private fun loadStyles() {
        db.collection("styles")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val stylesList = mutableListOf<ArchitectureStyle>()
                for (document in querySnapshot.documents) {
                    val style = document.toObject(ArchitectureStyle::class.java)
                    style?.let {
                        stylesList.add(it)
                    }
                }
                _styles.value = stylesList
            }
            .addOnFailureListener { exception ->
                Log.w("Get styles from db", "Error getting documents.", exception)
            }
    }
}
