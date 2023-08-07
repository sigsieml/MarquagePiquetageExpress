package fr.sieml.marquagepiquetage.pdf

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
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
    val image = AnimatedImageVector.animatedVectorResource(R.drawable.tractopelleanim)
    var atEnd by remember { mutableStateOf(false) }
    val painter = rememberAnimatedVectorPainter(image, atEnd)
    var progress by remember { mutableStateOf(0.1f) }
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
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth()
                .padding(bottom = 120.dp)
                .height(32.dp))
        }
    }

    LaunchedEffect(key1 = Unit, block = {
        var acceleration =0.0f
        while (progress < 0.9f) {
            progress += acceleration
            acceleration += 0.0001f
            delay(10)
        }
    })

    LaunchedEffect(key1 = Unit, block = {
        atEnd = true
        val marquageCreator = AndroidPdfMarquageCreator(context)
        val file = withContext(Dispatchers.IO) {
            marquageCreator.createPdfOnInternalStorage(marquage)
        }
        progress = 1f
        delay(100)

        if (this.isActive) {
            onPdfCreated(file)
        }
    })

}