package com.sparring.sports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class ResistenciaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_resistencia, container, false)

        val messageTextView: TextView = view.findViewById(R.id.messageResistenciaTextView)
        messageTextView.text = "Fragment Resistência"

        // Lógica para voltar à MainActivity (substituir com a lógica desejada)
        view.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
        }

        return view
    }
}
