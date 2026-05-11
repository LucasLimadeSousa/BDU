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

class EsqueciSenhaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_esqueci_senha)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btnConfirmar).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            Toast.makeText(this, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnVoltar).setOnClickListener {
            startActivity(Intent(this, AutenticacaoActivity::class.java))
        }
    }
}