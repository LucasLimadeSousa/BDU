package com.example.bdu.adm

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bdu.R
import com.example.bdu.livros.TelahomeActivity
import com.google.android.material.button.MaterialButton

class TelaPaginaDoLivroAdmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. PRIMEIRO define o layout da tela
        setContentView(R.layout.adm_tela_pagina_do_livro_adm)

        // 2. DEPOIS encontra os componentes (Botão Voltar e Botão Editar)
        val btnBack = findViewById<ImageButton>(R.id.btnBackLivro)
        val btnEditar = findViewById<MaterialButton>(R.id.btnEditarLivro)

        // Configuração do clique para voltar
        btnBack?.setOnClickListener {
            onBackPressed()
        }
        btnBack?.setOnClickListener {
            val intent = Intent(this, TelahomeActivity::class.java)
            startActivity(intent)
        }
        btnEditar?.setOnClickListener {
            val intent = Intent(this, EditarLivroAdmActivity::class.java)
            startActivity(intent)
        }
    }
}
