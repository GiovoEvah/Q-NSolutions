package com.example.qnsolutions.models

import android.graphics.Bitmap
import java.math.BigDecimal

data class PietanzaModel(val nome: String, val ingredienti: String, val prezzo: BigDecimal, var immagine: Bitmap?)
