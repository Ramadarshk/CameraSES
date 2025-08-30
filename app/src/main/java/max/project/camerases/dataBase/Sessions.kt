package max.project.camerases.dataBase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class Sessions (
    @PrimaryKey()
    val sessionId: String,

    val name: String,
    val age: Int
)


