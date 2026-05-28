package com.example.bdu.usuario

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bdu.R

import android.widget.TextView
import android.widget.Toast
import com.example.bdu.livros.RecommendationManager
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.button.MaterialButton

class PrivacidadeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.usuario_privacidade)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBack = findViewById<ImageButton>(R.id.btn_backReturn)
        val switchAlgoritmo = findViewById<SwitchMaterial>(R.id.switch_algoritmo)
        val tvStatusAlgoritmo = findViewById<TextView>(R.id.tv_status_algoritmo)
        val switchPrivarFavoritos = findViewById<SwitchMaterial>(R.id.switch_privar_favoritos)
        val tvStatusPrivarFavoritos = findViewById<TextView>(R.id.tv_status_privar_favoritos)
        val btnLimparGosto = findViewById<MaterialButton>(R.id.btn_limpar_gosto)

        // Inicializar estado do Switch Algoritmo
        val isEnabled = RecommendationManager.isAlgorithmEnabled(this)
        switchAlgoritmo?.isChecked = isEnabled
        tvStatusAlgoritmo?.text = if (isEnabled) "Ativado" else "Desativado"

        // Inicializar estado do Switch Privar Favoritos
        val isFavPrivate = RecommendationManager.isFavoritesPrivate(this)
        switchPrivarFavoritos?.isChecked = isFavPrivate
        tvStatusPrivarFavoritos?.text = if (isFavPrivate) "Ativado" else "Desativado"

        // Listener do Switch Algoritmo
        switchAlgoritmo?.setOnCheckedChangeListener { _, isChecked ->
            RecommendationManager.setAlgorithmEnabled(this, isChecked)
            tvStatusAlgoritmo?.text = if (isChecked) "Ativado" else "Desativado"
            val msg = if (isChecked) "Recomendações ativadas" else "Recomendações desativadas"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        // Listener do Switch Privar Favoritos
        switchPrivarFavoritos?.setOnCheckedChangeListener { _, isChecked ->
            RecommendationManager.setFavoritesPrivate(this, isChecked)
            tvStatusPrivarFavoritos?.text = if (isChecked) "Ativado" else "Desativado"
            val msg = if (isChecked) "Privacidade de favoritos ativada" else "Privacidade de favoritos desativada"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        // Botão Limpar Gosto
        btnLimparGosto?.setOnClickListener {
            RecommendationManager.clearInterests(this)
            Toast.makeText(this, "Gosto literário reiniciado!", Toast.LENGTH_SHORT).show()
        }

        // Configuração do botão voltar padrão
        btnBack?.setOnClickListener {
            finish()
        }
    }
}

