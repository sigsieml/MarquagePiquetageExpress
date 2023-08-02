package com.tlbail.marquagepiquetage

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tlbail.marquagepiquetage.pdf.PhotoUriManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.util.Calendar

const val simpleDateFormatPattern = "EEE, MMM d"

class MarquageViewModel (
    private val photoUriManager: PhotoUriManager
) : ViewModel(){

    private val questionOrder: List<MarquageQuestion> = listOf(
        MarquageQuestion.ATTESTATION,
        MarquageQuestion.ADRESSE,
        MarquageQuestion.DATE,
        MarquageQuestion.ELEMENTPRISENCOMPTEPOURLEMARQUAGE,
        MarquageQuestion.PHOTO,
        MarquageQuestion.SIGNATURE,
    )

    private val _marquage = MutableStateFlow(Marquage())

    val marquage: StateFlow<Marquage> = _marquage.asStateFlow()

    private val _marquageScreenData = mutableStateOf(createmarquageScreenData())
    val marquageScreenData: MarquageScreenData?
        get() = _marquageScreenData.value
    private var questionIndex = 0

    private val _isNextEnabled = mutableStateOf(false)
    val isNextEnabled: Boolean
        get() = _isNextEnabled.value

    fun onPreviousPressed() {
        if (questionIndex == 0) {
            throw IllegalStateException("onPreviousPressed when on question 0")
        }
        changeQuestion(questionIndex - 1)
    }

    fun onNextPressed() {
        changeQuestion(questionIndex + 1)
    }
    fun onDonePressed(onSurveyComplete: () -> Unit) {
        // Here is where you could validate that the requirements of the survey are complete
        onSurveyComplete()
    }

    private fun createmarquageScreenData(): MarquageScreenData {
        return MarquageScreenData(
            questionIndex = questionIndex,
            questionCount = questionOrder.size,
            shouldShowPreviousButton = questionIndex > 0,
            shouldShowDoneButton = questionIndex == questionOrder.size - 1,
            surveyQuestion = questionOrder[questionIndex],
        )
    }

    fun onBackPressed(): Boolean {
        if (questionIndex == 0) {
            return false
        }
        changeQuestion(questionIndex - 1)
        return true
    }

    private fun changeQuestion(newQuestionIndex: Int) {
        questionIndex = newQuestionIndex
        _isNextEnabled.value = getIsNextEnabled()
        _marquageScreenData.value = createmarquageScreenData()
    }
    private fun getIsNextEnabled(): Boolean {
        return when (questionOrder[questionIndex]) {
            MarquageQuestion.ATTESTATION -> _marquage.value.numOperation.isNotEmpty() &&
                    _marquage.value.nomSignataire.isNotEmpty()
            MarquageQuestion.ADRESSE -> _marquage.value.numRue != 0 &&
                    _marquage.value.nomRue.isNotEmpty() &&
                    _marquage.value.commune.isNotEmpty()
            MarquageQuestion.DATE -> _marquage.value.numOperation.isNotEmpty()
            MarquageQuestion.ELEMENTPRISENCOMPTEPOURLEMARQUAGE -> true
            MarquageQuestion.PHOTO -> true
            MarquageQuestion.SIGNATURE -> true
        }
    }

    fun setAttestation(attestation: Attestation) {
        val newMarquage: Marquage = Marquage(marquage.value)
        newMarquage.numOperation = attestation.numOperation
        newMarquage.libelleChantier = attestation.libelleChantier
        newMarquage.titulaire = attestation.titulaire
        newMarquage.nomSignataire = attestation.nomSignataire
        _marquage.compareAndSet(_marquage.value, newMarquage)
        _isNextEnabled.value = getIsNextEnabled()
    }

    fun setAdress(adress: Adress){
        val newMarquage: Marquage = Marquage(marquage.value)
        newMarquage.numRue = adress.numRue
        newMarquage.nomRue = adress.nomRue
        newMarquage.commune = adress.commune
        _marquage.compareAndSet(_marquage.value, newMarquage)
        _isNextEnabled.value = getIsNextEnabled()
    }

    fun setElements(element: ElementPriseComptePourLeMarquage){
        val newMarquage: Marquage = Marquage(marquage.value)
        newMarquage.dtdict = element.dtdict
        newMarquage.recepisseDesDict = element.recepisseDesDict
        newMarquage.marquageExploitant = element.marquageExploitant
        newMarquage.zoneMultiReseaux = element.zoneMultiReseaux
        newMarquage.instructionSieml = element.instructionSieml
        _marquage.compareAndSet(_marquage.value, newMarquage)
        _isNextEnabled.value = getIsNextEnabled()
    }
    fun getNewSelfieUri() = photoUriManager.buildNewUri()
    fun onSelfieResponse(uri: Uri) {
        val newMarquage: Marquage = Marquage(marquage.value)
        newMarquage.photos = newMarquage.photos.plus(uri.toString())
        _marquage.compareAndSet(_marquage.value, newMarquage)
        _isNextEnabled.value = getIsNextEnabled()
    }

    fun setDate(date: Long) {
        val newMarquage: Marquage = Marquage(marquage.value)
        newMarquage.date = Calendar.getInstance().apply { timeInMillis = date }
        _marquage.compareAndSet(_marquage.value, newMarquage)
    }

    fun onSignatureResponse(uri: Uri) {
        val newMarquage: Marquage = Marquage(marquage.value)
        newMarquage.signature = uri.toString()
        _marquage.compareAndSet(_marquage.value, newMarquage)
    }

    fun reset() {
       _marquage.value = Marquage()
        questionIndex = 0
        _isNextEnabled.value = getIsNextEnabled()
        _marquageScreenData.value = createmarquageScreenData()
    }
}


class MarquageViewModelFactory(
    private val photoUriManager: PhotoUriManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MarquageViewModel::class.java)) {
            return MarquageViewModel(photoUriManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


data class ElementPriseComptePourLeMarquage(
    var dtdict: Boolean = false,
    var recepisseDesDict: Boolean = false,
    var marquageExploitant: Boolean = false,
    var zoneMultiReseaux: Boolean = false,
    var instructionSieml: Boolean = false,
)

data class Attestation(
    val numOperation: String = "",
    val libelleChantier: String = "",
    val titulaire: String = "",
    val nomSignataire: String = "",
)

data class Adress(
    val numRue: Int = 1,
    val nomRue: String = "",
    val commune: String = "",
)

data class MarquageScreenData(
    val questionIndex: Int,
    val questionCount: Int,
    val shouldShowPreviousButton: Boolean,
    val shouldShowDoneButton: Boolean,
    val surveyQuestion: MarquageQuestion,
)

enum class MarquageQuestion {
    ATTESTATION,
    ADRESSE,
    DATE,
    ELEMENTPRISENCOMPTEPOURLEMARQUAGE,
    PHOTO,
    SIGNATURE
}