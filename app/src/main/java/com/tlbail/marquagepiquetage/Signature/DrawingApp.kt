package com.tlbail.marquagepiquetage.Signature

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Picture
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.view.MotionEvent
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.tlbail.marquagepiquetage.ui.theme.backgroundColor
import java.io.File
import java.io.FileOutputStream

@Preview
@Composable
fun DrawingApp(
    paddingValues: PaddingValues = PaddingValues(0.dp),
    paths: MutableList<Pair<Path, PathProperties>> = remember { mutableStateListOf<Pair<Path, PathProperties>>() },
    size: MutableState<IntSize> = remember { mutableStateOf(IntSize.Zero) }
) {

    val context = LocalContext.current
    val pathsUndone = remember { mutableStateListOf<Pair<Path, PathProperties>>() }
    var motionEvent by remember { mutableStateOf(MotionEvent.ACTION_OUTSIDE) }
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }
    var drawMode by remember { mutableStateOf(DrawMode.Draw) }
    var currentPath by remember { mutableStateOf(Path()) }
    var currentPathProperty by remember { mutableStateOf(PathProperties()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        val drawModifier = Modifier
            .padding(8.dp)
            .shadow(1.dp)
            .fillMaxSize()
            .weight(1f)
            .background(Color.White)
            .dragMotionEvent(
                onDragStart = { pointerInputChange ->
                    motionEvent = MotionEvent.ACTION_DOWN
                    currentPosition = pointerInputChange.position
                    pointerInputChange.consumeDownChange()

                },
                onDrag = { pointerInputChange ->
                    motionEvent = MotionEvent.ACTION_MOVE
                    currentPosition = pointerInputChange.position

                    if (drawMode == DrawMode.Touch) {
                        val change = pointerInputChange.positionChange()
                        println("DRAG: $change")
                        paths.forEach { entry ->
                            val path: Path = entry.first
                            path.translate(change)
                        }
                        currentPath.translate(change)
                    }
                    pointerInputChange.consumePositionChange()

                },
                onDragEnd = { pointerInputChange ->
                    motionEvent = MotionEvent.ACTION_UP
                    pointerInputChange.consumeDownChange()
                }
            )

        Canvas(modifier = drawModifier.onGloballyPositioned { coordinates ->
            size.value = coordinates.size
        }) {

            when (motionEvent) {

                MotionEvent.ACTION_DOWN -> {
                    if (drawMode != DrawMode.Touch) {
                        currentPath.moveTo(currentPosition.x, currentPosition.y)
                    }

                    previousPosition = currentPosition

                }
                MotionEvent.ACTION_MOVE -> {

                    if (drawMode != DrawMode.Touch) {
                        currentPath.quadraticBezierTo(
                            previousPosition.x,
                            previousPosition.y,
                            (previousPosition.x + currentPosition.x) / 2,
                            (previousPosition.y + currentPosition.y) / 2

                        )
                    }

                    previousPosition = currentPosition
                }

                MotionEvent.ACTION_UP -> {
                    if (drawMode != DrawMode.Touch) {
                        currentPath.lineTo(currentPosition.x, currentPosition.y)

                        // Pointer is up save current path
//                        paths[currentPath] = currentPathProperty
                        paths.add(Pair(currentPath, currentPathProperty))

                        // Since paths are keys for map, use new one for each key
                        // and have separate path for each down-move-up gesture cycle
                        currentPath = Path()

                        // Create new instance of path properties to have new path and properties
                        // only for the one currently being drawn
                        currentPathProperty = PathProperties(
                            strokeWidth = currentPathProperty.strokeWidth,
                            color = currentPathProperty.color,
                            strokeCap = currentPathProperty.strokeCap,
                            strokeJoin = currentPathProperty.strokeJoin,
                            eraseMode = currentPathProperty.eraseMode
                        )
                    }

                    // Since new path is drawn no need to store paths to undone
                    pathsUndone.clear()

                    // If we leave this state at MotionEvent.Up it causes current path to draw
                    // line from (0,0) if this composable recomposes when draw mode is changed
                    currentPosition = Offset.Unspecified
                    previousPosition = currentPosition
                    motionEvent = MotionEvent.ACTION_OUTSIDE
                }
                else -> Unit
            }

            with(drawContext.canvas.nativeCanvas) {

                val checkPoint = saveLayer(null, null)

                paths.forEach {

                    val path = it.first
                    val property = it.second

                    if (!property.eraseMode) {
                        drawPath(
                            color = property.color,
                            path = path,
                            style = Stroke(
                                width = property.strokeWidth,
                                cap = property.strokeCap,
                                join = property.strokeJoin
                            )
                        )
                    } else {

                        // Source
                        drawPath(
                            color = Color.Transparent,
                            path = path,
                            style = Stroke(
                                width = currentPathProperty.strokeWidth,
                                cap = currentPathProperty.strokeCap,
                                join = currentPathProperty.strokeJoin
                            ),
                            blendMode = BlendMode.Clear
                        )
                    }
                }

                if (motionEvent != MotionEvent.ACTION_OUTSIDE) {

                    if (!currentPathProperty.eraseMode) {
                        drawPath(
                            color = currentPathProperty.color,
                            path = currentPath,
                            style = Stroke(
                                width = currentPathProperty.strokeWidth,
                                cap = currentPathProperty.strokeCap,
                                join = currentPathProperty.strokeJoin
                            )
                        )
                    } else {
                        drawPath(
                            color = Color.Transparent,
                            path = currentPath,
                            style = Stroke(
                                width = currentPathProperty.strokeWidth,
                                cap = currentPathProperty.strokeCap,
                                join = currentPathProperty.strokeJoin
                            ),
                            blendMode = BlendMode.Clear
                        )
                    }
                }
                restoreToCount(checkPoint)
            }
        }

        DrawingPropertiesMenu(
            modifier = Modifier
                .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
                .shadow(1.dp, RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .background(Color.White)
                .padding(4.dp),
            pathProperties = currentPathProperty,
            onUndo = {
                if (paths.isNotEmpty()) {

                    val lastItem = paths.last()
                    val lastPath = lastItem.first
                    val lastPathProperty = lastItem.second
                    paths.remove(lastItem)

                    pathsUndone.add(Pair(lastPath, lastPathProperty))

                }
            },
            onRedo = {
                if (pathsUndone.isNotEmpty()) {

                    val lastPath = pathsUndone.last().first
                    val lastPathProperty = pathsUndone.last().second
                    pathsUndone.removeLast()
                    paths.add(Pair(lastPath, lastPathProperty))
                }
            }
        )
    }
}


fun createImageBitmapFromCanvas(paths: List<Pair<Path, PathProperties>>, size: IntSize): ImageBitmap {
    val picture = Picture()
    val recCanvas = picture.beginRecording(size.width, size.height)
    recCanvas.drawColor(Color.White.toArgb())
    paths.forEach {
        val path = it.first
        val property = it.second

        if (!property.eraseMode) {
            recCanvas.drawPath(
                path.asAndroidPath(),
                Paint().apply {
                    color = property.color.toArgb()
                    style = Paint.Style.STROKE
                    strokeWidth = property.strokeWidth
                    strokeCap = when(property.strokeCap) {
                        StrokeCap.Butt -> Paint.Cap.BUTT
                        StrokeCap.Round -> Paint.Cap.ROUND
                        StrokeCap.Square -> Paint.Cap.SQUARE
                        else -> { Paint.Cap.BUTT}
                    }
                    strokeJoin = when(property.strokeJoin) {
                        StrokeJoin.Miter -> Paint.Join.MITER
                        StrokeJoin.Round -> Paint.Join.ROUND
                        StrokeJoin.Bevel -> Paint.Join.BEVEL
                        else -> {
                           Paint.Join.MITER
                        }
                    }
                }
            )
        } else {
            recCanvas.drawPath(
                path.asAndroidPath(),
                Paint().apply {
                    xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                }
            )
        }
    }

    picture.endRecording()

    val bitmap = Bitmap.createBitmap(picture.width, picture.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap.asImageBitmap())
    picture.draw(canvas.nativeCanvas)

    return bitmap.asImageBitmap()
}

fun saveImageBitmapToFile(imageBitmap: ImageBitmap, file: File) {
    val result = imageBitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(file))
    if(result) {
        println("Image saved successfully.")
    } else {
        println("Failed to save image.")
    }
}
