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

        val btnFila = findViewById<ImageButton?>(R.id.btn_nav_fila)
        val btnMeusLivros = findViewById<ImageButton?>(R.id.btn_nav_meuslivros)
        val btnHome = findViewById<ImageButton?>(R.id.btn_nav_home)
        val btnDesejos = findViewById<ImageButton?>(R.id.btn_nav_desejos)
        val btnPerfil = findViewById<ImageButton?>(R.id.btn_nav_perfil)

        btnFila?.setOnClickListener {
            val intent = Intent(this, ListadeEsperaActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnMeusLivros?.setOnClickListener {
            val intent = Intent(this, MeusLivrosActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnHome?.setOnClickListener {
            val intent = Intent(this, TelahomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnDesejos?.setOnClickListener {
            val intent = Intent(this, ListaDesejosActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnPerfil?.setOnClickListener {
            val intent = Intent(this, MeuPerfilActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
