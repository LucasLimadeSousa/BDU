package com.example.bdu.usuario

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bdu.R
import com.example.bdu.livros.RecommendationManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial

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

        // Marketing
        val switchMarketing = findViewById<SwitchMaterial>(R.id.switch_marketing)
        val tvStatusMarketing = findViewById<TextView>(R.id.tv_status_marketing)

        // Algoritmo
        val switchAlgoritmo = findViewById<SwitchMaterial>(R.id.switch_algoritmo)
        val tvStatusAlgoritmo = findViewById<TextView>(R.id.tv_status_algoritmo)

        // Privar Favoritos
        val switchPrivarFavoritos = findViewById<SwitchMaterial>(R.id.switch_privar_favoritos)
        val tvStatusPrivarFavoritos = findViewById<TextView>(R.id.tv_status_privar_favoritos)

        val btnLimparGosto = findViewById<MaterialButton>(R.id.btn_limpar_gosto)

        // --- INICIALIZAR ESTADOS ---

        // Marketing
        val isMarketingEnabled = NotificationPrefsManager.isMarketingEnabled(this)
        switchMarketing?.isChecked = isMarketingEnabled
        tvStatusMarketing?.text = if (isMarketingEnabled) "Ativado" else "Desativado"

        // Algoritmo
        val isAlgEnabled = RecommendationManager.isAlgorithmEnabled(this)
        switchAlgoritmo?.isChecked = isAlgEnabled
        tvStatusAlgoritmo?.text = if (isAlgEnabled) "Ativado" else "Desativado"

        // Privar Favoritos
        val isFavPrivate = RecommendationManager.isFavoritesPrivate(this)
        switchPrivarFavoritos?.isChecked = isFavPrivate
        tvStatusPrivarFavoritos?.text = if (isFavPrivate) "Ativado" else "Desativado"

        // --- LISTENERS ---

        switchMarketing?.setOnCheckedChangeListener { _, isChecked ->
            NotificationPrefsManager.setMarketingEnabled(this, isChecked)
            tvStatusMarketing?.text = if (isChecked) "Ativado" else "Desativado"
            val msg = if (isChecked) "Notificações de marketing ativadas" else "Notificações de marketing desativadas"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        switchAlgoritmo?.setOnCheckedChangeListener { _, isChecked ->
            RecommendationManager.setAlgorithmEnabled(this, isChecked)
            tvStatusAlgoritmo?.text = if (isChecked) "Ativado" else "Desativado"
            val msg = if (isChecked) "Recomendações ativadas" else "Recomendações desativadas"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        switchPrivarFavoritos?.setOnCheckedChangeListener { _, isChecked ->
            RecommendationManager.setFavoritesPrivate(this, isChecked)
            tvStatusPrivarFavoritos?.text = if (isChecked) "Ativado" else "Desativado"
            val msg = if (isChecked) "Privacidade de favoritos ativada" else "Privacidade de favoritos desativada"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        btnLimparGosto?.setOnClickListener {
            RecommendationManager.clearInterests(this)
            Toast.makeText(this, "Gosto literário reiniciado!", Toast.LENGTH_SHORT).show()
        }

        btnBack?.setOnClickListener {
            finish()
        }
    }
}