package com.example.qnsolutions.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RistoranteModel(val numero_coperti: Int, val tipo_cucina: String, val orario_apertura: String, val orario_chiusura: String) : Parcelable
