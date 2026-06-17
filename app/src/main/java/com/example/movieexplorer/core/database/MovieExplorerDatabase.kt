package com.example.movieexplorer.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.movieexplorer.data.local.MovieDao
import com.example.movieexplorer.data.local.MovieEntity

@Database(
    entities = [MovieEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class MovieExplorerDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}
