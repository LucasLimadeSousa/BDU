package com.example.bdu.login

import Usuario
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.bdu.R
import com.example.bdu.network.SupabaseConfig
import com.example.bdu.suporte.TermosCondicoesActivity
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import android.widget.ImageButton
import android.widget.TextView

class CadastroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_cadastro)

        configurarLayoutEdgetoEdge()
        configurarLinkTermos()

        findViewById<TextView>(R.id.btnFinalizar).setOnClickListener {
            executarCadastro()
        }

        findViewById<ImageButton>(R.id.btn_backReturn).setOnClickListener { finish() }
    }

    private fun executarCadastro(){
        val nome = findViewById<EditText>(R.id.inputNome).text.toString().trim()
        val email = findViewById<EditText>(R.id.inputEmail).text.toString().trim()
        val confEmail = findViewById<EditText>(R.id.inputConfEmail).text.toString().trim()
        val senha = findViewById<EditText>(R.id.inputSenha).text.toString()
        val confSenha = findViewById<EditText>(R.id.inputConfSenha).text.toString()
        val cpf = findViewById<EditText>(R.id.inputCpf).text.toString().trim()
        val telefone = findViewById<EditText>(R.id.inputTelefone).text.toString().trim()
        val curso = findViewById<EditText>(R.id.inputCurso).text.toString().trim()
        val cidade = findViewById<EditText>(R.id.inputCidade).text.toString().trim()
        val estado = findViewById<EditText>(R.id.inputEstado).text.toString().trim()
        val dataNasc = findViewById<EditText>(R.id.inputDataNascimento).text.toString().trim()
        val aceitouTermos = findViewById<CheckBox>(R.id.checkboxTermos).isChecked

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || cpf.isEmpty() ||
            telefone.isEmpty() || curso.isEmpty() || cidade.isEmpty() || estado.isEmpty() || dataNasc.isEmpty())
        {
            exibirAlerta("Campos Vazios", "Por favor, preencha todos os campos obrigatórios.")
            return
        }

        if(email != confEmail){
            exibirAlerta("Erro no E-mail", "Os e-mails digitados não coincidem.")
            return
        }

        if(senha != confSenha){
            exibirAlerta("Erro na Senha", "As senhas digitadas não coincidem.")
            return
        }

        if(!aceitouTermos){
            exibirAlerta("Termos de Uso", "Você precisa aceitar os termos e condições para prosseguir.")
        }

        lifecycleScope.launch {
            try {
                val response = SupabaseConfig.client.auth.signUpWith(Email){
                    this.email = email
                    this.password = senha
                }

                val novoUsuario = Usuario(
                    nome = nome,
                    email = email,
                    senha = senha,
                    cpf = cpf,
                    telefone = telefone,
                    curso = curso,
                    cidade = cidade,
                    estado = estado,
                    data_nascimento = dataNasc
                )

                SupabaseConfig.client.from("Dados_Usuario").insert(novoUsuario)

                Toast.makeText(this@CadastroActivity, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@CadastroActivity, LoginActivity::class.java))
                finish()

            } catch (e: Exception){
                exibirAlerta("Erro no cadastro", "Não foi possivel realizar o cadastro: ${e.message}")
            }
        }
    }

    private fun exibirAlerta(titulo: String, mensagem: String) {
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(mensagem)
            .setPositiveButton("Entendi", null)
            .show()
    }

    private fun configurarLinkTermos(){
        val checkboxTermos = findViewById<TextView>(R.id.termosLink)
        val textoCompleto = "Concordo com os termos e condições"
        val textoLink = "termos e condições"
        val inicio = textoCompleto.indexOf(textoLink)
        val fim = inicio + textoLink.length
        val spannable = SpannableString(textoCompleto)

        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.unifor_blue)), inicio, fim,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(StyleSpan(android.graphics.Typeface.BOLD), inicio, fim, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@CadastroActivity, TermosCondicoesActivity::class.java))
            }
        }, inicio, fim, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        checkboxTermos.text = spannable
        checkboxTermos.movementMethod = LinkMovementMethod.getInstance()
        checkboxTermos.highlightColor = Color.TRANSPARENT
    }

    private fun configurarLayoutEdgetoEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) {v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}