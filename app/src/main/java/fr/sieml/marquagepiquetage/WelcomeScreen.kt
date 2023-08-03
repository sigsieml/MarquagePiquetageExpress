package fr.sieml.marquagepiquetage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun WelcomeScreen(navigateToCreateAttestation: () -> Unit, navigateToSieml: () -> Unit) {
    val elevation = 8.dp // Définir l'élévation désirée ici
    val radius = 16.dp // Définir le rayon de l'arrondi des bords ici

    val imageModifier = Modifier
        .graphicsLayer(shadowElevation = with(LocalDensity.current) { elevation.toPx() })
        .size(200.dp - elevation)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(normalPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = imageModifier.clip(shape = RoundedCornerShape(radius)),
                shape = RoundedCornerShape(radius),
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground), // Remplacez par le logo de votre application
                    contentDescription = "Logo de Marquage piquetage Express",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.height(largePadding))
            Text(
                text = "Marquage Piquetage Express",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(normalPadding))
            Text(
                text = "Créez et envoyez facilement vos attestations de marquage piquetage au SIEMl en quelques clics",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(largePadding))
            Button(
                onClick = navigateToCreateAttestation,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "Commencer la création de l'attestation")
            }
            Spacer(modifier = Modifier.height(normalPadding))
            TextButton(
                onClick = navigateToSieml,
                shape = MaterialTheme.shapes.small
            ) {
                Text(text = "À propos du SIEML", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

private val largePadding = 32.dp
private val normalPadding = 16.dp

@Preview
@Composable
fun PreviewWelcomeScreen() {
    WelcomeScreen({}, {})
}