package com.example.mychat.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.mychat.databinding.ActivitySignUpBinding
import com.example.mychat.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference()

        binding.signUpBtn.setOnClickListener {
            checkAndRegisterUsers()
        }

        binding.logInBtn.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
        }

    }

    private fun checkAndRegisterUsers() {
        val userName = binding.etName.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        if(TextUtils.isEmpty(userName)){
            Toast.makeText(applicationContext,"Name cannot be Empty", Toast.LENGTH_LONG).show()
        }

        if(TextUtils.isEmpty(email)){
            Toast.makeText(applicationContext,"Email cannot be Empty", Toast.LENGTH_LONG).show()
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(applicationContext,"Password cannot be Empty", Toast.LENGTH_LONG).show()
        }

        if(TextUtils.isEmpty(confirmPassword)){
            Toast.makeText(applicationContext,"Confirm Password cannot be Empty", Toast.LENGTH_LONG).show()
        }

        if(password != confirmPassword){
            Toast.makeText(applicationContext, "Confirm password doesn't match with the provided password", Toast.LENGTH_LONG).show()
            Log.d("SignInActivity","pass Not match $password $confirmPassword")
        }

        registerUser(userName,email,password)
    }

    private fun registerUser(userName: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    val userId: String = auth.currentUser?.uid.toString()

                    databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId)

                    val hashMap: HashMap<String,String> = HashMap()
                    hashMap.put("userId",userId)
                    hashMap.put("userName",userName)
                    hashMap.put("profileImage","")

                    databaseReference.setValue(hashMap).addOnCompleteListener {
                        if(it.isSuccessful) {
                            val intent = Intent(this@SignUpActivity, UsersActivity::class.java)
                            startActivity(intent)
                        }
                        else{
                            Toast.makeText(baseContext,"Some problem ${it.exception.toString()}",Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    Log.d("SignInActivity","${task.exception}")
                    Toast.makeText(applicationContext, "Something went wrong",Toast.LENGTH_LONG).show()
                }
            }
    }
}