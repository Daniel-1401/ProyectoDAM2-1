package com.chifanet.chifast.firestore

import android.util.Log
import com.chifanet.chifast.activities.RegisterActivity
import com.chifanet.chifast.modes.User
import com.chifanet.chifast.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {
    private val db = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {
        db.collection(Constants.Document_Users).document(userInfo.email)
            .set(hashMapOf("provider" to userInfo.provider,
                           "direccion" to userInfo.direccion,
                           "telefono" to userInfo.telefono,
                           "id" to userInfo.id
            )).addOnSuccessListener {
                activity.userRegistrationSucess()
            }

            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error registrando al usuario.",
                    e
                )
            }
    }

    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserID = ""
        if (currentUser !=null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }
}