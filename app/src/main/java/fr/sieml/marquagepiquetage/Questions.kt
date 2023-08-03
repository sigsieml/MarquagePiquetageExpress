package fr.sieml.marquagepiquetage

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import fr.sieml.marquagepiquetage.Signature.DrawingApp
import fr.sieml.marquagepiquetage.Signature.PathProperties
import fr.sieml.marquagepiquetage.Signature.createImageBitmapFromCanvas
import fr.sieml.marquagepiquetage.Signature.saveImageBitmapToFile
import fr.sieml.marquagepiquetage.ui.theme.slightlyDeemphasizedAlpha
import fr.sieml.marquagepiquetage.ui.theme.stronglyDeemphasizedAlpha
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.util.Calendar


@Composable
fun QuestionWrapper(
    @StringRes titleResourceId: Int,
    modifier: Modifier = Modifier,
    @StringRes directionsResourceId: Int? = null,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(32.dp))
        QuestionTitle(titleResourceId)
        directionsResourceId?.let {
            Spacer(Modifier.height(18.dp))
            QuestionDirections(it)
        }
        Spacer(Modifier.height(18.dp))

        content()
    }
}


@Composable
private fun QuestionTitle(
    @StringRes title: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(id = title),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = slightlyDeemphasizedAlpha),
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.inverseOnSurface,
                shape = MaterialTheme.shapes.small
            )
            .padding(vertical = 24.dp, horizontal = 16.dp)
    )
}


@Composable
private fun QuestionDirections(
    @StringRes directionsResourceId: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(id = directionsResourceId),
        color = MaterialTheme.colorScheme.onSurface
            .copy(alpha = stronglyDeemphasizedAlpha),
        style = MaterialTheme.typography.bodySmall,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttestationQuestion(
    marquageState: StateFlow<fr.sieml.marquagepiquetage.Marquage>,
    onAttestationChaned: (attestation: Attestation) -> Unit,
    modifier: Modifier = Modifier,
) {
    var marquage = marquageState.collectAsState()
    QuestionWrapper(titleResourceId = R.string.questionAttestation,
        modifier = modifier,
        directionsResourceId = R.string.questionAttestationDirections) {
        Column() {
            makeTextField(
                marquage.value.numOperation,
                { value -> onAttestationChaned(Attestation(value, marquage.value.libelleChantier, marquage.value.titulaire, marquage.value.nomSignataire, marquage.value.numDict)) },
                R.string.questionNOperation
            )
            makeTextField(
                marquage.value.libelleChantier,
                { value -> onAttestationChaned(Attestation(marquage.value.numOperation, value, marquage.value.titulaire, marquage.value.nomSignataire, marquage.value.numDict)) },
                R.string.questionLibelleChantier
            )
            makeTextField(
                marquage.value.nomSignataire,
                { value -> onAttestationChaned(Attestation(marquage.value.numOperation, marquage.value.libelleChantier, marquage.value.titulaire, value, marquage.value.numDict)) },
                R.string.questionNomSignataire
            )
            AutoCompleteTextField(label = stringResource(id = R.string.questionTitulaire), options = stringArrayResource(
                id = R.array.prestataires
            ), value = marquage.value.titulaire, onTextChaned =
                { value -> onAttestationChaned(Attestation(marquage.value.numOperation, marquage.value.libelleChantier, value, marquage.value.nomSignataire, marquage.value.numDict)) },
            )
            makeTextField(
                marquage.value.numDict,
                { value -> onAttestationChaned(Attestation(marquage.value.numOperation, marquage.value.libelleChantier, marquage.value.titulaire, marquage.value.nomSignataire, value)) },
                R.string.questionNumDict
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun makeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    resourceId: Int,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(text = stringResource(id = resourceId)) },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Text,
            autoCorrect = true
        )
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdresseQuestion(
    marquageState: StateFlow<fr.sieml.marquagepiquetage.Marquage>,
    onAdressChanged: (marquage: Adress) -> Unit,
    modifier: Modifier = Modifier,
) {
    var marquage = marquageState.collectAsState()

    val context = LocalContext.current
    val localisation = getUserLocation(context = context)
    var isDialogShown by remember { mutableStateOf(false) }


    val options = stringArrayResource(id = R.array.villes)

    if (isDialogShown && localisation.value.latitude == 0.toDouble() ) {
        Dialog(onDismissRequest = {
            isDialogShown = false
            if(localisation.value.latitude != 0.toDouble()){
                onAdressChanged(getReadableLocation(localisation.value.latitude, localisation.value.longitude, context = context))
            }
        }) {
            val localisation2 = getUserLocation(context = context)
            if(localisation2.value.latitude != 0.toDouble()){
                isDialogShown = false
                onAdressChanged(getReadableLocation(localisation2.value.latitude, localisation2.value.longitude, context = context))
            }
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .padding(16.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
    if(isDialogShown && localisation.value.latitude != 0.toDouble()){
        isDialogShown = false
        onAdressChanged(getReadableLocation(localisation.value.latitude, localisation.value.longitude, context = context))
    }


    QuestionWrapper(titleResourceId = R.string.questionAdresse,
        modifier = modifier,
        directionsResourceId = R.string.questionAdresseDirections) {
        Column {
            makeTextField(
                marquage.value.numRue.toString(),
                { value -> onAdressChanged(Adress((if(value.toIntOrNull() == null) 0 else value.toInt()), marquage.value.nomRue, marquage.value.commune)) },
                R.string.questionNumRue
            )
            makeTextField(
                marquage.value.nomRue,
                { value -> onAdressChanged(Adress(marquage.value.numRue, value, marquage.value.commune)) },
                R.string.questionRue
            )
            AutoCompleteTextField(
                options = options,
                value = marquage.value.commune,
                onTextChaned = { onAdressChanged(Adress(marquage.value.numRue, marquage.value.nomRue, it))},
                label = stringResource(id = R.string.questionCommune)
            )
            Button(onClick = {
                isDialogShown = true
                if (localisation.value.latitude.toInt() != 0)  {
                    onAdressChanged( getReadableLocation(localisation.value.latitude, localisation.value.longitude, context))
                    isDialogShown = false
                } },
                modifier = Modifier.padding(top = 16.dp)) {
                Icon(Icons.Filled.LocationOn, contentDescription = "Geolocalisation")
            }
        }
    }


}



@Composable
fun DateQuestion(
    date: Calendar,
    modifier: Modifier = Modifier,
    setDate: (Long) -> Unit,
) {
    DateImplQuestion(
        titleResourceId = R.string.questionDateTitle,
        directionsResourceId = R.string.questionDateDirections,
        date = date,
        setDate = setDate,
        modifier = modifier,
    )
}
@Composable
fun PhotoQuestion(
    marquageState: StateFlow<fr.sieml.marquagepiquetage.Marquage>,
    getNewImageUri: () -> Uri,
    onPhotoTaken: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    var marquage = marquageState.collectAsState()
    QuestionWrapper(
        titleResourceId = R.string.questionPhotoTitle,
        modifier = modifier,
    ) {
        Column() {
            val urisPhoto: List<String> = marquage.value.photos
            urisPhoto.forEach { uriPhoto ->
                PhotoImplQuestion(
                    imageUri = Uri.parse(uriPhoto),
                    getNewImageUri = getNewImageUri,
                    onPhotoTaken = onPhotoTaken,
                )
            }
            PhotoImplQuestion(
                imageUri = null,
                getNewImageUri = getNewImageUri,
                onPhotoTaken = onPhotoTaken,
            )
        }
    }
}

@Composable
fun ElementDePriseEnComptePourLeMarquageQuestion(
    marquageState: StateFlow<fr.sieml.marquagepiquetage.Marquage>,
    modifier: Modifier,
    setElement: (element: ElementPriseComptePourLeMarquage) -> Unit,
){
    var marquage = marquageState.collectAsState()
    var selectedAnswers:List<Int> = listOf()
    if(marquage.value.dtdict)selectedAnswers = selectedAnswers.plus(R.string.dtDict)
    if(marquage.value.recepisseDesDict)selectedAnswers = selectedAnswers.plus(R.string.recepisseDesDict)
    if(marquage.value.marquageExploitant)selectedAnswers = selectedAnswers.plus(R.string.marquageExploitant)
    if(marquage.value.zoneMultiReseaux)selectedAnswers = selectedAnswers.plus(R.string.zoneMultiReseaux)
    if(marquage.value.instructionSieml)selectedAnswers = selectedAnswers.plus(R.string.instructionsSieml)

    var possibleAnswers = listOf(R.string.dtDict, R.string.recepisseDesDict, R.string.marquageExploitant, R.string.zoneMultiReseaux)
    if(marquage.value.dtdict)possibleAnswers = possibleAnswers.plus(R.string.instructionsSieml)
    MultipleChoiceQuestion(
        titleResourceId = R.string.questionElementPriseEnCompteTitle,
        directionsResourceId = R.string.select_all,
        possibleAnswers = possibleAnswers,
        selectedAnswers = selectedAnswers,
        onOptionSelected =  { selected, answer ->
            val element = ElementPriseComptePourLeMarquage(
                marquage.value.dtdict,
                marquage.value.recepisseDesDict,
                marquage.value.marquageExploitant,
                marquage.value.zoneMultiReseaux,
                marquage.value.instructionSieml
            )
            if(selected) {
                when(answer) {
                    R.string.dtDict -> element.dtdict = true
                    R.string.recepisseDesDict -> element.recepisseDesDict = true
                    R.string.marquageExploitant -> element.marquageExploitant = true
                    R.string.zoneMultiReseaux -> element.zoneMultiReseaux = true
                    R.string.instructionsSieml -> element.instructionSieml = true
                }
            } else {
                when(answer) {
                    R.string.dtDict -> element.dtdict = false
                    R.string.recepisseDesDict -> element.recepisseDesDict = false
                    R.string.marquageExploitant -> element.marquageExploitant = false
                    R.string.zoneMultiReseaux -> element.zoneMultiReseaux = false
                    R.string.instructionsSieml -> element.instructionSieml = false
                }
            }
            setElement(element)
        },
        modifier = modifier,
    )
}

@Composable
fun SignatureQuestion(
    onSaveSignature: (Uri) -> Unit,
    modifier: Modifier= Modifier
){
    val context = LocalContext.current
    val paths = remember { mutableStateListOf<Pair<Path, PathProperties>>() }
    var size = remember { mutableStateOf(IntSize.Zero) }

    DisposableEffect(Unit){
        onDispose {
            val imageBitmap = createImageBitmapFromCanvas(paths, size.value)
            val outputFile = File(context.filesDir, "signature.png")
            saveImageBitmapToFile(imageBitmap, outputFile)
            onSaveSignature(outputFile.toUri())
        }
    }


    Column(modifier = modifier) {
        Spacer(Modifier.height(32.dp))
        QuestionTitle(R.string.questionSignatureTitle)
        R.string.questionSignatureDescription.let {
            Spacer(Modifier.height(18.dp))
            QuestionDirections(it)
        }
        Spacer(Modifier.height(18.dp))
        DrawingApp(paddingValues = PaddingValues(0.dp),
            paths = paths,
            size = size)
    }
}

