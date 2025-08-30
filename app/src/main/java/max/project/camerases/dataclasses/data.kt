package max.project.camerases.dataclasses

import android.graphics.Bitmap

data class PhotoInfo(var bitmap: Bitmap, var title: String )
data class SessionInfo(var sessionId: String, var photos: List<PhotoInfo>, var name: String, var age:Int){
    constructor():this("", emptyList(),"",0)
}
