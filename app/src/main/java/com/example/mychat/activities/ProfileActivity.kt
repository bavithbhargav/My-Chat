package com.example.mychat.activities

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.mychat.R
import com.example.mychat.databinding.ActivityProfileBinding
import com.example.mychat.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    private var filePath: Uri? = null
    private val PICK_IMG_REQUEST = 2020

    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val currUser = auth.currentUser

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(currUser?.uid!!)

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        databaseReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                binding.userName.setText(user!!.userName)

                if(user.profileImage == ""){
                    binding.userProfile.setImageResource(R.drawable.profile_img)
                }
                else{
                    Glide.with(this@ProfileActivity).load(user.profileImage).into(binding.userProfile)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,error.message.toString(),Toast.LENGTH_SHORT).show()
            }
        })

        binding.imgProfileBack.setOnClickListener {
            val intent = Intent(this,UsersActivity::class.java)
            startActivity(intent)
        }

        binding.userProfile.setOnClickListener {
            chooseImage()
        }

        binding.saveButton.setOnClickListener {
            uploadImage()
            binding.progressBar.visibility = View.VISIBLE
        }

    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,"Select Image"),PICK_IMG_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMG_REQUEST && requestCode != null){
            filePath = data!!.data
            try {
                var bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver,filePath)
                binding.userProfile.setImageBitmap(bitmap)
                binding.saveButton.visibility = View.VISIBLE
            }
            catch(e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage() {
        if(filePath != null) {

            var ref = storageReference.child("image/"+UUID.randomUUID().toString())
            ref.putFile(filePath!!)
                .addOnSuccessListener {


                    val hashMap: HashMap<String,String> = HashMap()
                    hashMap.put("userName",binding.userName.text.toString())
                    hashMap.put("profileImage",filePath.toString())
                    databaseReference.updateChildren(hashMap as Map<String, Any>)

                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(applicationContext,"Uploaded",Toast.LENGTH_SHORT).show()
                    binding.saveButton.visibility = View.GONE
                }
                .addOnFailureListener {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(applicationContext,"Failed"+it.message.toString(),Toast.LENGTH_SHORT).show()
                }
        }
    }
}