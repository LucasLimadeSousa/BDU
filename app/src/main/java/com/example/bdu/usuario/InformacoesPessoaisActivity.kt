package com.example.bdu.usuario

import Usuario
import android.graphics.BitmapFactory
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.bdu.R
import com.example.bdu.network.SupabaseConfig
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InformacoesPessoaisActivity : AppCompatActivity() {

    private lateinit var ivProfile: ImageView
    private var emailUsuarioLogado: String? = null

    private val NOME_DO_BUCKET = "avatars"

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedUri ->
            val imageBytes = uriToByteArray(selectedUri)
            if (imageBytes != null && emailUsuarioLogado != null) {
                // Exibe provisoriamente na tela
                ivProfile.setImageURI(selectedUri)
                ivProfile.imageTintList = null

                // Envia para o Bucket do Supabase
                salvarFotoNoBucket(imageBytes, emailUsuarioLogado!!)
            } else {
                Toast.makeText(this, "Erro ao processar imagem ou usuário não identificado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.usuario_informacoes_pessoais)

        // CORREÇÃO 2: Inicializando o ivProfile para evitar que o app feche
        ivProfile = findViewById(R.id.ivProfile)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<View>(R.id.btnBackLivro).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        ivProfile.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        carregarInformacoesDoSupabase()
    }

    private fun carregarInformacoesDoSupabase(){
        lifecycleScope.launch {
            try {
                val usuarioLogado = SupabaseConfig.client.auth.currentUserOrNull()
                val emailLogado = usuarioLogado?.email

                if (emailLogado != null){
                    // CORREÇÃO 3: Salvando o e-mail na variável global para ser usado no upload da foto
                    emailUsuarioLogado = emailLogado

                    val usuarioDados = withContext(Dispatchers.IO){
                        SupabaseConfig.client.postgrest["Dados_Usuario"]
                            .select(columns = Columns.ALL) {
                                filter {
                                    eq("email", emailLogado)
                                }
                            }.decodeSingle<Usuario>()
                    }

                    preencherCamposDaTela(usuarioDados)
                } else {
                    Toast.makeText(this@InformacoesPessoaisActivity, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
                }
            }
            catch (e: Exception){
                e.printStackTrace()
                Toast.makeText(this@InformacoesPessoaisActivity, "Erro ao carregar dados: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun preencherCamposDaTela(usuario: Usuario) {
        val tvNome = findViewById<TextView>(R.id.tvValueNome)
        val tvEmail = findViewById<TextView>(R.id.tvValueEmail)
        val tvCpf = findViewById<TextView>(R.id.tvValueCpf)
        val tvTelefone = findViewById<TextView>(R.id.tvValueTelefone)
        val tvIdade = findViewById<TextView>(R.id.tvValueIdade)
        val tvEstado = findViewById<TextView>(R.id.tvValueEstado)
        val tvCurso = findViewById<TextView>(R.id.tvValueCurso)

        tvNome.text = usuario.nome
        tvEmail.text = usuario.email
        tvCpf.text = formatarCpf(usuario.cpf)
        tvTelefone.text = usuario.telefone
        tvEstado.text = usuario.estado
        tvCurso.text = usuario.curso

        if (!usuario.data_nascimento.isNullOrEmpty()){
            tvIdade.text = calcularIdade(usuario.data_nascimento)
        } else {
            tvIdade.text = "Idade não informada"
        }

        // Faz o download da foto caso ela exista no banco
        if (!usuario.foto.isNullOrEmpty()) {
            lifecycleScope.launch {
                try {
                    val bytes = withContext(Dispatchers.IO) {
                        SupabaseConfig.client.storage.from(NOME_DO_BUCKET).downloadPublic(usuario.foto!!)
                    }
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    if(bitmap != null){
                        ivProfile.setImageBitmap(bitmap)
                        ivProfile.imageTintList = null
                    }
                } catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
    }

    private fun salvarFotoNoBucket(bytes: ByteArray, email: String){
        val nomeDoArquivo = "$email.jpg"

        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // CORREÇÃO 1: Ajustado o formato do upload com o bloco do 'upsert' correto
                    SupabaseConfig.client.storage.from(NOME_DO_BUCKET).upload(
                        path = nomeDoArquivo,
                        data = bytes
                    ) {
                        upsert = true
                    }

                    SupabaseConfig.client.postgrest["Dados_Usuario"].update(
                        update = {
                            set("foto", nomeDoArquivo)
                        }
                    ) {
                        filter {
                            eq("email", email)
                        }
                    }
                }
                Toast.makeText(this@InformacoesPessoaisActivity, "Foto de perfil atualizada!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception){
                e.printStackTrace()
                Toast.makeText(this@InformacoesPessoaisActivity, "Erro ao salvar foto: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uriToByteArray(uri: Uri): ByteArray? {
        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes()
            }
        } catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    private fun formatarCpf(cpf: String): String{
        val cpfLimpo = cpf.replace(Regex("[^\\d]"),"")
        if(cpfLimpo.length != 11){
            return cpf
        }
        return "${cpfLimpo.substring(0, 3)}.${cpfLimpo.substring(3, 6)}.${cpfLimpo.substring(6, 9)}-${cpfLimpo.substring(9, 11)}"
    }

    private fun calcularIdade(dataNascimento: String): String{
        try {
            val anoAtual = Calendar.getInstance().get(Calendar.YEAR)
            val anoNascimento = when{
                dataNascimento.contains("-") && dataNascimento.indexOf("-") == 4 -> {
                    dataNascimento.substring(0, 4).toInt()
                }
                else -> {
                    dataNascimento.takeLast(4).toInt()
                }
            }
            val idade = anoAtual - anoNascimento
            return "$idade anos"
        } catch (e: Exception){
            e.printStackTrace()
            return dataNascimento
        }
    }
}