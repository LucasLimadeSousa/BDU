package com.example.bdu.login

import Usuario
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.bdu.R
import com.example.bdu.livros.TelahomeActivity
import com.google.android.material.button.MaterialButton
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

val supabase = createSupabaseClient(
    supabaseUrl = "https://lorihszkrdjskiaaqjqq.supabase.co",
    supabaseKey = "sb_publishable_opk8Hq7zSjUbCBXR3h2etQ_0Fdk3e5y"
) {
    install(Postgrest)
}

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
            startActivity(Intent(this, TelahomeActivity::class.java))
        }

        lifecycleScope.launch {
            try {
                val usuarioTeste = Usuario(
                    nome = "Teste Lucas",
                    email = "teste@bdu.com",
                    senha = "123",
                    cpf = "000.000.000-00",
                    telefone = "85999999999",
                    curso = "Engenharia",
                    cidade = "Fortaleza",
                    estado = "CE",
                    data_nascimento = "2000-01-01"
                )

                supabase.from("Dados_Usuario").insert(usuarioTeste)

                println("SUCESSO: Enviado para o Supabase")
            } catch (e: Exception){
                e.printStackTrace()
                println("Erro no teste: ${e.message}")
            }
        }
    }
}