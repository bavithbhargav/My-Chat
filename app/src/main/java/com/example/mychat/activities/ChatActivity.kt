package com.example.mychat.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.mychat.R
import com.example.mychat.adapters.ChatAdapter
import com.example.mychat.databinding.ActivityChatBinding
import com.example.mychat.models.Chat
import com.example.mychat.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference

    private val chatList = ArrayList<Chat>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)

        val intent = getIntent()
        val userId = intent.getStringExtra("userId")

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)

        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val currUser = snapshot.getValue(User::class.java)
                binding.tvUserName.text = currUser!!.userName
                if(currUser!!.profileImage == ""){
                    binding.userProfileImg.setImageResource(R.drawable.profile_img)
                }
                else{
                    Glide.with(this@ChatActivity).load(currUser.profileImage).into(binding.userProfileImg)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        binding.backImgChat.setOnClickListener {
            onBackPressed()
        }

        binding.sendBtn.setOnClickListener {
            val message = binding.messageBox.text.trim().toString()
            if(message.isEmpty()){
                Toast.makeText(applicationContext,"Cannot send empty message",Toast.LENGTH_SHORT).show()
            }
            else{
                sendMessage(firebaseUser.uid,userId,message)
                binding.messageBox.setText("")
            }
        }

        readMessages(firebaseUser.uid,userId)
    }

    private fun sendMessage(senderId: String, receiverId: String, message: String) {
        var dbRef = FirebaseDatabase.getInstance().getReference()

        val hashMap: HashMap<String,String> = HashMap()
        hashMap.put("senderId",senderId)
        hashMap.put("receiverId",receiverId)
        hashMap.put("message",message)

        dbRef.child("Chat").push().setValue(hashMap)
    }

    private fun readMessages(senderId: String, receiverId: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Chat")

        databaseReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for(dataSnapshot in snapshot.children) {
                    val chat = dataSnapshot.getValue(Chat::class.java)

                    if(chat!!.senderId.equals(senderId) && chat!!.receiverId.equals(receiverId) ||
                        chat!!.senderId.equals(receiverId) && chat!!.receiverId.equals(senderId)) {
                        chatList.add(chat)
                    }
                }

                val chatAdapter = ChatAdapter(this@ChatActivity, chatList)
                binding.chatRecyclerView.adapter = chatAdapter
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}