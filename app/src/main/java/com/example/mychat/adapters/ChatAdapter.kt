package com.example.mychat.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mychat.R
import com.example.mychat.activities.ChatActivity
import com.example.mychat.databinding.ItemUsersBinding
import com.example.mychat.models.Chat
import com.example.mychat.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter(private val context: Context, private val chatList: ArrayList<Chat>): RecyclerView.Adapter<ChatAdapter.UserViewHolder>() {

    private val MESSAGE_TYPE_LEFT = 1
    private val MESSAGE_TYPE_RIGHT = 2
    private var firebaseUser: FirebaseUser? = null

    inner class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val textView: TextView = itemView.findViewById(R.id.userNameChat)
        val imageView: CircleImageView = itemView.findViewById(R.id.userImageChat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        if(viewType == MESSAGE_TYPE_RIGHT){
            val view = LayoutInflater.from(context).inflate(R.layout.item_right,parent,false)
            return UserViewHolder(view)
        }
        else{
            val view = LayoutInflater.from(context).inflate(R.layout.item_left,parent,false)
            return UserViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val chat = chatList[position]
        holder.textView.text = chat.message
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if(chatList[position].senderId == firebaseUser!!.uid){
            return MESSAGE_TYPE_RIGHT
        }
        else{
            return MESSAGE_TYPE_LEFT
        }
    }
}