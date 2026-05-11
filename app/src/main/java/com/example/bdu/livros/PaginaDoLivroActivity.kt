package com.example.bdu.livros

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.bdu.R
import com.example.bdu.adm.EditarLivroAdmActivity
import com.example.bdu.adm.TelaPaginaDoLivroAdmActivity
import com.example.bdu.usuario.MeuPerfilActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.core.graphics.toColorInt

class PaginaDoLivroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.livros_pagina_do_livro)

        val btnBotaoRetornar = findViewById<ImageButton>(R.id.btnBackLivro)

        val btnFila = findViewById<ImageButton?>(R.id.btn_nav_fila)
        val btnMeusLivros = findViewById<ImageButton?>(R.id.btn_nav_meuslivros)
        val btnHome = findViewById<ImageButton?>(R.id.btn_nav_home)
        val btnDesejos = findViewById<ImageButton?>(R.id.btn_nav_desejos)
        val btnperfil = findViewById<ImageButton?>(R.id.btn_nav_perfil)

        val btnADM = findViewById<Button>(R.id.btnADM)

        btnBotaoRetornar?.setOnClickListener {
            onBackPressed()
        }
        btnFila?.setOnClickListener {
            val intent = Intent(this, ListadeEsperaActivity::class.java)
            startActivity(intent)
        }
        btnMeusLivros?.setOnClickListener {
            val intent = Intent(this, MeusLivrosActivity::class.java)
            startActivity(intent)
        }

        btnHome?.setOnClickListener {
            val intent = Intent(this, TelahomeActivity::class.java)
            startActivity(intent)
        }

        btnDesejos?.setOnClickListener {
            val intent = Intent(this, ListaDesejosActivity::class.java)
            startActivity(intent)
        }

        btnperfil?.setOnClickListener {
            val intent = Intent(this, MeuPerfilActivity::class.java)
            startActivity(intent)
        }
        btnADM?.setOnClickListener {
            val intent = Intent(this, TelaPaginaDoLivroAdmActivity::class.java)
            startActivity(intent)
        }

        val btnAlugar = findViewById<MaterialButton>(R.id.btnAlugar)
        val tvBookTitle = findViewById<TextView>(R.id.tvBookTitleMain)

        val isEsgotado = intent.getBooleanExtra("IS_ESGOTADO", false)
        val bookTitleFromIntent = intent.getStringExtra("BOOK_TITLE")
        
        if (bookTitleFromIntent != null) {
            tvBookTitle.text = bookTitleFromIntent
        }

        btnAlugar?.setOnClickListener {
            val bookTitle = tvBookTitle?.text?.toString() ?: "O Livro"

            if (isEsgotado) {
                // LÓGICA PARA LIVRO ESGOTADO (FOTO 3)
                val builder = MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
                builder.setMessage("Este livro está esgotado. Gostaria de colocá-lo em sua lista de espera?")

                builder.setPositiveButton("Sim") { _, _ ->
                    // Segundo Pop Up com link clicável
                    val infoBuilder = MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
                    
                    val message = "Livro reservado com sucesso.\nVisualize sua lista de reservas."
                    val spannableString = SpannableString(message)
                    
                    val clickableSpan = object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            val intentList = Intent(this@PaginaDoLivroActivity, ListadeEsperaActivity::class.java)
                            startActivity(intentList)
                        }
                    }

                    val linkStart = message.indexOf("Visualize sua lista de reservas.")
                    val linkEnd = linkStart + "Visualize sua lista de reservas.".length
                    
                    spannableString.setSpan(clickableSpan, linkStart, linkEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableString.setSpan(ForegroundColorSpan(Color.BLUE), linkStart, linkEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableString.setSpan(UnderlineSpan(), linkStart, linkEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                    infoBuilder.setMessage(spannableString)
                    infoBuilder.setPositiveButton("Ok") { dialog, _ ->
                        dialog.dismiss()
                    }
                    
                    val infoDialog = infoBuilder.create()
                    infoDialog.show()
                    
                    // IMPORTANTE: Necessário para o link funcionar
                    infoDialog.findViewById<TextView>(android.R.id.message)?.movementMethod = LinkMovementMethod.getInstance()
                    infoDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.BLACK)
                }

                builder.setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }

                val dialog = builder.create()
                dialog.show()

                dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.let {
                    it.setBackgroundColor("#2E7D32".toColorInt())
                    it.setTextColor(Color.WHITE)
                }
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.let {
                    it.setBackgroundColor("#D32F2F".toColorInt())
                    it.setTextColor(Color.WHITE)
                }

            } else {
                // LÓGICA NORMAL PARA LIVRO DISPONÍVEL
                val builder = MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
                builder.setMessage("Você tem certeza que quer reservar o livro: $bookTitle ?")

                builder.setPositiveButton("Sim") { _, _ ->
                    val infoBuilder = MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
                    infoBuilder.setMessage("O livro está reservado para (Seu nome).\nBusque na Biblioteca Unifor em até (Dias) apresentando documento com foto.")
                    infoBuilder.setPositiveButton("OK") { _, _ ->
                        val intentHome = Intent(this, TelahomeActivity::class.java)
                        intentHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intentHome)
                        finish()
                    }
                    val infoDialog = infoBuilder.create()
                    infoDialog.show()
                    infoDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.BLACK)
                }

                builder.setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }

                val dialog = builder.create()
                dialog.show()

                dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.let {
                    it.setBackgroundColor("#2E7D32".toColorInt())
                    it.setTextColor(Color.WHITE)
                }
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.let {
                    it.setBackgroundColor("#D32F2F".toColorInt())
                    it.setTextColor(Color.WHITE)
                }
            }
        }
    }
}