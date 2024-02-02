package com.example.qnsolutions.models

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UtenteModel(var nome : String, var cognome : String, var data_nascita : String,  var sesso : String, var email : String, var residenza : String, var passw : String, var cellulare : String, var immagine: Bitmap?) : Parcelable