package com.example.bdu.adm

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.bdu.R
import com.example.bdu.livros.TelahomeActivity
import com.example.bdu.livros.ListadeEsperaActivity
import com.example.bdu.livros.MeusLivrosActivity
import com.example.bdu.livros.ListaDesejosActivity
import com.example.bdu.usuario.MeuPerfilActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.core.graphics.toColorInt
import coil.load

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

        val btnExcluir = findViewById<MaterialButton>(R.id.btnExcluir)
        val tvBookTitle = findViewById<TextView>(R.id.tvBookTitleMain)
        val ivBookCover = findViewById<ImageView>(R.id.ivBookCover)
        val tvBookAuthor = findViewById<TextView>(R.id.tvBookAuthor)
        val tvBookGenre = findViewById<TextView>(R.id.tvBookGenre)
        val tvBookPublication = findViewById<TextView>(R.id.tvBookPublication)
        val tvBookIsbn = findViewById<TextView>(R.id.tvBookIsbn)
        val tvBookPublisher = findViewById<TextView>(R.id.tvBookPublisher)
        val tvBookPages = findViewById<TextView>(R.id.tvBookPages)
        val tvBookSynopsis = findViewById<TextView>(R.id.tvBookSynopsis)

        val bookTitle = intent.getStringExtra("BOOK_TITLE")
        val bookAuthor = intent.getStringExtra("BOOK_AUTHOR")
        val bookGenre = intent.getStringExtra("BOOK_GENRE")
        val bookPublication = intent.getStringExtra("BOOK_PUBLICATION")
        val bookIsbn = intent.getStringExtra("BOOK_ISBN")
        val bookPublisher = intent.getStringExtra("BOOK_PUBLISHER")
        val bookPages = intent.getStringExtra("BOOK_PAGES")
        val bookSynopsis = intent.getStringExtra("BOOK_SYNOPSIS")
        val bookImage = intent.getStringExtra("BOOK_IMAGE")

        tvBookTitle.text = bookTitle ?: "Título Indisponível"
        tvBookAuthor.text = "Autor: ${bookAuthor ?: "Desconhecido"}"
        tvBookGenre.text = "Gênero: ${bookGenre ?: "Não informado"}"
        tvBookPublication.text = "Publicação: ${bookPublication ?: "N/A"}"
        tvBookIsbn.text = "ISBN: ${bookIsbn ?: "N/A"}"
        tvBookPublisher.text = "Editora: ${bookPublisher ?: "N/A"}"
        tvBookPages.text = "Páginas: ${bookPages ?: "N/A"}"
        tvBookSynopsis.text = bookSynopsis ?: "Sinopse não disponível."

        ivBookCover.load(bookImage) {
            placeholder(R.drawable.ic_launcher_background)
            error(R.drawable.ic_launcher_background)
        }

        btnExcluir?.setOnClickListener {
            val titleForExcluir = tvBookTitle?.text?.toString() ?: "LIVRO"

            val builder = MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
            builder.setMessage("Tem certeza que deseja excluir o livro ($titleForExcluir) do catálogo?")

            builder.setPositiveButton("Sim") { _, _ ->
                Toast.makeText(this, "Livro removido com sucesso.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, TelahomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()

            // Estilização dos botões
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.let {
                it.setBackgroundColor("#2E7D32".toColorInt()) // Verde
                it.setTextColor(Color.WHITE)
            }

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.let {
                it.setBackgroundColor("#D32F2F".toColorInt()) // Vermelho
                it.setTextColor(Color.WHITE)
            }
        }

        // Lógica do botão "Lista de Desejos" (Coração: Branco <-> Vermelho) com Persistência
        val btnWishlist = findViewById<MaterialButton>(R.id.btnWishlist)
        val prefs = getSharedPreferences("favoritos_prefs", MODE_PRIVATE)
        val bookKey = "fav_$bookTitle"

        var isFavorito = prefs.getBoolean(bookKey, false)

        // Estado inicial baseado no que foi salvo
        if (isFavorito) {
            btnWishlist.iconTint = ColorStateList.valueOf(Color.RED)
        } else {
            btnWishlist.iconTint = ColorStateList.valueOf(Color.WHITE)
        }

        btnWishlist?.setOnClickListener {
            isFavorito = !isFavorito
            
            // Salva o novo estado no sistema
            prefs.edit().putBoolean(bookKey, isFavorito).apply()

            if (isFavorito) {
                btnWishlist.iconTint = ColorStateList.valueOf(Color.RED)
                Toast.makeText(this, "Adicionado aos favoritos", Toast.LENGTH_SHORT).show()
            } else {
                btnWishlist.iconTint = ColorStateList.valueOf(Color.WHITE)
                Toast.makeText(this, "Removido dos favoritos", Toast.LENGTH_SHORT).show()
            }
        }

        // Configuração da Barra de Navegação Inferior
        findViewById<ImageButton>(R.id.btn_nav_fila)?.setOnClickListener {
            startActivity(Intent(this, ListadeEsperaActivity::class.java))
        }

        findViewById<ImageButton>(R.id.btn_nav_meuslivros)?.setOnClickListener {
            startActivity(Intent(this, MeusLivrosActivity::class.java))
        }

        findViewById<ImageButton>(R.id.btn_nav_home)?.setOnClickListener {
            startActivity(Intent(this, TelahomeActivity::class.java))
        }

        findViewById<ImageButton>(R.id.btn_nav_desejos)?.setOnClickListener {
            startActivity(Intent(this, ListaDesejosActivity::class.java))
        }

        findViewById<ImageButton>(R.id.btn_nav_perfil)?.setOnClickListener {
            startActivity(Intent(this, MeuPerfilActivity::class.java))
        }
    }
}
