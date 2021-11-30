package com.chifanet.chifast.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.chifanet.chifast.HomeActivity
import com.chifanet.chifast.ProviderType
import com.chifanet.chifast.R
import com.chifanet.chifast.firestore.FirestoreClass
import com.chifanet.chifast.modes.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.loginWithGoogle
import kotlinx.android.synthetic.main.activity_register.password
import kotlinx.android.synthetic.main.activity_register.username

class RegisterActivity : AppCompatActivity() {

    private val GOOGLE_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integracion de Firebase completa")
        analytics.logEvent("InitScreen", bundle)

        setup()
    }

    private fun setup() {

        title = "Autenticación"

        btnRegister.setOnClickListener {
            if (username.text.isNotEmpty() && password.text.isNotEmpty()){
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(
                        username.text.toString(),
                        password.text.toString()
                    ).addOnCompleteListener{
                        if (it.isSuccessful){
                            val firebaseUser: FirebaseUser = it.result!!.user!!

                            val user = User(
                                username.text.toString().trim{ it <= ' ' },
                                firebaseUser.uid,
                                ProviderType.GOOGLE.toString()
                            )

                            FirestoreClass().registerUser(this@RegisterActivity,user)

                            showHome(it.result?.user?.email?:"", ProviderType.BASIC)
                        }else{
                            showAlert()
                        }
                    }
            }
        }
        txtGoLogin.setOnClickListener {
            showLogin()
        }
        loginWithGoogle.setOnClickListener{
            // Configure Google Sign In
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //getString(R.String.default_web_client_id)
                .requestIdToken("170630311919-k6bhss4cvf5k9eh7u50qtvh75rgahnbf.apps.googleusercontent.com")
                .requestEmail()
                .build()
            val googleClient = GoogleSignIn.getClient(this, gso)
            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)

        }
    }

    private fun showLogin() {
        val loginIntent = Intent(this, AuthActivity::class.java)
        startActivity(loginIntent)
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ah producido un error en el registro del usuario")
        builder.setPositiveButton("Acepar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, provider: ProviderType){
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)

                if (account != null){

                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener{
                        if (it.isSuccessful){
                            val firebaseUser: FirebaseUser = it.result!!.user!!

                            val user = User(
                                firebaseUser.email.toString(),
                                username.text.toString().trim{ it <= ' ' }
                            )

                            FirestoreClass().registerUser(this@RegisterActivity,user)

                            showHome(it.result?.user?.email?:"", ProviderType.GOOGLE)
                        }else{
                            showAlert()
                        }
                    }
                }
            } catch (e: ApiException){
                showAlert()
            }


        }
    }

    fun userRegistrationSucess(){

        Toast.makeText(
            this@RegisterActivity,
            resources.getString(R.string.registro_exitoso),
            Toast.LENGTH_SHORT
        ).show()
    }

}