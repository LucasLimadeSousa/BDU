package com.example.bdu.adm

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bdu.R

import android.content.Intent
import android.graphics.Color
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.bdu.livros.PaginaDoLivroActivity
import com.example.bdu.livros.TelahomeActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.core.graphics.toColorInt

class AdicionarLivroAdmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.adm_adicionar_livro_adm)

        val btnBack = findViewById<View>(R.id.btnBackLivro)
        btnBack?.setOnClickListener {
            val intent = Intent(this, TelahomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        val mainLayout = findViewById<View>(R.id.main)
        if (mainLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        val etTitle = findViewById<EditText>(R.id.etTitle)
        val btnAddBook = findViewById<MaterialButton>(R.id.btnAddBook)

        btnAddBook.setOnClickListener {
            val bookTitle = etTitle.text.toString().ifEmpty { "LIVRO" }
            
            val builder = MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
            builder.setMessage("Tem certeza que quer adicionar o livro ($bookTitle) ao catálogo?")

            builder.setPositiveButton("Sim") { _, _ ->
                Toast.makeText(this, "Livro adicionado com sucesso.", Toast.LENGTH_SHORT).show()
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

            // Estilização manual dos botões
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