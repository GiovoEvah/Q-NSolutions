package com.example.qnsolutions.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.graphics.scale
import com.example.qnsolutions.R
import com.example.qnsolutions.common.FilterWrapper
import com.example.qnsolutions.models.AttrazioneTuristicaModel
import com.example.qnsolutions.models.CameraModel
import com.example.qnsolutions.models.CittaModel
import com.example.qnsolutions.models.CreditCardModel
import com.example.qnsolutions.models.NotificaModel
import com.example.qnsolutions.models.PietanzaModel
import com.example.qnsolutions.models.PrenotazioneModel
import com.example.qnsolutions.models.RecensioneModel
import com.example.qnsolutions.models.RistoranteModel
import com.example.qnsolutions.models.TransazioniModel
import com.example.qnsolutions.models.UtenteModel
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

//onFailure errore http
//onResponse http ok
//successful nessun errore sql
//not successfull errore sql

class DBMSQuery
{
    fun verificaCredenziali(context: Context, email: String, password: String, callback: QueryReturnCallback)
    {
        val query = "select * from utente where email = '${email}' and passw = '${password}';"
        Log.e("login", "dodadw")
        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    if(response.isSuccessful)
                    {
                        if((response.body()?.get("queryset") as JsonArray).size() == 1)//utente trovato
                        {
                            Log.e("login", "Giusto")
                            callback.onReturnValue(context.resources.getString(R.string.query_succesfull), context.resources.getString(
                                R.string.query_succesfull
                            ))
                        }
                        else
                        {
                            Log.e("login", "Errata")
                            callback.onQueryFailed("Email o password errata")
                        }
                    }
                    else//errore formattazione
                    {
                        Log.e("login", "Formattaz")
                        callback.onQueryError(context.resources.getString(R.string.query_format_error))
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)//errore di rete
                {
                    Log.e("login", "Errore")
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                }
            })
    }

    fun registrazioneUtente(context: Context, utente: UtenteModel, callback: QueryReturnCallback)
    {
        val query = "insert into webmobile.utente values('${utente.nome}', '${utente.cognome}', '${utente.data_nascita}', '${utente.sesso}', '${utente.email}', '${utente.residenza}', '${utente.passw}', '${utente.cellulare}', '${context.resources.getText(
            R.string.default_image_path
        )}');"

        ClientNetwork.retrofit.insert(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    if (response.isSuccessful)//registrazione effettuata
                    {
                        callback.onReturnValue(context.resources.getString(R.string.query_succesfull), "Registrazione effettuata")
                        DBMSQuery().inserisciNotifica(context, NotificaModel(utente.email, "Benvenuto", "Benvenuto in QNSolutions! Hai completato la procedura di registrazione!"), object  : QueryReturnCallback
                        {
                            override fun onReturnValue(response: Any, message: String) {}
                            override fun onQueryFailed(fail: String) {}
                            override fun onQueryError(error: String) {}
                        })
                    }
                    else//email esistente
                    {
                        callback.onQueryFailed("Email già in uso")
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)//errore di rete
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                }
            }
        )
    }

    fun recuperaPassword(context: Context, email: String, callback: QueryReturnCallback)
    {
        val query = " select * from webmobile.utente where email = '${email}';"

        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    if(response.isSuccessful)
                    {
                        if((response.body()?.get("queryset") as JsonArray).size() == 1)//email trovata
                        {
                            aggiornaPassword(context, email, context.resources.getText(R.string.default_password).toString(), callback)//imposto password di default
                        }
                        else//email non trovata
                        {
                            callback.onQueryFailed("Email non trovata")
                        }
                    }
                    else//errore di formattazione
                    {
                        callback.onQueryError(context.resources.getString(R.string.query_format_error))
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)//errore di rete
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                }
            }
        )
    }

    fun aggiornaPassword(context: Context, email: String, password: String, callback: QueryReturnCallback)
    {
        val query = "update webmobile.utente set passw = '${password}' where email = '${email}';"

        ClientNetwork.retrofit.update(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    if (response.isSuccessful)//password aggiornata
                    {
                        callback.onReturnValue(context.resources.getString(R.string.query_succesfull), "Password aggiornata")
                        DBMSQuery().inserisciNotifica(context, NotificaModel(email, "Password aggiornata!", "La tua password è stata aggiornata come richiesto!"), object  : QueryReturnCallback
                        {
                            override fun onReturnValue(response: Any, message: String) {}
                            override fun onQueryFailed(fail: String) {}
                            override fun onQueryError(error: String) {}
                        })
                    }
                    else//errore di formattazione
                    {
                        callback.onQueryError(context.resources.getString(R.string.query_format_error))
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)//errore di rete
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                }
            }
        )
    }

    fun getUtente(context: Context, email: String, callback: QueryReturnCallback)
    {
        val query = "select * from webmobile.utente where email = '${email}';"
        Log.e("login", "getutente")
        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    if(response.isSuccessful)
                    {
                        if((response.body()?.get("queryset") as JsonArray).size() == 1)//utente torvato
                        {
                            val utenteJson = (response.body()?.get("queryset") as JsonArray).get(0).asJsonObject
                            getImage(context, utenteJson, object : QueryReturnCallback
                            {
                                override fun onReturnValue(response: Any, message: String)
                                {
                                    Log.e("login", "sii")
                                    utenteJson.remove("immagine")
                                    utenteJson.add("immagine", null)
                                    val utente = Gson().fromJson(utenteJson, UtenteModel::class.java)
                                    utente.immagine = response as Bitmap
                                    callback.onReturnValue(utente, context.resources.getString(R.string.query_succesfull))
                                }

                                override fun onQueryFailed(fail: String)
                                {
                                    callback.onQueryFailed(fail)
                                    Log.e("login", fail)
                                }
                                override fun onQueryError(error: String)
                                {
                                    callback.onQueryError(error)
                                    Log.e("login", error)
                                }
                            })
                        }
                        else//utente non trovato
                        {
                            callback.onQueryFailed(context.resources.getString(R.string.query_failed))
                            Log.e("login", "formatt")
                        }
                    }
                    else//errore di formattazione
                    {
                        callback.onQueryError(context.resources.getString(R.string.query_format_error))
                        Log.e("login", "bo")
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)//errore di rete
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                }

            }
        )
    }

    fun salvaModifiche(context: Context, utente: UtenteModel, callback: QueryReturnCallback)
    {
        val query = "update webmobile.utente set nome = '${utente.nome}', cognome = '${utente.cognome}', data_nascita = '${utente.data_nascita}', sesso = '${utente.sesso}', residenza = '${utente.residenza}', cellulare = '${utente.cellulare}', passw = '${utente.passw}' where email = '${utente.email}';"

        ClientNetwork.retrofit.update(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    callback.onReturnValue(context.resources.getString(R.string.query_succesfull), "Informazioni aggiornate")
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)
                {
                    callback.onQueryError("Errore di rete")
                }
            }
        )
    }

    private fun getImage(context: Context, jsonObject: JsonObject, callback: QueryReturnCallback)
    {
        val url: String = jsonObject.get("immagine").asString

        ClientNetwork.retrofit.getAvatar(url).enqueue(
            object : Callback<ResponseBody>
            {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>)
                {
                    if(response.isSuccessful)
                    {
                        if (response.body() != null)
                        {
                            val avatar = BitmapFactory.decodeStream(response.body()?.byteStream())
                            callback.onReturnValue(avatar, context.resources.getString(R.string.query_succesfull))
                        }
                        else
                        {
                            callback.onQueryFailed(context.resources.getString(R.string.query_failed))
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable)
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                }

            }
        )
    }

    fun getListaCitta(context: Context, callback: QueryReturnCallback)
    {
        val query = "select * from webmobile.citta;"

        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    val queryset = (response.body()?.get("queryset") as JsonArray)
                    if(queryset.size() > 0)//città trovate
                    {
                        Log.d("DBMSQuery", queryset.toString())

                        for(item in queryset)
                        {
                            getImage(context, item.asJsonObject, object : QueryReturnCallback
                            {
                                override fun onReturnValue(response: Any, message: String)
                                {
                                    item.asJsonObject.remove("immagine")
                                    item.asJsonObject.add("immagine", null)
                                    val citta = Gson().fromJson(item, CittaModel::class.java)
                                    citta.immagine = (response as Bitmap).scale(400, 200)
                                    callback.onReturnValue(citta, context.resources.getString(R.string.query_succesfull))
                                }

                                override fun onQueryFailed(fail: String)
                                {
                                    item.asJsonObject.remove("immagine")
                                    val image = BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_background)
                                    item.asJsonObject.add("immagine", null)
                                    val citta = Gson().fromJson(item.asString, CittaModel::class.java)
                                    citta.immagine = image.scale(400, 200)
                                    callback.onReturnValue(citta, context.resources.getString(R.string.query_succesfull))
                                }

                                override fun onQueryError(error: String)
                                {
                                    item.asJsonObject.remove("immagine")
                                    val image = BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_background)
                                    item.asJsonObject.add("immagine", null)
                                    val citta = Gson().fromJson(item.asString, CittaModel::class.java)
                                    citta.immagine = image.scale(400, 200)
                                    callback.onReturnValue(citta, context.resources.getString(R.string.query_succesfull))
                                }
                            })
                        }
                    }
                    else//nessuna città nel dbms
                    {
                        callback.onQueryFailed(context.resources.getString(R.string.query_failed))
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)//errore di rete
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                }
            }
        )
    }

    fun getAttrazioniCitta(context: Context, filtri: FilterWrapper, callback: QueryReturnCallback)
    {
        var query = "select A.id, A.nome, A.descrizione, A.indirizzo, A.num_telefono, A.tipo, A.rating, A.prezzo_medio, A.preferito, A.immagine from webmobile.attrazione A where A.ref_citta = ${filtri.cittaId}"

        when(filtri.tipoAttrazione)
        {
            "Ristorante" ->
            {
                query = "select A.id, A.nome, A.descrizione, A.indirizzo, A.num_telefono, A.tipo, A.rating, A.prezzo_medio, A.preferito, A.immagine from webmobile.attrazione A, webmobile.ristorante R where R.ref_attrazione = A.id and A.ref_citta = ${filtri.cittaId}"
            }

            "Hotel" ->
            {
                query += " and A.tipo = 'Hotel'"
            }

            "Attrazione" ->
            {
                query += " and A.tipo <> 'Hotel' and A.tipo <> 'Ristorante'"
            }
        }

        if (filtri.tipoCucina != "Nessun filtro")
        {
            query += " and R.tipo_cucina = '${filtri.tipoCucina}'"
        }

        when(filtri.rating)
        {
            "1 stella" ->
            {
                query += " and rating <= 1.0"
            }

            "2 stelle" ->
            {
                query += " and rating <= 2.0 and rating > 1.0"
            }

            "3 stelle" ->
            {
                query += " and rating <= 3.0 and rating > 2.0"
            }

            "4 stelle" ->
            {
                query += " and rating <= 4.0 and rating > 3.0"
            }

            "5 stelle" ->
            {
                query += " and rating <= 5.0 and rating > 4.0"
            }
        }

        when(filtri.prezzo_medio)
        {
            "fino a 5€" ->
            {
                query += " and prezzo_medio <= 5.00"
            }

            "fino a 10€" ->
            {
                query += " and prezzo_medio <= 10.00"

            }

            "fino a 20€" ->
            {
                query += " and prezzo_medio <= 20.00"
            }

            "fino a 50€" ->
            {
                query += " and prezzo_medio <= 50.00"
            }

            "fino a 100€" ->
            {
                query += " and prezzo_medio <= 100.00"

            }

            "fino a 200€" ->
            {
                query += " and prezzo_medio <= 200.00"

            }

            "fino a 500€" ->
            {
                query += " and prezzo_medio <= 500.00"

            }

            "fino a 1000€" ->
            {
                query += " and prezzo_medio <= 1000.00"

            }
        }

        query += ";"

        Log.d("Query", query)

        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    if(response.isSuccessful)
                    {
                        Log.d("Query", "Succesfull")
                        val queryset = response.body()?.get("queryset") as JsonArray

                        if(queryset.size() > 0)
                        {
                            Log.d("Query", queryset.size().toString())
                            for (item in queryset)
                            {
                                Log.d("Query", item.toString())
                                getImage(context, item.asJsonObject, object : QueryReturnCallback
                                {
                                    override fun onReturnValue(response: Any, message: String)
                                    {
                                        item.asJsonObject.remove("immagine")
                                        item.asJsonObject.add("immagine", null)
                                        var attrazione = Gson().fromJson(item, AttrazioneTuristicaModel::class.java)
                                        attrazione.immagine = (response as Bitmap)
                                        callback.onReturnValue(attrazione, context.resources.getString(R.string.query_succesfull))
                                    }

                                    override fun onQueryFailed(fail: String)
                                    {
                                        item.asJsonObject.remove("immagine")
                                        item.asJsonObject.add("immagine", null)
                                        var attrazione = Gson().fromJson(item, AttrazioneTuristicaModel::class.java)
                                        attrazione.immagine = BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_background)
                                        callback.onReturnValue(attrazione, context.resources.getString(R.string.query_succesfull))
                                    }

                                    override fun onQueryError(error: String)
                                    {
                                        item.asJsonObject.remove("immagine")
                                        item.asJsonObject.add("immagine", null)
                                        var attrazione = Gson().fromJson(item, AttrazioneTuristicaModel::class.java)
                                        attrazione.immagine = BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_background)
                                        callback.onReturnValue(attrazione, context.resources.getString(R.string.query_succesfull))
                                    }
                                })
                            }
                        }
                        else
                        {
                            callback.onQueryFailed("Nessun risultato")
                            Log.e("Query", "Nessun risultato")
                        }
                    }
                    else
                    {
                        callback.onQueryError("Errore di formattazione")
                        Log.e("Query", "Errore di formattazione")
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                    Log.e("Query", "Errore di rete")
                }
            }
        )
    }

    fun getCarte(context: Context, email: String, callback: QueryReturnCallback)
    {
        val query = "select numero_carta, data_scadenza, cvv, saldo from webmobile.carta where ref_utente='${email}';"

        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    if(response.isSuccessful)
                    {
                        val queryset = response.body()?.get("queryset") as JsonArray

                        if(queryset.size() > 0)
                        {
                            val list = mutableListOf<CreditCardModel>()

                            for(item in queryset)
                            {
                                val carta = Gson().fromJson(item, CreditCardModel::class.java)
                                list.add(carta)
                            }

                            callback.onReturnValue(list, context.resources.getString(R.string.query_succesfull))
                        }
                        else//non ci sono carte
                        {
                            callback.onReturnValue(mutableListOf<CreditCardModel>(), context.resources.getString(R.string.query_succesfull))
                        }
                    }
                    else//errore di formattazione
                    {
                        callback.onQueryFailed(context.resources.getString(R.string.query_failed))
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)
                {
                    callback.onQueryFailed(context.resources.getString(R.string.query_error))
                }
            }
        )
    }

    fun salvaCarta(context: Context, email: String, carta: CreditCardModel, callback: QueryReturnCallback)
    {
        val query = "insert into webmobile.carta values('${email}', '${carta.numero_carta}', '${carta.data_scadenza}', ${carta.cvv}, ${carta.saldo});"

        ClientNetwork.retrofit.insert(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    if(response.isSuccessful)
                    {
                        callback.onReturnValue(Any(), "Carta aggiunta con successo")
                        DBMSQuery().inserisciNotifica(context, NotificaModel(email, "Carta aggiunta!", "Complimenti! Hai aggiunto un nuovo metodo di pagamento"), object  : QueryReturnCallback
                        {
                            override fun onReturnValue(response: Any, message: String) {}
                            override fun onQueryFailed(fail: String) {}
                            override fun onQueryError(error: String) {}
                        })
                    }
                    else
                    {
                        callback.onQueryFailed("Carta non trovata")
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                }
            }
        )
    }

    fun eliminaCarta(context: Context, email: String, numero_carta: String, callback: QueryReturnCallback)
    {
        val query = "delete from webmobile.carta where ref_utente = '${email}' and numero_carta = '${numero_carta}';"

        ClientNetwork.retrofit.delete(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    if(response.isSuccessful)
                    {
                        callback.onReturnValue(Any(), "Carta eliminata")
                        DBMSQuery().inserisciNotifica(context, NotificaModel(email, "Carta eliminata", "La tua carta è stata eliminata come richiesto!"), object  : QueryReturnCallback
                        {
                            override fun onReturnValue(response: Any, message: String) {}
                            override fun onQueryFailed(fail: String) {}
                            override fun onQueryError(error: String) {}
                        })
                    }
                    else
                    {
                        callback.onQueryFailed("Carta non trovata")
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                }
            }
        )
    }

    fun getTransazioni(context: Context, email: String, callback: QueryReturnCallback)
    {
        //prendere le transazioni di un utente data la mail
        val query = "select id, carta, data_ora_pagamento, importo from webmobile.transazione where ref_utente = '${email}'"
        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    if(response.isSuccessful)
                    {
                        val queryset = response.body()?.get("queryset") as JsonArray

                        if(queryset.size() > 0)
                        {
                            val list = mutableListOf<TransazioniModel>()

                            for(item in queryset)
                            {
                                val transazione = Gson().fromJson(item, TransazioniModel::class.java)
                                list.add(transazione)
                            }
                            callback.onReturnValue(list, "Lista transazioni")
                        }
                    }
                    else
                    {
                        callback.onQueryFailed("Transazione non trovata")
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                }
            }
        )
    }

    fun getPrenotazioni(context: Context, email: String, callback: QueryReturnCallback)
    {
        val query = "select A.nome, T.importo, P.ref_transazione, P.data_ora_prenotazione, P.numero_prenotati, P.ref_utente, P.ref_attrazione, T.carta from webmobile.prenotazione P, webmobile.transazione T, webmobile.attrazione A where P.ref_attrazione = A.id and T.id = P.ref_transazione and P.ref_utente = '${email}';"

        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    if(response.isSuccessful)
                    {
                        val queryset = response.body()?.get("queryset") as JsonArray

                        if(queryset.size() > 0)
                        {
                            val list = mutableListOf<PrenotazioneModel>()

                            for(item in queryset)
                            {
                                val prenotazione = Gson().fromJson(item, PrenotazioneModel::class.java)
                                list.add(prenotazione)
                            }

                            callback.onReturnValue(list, "Lista prenotazioni")
                        }
                    }
                    else
                    {
                        callback.onQueryFailed(response.message())
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                }
            }
        )
    }

    fun annullaPrenotazione(context: Context, prenotazione: PrenotazioneModel, rimborso : BigDecimal, callback: QueryReturnCallback)
    {
        val query = "delete from webmobile.prenotazione where ref_transazione = ${prenotazione.ref_transazione};"

        ClientNetwork.retrofit.delete(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    if(response.isSuccessful)
                    {
                        //effettua rimborso, se la prenotazione è per oltre 30 giorni avanti
                        //cifra totale, altrimenti ridotta
                        if(prenotazione.importo != "0.00".toBigDecimal())
                        {
                            //Emettere rimborso
                            emettiRimborso(context, rimborso, prenotazione.ref_utente, prenotazione.carta, object : QueryReturnCallback
                            {
                                override fun onReturnValue(response: Any, message: String)
                                {
                                    callback.onReturnValue(Any(), "Prenotazione eliminata con successo\n${message}")
                                    DBMSQuery().inserisciNotifica(context, NotificaModel(prenotazione.ref_utente , "Cancellazione prenotazione", "La tua prenotazione è stata cancellata con successo"), object  : QueryReturnCallback
                                    {
                                        override fun onReturnValue(response: Any, message: String) {}
                                        override fun onQueryFailed(fail: String) {}
                                        override fun onQueryError(error: String) {}
                                    })
                                }

                                override fun onQueryFailed(fail: String) {}

                                override fun onQueryError(error: String)
                                {
                                    callback.onQueryError(error)
                                }
                            })
                        }
                        else
                        {
                            callback.onReturnValue(Any(), "Prenotazione eliminata con successo")
                        }
                    }
                    else
                    {
                        callback.onQueryFailed("Prenotazione non trovata")
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                }
            }
        )
    }

    fun emettiRimborso(context: Context, rimborso: BigDecimal, ref_utente : String, numero_carta: String, callback: QueryReturnCallback)
    {
        val query = "update webmobile.carta set saldo = saldo + ${rimborso} where ref_utente = '${ref_utente}' and numero_carta = '${numero_carta}';"
        ClientNetwork.retrofit.update(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    callback.onReturnValue(Any(), "Rimborso effettuato")
                    DBMSQuery().inserisciNotifica(context, NotificaModel(ref_utente, "Rimborso emesso!", "Il tuo rimborso è stato emesso come richiesto!"), object  : QueryReturnCallback
                    {
                        override fun onReturnValue(response: Any, message: String) {}
                        override fun onQueryFailed(fail: String) {}
                        override fun onQueryError(error: String) {}
                    })
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                }
            }
        )
    }

    fun getAttrazione(context: Context, attrazioneId: Int, callback: QueryReturnCallback)
    {
        val query = "select A.id, A.nome, A.descrizione, A.indirizzo, A.num_telefono, A.tipo, A.rating, A.prezzo_medio, A.preferito, A.immagine from webmobile.attrazione A where A.id = ${attrazioneId};"

        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    if(response.isSuccessful)
                    {
                        if((response.body()?.get("queryset") as JsonArray).size() == 1)
                        {
                            val item = (response.body()?.get("queryset") as JsonArray).get(0).asJsonObject
                            getImage(context, item, object : QueryReturnCallback
                            {
                                override fun onReturnValue(response: Any, message: String)
                                {
                                    item.remove("immagine")
                                    item.add("immagine", null)
                                    val attrazione = Gson().fromJson(item, AttrazioneTuristicaModel::class.java)
                                    attrazione.immagine = response as Bitmap
                                    callback.onReturnValue(attrazione, context.resources.getString(R.string.query_succesfull))
                                }

                                override fun onQueryFailed(fail: String)
                                {
                                    item.remove("immagine")
                                    item.add("immagine", null)
                                    val attrazione = Gson().fromJson(item, AttrazioneTuristicaModel::class.java)
                                    attrazione.immagine = BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_background)
                                    callback.onReturnValue(attrazione, context.resources.getString(R.string.query_failed))
                                }

                                override fun onQueryError(error: String)
                                {
                                    item.remove("immagine")
                                    item.add("immagine", null)
                                    val attrazione = Gson().fromJson(item, AttrazioneTuristicaModel::class.java)
                                    attrazione.immagine = BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_background)
                                    callback.onReturnValue(attrazione, context.resources.getString(R.string.query_failed))
                                }
                            })
                        }
                        else
                        {
                            callback.onQueryFailed("Attrazione non trovata")
                        }
                    }
                    else
                    {
                        callback.onQueryError("Errore di formattazione")
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                }
            }
        )
    }

    fun eseguiTransazione(context: Context, utenteId: String, numero_carta: String, importo: BigDecimal, callback: QueryReturnCallback)
    {
        var query = "select ref_utente, numero_carta, saldo from webmobile.carta where ref_utente = '${utenteId}' and numero_carta = '${numero_carta}';"

        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    if(response.isSuccessful)
                    {
                        val queryset1 = response.body()?.get("queryset") as JsonArray
                        if (queryset1.size() == 1)
                        {
                            if (queryset1.get(0).asJsonObject.get("saldo").asBigDecimal >= importo)
                            {
                                query = "update webmobile.carta set saldo = saldo - ${importo} where ref_utente = '${utenteId}' and numero_carta = '${numero_carta}';"

                                ClientNetwork.retrofit.update(query).enqueue(
                                    object : Callback<JsonObject>
                                    {
                                        override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                                        {
                                            if (response.isSuccessful)//saldo aggiornato
                                            {
                                                val dataOraTransazione = LocalDateTime.now(ZoneId.of("Europe/Rome")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")).replace(' ', 'T')
                                                inserisciTransazione(context, utenteId, numero_carta, importo, dataOraTransazione, object : QueryReturnCallback
                                                {
                                                    override fun onReturnValue(response: Any, message: String)//transazione trovata
                                                    {
                                                        callback.onReturnValue(response, message)
                                                    }

                                                    override fun onQueryFailed(fail: String)//transazione non trovata
                                                    {
                                                        callback.onQueryFailed(fail)
                                                    }

                                                    override fun onQueryError(error: String)//errore di rete
                                                    {
                                                        callback.onQueryError(error)
                                                    }
                                                })
                                            }
                                            else//errore di formattazione
                                            {
                                                callback.onQueryFailed(context.resources.getString(R.string.query_failed))
                                                Log.d("Query", "eseguiTransaFail1")
                                            }
                                        }

                                        override fun onFailure(call: Call<JsonObject>, t: Throwable)//errore di rete
                                        {
                                            callback.onQueryError(context.resources.getString(R.string.query_error))
                                        }
                                    }
                                )
                            }
                            else//saldo non disponibile
                            {
                                callback.onQueryFailed("Saldo non disponibile")
                            }
                        }
                        else//carta non trovata in caso di carte non registrate si assume che il saldo c'è
                        {
                            val dataOraTransazione = LocalDateTime.now(ZoneId.of("Europe/Rome")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")).replace(' ', 'T')

                            inserisciTransazione(context, utenteId, numero_carta, importo, dataOraTransazione, object : QueryReturnCallback
                            {
                                override fun onReturnValue(response: Any, message: String)//transazione trovata
                                {
                                    callback.onReturnValue(response, message)
                                }

                                override fun onQueryFailed(fail: String)//errore di formattazione
                                {
                                    callback.onQueryFailed(fail)
                                }

                                override fun onQueryError(error: String)//errore di rete
                                {
                                    callback.onQueryError(error)
                                }
                            })
                        }
                    }
                    else//errore di formattazione
                    {
                        callback.onQueryFailed(context.resources.getString(R.string.query_failed))
                        Log.d("Query", "eseguiTransaFail2")
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)//errore di rete
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                }

            }
        )
    }

    fun inserisciTransazione(context: Context, utenteId: String, numero_carta: String, importo: BigDecimal, dataOraTransazione: String, callback: QueryReturnCallback)
    {

        var query = "insert into webmobile.transazione(ref_utente, carta, data_ora_pagamento, importo) values ('${utenteId}', '${numero_carta}', '${dataOraTransazione}', ${importo});"

        ClientNetwork.retrofit.insert(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    if (response.isSuccessful)
                    {
                        query = "select id from webmobile.transazione where ref_utente = '${utenteId}' and carta = '${numero_carta}' and data_ora_pagamento = '${dataOraTransazione}' and importo = ${importo};"

                        ClientNetwork.retrofit.select(query).enqueue(
                            object : Callback<JsonObject>
                            {
                                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                                {
                                    if (response.isSuccessful)
                                    {
                                        val queryset = response.body()?.get("queryset") as JsonArray
                                        if (queryset.size() == 1)
                                        {
                                            callback.onReturnValue(queryset.get(0).asJsonObject.get("id").asInt, context.resources.getString(R.string.query_succesfull))
                                        }
                                        else
                                        {
                                            callback.onQueryFailed("Transazione non trovata")
                                        }
                                    }
                                    else
                                    {
                                        callback.onQueryFailed(context.resources.getString(R.string.query_failed))
                                        Log.d("Query", "selezioneTransaFail")
                                    }
                                }

                                override fun onFailure(call: Call<JsonObject>, t: Throwable)
                                {
                                    callback.onQueryError(context.resources.getString(R.string.query_error))
                                }
                            }
                        )
                    }
                    else
                    {
                        callback.onQueryFailed(context.resources.getString(R.string.query_failed))
                        Log.d("Query", "inserisciTransaFail ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                }
            }
        )
    }

    fun inserisciPrenotazione(context: Context, ref_utente: String, ref_attrazione: Int, ref_transazione: Int, data_ora_prenotazione: String, numero_prenotati: Int, callback: QueryReturnCallback)
    {
        val query = "insert into webmobile.prenotazione values(${ref_attrazione}, '${ref_utente}', ${ref_transazione}, '${data_ora_prenotazione}', ${numero_prenotati});"

        ClientNetwork.retrofit.insert(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    if (response.isSuccessful)
                    {
                        callback.onReturnValue(Any(), "Prenotazione effettuata con successo")
                        Log.d("Query", "inserisciOk")
                        DBMSQuery().inserisciNotifica(context, NotificaModel(ref_utente, "Prenotazione effettuata", "La prenotazione è stata confermata!"), object  : QueryReturnCallback
                        {
                            override fun onReturnValue(response: Any, message: String) {}
                            override fun onQueryFailed(fail: String) {}
                            override fun onQueryError(error: String) {}
                        })
                    }
                    else
                    {
                        callback.onQueryFailed(context.resources.getString(R.string.query_failed))
                        Log.d("Query", "inserisciPrenFail")
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                    Log.d("Query", "inserisciError")
                }

            }
        )
    }

    fun prendiNotifiche(context: Context, utenteId: String, callback: QueryReturnCallback)
    {
        val query = "select N.ref_utente, N.titolo, N.descrizione from notifica N where N.ref_utente = '${utenteId}';"

        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    if (response.isSuccessful)
                    {
                        val queryset = response.body()?.get("queryset") as JsonArray
                        if (queryset.size() != 0)
                        {
                            //metti la notifica all'interno della recycler
                            var listaNotifiche = mutableListOf<NotificaModel>()
                            for(notifica in queryset){
                                listaNotifiche.add(Gson().fromJson(notifica.asJsonObject, NotificaModel::class.java))
                            }
                            callback.onReturnValue(listaNotifiche, "OK")
                        }
                        else
                        {
                            callback.onQueryFailed("Nessuna notifica trovata")
                        }
                    }
                    else
                    {
                        callback.onQueryFailed(context.resources.getString(R.string.query_failed))
                        Log.d("Query", "inserisciPrenFail")
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                    Log.d("Query", "inserisciError")
                }
            })
    }

    fun cancellaNotifica(context: Context, notificaModel: NotificaModel, callback: QueryReturnCallback)
    {
        val query = "delete from webmobile.notifica where ref_utente = '${notificaModel.ref_utente}' and titolo = '${notificaModel.titolo}' and descrizione = '${notificaModel.descrizione}';"

        ClientNetwork.retrofit.delete(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    if(response.isSuccessful)
                    {
                        callback.onReturnValue(Any(), "Eliminata")
                    }
                    else
                    {
                        callback.onQueryFailed("Notifica non trovata")
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                }
            }
        )
    }

    fun inserisciNotifica(context: Context, notificaModel: NotificaModel, callback: QueryReturnCallback)
    {
        val query = "insert into webmobile.notifica values('${notificaModel.ref_utente}', '${notificaModel.titolo}', '${notificaModel.descrizione}');"

        ClientNetwork.retrofit.insert(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    if (response.isSuccessful)
                    {
                        callback.onReturnValue(Any(), "Notifica inserita con successo")
                        Log.d("Query", "inserisciOk")
                    }
                    else
                    {
                        callback.onQueryFailed(context.resources.getString(R.string.query_failed))
                        Log.d("Query", "inserisciNotificaFail")
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                    Log.d("Query", "inserisciError")
                }
            }
        )
    }

    fun aggiungiRecensione(context: Context, attrazioneId: Int, utenteId: String, titolo: String, descrizione: String, rating: BigDecimal, callback: QueryReturnCallback)
    {
        val query = "insert into webmobile.recensione values('${titolo}', '${descrizione}', ${rating}, '${utenteId}', ${attrazioneId});"

        ClientNetwork.retrofit.insert(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    if (response.isSuccessful)
                    {
                        callback.onReturnValue(Any(), "Recensione inserita")
                        aggiornaRating(context, attrazioneId, rating, object : QueryReturnCallback
                        {
                            override fun onReturnValue(response: Any, message: String) {
                                Log.d("franco", "${(response as BigDecimal)} ${message}")
                            }

                            override fun onQueryFailed(fail: String) {
                                Log.d("franco", "Formatt")
                            }

                            override fun onQueryError(error: String) {
                                Log.d("franco", "Err")
                            }
                        })
                        DBMSQuery().inserisciNotifica(context, NotificaModel(utenteId, "Recensione inserita", "Complimenti! Hai inserito una recensione con successo!"), object  : QueryReturnCallback
                        {
                            override fun onReturnValue(response: Any, message: String) {}
                            override fun onQueryFailed(fail: String) {}
                            override fun onQueryError(error: String) {}
                        })
                    }
                    else
                    {
                        callback.onQueryFailed("Recensione già inserita")
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                }
            }
        )
    }

    fun getRecensioni(context: Context, attrazioneId: Int, callback: QueryReturnCallback)
    {
        val query = "select U.nome, R.titolo, R.descrizione, R.rating from webmobile.recensione R, webmobile.utente U where R.ref_attrazione = ${attrazioneId} and R.ref_utente = U.email;"

        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    if (response.isSuccessful)
                    {
                        val queryset = response.body()?.get("queryset") as JsonArray
                        var list = mutableListOf<RecensioneModel>()
                        if (queryset.size() > 0)
                        {
                            for (item in queryset)
                            {
                                list.add(Gson().fromJson(item.asJsonObject, RecensioneModel::class.java))
                            }
                        }

                        callback.onReturnValue(list, context.resources.getString(R.string.query_succesfull))
                    }
                    else
                    {
                        callback.onQueryFailed(context.resources.getString(R.string.query_failed))
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                }
            }
        )
    }

    fun getPietanze(context: Context, attrazioneId: Int, callback: QueryReturnCallback)
    {
        val query = "select nome, ingredienti, prezzo, immagine from webmobile.pietanza where ref_ristorante = ${attrazioneId};"
        //query pietanze
        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    if (response.isSuccessful)
                    {
                        val queryset = response.body()?.get("queryset") as JsonArray

                        Log.d("Query", queryset.toString())
                        if (queryset.size() > 0)
                        {
                            var list = mutableListOf<PietanzaModel>()
                            Log.d("Query", queryset.toString())
                            for (item in queryset)
                            {
                                getImage(context, item.asJsonObject, object : QueryReturnCallback
                                {
                                    override fun onReturnValue(response: Any, message: String)
                                    {
                                        item.asJsonObject.remove("immagine")
                                        item.asJsonObject.add("immagine", null)
                                        val pietanza = Gson().fromJson(item.asJsonObject, PietanzaModel::class.java)
                                        pietanza.immagine = response as Bitmap
                                        list.add(pietanza)
                                        Log.d("Query", list.toString())
                                        callback.onReturnValue(list, context.resources.getString(R.string.query_succesfull))
                                    }

                                    override fun onQueryFailed(fail: String)
                                    {
                                        item.asJsonObject.remove("immagine")
                                        item.asJsonObject.add("immagine", null)
                                        val pietanza = Gson().fromJson(item.asJsonObject, PietanzaModel::class.java)
                                        pietanza.immagine = BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_background)
                                        Log.d("Query", list.toString())
                                        list.add(pietanza)
                                        callback.onReturnValue(list, context.resources.getString(R.string.query_succesfull))
                                    }

                                    override fun onQueryError(error: String)
                                    {
                                        item.asJsonObject.remove("immagine")
                                        item.asJsonObject.add("immagine", null)
                                        val pietanza = Gson().fromJson(item.asJsonObject, PietanzaModel::class.java)
                                        pietanza.immagine = BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_background)
                                        Log.d("Query", list.toString())
                                        list.add(pietanza)
                                        callback.onReturnValue(list, context.resources.getString(R.string.query_succesfull))
                                    }
                                })
                            }
                        }

                    }
                    else
                    {
                        callback.onQueryFailed("Nessun piatto")
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                }

            }
        )
    }

    fun getCamere(context: Context, attrazioneId: Int, callback: QueryReturnCallback)
    {
        val query = "select * from webmobile.camera where ref_hotel = ${attrazioneId};"
        //query camere
        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    if (response.isSuccessful)
                    {
                        val queryset = response.body()?.get("queryset") as JsonArray
                        if (queryset.size() > 0)
                        {
                            val list = mutableListOf<CameraModel>()
                            for (item in queryset)
                            {
                                DBMSQuery().getImage(context, item.asJsonObject, object : QueryReturnCallback
                                {
                                    override fun onReturnValue(response: Any, message: String)
                                    {
                                        item.asJsonObject.remove("immagine")
                                        item.asJsonObject.add("immagine", null)
                                        val camera = Gson().fromJson(item.asJsonObject, CameraModel::class.java)
                                        camera.immagine = response as Bitmap
                                        list.add(camera)
                                        Log.d("Query", list.toString())
                                        callback.onReturnValue(list, context.resources.getString(R.string.query_succesfull))
                                    }

                                    override fun onQueryFailed(fail: String)
                                    {
                                        item.asJsonObject.remove("immagine")
                                        item.asJsonObject.add("immagine", null)
                                        val camera = Gson().fromJson(item.asJsonObject, CameraModel::class.java)
                                        camera.immagine =  BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_background)
                                        list.add(camera)
                                        Log.d("Query", list.toString())
                                        callback.onReturnValue(list, context.resources.getString(R.string.query_succesfull))
                                    }

                                    override fun onQueryError(error: String)
                                    {
                                        item.asJsonObject.remove("immagine")
                                        item.asJsonObject.add("immagine", null)
                                        val camera = Gson().fromJson(item.asJsonObject, CameraModel::class.java)
                                        camera.immagine =  BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_background)
                                        list.add(camera)
                                        Log.d("Query", list.toString())
                                        callback.onReturnValue(list, context.resources.getString(R.string.query_succesfull))
                                    }
                                })
                            }
                        }
                        else
                        {
                            callback.onQueryFailed("Camere non trovate")
                        }
                    }
                    else
                    {
                        callback.onQueryFailed(context.resources.getString(R.string.query_failed))
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                }

            }
        )
    }

    fun getRistorante(context: Context, ristoranteId: Int, callback: QueryReturnCallback)
    {
        val query = "select numero_coperti, tipo_cucina, orario_apertura, orario_chiusura from webmobile.ristorante where ref_attrazione = ${ristoranteId};"

        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    if (response.isSuccessful)
                    {
                        val queryset = response.body()?.get("queryset") as JsonArray

                        if (queryset.size() == 1)
                        {
                            val ristorante = queryset.get(0).asJsonObject
                            callback.onReturnValue(Gson().fromJson(ristorante, RistoranteModel::class.java), context.resources.getString(R.string.query_succesfull))
                        }
                        else
                        {
                            callback.onQueryFailed("Nessun ristorante trovato")
                        }
                    }
                    else
                    {
                        callback.onQueryFailed(context.resources.getString(R.string.query_failed))
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                }
            }
        )
    }

    fun aggiornaRating(context: Context, ristoranteId: Int, rating: BigDecimal, callback: QueryReturnCallback)
    {
        val query = "select rating from webmobile.attrazione A where A.id = ${ristoranteId};"
        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    if (response.isSuccessful)
                    {
                        val queryset = response.body()?.get("queryset") as JsonArray

                        if (queryset.size() == 1)
                        {
                            var rat = queryset.get(0).asJsonObject.get("rating").asBigDecimal
                            val query_rating = if(rat == "0.0".toBigDecimal())
                            {
                                "update webmobile.attrazione A set A.rating = ${rating} where A.id = ${ristoranteId};"
                            }
                            else
                            {
                                val media = (rat+rating)/("2.0".toBigDecimal())
                                "update webmobile.attrazione A set A.rating = ${media} where A.id = ${ristoranteId};"
                            }

                            ClientNetwork.retrofit.update(query_rating).enqueue(
                                object : Callback<JsonObject>
                                {
                                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {}

                                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {}
                                }
                            )
                        }
                        else
                        {
                            callback.onQueryFailed("Nessun ristorante trovato")
                        }
                    }
                    else
                    {
                        callback.onQueryFailed(context.resources.getString(R.string.query_failed))
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                }
            }
        )
    }

    fun aggiungiPreferiti(context: Context, utenteId: String, attrazioneId: Int, callback: QueryReturnCallback)
    {
        val query = "insert into webmobile.preferiti values('${utenteId}', ${attrazioneId});"

        ClientNetwork.retrofit.insert(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    if (response.isSuccessful)
                    {
                            //inserisci un nuovo preferito
                            callback.onReturnValue(true, "OK")
                    }
                    else
                    {
                        callback.onQueryFailed(context.resources.getString(R.string.query_failed))
                        Log.d("Query", "inserisciPrenFail")
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                    Log.d("Query", "inserisciError")
                }
            })
    }

    fun rimuoviPreferiti(context: Context, utenteId: String, attrazioneId: Int, callback: QueryReturnCallback)
    {
        val query = "delete from webmobile.preferiti where ref_utente = '${utenteId}' and ref_attrazione = ${attrazioneId};"

        ClientNetwork.retrofit.delete(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    if (response.isSuccessful)
                    {
                        //elimina l'attrazione dai preferiti
                        callback.onReturnValue(true, "OK")
                    }
                    else
                    {
                        callback.onQueryFailed(context.resources.getString(R.string.query_failed))
                        Log.d("Query", "inserisciPrenFail")
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                    Log.d("Query", "eliminaError")
                }
            })
    }

    fun prendiPreferiti(context: Context, utenteId: String, callback: QueryReturnCallback)
    {
        val query = "select ref_attrazione from webmobile.preferiti where ref_utente = '${utenteId}';"
        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    if (response.isSuccessful)
                    {
                        val queryset = response.body()?.get("queryset") as JsonArray

                        if (queryset.size() > 0)
                        {
                            var lista = mutableListOf<Int>()
                            for(i in queryset){
                                lista.add(i.asJsonObject.get("ref_attrazione").asInt)
                            }
                            callback.onReturnValue(lista, "OPPLA'")
                        }
                        else
                        {
                            callback.onQueryFailed("Nessun preferito trovato")
                        }
                    }
                    else
                    {
                        callback.onQueryFailed(context.resources.getString(R.string.query_failed))
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                }
            }
        )
    }

    fun getAttrazioniPreferite(context: Context, utenteId: String, callback: QueryReturnCallback)
    {
        val query = "select id, nome, descrizione, indirizzo, num_telefono, tipo, rating, prezzo_medio, preferito, immagine from webmobile.attrazione A, webmobile.preferiti P where A.id = P.ref_attrazione and P.ref_utente = '${utenteId}';"

        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
                {
                    if (response.isSuccessful)
                    {
                        val queryset = response.body()?.get("queryset") as JsonArray

                        for (item in queryset)
                        {
                            DBMSQuery().getImage(context, item.asJsonObject, object : QueryReturnCallback
                            {
                                override fun onReturnValue(response: Any, message: String)
                                {
                                    item.asJsonObject.remove("immagine")
                                    item.asJsonObject.add("immagine", null)
                                    val attrazione = Gson().fromJson(item.asJsonObject, AttrazioneTuristicaModel::class.java)
                                    attrazione.immagine = response as Bitmap
                                    callback.onReturnValue(attrazione, context.resources.getString(R.string.query_succesfull))
                                }

                                override fun onQueryFailed(fail: String)
                                {
                                    item.asJsonObject.remove("immagine")
                                    item.asJsonObject.add("immagine", null)
                                    val attrazione = Gson().fromJson(item.asJsonObject, AttrazioneTuristicaModel::class.java)
                                    attrazione.immagine = BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_background)
                                    callback.onReturnValue(attrazione, context.resources.getString(R.string.query_failed))
                                }

                                override fun onQueryError(error: String)
                                {
                                    item.asJsonObject.remove("immagine")
                                    item.asJsonObject.add("immagine", null)
                                    val attrazione = Gson().fromJson(item.asJsonObject, AttrazioneTuristicaModel::class.java)
                                    attrazione.immagine = BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_background)
                                    callback.onReturnValue(attrazione, context.resources.getString(R.string.query_error))
                                }
                            })
                        }
                    }
                    else
                    {
                        callback.onQueryFailed(context.resources.getString(R.string.query_failed))
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable)
                {
                    callback.onQueryError(context.resources.getString(R.string.query_error))
                }
            }
        )
    }
}