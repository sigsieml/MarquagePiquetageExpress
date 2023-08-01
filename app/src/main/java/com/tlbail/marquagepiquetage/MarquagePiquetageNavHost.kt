package com.tlbail.marquagepiquetage

import android.content.Intent
import android.net.Uri
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
import com.tlbail.marquagepiquetage.Destinations.CREATE_SIGNATURE_ROUTE
import com.tlbail.marquagepiquetage.Destinations.MARQUAGE_COMPLETE_ROUTE
import com.tlbail.marquagepiquetage.Destinations.SPLASH_ROUTE
import com.tlbail.marquagepiquetage.Destinations.WELCOME_ROUTE
import com.tlbail.marquagepiquetage.Signature.DrawingApp
import com.tlbail.marquagepiquetage.pdf.PhotoUriManager

object Destinations {
    const val SPLASH_ROUTE = "splash"
    const val WELCOME_ROUTE = "welcome"
    const val CREATE_MARQUAGE_ROUTE = "createmarquage"
    const val CREATE_SIGNATURE_ROUTE = "createsignature"
    const val MARQUAGE_COMPLETE_ROUTE = "marquagecomplete"
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
            onNavUp = { navController.navigateUp() },
            onCreateSignatureClick = { navController.navigate(CREATE_SIGNATURE_ROUTE) },
            onMarquageComplete = { navController.navigate(MARQUAGE_COMPLETE_ROUTE) }
        ) }
        composable(CREATE_SIGNATURE_ROUTE) { DrawingApp(paddingValues = PaddingValues()) }
        composable(MARQUAGE_COMPLETE_ROUTE) { MarqueCompleteRoute(
            viewModel = viewModel,
            onDonePressed = { navController.navigate(
            WELCOME_ROUTE) }) }
    }
}
