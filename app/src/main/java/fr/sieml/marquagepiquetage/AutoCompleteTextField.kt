package fr.sieml.marquagepiquetage

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoCompleteTextField(
    label: String,
    options: Array<String>,
    value: String,
    onTextChaned: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = {
        expanded = !expanded
    }) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                onTextChaned(it)
            },
            label = {
                Text(
                    text = label
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            keyboardOptions = KeyboardOptions(
                imeAction = androidx.compose.ui.text.input.ImeAction.Done
            )
        )
        // filter options based on text field value
        var filteringOptions =
            options.filter { it.contains(value, ignoreCase = true) }
        if (filteringOptions.isNotEmpty()) {
            //truncate first 100 options
            filteringOptions  = filteringOptions.take(100)
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                filteringOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        onClick = {
                            onTextChaned(selectionOption)
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
}