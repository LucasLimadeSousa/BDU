package com.example.bdu.livros

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bdu.R
import com.example.bdu.usuario.MeuPerfilActivity

class ListaDesejosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.livros_lista_desejos)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener{
            startActivity(Intent(this, R.layout.livros_telahome::class.java))
        }

        findViewById<ImageButton>(R.id.imgBtnLivro).setOnClickListener{
            startActivity(Intent(this, R.layout.livros_pagina_do_livro::class.java))
        }


    }
}
