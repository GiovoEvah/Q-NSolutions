package com.example.qnsolutions.models

import java.math.BigDecimal

data class PrenotazioneModel(val nome : String, val importo: BigDecimal, val ref_transazione: Int, val data_ora_prenotazione : String, val numero_prenotati : Int, val ref_utente : String, val ref_attrazione : Int, val carta : String)