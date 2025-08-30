package max.project.camerases.Nav

import kotlinx.serialization.Serializable
import max.project.camerases.dataBase.Sessions

@Serializable
object SessionCreateScreen{}

@Serializable
object CameraScreen

@Serializable
object SessionViewScreen{}

@Serializable
object SearchScreen

@Serializable
data class PhotosOfSession(val sessionId: String,val name: String,val age: Int)