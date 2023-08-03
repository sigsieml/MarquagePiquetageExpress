package com.tlbail.marquagepiquetage

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tlbail.marquagepiquetage.Destinations.CREATE_MARQUAGE_ROUTE
import com.tlbail.marquagepiquetage.Destinations.MARQUAGE_COMPLETE_ROUTE
import com.tlbail.marquagepiquetage.Destinations.PDF_CREATOR_ROUTE
import com.tlbail.marquagepiquetage.Destinations.SPLASH_ROUTE
import com.tlbail.marquagepiquetage.Destinations.WELCOME_ROUTE
import com.tlbail.marquagepiquetage.Signature.DrawingApp
import com.tlbail.marquagepiquetage.pdf.PdfCreatorRoute
import com.tlbail.marquagepiquetage.pdf.PhotoUriManager
import java.io.File

object Destinations {
    const val SPLASH_ROUTE = "splash"
    const val WELCOME_ROUTE = "welcome"
    const val CREATE_MARQUAGE_ROUTE = "createmarquage"
    const val MARQUAGE_COMPLETE_ROUTE = "marquagecomplete"
    const val PDF_CREATOR_ROUTE = "pdfcreator"
}
@Composable
fun MarquagePiquetageNavHost(
    navController: NavHostController = rememberNavController()
){
    val viewModel: MarquageViewModel = viewModel(
        factory = MarquageViewModelFactory(
            PhotoUriManager(LocalContext.current)
        ))
    val context = LocalContext.current

    var pdfFile: File? = null
    NavHost(navController = navController, startDestination = SPLASH_ROUTE){
        composable(SPLASH_ROUTE){
            SplashScreen (onSplashFinish = { navController.navigate(WELCOME_ROUTE) })
        }
        composable(WELCOME_ROUTE) { WelcomeScreen(navigateToCreateAttestation = { navController.navigate(
            CREATE_MARQUAGE_ROUTE) }, navigateToSieml = {
                Intent(Intent.ACTION_VIEW).apply {
                    data =  Uri.parse("https://sieml.fr")
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }.also { intent ->
                    context.startActivity(intent)
                }
        }) }
        composable(CREATE_MARQUAGE_ROUTE) { CreateMarquageRoute(
            viewModel = viewModel,
            onNavUp = { navController.navigateUp()
                      viewModel.reset()},
            onMarquageComplete = {
                navController.navigate(PDF_CREATOR_ROUTE)
            }
        ) }
        composable(PDF_CREATOR_ROUTE){
            PdfCreatorRoute(
                marquage= viewModel.getMarquage,
                onPdfCreated = {
                    pdfFile = it
                    navController.popBackStack()
                    navController.navigate(MARQUAGE_COMPLETE_ROUTE) },
            )
        }
        composable(MARQUAGE_COMPLETE_ROUTE) {
            if(pdfFile == null){
                navController.navigate(CREATE_MARQUAGE_ROUTE)
                Toast.makeText(context, "Erreur lors de la création du PDF", Toast.LENGTH_LONG).show()
                return@composable
            }
            MarqueCompleteRoute(
                onNavUp = { navController.navigateUp() },
                pdfFile = pdfFile!!,
                viewModel = viewModel,
                onDonePressed = { navController.navigate(
                    WELCOME_ROUTE)
                    viewModel.reset()
                })
        }
    }
}
