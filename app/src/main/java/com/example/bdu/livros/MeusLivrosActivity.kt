package com.example.bdu.livros

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bdu.R
import com.example.bdu.usuario.MeuPerfilActivity
import com.google.android.material.button.MaterialButton

class MeusLivrosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.livros_meuslivros)

        val btnReport = findViewById<LinearLayout?>(R.id.btn_report)
        val btnVerTudo = findViewById<TextView?>(R.id.VerTudo)
        val btnAddDesejos = findViewById<MaterialButton?>(R.id.btn_desejos)

        val btnFila = findViewById<ImageButton?>(R.id.btn_nav_fila)
        val btnMeusLivros = findViewById<ImageButton?>(R.id.btn_nav_meuslivros)
        val btnHome = findViewById<ImageButton?>(R.id.btn_nav_home)
        val btnDesejosNav = findViewById<ImageButton?>(R.id.btn_nav_desejos)
        val btnPerfil = findViewById<ImageButton?>(R.id.btn_nav_perfil)

        val mainView = findViewById<View>(R.id.main)
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        // Lógica do botão "Lista de Desejos" (Coração: Branco <-> Vermelho) com Persistência
        val prefs = getSharedPreferences("favoritos_prefs", MODE_PRIVATE)
        val bookTitle = findViewById<TextView>(R.id.tvBookTitle)?.text?.toString() ?: "LivroPosse"
        val bookKey = "fav_$bookTitle"

        var isFavorito = prefs.getBoolean(bookKey, false)

        // Estado inicial
        if (isFavorito) {
            btnAddDesejos?.iconTint = ColorStateList.valueOf(Color.RED)
        } else {
            btnAddDesejos?.iconTint = ColorStateList.valueOf(Color.WHITE)
        }

        btnAddDesejos?.setOnClickListener {
            isFavorito = !isFavorito
            
            // Salva o estado
            prefs.edit().putBoolean(bookKey, isFavorito).apply()

            if (isFavorito) {
                btnAddDesejos.iconTint = ColorStateList.valueOf(Color.RED)
                Toast.makeText(this, "Adicionado aos favoritos", Toast.LENGTH_SHORT).show()
            } else {
                btnAddDesejos.iconTint = ColorStateList.valueOf(Color.WHITE)
                Toast.makeText(this, "Removido dos favoritos", Toast.LENGTH_SHORT).show()
            }
        }

        btnReport?.setOnClickListener {
            val intent = Intent(this, com.example.bdu.suporte.ReportarProblema2Activity::class.java)
            startActivity(intent)
        }

        btnVerTudo?.setOnClickListener {
            val intent = Intent(this, VerTudoActivity::class.java)
            startActivity(intent)
        }

        // Barra de tarefas
        btnFila?.setOnClickListener {
            startActivity(Intent(this, ListadeEsperaActivity::class.java))
        }
        
        btnMeusLivros?.setOnClickListener {
            // Já está na atividade Meus Livros
        }
        
        btnHome?.setOnClickListener {
            startActivity(Intent(this, TelahomeActivity::class.java))
        }
        
        btnDesejosNav?.setOnClickListener {
            startActivity(Intent(this, ListaDesejosActivity::class.java))
        }
        
        btnPerfil?.setOnClickListener {
            startActivity(Intent(this, MeuPerfilActivity::class.java))
        }
    }
}
