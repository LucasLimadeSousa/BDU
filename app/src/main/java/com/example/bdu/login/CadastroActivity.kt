package com.example.bdu.login

import com.example.bdu.model.Usuario
import android.graphics.Bitmap
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class CadastroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_cadastro)

        configurarLayoutEdgetoEdge()
        configurarLinkTermos()
        configurarMascaraCPF()
        configurarMascaraTelefone()
        configurarMascaraData()

        val inputSenha = findViewById<EditText>(R.id.inputSenha)
        val rule1Icon = findViewById<ImageView>(R.id.rule1Icon)
        val rule2Icon = findViewById<ImageView>(R.id.rule2Icon)
        val rule3Icon = findViewById<ImageView>(R.id.rule3Icon)
        val rule1Text = findViewById<TextView>(R.id.rule1Text)
        val rule2Text = findViewById<TextView>(R.id.rule2Text)
        val rule3Text = findViewById<TextView>(R.id.rule3Text)

        val colorOk = ContextCompat.getColor(this, android.R.color.holo_green_dark)
        val colorDefault = ContextCompat.getColor(this, android.R.color.darker_gray)

        inputSenha.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val senha = s.toString()

                val isTamanhoOk = senha.length >= 8
                val isMaiusculaOk = senha.any { it.isUpperCase() }
                val isNumEspecialOk = senha.any { it.isDigit() } && senha.any { !it.isLetterOrDigit() }

                updateRule(rule1Icon, rule1Text, isTamanhoOk, colorOk, colorDefault)
                updateRule(rule2Icon, rule2Text, isMaiusculaOk, colorOk, colorDefault)
                updateRule(rule3Icon, rule3Text, isNumEspecialOk, colorOk, colorDefault)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        findViewById<View>(R.id.btnFinalizar).setOnClickListener {
            executarCadastro()
        }

        findViewById<ImageButton>(R.id.btn_backReturn).setOnClickListener { finish() }
    }

    private fun updateRule(icon: ImageView, text: TextView, isOk: Boolean, colorOk: Int, colorDefault: Int) {
        val color = if (isOk) colorOk else colorDefault
        text.setTextColor(color)

        // Remove qualquer filtro de cor anterior para evitar efeitos indesejados
        icon.clearColorFilter()

        // Define o ícone: apenas o check ou o X, sem fundo.
        // Se desejar um check mais limpo, verifique se seu projeto já possui um ic_check.
        // Aqui estamos usando um recurso que geralmente desenha apenas a marca.
        icon.setImageResource(if (isOk) android.R.drawable.presence_online else android.R.drawable.presence_busy)
        icon.setColorFilter(color)
    }

    private fun senhaEValida(senha: String): Boolean{
        val temOitoCaracteres = senha.length >= 8
        val temMaiuscula = senha.any{ it.isUpperCase() }
        val temNumero = senha.any { it.isDigit() }
        val temEspecial = senha.any { !it.isLetterOrDigit() }

        return temOitoCaracteres && temMaiuscula && temNumero && temEspecial
    }

    private fun executarCadastro(){
        val nome = findViewById<EditText>(R.id.inputNome).text.toString().trim()
        val email = findViewById<EditText>(R.id.inputEmail).text.toString().trim()
        val confEmail = findViewById<EditText>(R.id.inputConfEmail).text.toString().trim()
        val senha = findViewById<EditText>(R.id.inputSenha).text.toString()
        val confSenha = findViewById<EditText>(R.id.inputConfSenha).text.toString()
        val cpf = findViewById<EditText>(R.id.inputCpf).text.toString().trim()
        val telefone = findViewById<EditText>(R.id.inputTelefone).text.toString().trim()
        val curso = findViewById<Spinner>(R.id.inputCurso).selectedItem.toString()
        val cidade = findViewById<EditText>(R.id.inputCidade).text.toString().trim()
        val estado = findViewById<EditText>(R.id.inputEstado).text.toString().trim()
        val dataNascRaw = findViewById<EditText>(R.id.inputDataNascimento).text.toString().trim()
        val aceitouTermos = findViewById<CheckBox>(R.id.checkboxTermos).isChecked

        if (curso == "Cursos") {
            exibirAlerta("Campo Obrigatório", "Por favor, selecione seu curso.")
            return
        }

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || cpf.isEmpty() ||
            telefone.isEmpty() || curso.isEmpty() || cidade.isEmpty() || estado.isEmpty() || dataNascRaw.isEmpty())
        {
            exibirAlerta("Campos Vazios", "Por favor, preencha todos os campos obrigatórios.")
            return
        }

        if(email != confEmail){
            exibirAlerta("Erro no E-mail", "Os e-mails digitados não coincidem.")
            return
        }

        if (!senhaEValida(senha)) {
            exibirAlerta(
                "Senha Fraca",
                "A senha deve conter:\n" +
                        "1- No mínimo 8 caracteres\n" +
                        "2- Uma letra maiúscula\n" +
                        "3- Um número e um caractere especial"
            )
            return
        }

        if(senha != confSenha){
            exibirAlerta("Erro na Senha", "As senhas digitadas não coincidem.")
            return
        }

        if(!aceitouTermos){
            exibirAlerta("Termos de Uso", "Você precisa aceitar os termos e condições para prosseguir.")
            return
        }

        // Validação de CPF (11 dígitos numéricos)
        val cpfLimpo = cpf.replace(Regex("[^\\d]"), "")
        if (cpfLimpo.length != 11) {
            Toast.makeText(this, "CPF incorreto. Digite os 11 números.", Toast.LENGTH_SHORT).show()
            return
        }

        // Validação de Telefone (11 dígitos numéricos - DDD + 9 + número)
        val telefoneLimpo = telefone.replace(Regex("[^\\d]"), "")
        if (telefoneLimpo.length != 11) {
            Toast.makeText(this, "Número de telefone incorreto. Digite o DDD e os 9 dígitos.", Toast.LENGTH_SHORT).show()
            return
        }

        // Validação de Data de Nascimento
        val dataFormatada = formatarDataParaBanco(dataNascRaw)
        if (dataNascRaw.length < 10 || dataFormatada == dataNascRaw) {
            Toast.makeText(this, "Data de nascimento incorreta. Digite novamente.", Toast.LENGTH_SHORT).show()
            return
        }

        // Validação extra de valores de data impossíveis
        try {
            val partes = dataNascRaw.split("/")
            val dia = partes[0].toInt()
            val mes = partes[1].toInt()
            val ano = partes[2].toInt()
            
            if (dia !in 1..31 || mes !in 1..12 || ano > 2024 || ano < 1900) {
                Toast.makeText(this, "Data de nascimento incorreta. Digite novamente.", Toast.LENGTH_SHORT).show()
                return
            }
            // Validação simples para meses com 30 dias e fevereiro
            if ((mes == 4 || mes == 6 || mes == 9 || mes == 11) && dia > 30) {
                Toast.makeText(this, "Data de nascimento incorreta. Digite novamente.", Toast.LENGTH_SHORT).show()
                return
            }
            if (mes == 2 && dia > 29) {
                Toast.makeText(this, "Data de nascimento incorreta. Digite novamente.", Toast.LENGTH_SHORT).show()
                return
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Data de nascimento incorreta. Digite novamente.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                // 1. Verificar se e-mail já existe na tabela de dados
                val emailExiste = SupabaseConfig.client.from("Dados_Usuario")
                    .select {
                        filter {
                            eq("email", email)
                        }
                    }.data != "[]"

                if (emailExiste) {
                    exibirAlerta("E-mail Já Cadastrado", "Este e-mail já está sendo utilizado por outra conta")
                    return@launch
                }

                // 2. Realizar o Sign Up no Supabase Auth
                SupabaseConfig.client.auth.signUpWith(Email){
                    this.email = email
                    this.password = senha
                }

                // 3. Inserir os dados complementares na tabela Dados_Usuario
                val novoUsuario = Usuario(
                    nome = nome,
                    email = email,
                    senha = senha,
                    cpf = cpf,
                    telefone = telefone,
                    curso = curso,
                    cidade = cidade,
                    estado = estado,
                    data_nascimento = formatarDataParaBanco(dataNascRaw),
                    adm = false,
                    foto = null
                )

                // Tenta inserir e aguarda o resultado
                SupabaseConfig.client.from("Dados_Usuario").insert(novoUsuario)

                Toast.makeText(this@CadastroActivity, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@CadastroActivity, LoginActivity::class.java))
                finish()

            } catch (e: Exception){
                e.printStackTrace()
                exibirAlerta("Erro no cadastro", "Erro: ${e.localizedMessage}")
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

    private fun formatarDataParaBanco(dataBr: String): String {
        return try {
            val partes = dataBr.split("/")
            if (partes.size == 3) {
                "${partes[2]}-${partes[1]}-${partes[0]}"
            } else {
                dataBr
            }
        } catch (e: Exception) {
            dataBr
        }
    }

    private fun configurarMascaraCPF() {
        val inputCpf = findViewById<EditText>(R.id.inputCpf)
        inputCpf.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val str = s.toString().replace(Regex("[^\\d]"), "")
                if (isUpdating) {
                    isUpdating = false
                    return
                }

                var formatted = ""
                var i = 0
                val mask = "###.###.###-##"
                for (m in mask.toCharArray()) {
                    if (m != '#' && str.length > i) {
                        formatted += m
                        continue
                    }
                    try {
                        formatted += str[i]
                    } catch (e: Exception) {
                        break
                    }
                    i++
                }

                isUpdating = true
                inputCpf.setText(formatted)
                inputCpf.setSelection(formatted.length)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun configurarMascaraTelefone() {
        val inputTelefone = findViewById<EditText>(R.id.inputTelefone)
        inputTelefone.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val str = s.toString().replace(Regex("[^\\d]"), "")
                if (isUpdating) {
                    isUpdating = false
                    return
                }

                var formatted = ""
                var i = 0
                val mask = "(##) #####-####"

                for (m in mask.toCharArray()) {
                    if (m != '#' && str.length > i) {
                        formatted += m
                        continue
                    }
                    try {
                        formatted += str[i]
                    } catch (e: Exception) {
                        break
                    }
                    i++
                }

                isUpdating = true
                inputTelefone.setText(formatted)
                inputTelefone.setSelection(formatted.length)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun configurarMascaraData() {
        val inputData = findViewById<EditText>(R.id.inputDataNascimento)
        inputData.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val str = s.toString().replace(Regex("[^\\d]"), "")
                if (isUpdating) {
                    isUpdating = false
                    return
                }

                var formatted = ""
                var i = 0
                val mask = "##/##/####"

                for (m in mask.toCharArray()) {
                    if (m != '#' && str.length > i) {
                        formatted += m
                        continue
                    }
                    try {
                        formatted += str[i]
                    } catch (e: Exception) {
                        break
                    }
                    i++
                }

                isUpdating = true
                inputData.setText(formatted)
                inputData.setSelection(formatted.length)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}