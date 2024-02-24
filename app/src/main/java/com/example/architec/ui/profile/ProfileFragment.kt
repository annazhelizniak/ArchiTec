package com.example.architec.ui.profile

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.architec.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        setUpProfile()
        binding.signOut.setOnClickListener {
            auth.signOut()
            activity?.finish()
        }
        return binding.root
    }

    private fun setUpProfile(){
        Thread{
            val image = Picasso.get().load(auth.currentUser?.photoUrl).get()
            val dIcon = BitmapDrawable(resources, image)
            val displayedName = auth.currentUser?.displayName ?: "Unknown"
            val name = displayedName.split(" ")[0]
            val surname = displayedName.split(" ")[1]
            val email = auth.currentUser?.email
            activity?.runOnUiThread{
                binding.userProfileImage.setImageDrawable(dIcon)
                binding.userProfileName.text = name
                binding.userProfileSurname.text  = surname
                binding.userProfileEmail.text = email
            }
        }.start()
    }
}
