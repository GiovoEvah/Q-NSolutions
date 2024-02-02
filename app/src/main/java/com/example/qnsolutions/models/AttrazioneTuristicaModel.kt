package com.example.qnsolutions.models

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
@Parcelize
data class AttrazioneTuristicaModel(val id: Int, val nome: String, val descrizione: String, val indirizzo: String, val num_telefono: String, val tipo: String, val rating: BigDecimal, val prezzo_medio: BigDecimal, var preferito: Boolean, var immagine: Bitmap?) : Parcelable