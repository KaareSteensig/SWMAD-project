package groupassignment.tourshare

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import groupassignment.tourshare.firebase.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var isLoggedIn = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Check if the user is logged in
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            // User is logged in, start the main activity
            // startActivity(Intent(this, MainScreenActivity::class.java))
        } else {
            // User is not logged in, start the login activity
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Finish the current activity so that the user cannot go back to it after logging in
        finish()
    }

}