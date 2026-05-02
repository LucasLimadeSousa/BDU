package com.example.bdu.suporte

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bdu.R
import com.google.android.material.button.MaterialButton

class TermosCondicoesActivity : AppCompatActivity () {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.suporte_termos_condicoes)

        findViewById<MaterialButton>(R.id.btnFinalizar2).setOnClickListener {
            finish()
        }
    }
}