package com.example.bdu

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity

class SelecionarMetodoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.pagamentos_selecionar_metodo)

        findViewById<Button>(R.id.btn_pix).setOnClickListener{
            startActivity(Intent(this, R.layout.pagamentos_pix::class.java))
        }
        findViewById<Button>(R.id.btn_cartaoCred).setOnClickListener{
            startActivity(Intent(this, R.layout.pagamentos_cartao_cred::class.java))
        }

        findViewById<Button>(R.id.btn_cartaoDeb).setOnClickListener{
            startActivity(Intent(this, R.layout.pagamentos_cartao_cred::class.java))
        }

        findViewById<Button>(R.id.btn_boleto).setOnClickListener{
            startActivity(Intent(this, R.layout.pagamentos_boleto::class.java))
        }

        findViewById<ImageButton>(R.id.imgBtnVoltar).setOnClickListener{
            startActivity(Intent(this, R.layout.usuario_meu_perfil::class.java))
        }
    }


 }
