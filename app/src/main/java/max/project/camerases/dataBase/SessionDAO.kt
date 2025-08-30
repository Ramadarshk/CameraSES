package max.project.camerases.dataBase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow


@Dao
interface SessionDAO {

    @Upsert
    suspend fun upsertSession(session: Sessions)

    @Delete
    suspend fun deleteSession(session: Sessions)

    @Query("SELECT * FROM sessions ORDER BY sessionId ASC")
    fun getAllSessions(): Flow<List<Sessions>>
    @Query("SELECT * FROM sessions WHERE sessionId = :sessionId")
    fun getSessionById(sessionId: String): Flow<Sessions>

    @Query("SELECT * FROM sessions WHERE sessionId LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%'")
    fun searchSessions(query: String): Flow<List<Sessions>>
}