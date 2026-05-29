package com.example.bdu.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.bdu.R
import com.example.bdu.network.SupabaseConfig
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.OtpType
import kotlinx.coroutines.launch

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

        val email = intent.getStringExtra("email") ?: ""
        val codigoInput = findViewById<EditText>(R.id.editTextCodigo)

        findViewById<Button>(R.id.btnConfirmarCodigo).setOnClickListener {
            val codigo = codigoInput.text.toString().trim()
            if (codigo.isEmpty()) {
                Toast.makeText(this, "Por favor, insira o código", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    // Validando no CLIENT ORIGINAL
                    SupabaseConfig.client.auth.verifyEmailOtp(
                        type = OtpType.Email.RECOVERY,
                        email = email,
                        token = codigo
                    )
                    Toast.makeText(this@AutenticacaoActivity, "Código validado!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@AutenticacaoActivity, EsqueciSenhaActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this@AutenticacaoActivity, "Código inválido ou expirado.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        findViewById<TextView>(R.id.textViewNaoRecebi).setOnClickListener {
            lifecycleScope.launch {
                try {
                    // Reenviando pelo CLIENT ORIGINAL
                    SupabaseConfig.client.auth.resetPasswordForEmail(email)
                    Toast.makeText(this@AutenticacaoActivity, "Novo código enviado!", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this@AutenticacaoActivity, "Erro ao reenviar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        findViewById<Button>(R.id.btnVoltar).setOnClickListener {
            finish()
        }
    }
}