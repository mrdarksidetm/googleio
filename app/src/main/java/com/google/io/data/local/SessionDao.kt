package com.google.io.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.google.io.data.model.Session
import kotlinx.coroutines.flow.Flow

/**
 * DAO stands for "Data Access Object". 
 * It's like a list of instructions we give to the database.
 * "Hey database, please save this!" or "Hey database, give me all the saved talks!"
 */
@Dao
interface SessionDao {
    // This gives us all the sessions saved in the database
    @Query("SELECT * FROM sessions")
    fun getAllSessions(): Flow<List<Session>>

    // This gives us ONLY the sessions we bookmarked (the ones we liked)
    @Query("SELECT * FROM sessions WHERE isBookmarked = 1")
    fun getBookmarkedSessions(): Flow<List<Session>>

    // This saves sessions into the database. 
    // If we already have a session with the same ID, it just replaces it (REPLACE).
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<Session>)

    /**
     * TASK 1: Intelligent Search & Filtering
     * This query searches through our sessions based on a text query AND a category.
     * 
     * - "LIKE '%' || :query || '%'" looks for the search text anywhere inside 
     *   the title or description. It's like a Google search for our app!
     * - "(:category = 'All' OR category = :category)" is a clever trick. 
     *   If the user picks 'All', it shows everything. Otherwise, it 
     *   matches the specific category (like 'AI' or 'Mobile').
     */
    @Query("""
        SELECT * FROM sessions 
        WHERE (title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%') 
        AND (:category = 'All' OR category = :category)
    """)
    fun getFilteredSessions(query: String, category: String): Flow<List<Session>>

    // Toggle bookmark status
    @Query("UPDATE sessions SET isBookmarked = :isBookmarked WHERE id = :sessionId")
    suspend fun updateBookmark(sessionId: String, isBookmarked: Boolean)
}
