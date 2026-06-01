package com.example.bdu.suporte

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bdu.R
import com.example.bdu.network.SupabaseConfig
import com.example.bdu.model.Usuario
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Returning
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.*

class ReportarProblemaActivity : AppCompatActivity() {

    private var imageBytes: ByteArray? = null
    private lateinit var flImagePlaceholder: FrameLayout
    private lateinit var ivCapturedImage: ImageView
    private lateinit var ivPlaceholderIcon: ImageView

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            contentResolver.openInputStream(it)?.use { stream ->
                updateImage(stream.readBytes())
            }
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            val stream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.JPEG, 80, stream)
            updateImage(stream.toByteArray())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.suporte_reportar_problema)
        
        flImagePlaceholder = findViewById(R.id.flImagePlaceholder)
        ivCapturedImage = findViewById(R.id.ivCapturedImage)
        ivPlaceholderIcon = findViewById(R.id.ivPlaceholderIcon)
        
        val tipoProblema = intent.getStringExtra("TIPO_PROBLEMA")
        if (tipoProblema != null) {
            findViewById<TextView>(R.id.tvProblemType).text = tipoProblema
        }
        
        flImagePlaceholder.setOnClickListener { mostrarDialogoOpcoesFoto() }

        findViewById<ImageButton>(R.id.btnBackLivro).setOnClickListener { finish() }

        findViewById<Button>(R.id.btnReport).setOnClickListener {
            enviarRelatorio()
        }
    }

    private fun updateImage(bytes: ByteArray?) {
        imageBytes = bytes
        if (bytes != null) {
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            ivCapturedImage.setImageBitmap(bitmap)
            ivCapturedImage.visibility = View.VISIBLE
            ivPlaceholderIcon.visibility = View.GONE
            Toast.makeText(this, "Imagem carregada!", Toast.LENGTH_SHORT).show()
        } else {
            ivCapturedImage.setImageDrawable(null)
            ivCapturedImage.visibility = View.GONE
            ivPlaceholderIcon.visibility = View.VISIBLE
            Toast.makeText(this, "Imagem removida!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarDialogoOpcoesFoto() {
        AlertDialog.Builder(this)
            .setTitle("Foto do problema")
            .setItems(arrayOf("Tirar foto", "Carregar imagem", "Remover foto")) { _, which ->
                when (which) {
                    0 -> takePictureLauncher.launch(null)
                    1 -> pickImageLauncher.launch("image/*")
                    2 -> updateImage(null)
                }
            }.show()
    }

    private fun enviarRelatorio() {
        val descricao = findViewById<EditText>(R.id.etDescription).text.toString()
        if (descricao.isEmpty()) {
            Toast.makeText(this, "Descreva o problema", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val user = SupabaseConfig.client.auth.currentUserOrNull()
                var userName = "Usuário Anônimo"
                
                if (user?.email != null) {
                    val usuarioDados = SupabaseConfig.client.postgrest["Dados_Usuario"]
                        .select(columns = Columns.ALL) {
                            filter { eq("email", user.email!!) }
                        }.decodeSingle<Usuario>()
                    userName = usuarioDados.nome ?: "Usuário sem nome"
                }

                var imageUrl = ""

                if (imageBytes != null) {
                    val fileName = "${UUID.randomUUID()}.jpg"
                    SupabaseConfig.client.storage.from("relatorios").upload(fileName, imageBytes!!)
                    imageUrl = SupabaseConfig.client.storage.from("relatorios").publicUrl(fileName)
                }

                SupabaseConfig.client.from("Relatorios").insert(
                    mapOf(
                        "usuario_name" to userName,
                        "descricao" to descricao,
                        "imagem_url" to imageUrl
                    )
                )

                Toast.makeText(this@ReportarProblemaActivity, "Relatório enviado!", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                // Altere para isso para ver a mensagem completa do erro no Logcat
                e.printStackTrace()
                Toast.makeText(this@ReportarProblemaActivity, "Erro completo: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
