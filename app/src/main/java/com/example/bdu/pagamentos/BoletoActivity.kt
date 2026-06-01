package com.example.bdu.pagamentos

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bdu.R
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.Locale

class BoletoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.pagamentos_boleto)

        val mainView = findViewById<View>(R.id.main)
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        val tvValorTotal = findViewById<TextView>(R.id.textViewValorTotal2)
        val tvLinhaDigitavel = findViewById<TextView>(R.id.textView25)
        val layoutCopy = findViewById<LinearLayout>(R.id.layoutCopyBoleto)
        val btnBaixarPdf = findViewById<View>(R.id.btnBaixarPdf)
        val imgBtnVoltar = findViewById<ImageButton>(R.id.imgBtnVoltarBoleto)

        // 1. Configurar o Valor Total vindo do PaymentManager
        val valorFormatado = String.format(Locale.getDefault(), "Valor total: R$%.2f", PaymentManager.divida)
        tvValorTotal.text = valorFormatado

        // 2. Lógica de Copiar Linha Digitável
        layoutCopy.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Boleto BDU", tvLinhaDigitavel.text.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Linha digitável copiada!", Toast.LENGTH_SHORT).show()
        }

        // 3. Lógica de Gerar e Baixar PDF
        btnBaixarPdf.setOnClickListener {
            gerarPdfBoleto(tvLinhaDigitavel.text.toString(), valorFormatado)
        }

        imgBtnVoltar.setOnClickListener {
            finish()
        }
    }

    private fun gerarPdfBoleto(linhaDigitavel: String, valor: String) {
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val titlePaint = Paint()

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        titlePaint.color = Color.parseColor("#003399")
        titlePaint.textSize = 24f
        titlePaint.isFakeBoldText = true
        canvas.drawText("Biblioteca Digital UNIFOR - BDU", 50f, 50f, titlePaint)

        paint.color = Color.BLACK
        paint.textSize = 14f
        canvas.drawText("Recibo de Cobrança - Boleto Bancário", 50f, 80f, paint)
        canvas.drawLine(50f, 90f, 545f, 90f, paint)

        paint.textSize = 12f
        canvas.drawText("Beneficiário: Fundação Edson Queiroz", 50f, 120f, paint)
        canvas.drawText("CNPJ: 07.373.434/0001-86", 50f, 140f, paint)

        paint.isFakeBoldText = true
        canvas.drawText("LINHA DIGITÁVEL:", 50f, 180f, paint)
        paint.isFakeBoldText = false
        canvas.drawText(linhaDigitavel, 50f, 200f, paint)

        paint.isFakeBoldText = true
        paint.textSize = 16f
        canvas.drawText(valor, 50f, 240f, paint)

        paint.textSize = 10f
        paint.color = Color.GRAY
        canvas.drawText("Este é um boleto gerado eletronicamente pelo aplicativo BDU.", 50f, 300f, paint)
        canvas.drawText("Validade de 3 dias úteis após a emissão.", 50f, 315f, paint)

        pdfDocument.finishPage(page)

        val fileName = "Boleto_BDU_${System.currentTimeMillis()}.pdf"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
        }

        val resolver = contentResolver
        val uriFinal: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
        }

        try {
            if (uriFinal != null) {
                val outputStream: OutputStream? = resolver.openOutputStream(uriFinal)
                if (outputStream != null) {
                    pdfDocument.writeTo(outputStream)
                    outputStream.close()
                    Toast.makeText(this, "PDF salvo na pasta Downloads!", Toast.LENGTH_LONG).show()
                    abrirPdf(uriFinal)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                val fallbackFile = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
                val fallbackUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", fallbackFile)
                pdfDocument.writeTo(FileOutputStream(fallbackFile))
                abrirPdf(fallbackUri)
            } catch (ex: Exception) {
                ex.printStackTrace()
                Toast.makeText(this, "Erro ao gerar PDF: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } finally {
            pdfDocument.close()
        }
    }

    private fun abrirPdf(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

        try {
            val chooser = Intent.createChooser(intent, "Abrir Boleto")
            startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(this, "Instale um leitor de PDF para visualizar o arquivo.", Toast.LENGTH_LONG).show()
        }
    }
}