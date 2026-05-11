package com.example.bdu.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bdu.R

class AutenticacaoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_autenticacao)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btnConfirmarCodigo).setOnClickListener {
            Toast.makeText(this, "Email cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, EsqueciSenhaActivity::class.java))
        }

        findViewById<Button>(R.id.btnVoltar).setOnClickListener {
            startActivity(Intent(this, EmailRecuperacaoActivity::class.java))
        }
    }
}