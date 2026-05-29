package com.example.bdu.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bdu.R
import com.example.bdu.network.SupabaseConfig
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class EmailRecuperacaoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_email_recuperacao)

        val emailInput = findViewById<EditText>(R.id.editTextEmail)

        findViewById<Button>(R.id.buttonConfirmar).setOnClickListener {
            val email = emailInput.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor, insira seu e-mail", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    // Voltando para o CLIENT ORIGINAL para buscar o usuário e enviar o e-mail
                    val response = SupabaseConfig.client.from("Dados_Usuario")
                        .select {
                            filter {
                                eq("email", email)
                            }
                        }
                    
                    val userExists = response.data != "[]"

                    if (!userExists) {
                        Toast.makeText(this@EmailRecuperacaoActivity, "Este e-mail não está cadastrado.", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    // Enviando pelo CLIENT ORIGINAL
                    SupabaseConfig.client.auth.resetPasswordForEmail(email)
                    
                    val intent = Intent(this@EmailRecuperacaoActivity, AutenticacaoActivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this@EmailRecuperacaoActivity, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        findViewById<Button>(R.id.buttonVoltar).setOnClickListener {
            finish()
        }
    }
}
