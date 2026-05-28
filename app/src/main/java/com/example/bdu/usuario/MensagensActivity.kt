package com.example.bdu.usuario

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bdu.R
import com.example.bdu.livros.RentalManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MensagensActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.usuario_mensagens)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<android.view.View>(R.id.btnVoltar).setOnClickListener {
            finish()
        }

        popularMensagens()
    }

    private fun popularMensagens() {
        val container = findViewById<LinearLayout>(R.id.container_mensagens)
        val inflater = LayoutInflater.from(this)
        
        // Obtém o horário atual de acesso
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Calendar.getInstance().time)

        // 1. Mensagem de Bem-vindo (Sempre aparece)
        adicionarMensagem(container, inflater, 
            "Biblioteca Digital UNIFOR", 
            "Bem vindo(a) a Biblioteca Digital UNIFOR (BDU)", 
            currentTime
        )

        // 2. Mensagem de Aluguel (Só aparece se houver aluguel na sessão)
        if (RentalManager.hasRentedInSession()) {
            adicionarMensagem(container, inflater, 
                "Biblioteca Digital UNIFOR", 
                "Você alugou um livro, se divirta!", 
                currentTime
            )
        }

        // 3. Mensagem de Atraso (Só aparece se houver livro atrasado no histórico)
        if (RentalManager.hasOverdueBook(this)) {
            adicionarMensagem(container, inflater, 
                "Biblioteca Digital UNIFOR", 
                "está em atraso a entrega de seu livro", 
                currentTime
            )
        }
    }

    private fun adicionarMensagem(container: LinearLayout, inflater: LayoutInflater, title: String, body: String, time: String) {
        val view = inflater.inflate(R.layout.item_mensagem, container, false)
        view.findViewById<TextView>(R.id.msg_title).text = title
        view.findViewById<TextView>(R.id.msg_body).text = body
        view.findViewById<TextView>(R.id.msg_time).text = time
        container.addView(view)
    }
}
