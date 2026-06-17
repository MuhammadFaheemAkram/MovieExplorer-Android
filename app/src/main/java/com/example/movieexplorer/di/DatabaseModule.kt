package com.example.movieexplorer.di

import android.content.Context
import androidx.room.Room
import com.example.movieexplorer.core.database.MovieExplorerDatabase
import com.example.movieexplorer.data.local.MovieDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMovieExplorerDatabase(
        @ApplicationContext context: Context,
    ): MovieExplorerDatabase = Room.databaseBuilder(
        context,
        MovieExplorerDatabase::class.java,
        "movie_explorer.db",
    ).build()

    @Provides
    fun provideMovieDao(database: MovieExplorerDatabase): MovieDao =
        database.movieDao()
}
