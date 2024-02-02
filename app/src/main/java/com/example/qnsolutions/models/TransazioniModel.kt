package com.example.qnsolutions.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class TransazioniModel( val id: Int, val carta: String, val data_ora_pagamento: String, val importo: BigDecimal) : Parcelable