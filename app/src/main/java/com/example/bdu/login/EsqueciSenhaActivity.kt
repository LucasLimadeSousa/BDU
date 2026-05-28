package com.example.bdu.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.bdu.R
import com.example.bdu.network.SupabaseConfig
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

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

        val novaSenhaInput = findViewById<EditText>(R.id.et_nova_senha)
        val confirmarSenhaInput = findViewById<EditText>(R.id.et_confirmar_senha)

        findViewById<Button>(R.id.btnConfirmar).setOnClickListener {
            val novaSenha = novaSenhaInput.text.toString()
            val confirmarSenha = confirmarSenhaInput.text.toString()

            if (novaSenha.isEmpty() || confirmarSenha.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (novaSenha != confirmarSenha) {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!senhaEValida(novaSenha)) {
                Toast.makeText(this, "A senha deve ter 8 caracteres, uma maiúscula, um número e um símbolo", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    // Atualizando no CLIENT ORIGINAL
                    SupabaseConfig.client.auth.updateUser {
                        password = novaSenha
                    }
                    Toast.makeText(this@EsqueciSenhaActivity, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@EsqueciSenhaActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@EsqueciSenhaActivity, "Erro ao alterar senha: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        findViewById<Button>(R.id.btnVoltar).setOnClickListener {
            finish()
        }
    }

    private fun senhaEValida(senha: String): Boolean {
        val temOitoCaracteres = senha.length >= 8
        val temMaiuscula = senha.any { it.isUpperCase() }
        val temNumero = senha.any { it.isDigit() }
        val temEspecial = senha.any { !it.isLetterOrDigit() }

        return temOitoCaracteres && temMaiuscula && temNumero && temEspecial
    }
}