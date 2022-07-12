package com.example.mychat.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.mychat.databinding.ActivityLogInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LogInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        if(auth.currentUser != null){
            val intent = Intent(this@LogInActivity, UsersActivity::class.java)
            startActivity(intent)
        }

        binding.signUpBtnLog.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.logInBtnLog.setOnClickListener {
            logInUser()
        }
    }

    private fun logInUser() {
        val email = binding.etEmailLog.text.toString()
        val password = binding.etPasswordLog.text.toString()

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(this@LogInActivity,"Enter proper email and password to Log In", Toast.LENGTH_SHORT).show()
        }
        else{
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this@LogInActivity, UsersActivity::class.java)
                        startActivity(intent)
                    } else {
                        Log.d("LogInActivity","${task.exception}")
                        Toast.makeText(applicationContext, "Something went wrong",Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}