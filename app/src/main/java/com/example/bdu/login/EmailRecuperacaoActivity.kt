package com.example.bdu.login

import Usuario
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.bdu.R
import com.example.bdu.network.SupabaseConfig
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class EmailRecuperacaoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_email_recuperacao)

        val emailInput = findViewById<EditText>(R.id.editTextEmail)

        findViewById<Button>(R.id.buttonConfirmar).setOnClickListener {
            val email = emailInput.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor, insira o seu e-mail", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    // Verificar se o email existe na tabela Dados_Usuario
                    val response = SupabaseConfig.client.from("Dados_Usuario")
                        .select {
                            filter {
                                eq("email", email)
                            }
                        }
                    val userExists = response.decodeList<Usuario>().isNotEmpty()

                    if (userExists) {
                        SupabaseConfig.client.auth.resetPasswordForEmail(email)
                        Toast.makeText(this@EmailRecuperacaoActivity, "Código de recuperação enviado!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@EmailRecuperacaoActivity, AutenticacaoActivity::class.java)
                        intent.putExtra("email", email)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@EmailRecuperacaoActivity, "E-mail não encontrado.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@EmailRecuperacaoActivity, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        findViewById<Button>(R.id.buttonVoltar).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }
}