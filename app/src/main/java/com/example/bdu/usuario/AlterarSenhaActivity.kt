package com.example.bdu.usuario

import Usuario
import android.graphics.Color
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.bdu.R
import com.example.bdu.login.EmailRecuperacaoActivity
import com.example.bdu.login.LoginActivity
import com.example.bdu.network.SupabaseConfig
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class AlterarSenhaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.usuario_alterar_senha)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val editNovaSenha = findViewById<EditText>(R.id.editTextNovaSenha)
        val editConfirmarSenha = findViewById<EditText>(R.id.editTextConfirmarSenha)
        val tilConfirmarSenha = findViewById<TextInputLayout>(R.id.tilConfirmarSenha)

        val watcherSenhas = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val nova = editNovaSenha.text.toString()
                val confirma = editConfirmarSenha.text.toString()

                atualizarRequisitos(nova)

                if (confirma.isNotEmpty() && nova != confirma) {
                    tilConfirmarSenha.error = "As senhas não coincidem"
                } else {
                    tilConfirmarSenha.error = null
                    tilConfirmarSenha.isErrorEnabled = false
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        editNovaSenha.addTextChangedListener(watcherSenhas)
        editConfirmarSenha.addTextChangedListener(watcherSenhas)

        findViewById<ImageButton>(R.id.imageButton2).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.textView24).setOnClickListener {
            val intent = Intent(this, EmailRecuperacaoActivity::class.java)
            startActivity(intent)
        }

        findViewById<MaterialButton>(R.id.button4).setOnClickListener {
            executarVerificacaoEConfirmacao()
        }
    }

    private fun atualizarRequisitos(senha: String) {
        val temOitoCaracteres = senha.length >= 8
        val temMaiuscula = senha.any { it.isUpperCase() }
        val temNumero = senha.any { it.isDigit() }
        val temEspecial = senha.any { !it.isLetterOrDigit() }

        // Regra 1: Mínimo 8 caracteres
        atualizarItemRegra(R.id.rule1Icon, R.id.rule1Text, temOitoCaracteres)
        
        // Regra 2: Uma letra maiúscula
        atualizarItemRegra(R.id.rule2Icon, R.id.rule2Text, temMaiuscula)
        
        // Regra 3: Um número e um caractere especial
        atualizarItemRegra(R.id.rule3Icon, R.id.rule3Text, temNumero && temEspecial)
    }

    private fun atualizarItemRegra(iconId: Int, textId: Int, satisfeita: Boolean) {
        val icon = findViewById<ImageView>(iconId)
        val text = findViewById<TextView>(textId)
        
        if (satisfeita) {
            icon.setImageResource(R.drawable.ic_check)
            icon.setColorFilter("#2E7D32".toColorInt()) // Verde
            text.setTextColor("#2E7D32".toColorInt())
        } else {
            icon.setImageResource(R.drawable.ic_cancel)
            icon.setColorFilter("#888888".toColorInt()) // Cinza original
            text.setTextColor("#666666".toColorInt()) // Cor original
        }
    }

    private fun executarVerificacaoEConfirmacao() {
        val senhaAtual = findViewById<EditText>(R.id.editTextSenhaAtual).text.toString()
        val novaSenha = findViewById<EditText>(R.id.editTextNovaSenha).text.toString()
        val confirmarSenha = findViewById<EditText>(R.id.editTextConfirmarSenha).text.toString()

        if (senhaAtual.isEmpty() || novaSenha.isEmpty() || confirmarSenha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (novaSenha != confirmarSenha) {
            Toast.makeText(this, "As novas senhas não coincidem", Toast.LENGTH_SHORT).show()
            return
        }

        if (novaSenha == senhaAtual) {
            Toast.makeText(this, "A nova senha não pode ser igual à senha atual", Toast.LENGTH_SHORT).show()
            return
        }

        if (!senhaEValida(novaSenha)) {
            AlertDialog.Builder(this)
                .setTitle("Senha Fraca")
                .setMessage("A senha deve conter:\n1- No mínimo 8 caracteres\n2- Uma letra maiúscula\n3- Um número e um caractere especial")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        val builder = MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
        builder.setMessage("Tem certeza que deseja alterar a senha?")
        
        builder.setPositiveButton("Sim") { _, _ ->
            alterarSenhaNoBanco(senhaAtual, novaSenha)
        }
        
        builder.setNegativeButton("Não") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()

        // Personalizando as cores dos botões para corresponder à imagem
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.let {
            it.setBackgroundColor("#2E7D32".toColorInt()) // Verde
            it.setTextColor(Color.WHITE)
            
            val params = it.layoutParams as android.widget.LinearLayout.LayoutParams
            params.setMargins(10, 0, 10, 0)
            it.layoutParams = params
        }

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.let {
            it.setBackgroundColor("#D32F2F".toColorInt()) // Vermelho
            it.setTextColor(Color.WHITE)

            val params = it.layoutParams as android.widget.LinearLayout.LayoutParams
            params.setMargins(10, 0, 10, 0)
            it.layoutParams = params
        }
    }

    private fun senhaEValida(senha: String): Boolean {
        val temOitoCaracteres = senha.length >= 8
        val temMaiuscula = senha.any { it.isUpperCase() }
        val temNumero = senha.any { it.isDigit() }
        val temEspecial = senha.any { !it.isLetterOrDigit() }

        return temOitoCaracteres && temMaiuscula && temNumero && temEspecial
    }

    private fun alterarSenhaNoBanco(senhaAtual: String, novaSenha: String) {
        lifecycleScope.launch {
            try {
                val user = SupabaseConfig.client.auth.currentUserOrNull()
                val email = user?.email

                if (email == null) {
                    Toast.makeText(this@AlterarSenhaActivity, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // 1. Verificar senha atual no banco Dados_Usuario
                val usuarioDados = SupabaseConfig.client.from("Dados_Usuario").select {
                    filter {
                        eq("email", email)
                    }
                }.decodeSingle<Usuario>()

                if (usuarioDados.senha != senhaAtual) {
                    Toast.makeText(this@AlterarSenhaActivity, "Senha atual incorreta", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // 2. Atualizar no Supabase Auth
                SupabaseConfig.client.auth.updateUser {
                    password = novaSenha
                }

                // 3. Atualizar na tabela Dados_Usuario
                SupabaseConfig.client.from("Dados_Usuario").update(
                    {
                        set("senha", novaSenha)
                    }
                ) {
                    filter {
                        eq("email", email)
                    }
                }

                Toast.makeText(this@AlterarSenhaActivity, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show()
                
                // Redirecionar para login como no código original
                val intent = Intent(this@AlterarSenhaActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@AlterarSenhaActivity, "Erro ao alterar senha: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
