package com.example.bdu.livros

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bdu.R

class ListaDesejosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.livros_lista_desejos)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener{
            startActivity(Intent(this, R.layout.pagamentos_selecionar_metodo::class.java))
        }

        findViewById<ImageButton>(R.id.imgBtnLivro).setOnClickListener{
            startActivity(Intent(this, R.layout.livros_pagina_do_livro::class.java))
        }


        }
    }
