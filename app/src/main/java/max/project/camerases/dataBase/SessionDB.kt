package max.project.camerases.dataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Sessions::class], version = 1, exportSchema = false)
abstract class SessionDB:RoomDatabase() {
    abstract fun sessionDao(): SessionDAO

    companion object{
        @Volatile
        private var INSTANCE: SessionDB? = null

        fun getDatabase(context: Context): SessionDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SessionDB::class.java,
                    "session_database"
                ).build()
                INSTANCE = instance
                instance
            }

        }
    }

}