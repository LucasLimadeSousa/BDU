package com.example.bdu.pagamentos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.bdu.R

class SelecionarMetodoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pagamentos_selecionar_metodo)

        // As opções agora são ConstraintLayouts clicáveis no XML
        findViewById<View>(R.id.btn_pix).setOnClickListener {
            startActivity(Intent(this, PixActivity::class.java))
        }
        
        findViewById<View>(R.id.btn_cartaoCred).setOnClickListener {
            startActivity(Intent(this, CartaoCredActivity::class.java))
        }

        findViewById<View>(R.id.btn_cartaoDeb).setOnClickListener {
            startActivity(Intent(this, CartaoDebActivity::class.java))
        }

        findViewById<View>(R.id.btn_boleto).setOnClickListener {
            startActivity(Intent(this, BoletoActivity::class.java))
        }

        findViewById<ImageButton>(R.id.imgBtnVoltar).setOnClickListener {
            finish()
        }
    }
}
