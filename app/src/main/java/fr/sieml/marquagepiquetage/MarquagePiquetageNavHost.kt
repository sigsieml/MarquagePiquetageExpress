package fr.sieml.marquagepiquetage

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.sieml.marquagepiquetage.Destinations.CREATE_MARQUAGE_ROUTE
import fr.sieml.marquagepiquetage.Destinations.MARQUAGE_COMPLETE_ROUTE
import fr.sieml.marquagepiquetage.Destinations.PDF_CREATOR_ROUTE
import fr.sieml.marquagepiquetage.Destinations.SPLASH_ROUTE
import fr.sieml.marquagepiquetage.Destinations.WELCOME_ROUTE
import fr.sieml.marquagepiquetage.pdf.PdfCreatorRoute
import fr.sieml.marquagepiquetage.pdf.PhotoUriManager
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
