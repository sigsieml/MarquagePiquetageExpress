package com.tlbail.marquagepiquetage.pdf

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.tlbail.marquagepiquetage.Marquage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun PdfCreatorRoute(marquage: Marquage, onPdfCreated: (File) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        val marquageCreator = AndroidPdfMarquageCreator(context)
        val file = withContext(Dispatchers.IO) {
            marquageCreator.createPdfOnInternalStorage(marquage)
        }

        if (this.isActive) {
            onPdfCreated(file)
        }
    }

    // Ajoutez votre UI pour l'écran de chargement ici
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator() // Un simple spinner de chargement
    }
}