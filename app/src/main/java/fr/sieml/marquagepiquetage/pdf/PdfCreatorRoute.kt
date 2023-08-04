package fr.sieml.marquagepiquetage.pdf

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import fr.sieml.marquagepiquetage.Marquage.Marquage
import fr.sieml.marquagepiquetage.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun PdfCreatorRoute(marquage: Marquage, onPdfCreated: (File) -> Unit) {
    val context = LocalContext.current
    val image = AnimatedImageVector.animatedVectorResource(R.drawable.avd_anim)
    var atEnd by remember { mutableStateOf(false) }
    val painter = rememberAnimatedVectorPainter(image, atEnd)
    var isAnimationFinished by remember { mutableStateOf(false) }
    Surface(modifier = Modifier
        .fillMaxWidth()
        .wrapContentWidth(align = Alignment.CenterHorizontally)
        .wrapContentHeight(align = Alignment.CenterVertically)
        .widthIn(max = 840.dp)) {
        Image(
            painter = painter,
            contentDescription = "Splash Screen",
            contentScale = ContentScale.FillHeight
        )
        if(isAnimationFinished){
            // Ajoutez votre UI pour l'écran de chargement ici
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator() // Un simple spinner de chargement
            }
        }
    }

    LaunchedEffect(key1 = Unit, block = {
        delay(2000)
        isAnimationFinished = true
    })

    LaunchedEffect(key1 = Unit, block = {
        atEnd = true
        val marquageCreator = fr.sieml.marquagepiquetage.pdf.AndroidPdfMarquageCreator(context)
        val file = withContext(Dispatchers.IO) {
            marquageCreator.createPdfOnInternalStorage(marquage)
        }

        if (this.isActive) {
            onPdfCreated(file)
        }
    })

}