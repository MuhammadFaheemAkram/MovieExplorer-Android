package co.bitfuse.movieexplorer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Query("SELECT * FROM favorite_movies ORDER BY title ASC")
    fun observeFavoriteMovies(): Flow<List<MovieEntity>>

    @Query("SELECT id FROM favorite_movies")
    fun observeFavoriteMovieIds(): Flow<List<Int>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_movies WHERE id = :movieId)")
    suspend fun isFavorite(movieId: Int): Boolean

    @Query("SELECT * FROM favorite_movies WHERE id = :movieId LIMIT 1")
    suspend fun getFavoriteMovie(movieId: Int): MovieEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFavoriteMovie(movie: MovieEntity)

    @Query("DELETE FROM favorite_movies WHERE id = :movieId")
    suspend fun deleteFavoriteMovieById(movieId: Int)
}
