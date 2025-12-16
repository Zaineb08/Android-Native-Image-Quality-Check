package com.example.tessilite

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private lateinit var imageView: ImageView
    private lateinit var button: Button

    // 1. Lanceur de la Caméra (Ce qui se passe quand la photo est prise)
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        if (bitmap != null) {
            // ASTUCE SAMSUNG : On convertit l'image en format standard (ARGB_8888)
            // Sinon le C++ va planter car il ne peut pas lire le format "Hardware" de Samsung
            val cleanBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

            imageView.setImageBitmap(cleanBitmap)
            analyzeImage(cleanBitmap)
        } else {
            textView.text = "Aucune photo prise"
        }
    }

    // 2. Lanceur de Permission (Ce qui se passe quand l'utilisateur répond au popup)
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            // S'il dit OUI, on lance la caméra tout de suite
            takePictureLauncher.launch(null)
        } else {
            // S'il dit NON, on affiche un message
            Toast.makeText(this, "Permission caméra nécessaire !", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.sample_text)
        button = findViewById(R.id.btn_check)
        imageView = findViewById(R.id.imageView)

        button.text = "Prendre une Photo"

        button.setOnClickListener {
            checkPermissionAndOpenCamera()
        }
    }

    private fun checkPermissionAndOpenCamera() {
        // On vérifie si on a DÉJÀ la permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Oui -> On ouvre la caméra
            takePictureLauncher.launch(null)
        } else {
            // Non -> On affiche le popup de demande
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun analyzeImage(bitmap: Bitmap) {
        // Appel à la fonction C++
        val isGood = checkBrightnessNative(bitmap)

        if (isGood) {
            textView.text = "Analyse C++ : Photo Valide ✅"
            textView.setTextColor(Color.GREEN)
        } else {
            textView.text = "Analyse C++ : Trop Sombre ! ❌"
            textView.setTextColor(Color.RED)
        }
    }

    // Lien avec le C++
    external fun checkBrightnessNative(bitmap: Bitmap): Boolean

    companion object {
        init {
            System.loadLibrary("tessilite")
        }
    }
}