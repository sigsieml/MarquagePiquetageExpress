/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.sieml.marquagepiquetage.pdf

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Manages the creation of photo Uris. The Uri is used to store the photos taken with camera.
 */
class PhotoUriManager(private val appContext: Context) {
    fun buildNewUri(): Uri {
        // Add a specific media item.
        val resolver: ContentResolver = appContext.getContentResolver()

// Find all audio files on the primary external storage device.
        val imageCollection: Uri
        imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media
                .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        // Générer un nom de fichier unique pour la photo
        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(Date())
        val fileName = "IMG_$timeStamp.jpg"
        val newImageDetails = ContentValues()
        newImageDetails.put(
            MediaStore.Images.Media.DISPLAY_NAME,
            fileName
        )
        newImageDetails.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        return resolver
            .insert(imageCollection, newImageDetails)!!
    }


}
