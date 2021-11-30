package com.chifanet.chifast

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chifanet.chifast.activities.AuthActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

enum class ProviderType{
    BASIC,
    GOOGLE
}

class HomeActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        setup(email ?:"", provider ?: "")

        // Guardado de datoa

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.commit()
    }

    private fun setup(email: String, provider: String) {
        title = "Inicio"

        emailTextView.text = email
        providerTextView.text = provider

        btnLogOut.setOnClickListener {

            //BORRADO DE DATOS
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.commit()

            FirebaseAuth.getInstance().signOut()

            val loginIntent = Intent(this, AuthActivity::class.java)
            startActivity(loginIntent)
        }
    }
}

