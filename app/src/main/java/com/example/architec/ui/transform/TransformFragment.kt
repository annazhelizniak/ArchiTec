package com.example.architec.ui.transform

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.architec.data.ArchitectureStyle
import com.example.architec.databinding.FragmentTransformBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore


class TransformFragment : Fragment() {

    private var _binding: FragmentTransformBinding? = null
    private val db = Firebase.firestore
    private val _styles = MutableLiveData<List<ArchitectureStyle>>()
    val stylesList = ArrayList<ArchitectureStyle>()
    private val binding get() = _binding!!
    private lateinit var viewModel: TransformViewModel

    private lateinit var transformViewModel: TransformViewModel
    private var adapter: CustomAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransformBinding.inflate(inflater, container, false)

        db.collection("styles").get()
            .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot> {
                override fun onComplete(@NonNull task: Task<QuerySnapshot>) {
                    val styles: MutableList<ArchitectureStyle> = ArrayList<ArchitectureStyle>()
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            val st: ArchitectureStyle = document.toObject(ArchitectureStyle::class.java)
                            st.id = document.id
                            styles.add(st)
                        }
                        val listView = binding.listView as ListView
                        val custom = CustomAdapter(styles, requireContext())
                        listView.adapter = custom
                        showToast("Done")
                    } else {
                        Log.d("Styles", "Error getting documents: ", task.exception)
                    }

                }
            })

        return binding.root
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}


