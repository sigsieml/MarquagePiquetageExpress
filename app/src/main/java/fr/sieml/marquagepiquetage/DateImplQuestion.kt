package fr.sieml.marquagepiquetage


import android.app.TimePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.res.Configuration
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.accessibility.AccessibilityManager
import android.widget.TimePicker
import androidx.annotation.FloatRange
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.sieml.marquagepiquetage.ui.theme.MarquagePiquetageTheme
import fr.sieml.marquagepiquetage.ui.theme.slightlyDeemphasizedAlpha
import fr.sieml.marquagepiquetage.util.getDefaultDateInMillis
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateImplQuestion(
    @StringRes titleResourceId: Int,
    @StringRes directionsResourceId: Int,
    date: Calendar = Calendar.getInstance(),
    setDate: (Long) -> Unit,
    chantierDuration: Int,
    setChantierDuration: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val supportFragmentManager =
        LocalContext.current.findActivity().supportFragmentManager
    QuestionWrapper(
        titleResourceId = titleResourceId,
        directionsResourceId = directionsResourceId,
        modifier = modifier,
    ) {
        // All times are stored in UTC, so generate the display from UTC also
        val dateFormat = SimpleDateFormat(simpleDateFormatPattern, Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val dateString = dateFormat.format(date.timeInMillis ?: getDefaultDateInMillis())

        val valueRange: ClosedFloatingPointRange<Float> = 0f..27f
        var sliderPosition by remember {
            mutableIntStateOf(chantierDuration)
        }
        val view = LocalView.current
        Column {
            TimePicker(setDate,date)
            Button(
                onClick = {
                    showTakeawayDatePicker(
                        date = date.timeInMillis,
                        supportFragmentManager = supportFragmentManager,
                        onDateSelected = { date ->
                            setDate(date)}
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                        .copy(alpha = slightlyDeemphasizedAlpha),
                ),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .height(54.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)),
            ) {
                Text(
                    text = dateString,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.8f)
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.2f)
                )
            }
            Text(text = "Durée du chantier :")
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Slider(value = sliderPosition.toFloat(), onValueChange = {
                    if(sliderPosition != it.toInt()){
                        sliderPosition = it.toInt()
                        view.vibrate()
                    }
                },
                    valueRange = valueRange,
                    steps = 27,
                    onValueChangeFinished = {
                        setChantierDuration(sliderPosition.toInt())
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp))
                Text(text = "${sliderPosition.toInt()} semaines")
            }
        }

    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(setDate: (Long) -> Unit, date: Calendar = Calendar.getInstance()) {
    //time picker
    var showTimePicker by remember { mutableStateOf(false) }
    val formatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val onTimesetListener: TimePickerDialog.OnTimeSetListener = TimePickerDialog.OnTimeSetListener(
        fun(view: TimePicker?, hourOfDay: Int, minute: Int) {
            date.set(Calendar.HOUR_OF_DAY, hourOfDay)
            date.set(Calendar.MINUTE, minute)
            setDate(date.timeInMillis)
            showTimePicker = false
        }
    )
    val timePicker = TimePickerDialog(
        LocalContext.current,
        onTimesetListener,
        date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE),
        true
    )
    timePicker.setOnDismissListener(DialogInterface.OnDismissListener {
        showTimePicker = false
    })
    if (showTimePicker && !timePicker.isShowing) {
        timePicker.show()
    }
    if(!showTimePicker && timePicker.isShowing){
        timePicker.dismiss()
    }

    Button(
        onClick = {
            showTimePicker = true
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
                .copy(alpha = slightlyDeemphasizedAlpha),
        ),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .padding(vertical = 20.dp)
            .height(54.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)),
    ) {
        Text(
            text = formatter.format(date.timeInMillis ?: getDefaultDateInMillis()),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.8f)
        )
        Icon(
            imageVector = Icons.Filled.ArrowDropDown,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.2f)
        )
    }
}

private tailrec fun Context.findActivity(): AppCompatActivity =
    when (this) {
        is AppCompatActivity -> this
        is ContextWrapper -> this.baseContext.findActivity()
        else -> throw IllegalArgumentException("Could not find activity!")
    }


@Preview(name = "Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DateQuestionPreview() {
    MarquagePiquetageTheme {
        Surface {
            DateImplQuestion(
                titleResourceId = R.string.questionDateTitle,
                directionsResourceId = R.string.questionDateDirections,
                date = Calendar.getInstance(),
                setDate = {},
                chantierDuration = 22,
                setChantierDuration = {},
            )
        }
    }
}
fun View.vibrate() = reallyPerformHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
fun View.vibrateStrong() = reallyPerformHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

private fun View.reallyPerformHapticFeedback(feedbackConstant: Int) {
    if (context.isTouchExplorationEnabled()) {
        // Don't mess with a blind person's vibrations
        return
    }
    // Either this needs to be set to true, or android:hapticFeedbackEnabled="true" needs to be set in XML
    isHapticFeedbackEnabled = true

    // Most of the constants are off by default: for example, clicking on a button doesn't cause the phone to vibrate anymore
    // if we still want to access this vibration, we'll have to ignore the global settings on that.
    performHapticFeedback(feedbackConstant, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
}

private fun Context.isTouchExplorationEnabled(): Boolean {
    // can be null during unit tests
    val accessibilityManager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager?
    return accessibilityManager?.isTouchExplorationEnabled ?: false
}