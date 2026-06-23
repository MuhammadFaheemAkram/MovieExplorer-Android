package co.bitfuse.movieexplorer.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import co.bitfuse.movieexplorer.data.local.MovieDao
import co.bitfuse.movieexplorer.data.local.MovieEntity

@Database(
    entities = [MovieEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class MovieExplorerDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}
