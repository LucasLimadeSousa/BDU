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
import com.example.bdu.pagamentos.PixActivity
import com.example.bdu.usuario.MeuPerfilActivity

class VerTudoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.livros_ver_tudo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<ImageButton>(R.id.btn_backReturn)?.setOnClickListener {
            startActivity(Intent(this, TelahomeActivity::class.java))
        }

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