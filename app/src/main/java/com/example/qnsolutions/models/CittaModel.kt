package com.example.qnsolutions.models

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CittaModel(val id: Int, val nome: String, val nazione: String, val descrizione: String, var immagine: Bitmap?) : Parcelable