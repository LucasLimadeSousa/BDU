package com.example.bdu.pagamentos

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bdu.R
import java.util.Locale

class PixActivity : AppCompatActivity() {

    private lateinit var tvChavePix: TextView
    private lateinit var imageViewQrCode: ImageView
    private var pixPayload: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.pagamentos_pix)

        val mainView = findViewById<android.view.View>(R.id.main)
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        val btnVoltar = findViewById<ImageButton>(R.id.btnVoltarPix)
        tvChavePix = findViewById(R.id.textView25)
        imageViewQrCode = findViewById(R.id.imageView8)
        val layoutCopy = findViewById<LinearLayout>(R.id.layoutCopyPix)

        // Gerar o payload dinâmico com o valor do PaymentManager
        pixPayload = generatePixPayload(PaymentManager.divida)

        // Atualizar o texto da chave na tela
        tvChavePix.text = pixPayload

        // Carregar o QR Code dinâmico
        carregarQrCodeDinamico(pixPayload)

        btnVoltar.setOnClickListener {
            finish()
        }

        layoutCopy.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Chave PIX BDU", pixPayload)
            clipboard.setPrimaryClip(clip)
            val valorMsg = String.format(Locale.getDefault(), "%.2f", PaymentManager.divida)
            Toast.makeText(this, "Chave PIX copiada com valor R$ $valorMsg", Toast.LENGTH_SHORT).show()
        }
    }

    private fun carregarQrCodeDinamico(payload: String) {
        // Usamos uma API pública para gerar o QR Code com o valor real embutido
        val qrCodeUrl = "https://api.qrserver.com/v1/create-qr-code/?size=500x500&data=${java.net.URLEncoder.encode(payload, "UTF-8")}"

        Thread {
            try {
                val url = java.net.URL(qrCodeUrl)
                val connection = url.openConnection() as java.net.HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                val bitmap = android.graphics.BitmapFactory.decodeStream(input)
                runOnUiThread {
                    imageViewQrCode.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    // Fallback para a imagem local caso esteja offline
                    imageViewQrCode.setImageResource(R.drawable.qrcode_pix)
                }
            }
        }.start()
    }

    private fun generatePixPayload(valor: Double): String {
        val chavePix = "fd35b02b-ae03-4fbf-8e88-0ac9bb1615f2"
        val nomeRecebedor = "JOAO T BARROSO"
        val cidadeRecebedor = "AQUIRAZ"
        val valorFormatado = String.format(Locale.US, "%.2f", valor)

        // Montagem do Payload PIX (Padrão EMV BR Code)
        var payload = "000201" // Payload Format Indicator
        payload += "010211"   // Point of Initiation Method

        // Merchant Account Information (26)
        val merchantInfo = "0014br.gov.bcb.pix" + "01${chavePix.length}${chavePix}"
        payload += "26${String.format(Locale.US, "%02d", merchantInfo.length)}$merchantInfo"

        payload += "52040000" // Merchant Category Code
        payload += "5303986"  // Transaction Currency (986 = BRL)

        // Transaction Amount (54) - Valor dinâmico
        payload += "54${String.format(Locale.US, "%02d", valorFormatado.length)}$valorFormatado"

        payload += "5802BR"   // Country Code

        // Merchant Name (59)
        payload += "59${String.format(Locale.US, "%02d", nomeRecebedor.length)}$nomeRecebedor"

        // Merchant City (60)
        payload += "60${String.format(Locale.US, "%02d", cidadeRecebedor.length)}$cidadeRecebedor"

        // Additional Data Field (62)
        val additionalData = "0503***"
        payload += "62${String.format(Locale.US, "%02d", additionalData.length)}$additionalData"

        // CRC16 (63)
        payload += "6304"
        payload += calculateCRC16(payload)

        return payload
    }

    private fun calculateCRC16(data: String): String {
        var crc = 0xFFFF
        val polynomial = 0x1021

        for (b in data.toByteArray()) {
            for (i in 0..7) {
                val bit = b.toInt() shr 7 - i and 1 == 1
                val c15 = crc shr 15 and 1 == 1
                crc = crc shl 1
                if (c15 xor bit) crc = crc xor polynomial
            }
        }

        crc = crc and 0xFFFF
        return String.format(Locale.US, "%04X", crc).uppercase()
    }
}