package com.example.bdu.usuario

import Usuario
import android.graphics.BitmapFactory
import android.graphics.Color
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.bdu.R
import com.example.bdu.livros.TelahomeActivity
import com.example.bdu.login.LoginActivity
import com.example.bdu.network.SupabaseConfig
import com.example.bdu.pagamentos.JurosMultaActivity
import com.example.bdu.suporte.TermosCondicoesActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MeuPerfilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.usuario_meu_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        carregarDadosUsuario()

        findViewById<ImageButton>(R.id.btn_nav_fila).setOnClickListener {
            startActivity(Intent(this, com.example.bdu.livros.ListadeEsperaActivity::class.java))
        }

        findViewById<ImageButton>(R.id.btn_nav_meuslivros).setOnClickListener {
            startActivity(Intent(this, com.example.bdu.livros.MeusLivrosActivity::class.java))
        }

        findViewById<ImageButton>(R.id.btn_nav_home).setOnClickListener {
            startActivity(Intent(this, TelahomeActivity::class.java))
        }

        findViewById<ImageButton>(R.id.btn_nav_desejos).setOnClickListener {
            startActivity(Intent(this, com.example.bdu.livros.ListaDesejosActivity::class.java))
        }

        findViewById<ImageButton>(R.id.btn_nav_perfil).setOnClickListener {
            // Já estamos no perfil
        }

        findViewById<TextView>(R.id.textView11).setOnClickListener {
            val intent = Intent(this, InformacoesPessoaisActivity::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.textView12).setOnClickListener {
            val intent = Intent(this, MensagensActivity::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.textView13).setOnClickListener {
            val intent = Intent(this, HistoricoDeLivrosActivity::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.textView14).setOnClickListener {
            val intent = Intent(this, PrivacidadeActivity::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.textView15).setOnClickListener {
            val intent = Intent(this, AlterarSenhaActivity::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.textView16).setOnClickListener {
            val intent = Intent(this, JurosMultaActivity::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.textView17).setOnClickListener {
            val intent = Intent(this, TermosCondicoesActivity::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.textView18).setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
            builder.setMessage("Tem certeza que deseja sair da sua Conta?")

            builder.setPositiveButton("Sim") { _, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()

            // Personalizando as cores dos botões para corresponder ao estilo da tela de alterar senha
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
    }

    override fun onResume() {
        super.onResume()
        carregarDadosUsuario()
    }

    private fun carregarDadosUsuario(){
        lifecycleScope.launch {
            try {
                val usuarioLogado = SupabaseConfig.client.auth.currentUserOrNull()
                val emailLogado = usuarioLogado?.email

                if (emailLogado != null) {
                    val usuarioDados = withContext(Dispatchers.IO) {
                        SupabaseConfig.client.postgrest["Dados_Usuario"]
                            .select(columns = Columns.ALL) {
                                filter {
                                    eq("email", emailLogado)
                                }
                            }.decodeSingle<Usuario>()
                    }

                    findViewById<TextView>(R.id.textView10).text = usuarioDados.nome

                    // Faz o download da foto caso ela exista no banco
                    if (!usuarioDados.foto.isNullOrEmpty()) {
                        try {
                            val bytes = withContext(Dispatchers.IO) {
                                SupabaseConfig.client.storage.from("avatars").downloadPublic(usuarioDados.foto!!)
                            }
                            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            if (bitmap != null) {
                                val ivProfile = findViewById<ImageView>(R.id.imageView6)
                                ivProfile.setImageBitmap(bitmap)
                                ivProfile.imageTintList = null
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
            }
        } catch (e : Exception){
            e.printStackTrace()
            }
        }
    }
}