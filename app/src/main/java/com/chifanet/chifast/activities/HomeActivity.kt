package com.chifanet.chifast

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chifanet.chifast.activities.AuthActivity
import com.chifanet.chifast.modes.Menu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.menu_content.view.*

enum class ProviderType{
    BASIC,
    GOOGLE
}

class HomeActivity : AppCompatActivity() {

    private val database = Firebase.database
    private lateinit var messagesListener: ValueEventListener
    private val listMenus:MutableList<Menu> = ArrayList()
    val myRef = database.getReference("menu")

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        setup(email ?:"", provider ?: "")


        listMenus.clear()
        setupRecyclerView(menuRecyclerView)
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

    private fun setupRecyclerView(recyclerView: RecyclerView) {

        messagesListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listMenus.clear()
                dataSnapshot.children.forEach { child ->
                    val menu: Menu? =
                        Menu(child.child("name").getValue<String>(),
                            child.child("precio").getValue<String>(),
                            child.child("description").getValue<String>(),
                            child.child("url").getValue<String>(),
                            child.key)
                    menu?.let { listMenus.add(it) }
                }
                recyclerView.adapter = MenuViewAdapter(listMenus)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "messages:onCancelled: ${error.message}")
            }
        }
        myRef.addValueEventListener(messagesListener)

    }

    class MenuViewAdapter(private val values: List<Menu>) :
        RecyclerView.Adapter<MenuViewAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.menu_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val menu = values[position]
            holder.mNameTextView.text = menu.name
            holder.mPrecioTextView.text = menu.precio
            holder.mPosterImgeView?.let {
                Glide.with(holder.itemView.context)
                    .load(menu.url)
                    .into(it)
            }

        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val mNameTextView: TextView = view.nameTextView
            val mPrecioTextView: TextView = view.precioTextView
            val mPosterImgeView: ImageView? = view.posterImgeView
        }
    }
}

