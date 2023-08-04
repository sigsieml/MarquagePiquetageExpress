package fr.sieml.marquagepiquetage

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset

private const val CONTENT_ANIMATION_DURATION = 300
@Composable
fun CreateMarquageRoute(viewModel: MarquageViewModel, onNavUp: ()-> Unit, onMarquageComplete: () -> Unit) {

    val marquageScreenData = viewModel.marquageScreenData ?: return

    //log marquageScreenData
    Log.i("MarquageScreenData", marquageScreenData.toString())
    BackHandler {
        if (!viewModel.onBackPressed()) {
            onNavUp()
        }
    }

    MarquageQuestionsScreen(
        marquageScreenData = marquageScreenData,
        isNextEnabled = viewModel.isNextEnabled,
        onClosePressed = {
            onNavUp()
        },
        onPreviousPressed = { viewModel.onPreviousPressed()
             },
        onNextPressed = { viewModel.onNextPressed()
             },
        onDonePressed = { viewModel.onDonePressed(onMarquageComplete)
             }
    ) { paddingValues ->

        val modifier = Modifier.padding(paddingValues)

        AnimatedContent(
            targetState = marquageScreenData,
            transitionSpec = {
                val animationSpec: TweenSpec<IntOffset> = tween(CONTENT_ANIMATION_DURATION)

                val direction = getTransitionDirection(
                    initialIndex = initialState.questionIndex,
                    targetIndex = targetState.questionIndex,
                )

                slideIntoContainer(
                    towards = direction,
                    animationSpec = animationSpec,
                ) togetherWith slideOutOfContainer(
                    towards = direction,
                    animationSpec = animationSpec
                )
            },
            label = "surveyScreenDataAnimation"
        ) { targetState ->

            when (targetState.surveyQuestion) {
                MarquageQuestion.ATTESTATION -> {
                    AttestationQuestion(
                        marquageState = viewModel.marquage,
                        onAttestationChanged = {
                            viewModel.setAttestation(it)
                        },
                        modifier = modifier,
                    )
                }

                MarquageQuestion.ADRESSE -> {
                    AdresseQuestion(
                        marquageState = viewModel.marquage,
                        onAdressChanged = {
                            viewModel.setAdress(it)
                        },
                        modifier = modifier,
                    )
                }

                MarquageQuestion.DATE -> {
                    DateQuestion(
                        date = viewModel.getMarquage.date,
                        setDate = { viewModel.setDate(it) },
                        modifier = modifier,
                        chantierDuration = viewModel.getMarquage.chantierDuration,
                        setChantierDuration = { viewModel.setChantierDuration(it) }
                    )
                }

                MarquageQuestion.ELEMENTPRISENCOMPTEPOURLEMARQUAGE ->
                    ElementDePriseEnComptePourLeMarquageQuestion(
                        marquageState = viewModel.marquage,
                        modifier = modifier,
                        setElement = { viewModel.setElements(it) }
                    )

                MarquageQuestion.TECHNIQUES ->
                    TechniqueQuestion(
                        marquage = viewModel.marquage,
                        modifier = modifier,
                        setTechniques = { viewModel.setTechnique(it) }
                    )
                MarquageQuestion.OBSERVATIONS ->
                    ObservationQuestion(
                        marquage = viewModel.marquage,
                        modifier = modifier,
                        setObservation = { viewModel.setObservation(it) },
                        setAutreEnginDeChantier = { viewModel.setAutreEnginDeChantier(it) }
                    )
                MarquageQuestion.PHOTO -> {
                    PhotoQuestion(
                        marquageState = viewModel.marquage,
                        getNewImageUri = { viewModel.getNewSelfieUri() },
                        onPhotoTaken = { viewModel.onSelfieResponse(it)},
                        onPhotoDeleted = { viewModel.onSelfieDeleted(it) },
                        modifier = modifier,
                    )
                }
                
                MarquageQuestion.SIGNATURE ->{
                    SignatureQuestion(
                        onSaveSignature = { viewModel.onSignatureResponse(it) },
                        modifier =  modifier
                    )
                }

            }
        }
    }
}


private fun getTransitionDirection(
    initialIndex: Int,
    targetIndex: Int
): AnimatedContentTransitionScope.SlideDirection {
    return if (targetIndex > initialIndex) {
        // Going forwards in the survey: Set the initial offset to start
        // at the size of the content so it slides in from right to left, and
        // slides out from the left of the screen to -fullWidth
        AnimatedContentTransitionScope.SlideDirection.Left
    } else {
        // Going back to the previous question in the set, we do the same
        // transition as above, but with different offsets - the inverse of
        // above, negative fullWidth to enter, and fullWidth to exit.
        AnimatedContentTransitionScope.SlideDirection.Right
    }
}

