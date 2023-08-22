package fr.sieml.marquagepiquetage

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import fr.sieml.marquagepiquetage.Marquage.Marquage
import fr.sieml.marquagepiquetage.Marquage.Techniques
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
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = Color.White,
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primary,
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
    marquageState: StateFlow<Marquage>,
    onAttestationChanged: (attestation: Attestation) -> Unit,
    modifier: Modifier = Modifier,
) {
    var marquage = marquageState.collectAsState()
    QuestionWrapper(titleResourceId = R.string.questionAttestation,
        modifier = modifier,
        directionsResourceId = R.string.questionAttestationDirections) {
        Column() {
            makeTextField(
                marquage.value.numOperation,
                { value -> onAttestationChanged(Attestation(value, marquage.value.libelleChantier, marquage.value.titulaire, marquage.value.nomSignataire, marquage.value.numDict)) },
                R.string.questionNOperation
            )
            makeTextField(
                marquage.value.libelleChantier,
                { value -> onAttestationChanged(Attestation(marquage.value.numOperation, value, marquage.value.titulaire, marquage.value.nomSignataire, marquage.value.numDict)) },
                R.string.questionLibelleChantier
            )
            makeTextField(
                marquage.value.nomSignataire,
                { value -> onAttestationChanged(Attestation(marquage.value.numOperation, marquage.value.libelleChantier, marquage.value.titulaire, value, marquage.value.numDict)) },
                R.string.questionNomSignataire
            )
            makeTextField(
                marquage.value.numDict,
                { value -> onAttestationChanged(Attestation(marquage.value.numOperation, marquage.value.libelleChantier, marquage.value.titulaire, marquage.value.nomSignataire, value)) },
                R.string.questionNumDict
            )
            AutoCompleteTextField(label = stringResource(id = R.string.questionTitulaire), options = stringArrayResource(
                id = R.array.prestataires
            ), value = marquage.value.titulaire, onTextChaned =
            { value -> onAttestationChanged(Attestation(marquage.value.numOperation, marquage.value.libelleChantier, value, marquage.value.nomSignataire, marquage.value.numDict)) },
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
    marquageState: StateFlow<Marquage>,
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
            Row {
                OutlinedTextField(
                    value = if(marquage.value.numRue < 0) "" else marquage.value.numRue.toString(),
                    onValueChange =  { value:String -> onAdressChanged(Adress((if(value.toIntOrNull() == null) -1 else value.toInt()), marquage.value.numRueFin, marquage.value.nomRue, marquage.value.commune)) },
                    label = {Text(text = stringResource(id = R.string.questionNumRue)) },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Number,
                        autoCorrect = true
                    ),
                    modifier = Modifier.width(64.dp)

                )
                Spacer(modifier = Modifier.width(64.dp))
                Text(text = stringResource(id = R.string.questionNumRueFin),
                    modifier = Modifier.align(Alignment.Bottom))
                Spacer(modifier = Modifier.width(64.dp))
                OutlinedTextField(
                    value = if(marquage.value.numRueFin < 0) "" else marquage.value.numRueFin.toString(),
                    onValueChange =  { value:String -> onAdressChanged(Adress(marquage.value.numRue, (if(value.toIntOrNull() == null) -1 else value.toInt()), marquage.value.nomRue, marquage.value.commune)) },
                    label = {Text(text = stringResource(id = R.string.questionNumRue)) },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Number,
                        autoCorrect = true
                    ),
                    modifier = Modifier.width(64.dp)

                )
            }
            makeTextField(
                marquage.value.nomRue,
                { value -> onAdressChanged(Adress(marquage.value.numRue,marquage.value.numRueFin, value, marquage.value.commune)) },
                R.string.questionRue
            )
            AutoCompleteTextField(
                options = options,
                value = marquage.value.commune,
                onTextChaned = { onAdressChanged(Adress(marquage.value.numRue,marquage.value.numRueFin, marquage.value.nomRue, it))},
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
    chantierDuration: Int,
    setChantierDuration: (Int) -> Unit
) {
    DateImplQuestion(
        titleResourceId = R.string.questionDateTitle,
        directionsResourceId = R.string.questionDateDirections,
        date = date,
        setDate = setDate,
        modifier = modifier,
        chantierDuration = chantierDuration,
        setChantierDuration = setChantierDuration
    )
}
@Composable
fun PhotoQuestion(
    marquageState: StateFlow<Marquage>,
    getNewImageUri: () -> Uri,
    onPhotoTaken: (Uri) -> Unit,
    onPhotoDeleted: (Uri) -> Unit,
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
                    onPhotoDeleted = { uri -> onPhotoDeleted(uri) }
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
    marquageState: StateFlow<Marquage>,
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
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            QuestionTitle(R.string.questionSignatureTitle)
            R.string.questionSignatureDescription.let {
                Spacer(Modifier.height(18.dp))
                QuestionDirections(it)
            }
        }
        Spacer(Modifier.height(18.dp))
        DrawingApp(paddingValues = PaddingValues(0.dp),
            paths = paths,
            size = size)
    }
}

@Composable
fun TechniqueQuestion(
    marquage: StateFlow<Marquage>,
    setTechniques: (Techniques) -> Unit,
    modifier: Modifier
){
    val marquage = marquage.collectAsState()
    val techniques = marquage.value.techniques
    var selectedAnswers:List<Int> = listOf()
    if(techniques.forageAvecTariere)selectedAnswers = selectedAnswers.plus(R.string.forageAvecTariere)
    if(techniques.forageDirige)selectedAnswers = selectedAnswers.plus(R.string.forageDirige)
    if(techniques.fuseOuOgive)selectedAnswers = selectedAnswers.plus(R.string.fuseOuOgive)
    if(techniques.briseRoche)selectedAnswers = selectedAnswers.plus(R.string.briseRoche)
    if(techniques.enginElevateur)selectedAnswers = selectedAnswers.plus(R.string.EnginElevateur)
    if(techniques.enginVibrant)selectedAnswers = selectedAnswers.plus(R.string.enginVibrant)
    if(techniques.grue)selectedAnswers = selectedAnswers.plus(R.string.grue)
    if(techniques.manuelOuManutentionDobjetOuMateriel) selectedAnswers = selectedAnswers.plus(R.string.manuelOuManutentionDobjetOuMateriel)
    if(techniques.pelleMecanique) selectedAnswers = selectedAnswers.plus(R.string.pelleMecanique)
    if(techniques.trancheuse) selectedAnswers = selectedAnswers.plus(R.string.trancheuse)
    if(techniques.raboteuse) selectedAnswers = selectedAnswers.plus(R.string.raboteuse)
    if(techniques.techniqueDouce) selectedAnswers = selectedAnswers.plus(R.string.techniqueDouce)

    var possibleAnswers = listOf(
        R.string.forageAvecTariere,
        R.string.forageDirige,
        R.string.fuseOuOgive,
        R.string.briseRoche,
        R.string.EnginElevateur,
        R.string.enginVibrant,
        R.string.grue,
        R.string.manuelOuManutentionDobjetOuMateriel,
        R.string.pelleMecanique,
        R.string.trancheuse,
        R.string.raboteuse,
        R.string.techniqueDouce
    )
    MultipleChoiceQuestion(
        titleResourceId = R.string.questionTechnique,
        directionsResourceId = R.string.select_all,
        possibleAnswers = possibleAnswers,
        selectedAnswers = selectedAnswers,
        onOptionSelected =  { selected, answer ->
            val element = Techniques(techniques )
            if(selected) {
                when(answer) {
                    R.string.forageAvecTariere -> element.forageAvecTariere = true
                    R.string.forageDirige -> element.forageDirige = true
                    R.string.fuseOuOgive -> element.fuseOuOgive = true
                    R.string.briseRoche -> element.briseRoche = true
                    R.string.EnginElevateur -> element.enginElevateur = true
                    R.string.enginVibrant -> element.enginVibrant = true
                    R.string.grue -> element.grue = true
                    R.string.manuelOuManutentionDobjetOuMateriel -> element.manuelOuManutentionDobjetOuMateriel = true
                    R.string.pelleMecanique -> element.pelleMecanique = true
                    R.string.trancheuse -> element.trancheuse = true
                    R.string.raboteuse -> element.raboteuse = true
                    R.string.techniqueDouce -> element.techniqueDouce = true
                }
            } else {
                when(answer) {
                    R.string.forageAvecTariere -> element.forageAvecTariere = false
                    R.string.forageDirige -> element.forageDirige = false
                    R.string.fuseOuOgive -> element.fuseOuOgive = false
                    R.string.briseRoche -> element.briseRoche = false
                    R.string.EnginElevateur -> element.enginElevateur = false
                    R.string.enginVibrant -> element.enginVibrant = false
                    R.string.grue -> element.grue = false
                    R.string.manuelOuManutentionDobjetOuMateriel -> element.manuelOuManutentionDobjetOuMateriel = false
                    R.string.pelleMecanique -> element.pelleMecanique = false
                    R.string.trancheuse -> element.trancheuse = false
                    R.string.raboteuse -> element.raboteuse = false
                    R.string.techniqueDouce -> element.techniqueDouce = false
                }
            }
            setTechniques(element)
        },
        modifier = modifier,
    )
}


@Composable
fun ObservationQuestion(
    marquage: StateFlow<Marquage>,
    setObservation: (String) -> Unit,
    setAutreEnginDeChantier: (String) -> Unit,
    modifier: Modifier){
    val marquage = marquage.collectAsState()
    val observation = marquage.value.observation
    val autreEnginDeChantier = marquage.value.autreEnginDeChantier


    QuestionWrapper(titleResourceId = R.string.observationsTitle,
        modifier = modifier,
        directionsResourceId = R.string.observationsDirections) {
        Column() {
            makeTextField(
                marquage.value.autreEnginDeChantier,
                { value -> setAutreEnginDeChantier(value) },
                R.string.questionAutreEnginDeChantier
            )
            OutlinedTextField(
                value = marquage.value.observation,
                onValueChange = setObservation,
                modifier = modifier
                    .fillMaxWidth()
                    .height(200.dp),
                label = { Text(text = stringResource(id = R.string.questionObservation)) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text,
                    autoCorrect = true
                )
            )
        }
    }
}
