package fr.sieml.marquagepiquetage

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import fr.sieml.marquagepiquetage.ui.theme.MarquagePiquetageTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                }
                permissions.getOrDefault(Manifest.permission.WRITE_EXTERNAL_STORAGE, false) -> {
                    // Write permission granted
                }
                permissions.getOrDefault(Manifest.permission.READ_EXTERNAL_STORAGE, false) -> {
                    // Read permission granted
                }
                else -> {
                    // Permission denied.
                    Toast.makeText(this, "L'application a besoin des permissions pour fonctionner", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE))


        setContent {
            MarquagePiquetageTheme {
                MarquagePiquetageNavHost()
            }
        }
    }
}