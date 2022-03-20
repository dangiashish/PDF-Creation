package com.codebyashish.pdfcreation;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    boolean doublePress = false;
    Button btnCreate;
    public final int REQUEST_CODE = 100;
    int pageWidth = 720;
    int pageHeight = 1200;
    Bitmap imageBitmap, scaledImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.header_image);
        scaledImageBitmap = Bitmap.createScaledBitmap(imageBitmap, 720, 257, false);

        btnCreate = findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (
                        ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED
                                &&
                                ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE)
                                        == PackageManager.PERMISSION_GRANTED
                ) {
                    createPDF();
                } else{
                    requestAllPermission();
                }
            }
        });

    }

    private void createPDF() {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();


        canvas.drawBitmap(scaledImageBitmap, 0, 0, paint);


        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(50);
        paint.setColor(getResources().getColor(android.R.color.holo_blue_bright));
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("CodeByAshish", pageWidth/2, 200, paint);



        pdfDocument.finishPage(page);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "/CodeByAshish"  + ".pdf");

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "PDF saved successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        pdfDocument.close();



    }

    private void requestAllPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{ READ_EXTERNAL_STORAGE,
                WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (doublePress){
            super.onBackPressed();
            return;
        }
        this.doublePress = true;
        Toast.makeText(this, "Press again to exit app", Toast.LENGTH_SHORT).show();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                doublePress = false;
            }
        }, 2000);
    }

}