package com.chifanet.chifast.modes
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Menu(val name: String? = null, val precio: String? = null,  val description: String? = null, val url: String? = null, @Exclude val key: String? = null) {
}