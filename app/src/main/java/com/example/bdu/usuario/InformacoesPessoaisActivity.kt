package com.example.bdu.usuario

import Usuario
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.bdu.R
import com.example.bdu.network.SupabaseConfig
import com.google.android.material.imageview.ShapeableImageView
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class InformacoesPessoaisActivity : AppCompatActivity() {

    private lateinit var ivProfile: ShapeableImageView
    private var emailUsuarioLogado: String? = null
    private val NOME_DO_BUCKET = "avatars"

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedUri ->
            val imageBytes = uriToByteArray(selectedUri)
            if (imageBytes != null && emailUsuarioLogado != null) {
                ivProfile.setImageURI(selectedUri)
                ivProfile.imageTintList = null
                salvarFotoNoBucket(imageBytes, emailUsuarioLogado!!)
            }
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null && emailUsuarioLogado != null) {
            ivProfile.setImageBitmap(bitmap)
            ivProfile.imageTintList = null
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
            val imageBytes = stream.toByteArray()
            salvarFotoNoBucket(imageBytes, emailUsuarioLogado!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.usuario_informacoes_pessoais)
        ivProfile = findViewById(R.id.ivProfile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<View>(R.id.btnBackLivro).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        ivProfile.setOnClickListener { mostrarDialogoOpcoesFoto() }
        carregarInformacoesDoSupabase()
    }

    private fun mostrarDialogoOpcoesFoto() {
        if (emailUsuarioLogado == null) return
        val opcoes = arrayOf("Tirar foto", "Carregar imagem", "Remover foto")
        AlertDialog.Builder(this)
            .setTitle("Foto de perfil")
            .setItems(opcoes) { _, which ->
                when (which) {
                    0 -> takePictureLauncher.launch(null)
                    1 -> pickImageLauncher.launch("image/*")
                    2 -> removerFoto()
                }
            }
            .show()
    }

    private fun carregarInformacoesDoSupabase() {
        lifecycleScope.launch {
            try {
                val usuarioLogado = SupabaseConfig.client.auth.currentUserOrNull()
                val emailLogado = usuarioLogado?.email
                if (emailLogado != null) {
                    emailUsuarioLogado = emailLogado
                    val usuarioDados = withContext(Dispatchers.IO) {
                        SupabaseConfig.client.postgrest["Dados_Usuario"]
                            .select(columns = Columns.ALL) {
                                filter { eq("email", emailLogado) }
                            }.decodeSingle<Usuario>()
                    }
                    preencherCamposDaTela(usuarioDados)
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    private fun preencherCamposDaTela(usuario: Usuario) {
        findViewById<TextView>(R.id.tvValueNome).text = usuario.nome
        findViewById<TextView>(R.id.tvValueEmail).text = usuario.email
        findViewById<TextView>(R.id.tvValueCpf).text = formatarCpf(usuario.cpf)
        findViewById<TextView>(R.id.tvValueTelefone).text = usuario.telefone
        findViewById<TextView>(R.id.tvValueEstado).text = usuario.estado
        findViewById<TextView>(R.id.tvValueCurso).text = usuario.curso
        findViewById<TextView>(R.id.tvValueIdade).text = if (!usuario.data_nascimento.isNullOrEmpty()) calcularIdade(usuario.data_nascimento) else "Idade não informada"

        if (!usuario.foto.isNullOrEmpty()) {
            lifecycleScope.launch {
                try {
                    val bytes = withContext(Dispatchers.IO) {
                        // Adicionamos um timestamp para ignorar o cache do servidor
                        val urlComCacheBust = "${usuario.foto!!}?v=${System.currentTimeMillis()}"
                        SupabaseConfig.client.storage.from(NOME_DO_BUCKET).downloadPublic(urlComCacheBust)
                    }
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    if (bitmap != null) {
                        ivProfile.setImageBitmap(bitmap)
                        ivProfile.imageTintList = null
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }
        }
    }

    private fun salvarFotoNoBucket(bytes: ByteArray, email: String) {
        val nomeDoArquivo = "$email.jpg"
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    SupabaseConfig.client.storage.from(NOME_DO_BUCKET).upload(
                        path = nomeDoArquivo,
                        data = bytes
                    ) { upsert = true }

                    SupabaseConfig.client.postgrest["Dados_Usuario"].update(
                        update = { set("foto", nomeDoArquivo) }
                    ) { filter { eq("email", email) } }
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@InformacoesPessoaisActivity, "Foto de perfil atualizada!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    private fun removerFoto() {
        val email = emailUsuarioLogado ?: return
        ivProfile.setImageResource(R.drawable.ic_person)
        ivProfile.imageTintList = android.content.res.ColorStateList.valueOf(getColor(R.color.unifor_blue))
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    SupabaseConfig.client.postgrest["Dados_Usuario"].update(
                        update = { set("foto", null as String?) }
                    ) { filter { eq("email", email) } }
                    try {
                        SupabaseConfig.client.storage.from(NOME_DO_BUCKET).delete("$email.jpg")
                    } catch (e: Exception) { }
                }
                Toast.makeText(this@InformacoesPessoaisActivity, "Foto removida!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    private fun uriToByteArray(uri: Uri): ByteArray? {
        return try {
            contentResolver.openInputStream(uri)?.use { it.readBytes() }
        } catch (e: Exception) { null }
    }

    private fun formatarCpf(cpf: String): String {
        val clean = cpf.replace(Regex("[^\\d]"), "")
        return if (clean.length == 11) "${clean.substring(0, 3)}.${clean.substring(3, 6)}.${clean.substring(6, 9)}-${clean.substring(9, 11)}" else cpf
    }

    private fun calcularIdade(dataNascimento: String): String {
        return try {
            val anoAtual = Calendar.getInstance().get(Calendar.YEAR)
            val anoNasc = if (dataNascimento.contains("-") && dataNascimento.indexOf("-") == 4) dataNascimento.substring(0, 4).toInt() else dataNascimento.takeLast(4).toInt()
            "${anoAtual - anoNasc} anos"
        } catch (e: Exception) { dataNascimento }
    }
}
