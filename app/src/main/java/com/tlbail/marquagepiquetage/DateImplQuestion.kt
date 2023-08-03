package com.tlbail.marquagepiquetage


import android.app.TimePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.widget.TimePicker
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tlbail.marquagepiquetage.ui.theme.MarquagePiquetageTheme
import com.tlbail.marquagepiquetage.ui.theme.slightlyDeemphasizedAlpha
import com.tlbail.marquagepiquetage.util.getDefaultDateInMillis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateImplQuestion(
    @StringRes titleResourceId: Int,
    @StringRes directionsResourceId: Int,
    date: Calendar = Calendar.getInstance(),
    setDate: (Long) -> Unit,
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
    if (showTimePicker) {
        timePicker.show()
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
            )
        }
    }
}
