package com.example.bdu.login

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.bdu.R
import com.example.bdu.livros.TelahomeActivity
import com.example.bdu.network.SupabaseConfig
import com.google.android.material.button.MaterialButton
import android.widget.ImageButton
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import com.example.bdu.model.Usuario
import com.example.bdu.adm.AdicionarLivroAdmActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<MaterialButton>(R.id.buttonCadastrar).setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }

        findViewById<TextView>(R.id.textViewEsqueciSenha).setOnClickListener {
            startActivity(Intent(this, EmailRecuperacaoActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.buttonLogar).setOnClickListener {
            executarLogin()
        }

    }

    private fun executarLogin() {
        val email = findViewById<EditText>(R.id.editTextEmail).text.toString().trim()
        val senha = findViewById<EditText>(R.id.editTextSenha).text.toString()

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                // 1. Autentica no Supabase Auth
                SupabaseConfig.client.auth.signInWith(Email) {
                    this.email = email
                    this.password = senha
                }

                // 2. Busca os detalhes do usuário na tabela Dados_Usuario
                val usuarioLogado = SupabaseConfig.client.from("Dados_Usuario")
                    .select {
                        filter {
                            eq("email", email)
                        }
                    }.decodeSingle<Usuario>()

                // 3. Vai para a Home levando a informação se é ADM
                val intent = Intent(this@LoginActivity, TelahomeActivity::class.java)
                intent.putExtra("USER_IS_ADM", usuarioLogado.adm)
                startActivity(intent)
                finish()

            } catch (e: Exception) {
                e.printStackTrace()
                AlertDialog.Builder(this@LoginActivity)
                    .setTitle("Erro de Login")
                    .setMessage("Não foi possível logar. Detalhe: ${e.message}")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }
}