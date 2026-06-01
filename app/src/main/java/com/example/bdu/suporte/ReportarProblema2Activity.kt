package com.example.bdu.suporte

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bdu.R
import com.example.bdu.livros.ListaDesejosActivity
import com.example.bdu.livros.ListadeEsperaActivity
import com.example.bdu.livros.MeusLivrosActivity
import com.example.bdu.livros.TelahomeActivity
import com.example.bdu.usuario.MeuPerfilActivity

class ReportarProblema2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.suporte_reportar_problema_2)

        val buttonBack = findViewById<View>(R.id.btnBack)

        val itemDamage = findViewById<View>(R.id.itemDamage)
        val itemContent = findViewById<View>(R.id.itemContent)
        val itemInfo = findViewById<View>(R.id.itemInfo)
        val itemOther = findViewById<View>(R.id.itemOther)

        val reportIntent = Intent(this, ReportarProblemaActivity::class.java)

        itemDamage?.setOnClickListener { 
            reportIntent.putExtra("TIPO_PROBLEMA", "Dano físico")
            startActivity(reportIntent) 
        }
        itemContent?.setOnClickListener { 
            reportIntent.putExtra("TIPO_PROBLEMA", "Conteúdo do livro")
            startActivity(reportIntent) 
        }
        itemInfo?.setOnClickListener { 
            reportIntent.putExtra("TIPO_PROBLEMA", "Informações incorretas")
            startActivity(reportIntent) 
        }
        itemOther?.setOnClickListener { 
            reportIntent.putExtra("TIPO_PROBLEMA", "Outro")
            startActivity(reportIntent) 
        }

        buttonBack?.setOnClickListener {
            val intent = Intent(this, MeusLivrosActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
