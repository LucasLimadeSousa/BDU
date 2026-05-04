package com.example.bdu.usuario

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bdu.R
import com.example.bdu.livros.TelahomeActivity
import com.example.bdu.login.LoginActivity
import com.example.bdu.pagamentos.JurosMultaActivity
import com.example.bdu.suporte.TermosCondicoesActivity

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
            val intent = Intent(this, LoginActivity::class.java)
            // Limpa o histórico de telas para que o usuário não volte ao perfil apertando o botão 'voltar'
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}