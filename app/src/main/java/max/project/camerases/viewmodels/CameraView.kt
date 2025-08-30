package max.project.camerases.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import max.project.camerases.dataclasses.PhotoInfo
import max.project.camerases.dataclasses.SessionInfo
import java.io.File
import java.io.FileOutputStream
import androidx.core.graphics.createBitmap
import max.project.camerases.dataBase.SessionDB
import max.project.camerases.dataBase.Sessions

class CameraView: ViewModel() {
    private val _sessionDetails = MutableStateFlow(SessionInfo())
    val bitmapImages=_sessionDetails.asStateFlow()
    fun onPhotoTaken(
        bitmap: Bitmap , title: String , context: Context
    ) {
        _sessionDetails.value =_sessionDetails.value.copy(
            photos = _sessionDetails.value.photos + PhotoInfo(bitmap, title)
        )
        Toast.makeText( context, "photo Saved" , Toast.LENGTH_SHORT).show()
    }
    fun save(sessionInfo: SessionInfo, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val dir = context.filesDir
            val sessionDir = File(dir, "sessions/${sessionInfo.sessionId}")
            if (!sessionDir.exists()) sessionDir.mkdirs()
            Log.d("TAG", "Saving to ${sessionDir.absolutePath} with files: ${sessionDir.listFiles()?.map { it.name }.orEmpty()}")
            sessionInfo.photos.forEach { photo ->
                val safeTitle = photo.title.replace(Regex("[^A-Za-z0-9_]"), "_")
                val file = File(sessionDir, "IMG_${safeTitle}.jpg")

                FileOutputStream(file).use { out ->
                    val success = photo.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    Log.d("TAG", "Saving ${file.name}: success=$success, path=${file.absolutePath}")
                }

                Log.d("TAG", "File exists=${file.exists()}, size=${file.length()}")
            }
            SessionDB.getDatabase(context)
                .sessionDao()
                .upsertSession(
                    Sessions(
                        sessionId = sessionInfo.sessionId,
                        name = sessionInfo.name,
                        age = sessionInfo.age
                    )
                )
            _sessionDetails.value = SessionInfo()
        }
    }
    fun takePicture(controller: LifecycleCameraController, context: Context){
        controller.takePicture(
            ContextCompat.getMainExecutor(context),

            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    val matrix = Matrix().apply {
                        postRotate(image.imageInfo.rotationDegrees.toFloat())
                    }
                    val rotatedBitmap = Bitmap.createBitmap(
                        image.toBitmap() ,
                        0 ,
                        0 ,
                        image.width ,
                        image.height ,
                        matrix ,
                        true
                    )
                    val name = System.currentTimeMillis()
                    onPhotoTaken(rotatedBitmap , name.toString() , context)
                    image.close()
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e("takePhoto" , "can't take photo due to an Error: ${exception.message}")
                }
            }

        )
    }

    fun updateName(name: String) {
        _sessionDetails.value = _sessionDetails.value.copy(name = name)
    }

    fun updateAge(age: Int) {
        _sessionDetails.value = _sessionDetails.value.copy(age = age)
    }

    fun updateID(){
        _sessionDetails.value = _sessionDetails.value.copy(
            sessionId = "${_sessionDetails.value.name}_${_sessionDetails.value.age}_${System.currentTimeMillis()}"
        )
    }
    fun getAllPhotos(sessionId: String,context: Context): List<File> {
        val dir = context.filesDir
        val sessionDir = File(dir, "sessions/${sessionId}")
        return sessionDir.listFiles()?.toList() ?: emptyList()
    }
}