package com.example.bdu.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.example.bdu.R

class EmailRecuperacaoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_email_recuperacao)

        findViewById<Button>(R.id.buttonConfirmar).setOnClickListener {
            startActivity(Intent(this, AutenticacaoActivity::class.java))
        }

        findViewById<Button>(R.id.buttonVoltar).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}