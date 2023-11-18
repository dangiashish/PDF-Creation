package com.codebyashish.pdfcreation

import android.Manifest.permission
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var btnCreate: Button? = null
    private val REQUESTCODE = 100
    private var pageWidth = 720
    private var pageHeight = 1200
    private var imageBitmap: Bitmap? =  null
    private var scaledImageBitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageBitmap = BitmapFactory.decodeResource(resources, R.drawable.header_image)
        scaledImageBitmap = imageBitmap?.let { Bitmap.createScaledBitmap(it, 720, 257, false) }
        btnCreate = findViewById(R.id.btnCreate)

        btnCreate?.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        applicationContext,
                        permission.READ_MEDIA_IMAGES
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    createPDF()
                } else {
                    requestAllPermission()
                }
            } else {
                if (ContextCompat.checkSelfPermission(
                        applicationContext,
                        permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        applicationContext,
                        permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    createPDF()
                } else {
                    requestAllPermission()
                }
            }
        }
    }

    private fun createPDF() {
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val pageInfo = PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        canvas.drawBitmap(scaledImageBitmap!!, 0f, 0f, paint)
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 50f
        paint.color = resources.getColor(android.R.color.holo_blue_bright, null)
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
        canvas.drawText("CodeByAshish", (pageWidth / 2).toFloat(), 200f, paint)
        val line = Paint()
        line.strokeWidth = 2f

        // horizontal lines
        canvas.drawLine(150f, 350f, 550f, 350f, line)
        canvas.drawLine(150f, 400f, 550f, 400f, line)
        canvas.drawLine(150f, 450f, 550f, 450f, line)
        canvas.drawLine(150f, 500f, 550f, 500f, line)
        canvas.drawLine(150f, 550f, 550f, 550f, line)


        // vertical lines
        canvas.drawLine(150f, 350f, 150f, 550f, line)
        canvas.drawLine(350f, 350f, 350f, 550f, line)
        canvas.drawLine(550f, 350f, 550f, 550f, line)
        pdfDocument.finishPage(page)
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "/CodeByAshish" + ".pdf"
        )
        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(this, "PDF saved to " + file.absolutePath, Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        pdfDocument.close()
    }


    private fun requestAllPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf<String>(permission.READ_MEDIA_IMAGES),
                REQUESTCODE
            )
        } else {
            ActivityCompat.requestPermissions(
                this@MainActivity, arrayOf(
                    permission.READ_EXTERNAL_STORAGE,
                    permission.WRITE_EXTERNAL_STORAGE
                ), REQUESTCODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUESTCODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

}