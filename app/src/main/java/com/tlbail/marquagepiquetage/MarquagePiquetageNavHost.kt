package com.tlbail.marquagepiquetage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tlbail.marquagepiquetage.Destinations.CREATE_MARQUAGE_ROUTE
import com.tlbail.marquagepiquetage.Destinations.CREATE_SIGNATURE_ROUTE
import com.tlbail.marquagepiquetage.Destinations.MARQUAGE_COMPLETE_ROUTE
import com.tlbail.marquagepiquetage.Destinations.WELCOME_ROUTE

object Destinations {
    const val WELCOME_ROUTE = "welcome"
    const val CREATE_MARQUAGE_ROUTE = "createmarquage"
    const val CREATE_SIGNATURE_ROUTE = "createsignature"
    const val MARQUAGE_COMPLETE_ROUTE = "marquagecomplete"
}
@Composable
fun MarquagePiquetageNavHost(
    navController: NavHostController = rememberNavController()
){

    NavHost(navController = navController, startDestination = WELCOME_ROUTE) {
        composable(WELCOME_ROUTE) { SuperButtonNavigator(text = WELCOME_ROUTE, onClick = { navController.navigate(
            CREATE_MARQUAGE_ROUTE) }) }
        composable(CREATE_MARQUAGE_ROUTE) { CreateMarquageRoute(
            onNavUp = { navController.navigateUp() },
            onCreateSignatureClick = { navController.navigate(CREATE_SIGNATURE_ROUTE) },
            onMarquageComplete = { navController.navigate(MARQUAGE_COMPLETE_ROUTE) }
        ) }
        composable(CREATE_SIGNATURE_ROUTE) { SuperButtonNavigator(text = CREATE_SIGNATURE_ROUTE, onClick = { navController.navigate(
            CREATE_MARQUAGE_ROUTE) }) }
        composable(MARQUAGE_COMPLETE_ROUTE) { MarqueCompleteRoute(onDonePressed = { navController.navigate(
            WELCOME_ROUTE) }) }
    }
}

@Composable
fun SuperButtonNavigator(text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(text)
    }

}
