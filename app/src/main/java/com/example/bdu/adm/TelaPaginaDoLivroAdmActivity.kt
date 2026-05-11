package com.example.bdu.adm

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.bdu.R
import com.example.bdu.livros.TelahomeActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.core.graphics.toColorInt

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

        btnExcluir?.setOnClickListener {
            val bookTitle = tvBookTitle?.text?.toString() ?: "LIVRO"

            val builder = MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
            builder.setMessage("Tem certeza que deseja excluir o livro ($bookTitle) do catálogo?")

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
    }
}
