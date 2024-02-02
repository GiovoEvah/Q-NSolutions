package com.example.qnsolutions.activity.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qnsolutions.adapters.PietanzaAdapter
import com.example.qnsolutions.databinding.FragmenRistoranteMenuBinding
import com.example.qnsolutions.models.PietanzaModel
import com.example.qnsolutions.util.DBMSQuery
import com.example.qnsolutions.util.QueryReturnCallback

class RistoranteMenuFragment(val attrazioneid: Int) : DialogFragment()
{
    lateinit var binding: FragmenRistoranteMenuBinding
    private var list = mutableListOf<PietanzaModel>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmenRistoranteMenuBinding.inflate(inflater)
        binding.pietanzeRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.pietanzeRecycler.adapter = PietanzaAdapter(list)
        //query pietanze
        DBMSQuery().getPietanze(requireContext(), attrazioneid, object : QueryReturnCallback
        {
            override fun onReturnValue(response: Any, message: String)
            {
                (binding.pietanzeRecycler.adapter as PietanzaAdapter).list = response as MutableList<PietanzaModel>
                (binding.pietanzeRecycler.adapter as PietanzaAdapter).notifyDataSetChanged()
            }

            override fun onQueryFailed(fail: String)
            {
                Toast.makeText(requireContext(), fail, Toast.LENGTH_SHORT).show()
            }

            override fun onQueryError(error: String)
            {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        })

        dialog?.setCanceledOnTouchOutside(true)

        return binding.root
    }
}