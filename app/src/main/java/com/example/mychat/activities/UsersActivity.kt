
package com.example.mychat.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.mychat.R
import com.example.mychat.adapters.UserAdapter
import com.example.mychat.databinding.ActivityUsersBinding
import com.example.mychat.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsersBinding
    private lateinit var userAdapter: UserAdapter

    var usersList = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prepareRecyclerView()

        binding.userProfileImg.setOnClickListener {
            val intent = Intent(this,ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun prepareRecyclerView() {

        getUsersList()
        userAdapter = UserAdapter(this@UsersActivity, usersList)

        binding.usersRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@UsersActivity, LinearLayoutManager.VERTICAL, false)
            adapter = userAdapter
        }
    }

    private fun getUsersList() {
        var firebaseUser = FirebaseAuth.getInstance().currentUser
        var databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        databaseReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                usersList.clear()

                val currUser = snapshot.getValue(User::class.java)
                if(currUser!!.profileImage == ""){
                    binding.userProfileImg.setImageResource(R.drawable.profile_img)
                }
                else{
                    Glide.with(this@UsersActivity).load(currUser.profileImage).into(binding.userProfileImg)
                }


                for(dataSnapshot in snapshot.children){
                    val user = dataSnapshot.getValue(User::class.java)

                    if(user!!.userId != firebaseUser!!.uid){
                        usersList.add(User(user.userId,user.userName,user.profileImage))
                        Log.d("UserActivity","User ${user?.userName} ")
                        Log.d("UserActivity","${usersList.size}")
                    }
                }
                Log.d("UserActivity","${usersList.size}")
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message.toString(),Toast.LENGTH_SHORT).show()
            }
        })
    }
}