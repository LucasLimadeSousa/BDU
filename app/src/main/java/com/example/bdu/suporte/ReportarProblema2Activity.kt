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

        val itemDamage = findViewById<View>(R.id.itemDamage)
        val itemContent = findViewById<View>(R.id.itemContent)
        val itemInfo = findViewById<View>(R.id.itemInfo)
        val itemOther = findViewById<View>(R.id.itemOther)

        val reportIntent = Intent(this, ReportarProblemaActivity::class.java)

        itemDamage?.setOnClickListener { startActivity(reportIntent) }
        itemContent?.setOnClickListener { startActivity(reportIntent) }
        itemInfo?.setOnClickListener { startActivity(reportIntent) }
        itemOther?.setOnClickListener { startActivity(reportIntent) }

        val btnFila = findViewById<ImageButton?>(R.id.btn_nav_fila)
        val btnMeusLivros = findViewById<ImageButton?>(R.id.btn_nav_meuslivros)
        val btnHome = findViewById<ImageButton?>(R.id.btn_nav_home)
        val btnDesejos = findViewById<ImageButton?>(R.id.btn_nav_desejos)
        val btnPerfil = findViewById<ImageButton?>(R.id.btn_nav_perfil)

        btnFila?.setOnClickListener {
            val intent = Intent(this, ListadeEsperaActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnMeusLivros?.setOnClickListener {
            val intent = Intent(this, MeusLivrosActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnHome?.setOnClickListener {
            val intent = Intent(this, TelahomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnDesejos?.setOnClickListener {
            val intent = Intent(this, ListaDesejosActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnPerfil?.setOnClickListener {
            val intent = Intent(this, MeuPerfilActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
