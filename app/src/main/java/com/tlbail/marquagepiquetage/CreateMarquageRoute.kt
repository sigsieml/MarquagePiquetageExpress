package com.tlbail.marquagepiquetage

import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.tlbail.marquagepiquetage.Signature.DrawingApp
import kotlinx.coroutines.flow.StateFlow
import java.util.Calendar

private const val CONTENT_ANIMATION_DURATION = 300
@Composable
fun CreateMarquageRoute(viewModel: MarquageViewModel, onNavUp: ()-> Unit, onCreateSignatureClick: () -> Unit, onMarquageComplete: () -> Unit) {

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
                        onAttestationChaned = {
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
                    val supportFragmentManager =
                        LocalContext.current.findActivity().supportFragmentManager
                    DateQuestion(
                        marquage = viewModel.marquage,
                        onClick = {
                            showTakeawayDatePicker(
                                date = viewModel.marquage.value.date.timeInMillis,
                                supportFragmentManager = supportFragmentManager,
                                onDateSelected = { date ->
                                    viewModel.setDate(date)}
                            )
                        },
                        modifier = modifier,
                    )
                }

                MarquageQuestion.ELEMENTPRISENCOMPTEPOURLEMARQUAGE ->
                    ElementDePriseEnComptePourLeMarquageQuestion(
                        marquageState = viewModel.marquage,
                        modifier = modifier,
                        setElement = { viewModel.setElements(it) }
                    )

                MarquageQuestion.PHOTO -> {
                    PhotoQuestion(
                        marquageState = viewModel.marquage,
                        getNewImageUri = { viewModel.getNewSelfieUri() },
                        onPhotoTaken = { viewModel.onSelfieResponse(it)},
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

private tailrec fun Context.findActivity(): AppCompatActivity =
    when (this) {
        is AppCompatActivity -> this
        is ContextWrapper -> this.baseContext.findActivity()
        else -> throw IllegalArgumentException("Could not find activity!")
    }


private fun showTakeawayDatePicker(
    date: Long?,
    supportFragmentManager: FragmentManager,
    onDateSelected: (date: Long) -> Unit,
) {
    val picker = MaterialDatePicker.Builder.datePicker()
        .setSelection(date)
        .build()
    picker.show(supportFragmentManager, picker.toString())
    picker.addOnPositiveButtonClickListener {
        picker.selection?.let {
            onDateSelected(it)
        }
    }
}