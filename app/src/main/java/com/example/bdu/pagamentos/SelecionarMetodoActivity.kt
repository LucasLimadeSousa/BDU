package com.example.bdu.pagamentos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.bdu.R

class SelecionarMetodoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pagamentos_selecionar_metodo)

        findViewById<Button>(R.id.btn_pix).setOnClickListener {
            startActivity(Intent(this, PixActivity::class.java))
        }
        findViewById<Button>(R.id.btn_cartaoCred).setOnClickListener {
            startActivity(Intent(this, CartaoCredActivity::class.java))
        }

        findViewById<Button>(R.id.btn_cartaoDeb).setOnClickListener {
            startActivity(Intent(this, CartaoDebActivity::class.java))
        }

        findViewById<Button>(R.id.btn_boleto).setOnClickListener {
            startActivity(Intent(this, BoletoActivity::class.java))
        }

        findViewById<ImageButton>(R.id.imgBtnVoltar).setOnClickListener {
            finish()
        }
    }
}
