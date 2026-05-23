package com.example.bdu.usuario

import Usuario
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.bdu.R
import com.example.bdu.network.SupabaseConfig
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InformacoesPessoaisActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.usuario_informacoes_pessoais)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<View>(R.id.btnBackLivro).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        carregarInformacoesDoSupabase()
    }

    private fun carregarInformacoesDoSupabase(){
        lifecycleScope.launch {
            try {

                val usuarioLogado = SupabaseConfig.client.auth.currentUserOrNull()
                val emailLogado = usuarioLogado?.email

                if (emailLogado != null){

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
                    Toast.makeText(this@InformacoesPessoaisActivity, "Usuario não autenticado", Toast.LENGTH_SHORT).show()
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
        val tvDataNasc = findViewById<TextView>(R.id.tvValueDataNascimento)
        val tvCidade = findViewById<TextView>(R.id.tvValueCidade)
        val tvEstado = findViewById<TextView>(R.id.tvValueEstado)
        val tvCurso = findViewById<TextView>(R.id.tvValueCurso)

        tvNome.text = usuario.nome
        tvEmail.text = usuario.email
        tvCpf.text = formatarCpf(usuario.cpf)
        tvTelefone.text = usuario.telefone
        tvDataNasc.text = usuario.data_nascimento
        tvCidade.text = usuario.cidade
        tvEstado.text = usuario.estado
        tvCurso.text = usuario.curso
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