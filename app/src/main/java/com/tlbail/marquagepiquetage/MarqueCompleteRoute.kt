package com.tlbail.marquagepiquetage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import com.tlbail.marquagepiquetage.pdf.AndroidPdfMarquageCreator
import com.tlbail.marquagepiquetage.pdf.PdfMarquageCreator
import java.io.File
import java.util.Calendar
private lateinit var sendMailLauncher: ActivityResultLauncher<Intent>

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarqueCompleteRoute(
    onNavUp : () -> Unit,
    pdfFile: File,
    viewModel: MarquageViewModel, onDonePressed: () -> Unit) {

    BackHandler {
        onNavUp()
    }


    val marquage = viewModel.marquage.collectAsState()
    val nomRecipient = stringArrayResource(id = R.array.nomRecipients)
    val mailRecipient = stringArrayResource(id = R.array.mailRecipients)

    val selectedRecipient = remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val openDialog = remember { mutableStateOf(false)}

    sendMailLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        openDialog.value = true
    }


    if(openDialog.value) {
        AlertDialog(onDismissRequest = {openDialog.value = false }, confirmButton = { Button(
            onClick = {
                openDialog.value = false
                onDonePressed()
            }) {
                Text("Terminer")
            } },
            title = { Text(text = "Envoi du mail") },
            text = { Text(text = "Le mail a bien été envoyé") },
            dismissButton = { Button(onClick = { openDialog.value = false }) {
                Text("Retour")
            } }
            )
    }


    Surface(modifier = Modifier
        .fillMaxWidth()
        .wrapContentWidth(align = Alignment.CenterHorizontally)
        .widthIn(max = 840.dp)) {
        Scaffold(
            content = { innerPadding ->
                val modifier = Modifier.padding(innerPadding)
                MarquageComplete(
                    title = stringResource(R.string.survey_result_title),
                    subtitle = stringResource(R.string.survey_result_subtitle),
                    description = stringResource(R.string.survey_result_description),
                    modifier = modifier,
                    marquage = marquage.value,
                    emailList = nomRecipient.toList(),
                    selectedRecipient = selectedRecipient
                )
            },
            bottomBar = {
                OutlinedButton(
                    onClick = { sendMail(pdfFile,context,marquage.value,mailRecipient,nomRecipient, selectedRecipient) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Text(text = stringResource(id = R.string.done))
                }
            }
        )
    }
}
fun sendMail(file:File,  context: Context, marquage: Marquage, mailRecipient: Array<String>, nomRecipient: Array<String>, selectedNomRecepient: MutableState<String?>) {
    if(selectedNomRecepient.value == null || selectedNomRecepient.value == "") {
        Toast.makeText(context, "Veuillez sélectionner un destinataire", Toast.LENGTH_SHORT).show()
        return
    }


    //get nomRecipient index from selectedNomRecepient
    val index = nomRecipient.indexOf(selectedNomRecepient.value)
    val recipient = mailRecipient[index]
    val intent = Intent(Intent.ACTION_SEND)
    intent.data = Uri.parse("mailto:")
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
    intent.putExtra(Intent.EXTRA_SUBJECT, "Marquage Piquetage")
    intent.putExtra(Intent.EXTRA_TEXT, "Bonjour,\n\nVeuillez trouver ci-joint l'attestation de marquage piquetage.\n\nCordialement,\n\n${marquage.nomSignataire}")
    val FILE_PROVIDER = "fileprovider"
    val authority = "${context.packageName}.${FILE_PROVIDER}"
    val fileUri: Uri = FileProvider.getUriForFile(context, authority, file)
    intent.putExtra(Intent.EXTRA_STREAM, fileUri)

    val chooserIntent = Intent.createChooser(intent, "Choisissez votre application de messagerie")
    if (chooserIntent.resolveActivity(context.packageManager) != null) {
        sendMailLauncher.launch(chooserIntent)
    } else {
        Toast.makeText(context, "Aucune application de messagerie trouvée", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun MarquageComplete(
    title: String,
    subtitle: String,
    description: String,
    modifier: Modifier = Modifier,
    marquage: Marquage,
    emailList: List<String>,
    selectedRecipient: MutableState<String?>
) {

    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            Spacer(modifier = Modifier.height(44.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(20.dp)
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            EmailSpinner(
                emailList = emailList.toList(),
                selectedEmail = selectedRecipient,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailSpinner(
    emailList: List<String>,
    selectedEmail: MutableState<String?>,
    modifier: Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = {
        expanded = !expanded
    }) {
        CompositionLocalProvider(LocalTextInputService provides null) {
            OutlinedTextField(
                readOnly = true,
                value = selectedEmail.value ?: "",
                onValueChange = {
                },
                label = {
                    Text(
                        text = "Destinataire"
                    )
                },
                trailingIcon = {
                    TrailingIcon(
                        expanded = expanded
                    )
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = modifier.menuAnchor()
            )
        }
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            emailList.forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        selectedEmail.value = selectionOption
                        expanded = false
                    },
                    text = {
                        Text(text = selectionOption)
                    }
                )
            }
        }
    }


}


@Preview
@Composable
fun MarqueCompletePreview() {


    val emailList = listOf(
        "d.penot@sieml.fr", "l.baradeau@sieml.fr", "c.vanachter@sieml.fr",
        // ajouter le reste des emails ici
    )
    val selectedEmail = remember { mutableStateOf<String?>(null) }
    MarquageComplete(
        title = "title",
        subtitle = "subtitle",
        description = "description",
        marquage =  Marquage(
            "1",
            "2",
            "3",
            "4",
            "5",
            5,
            "6",
            "7",
            Calendar.getInstance(),
            listOf(),
            false,
            false,
            false,
            false,
            false,
            ""
        ),
        emailList = emailList,
        selectedRecipient = selectedEmail

    )
}