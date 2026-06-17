package com.example.movieexplorer.di

import com.example.movieexplorer.data.remote.FakeMovieApiService
import com.example.movieexplorer.data.remote.MovieApiService
import com.example.movieexplorer.data.repository.DefaultMovieRepository
import com.example.movieexplorer.data.repository.DefaultSettingsRepository
import com.example.movieexplorer.domain.repository.MovieRepository
import com.example.movieexplorer.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindMovieApiService(
        fakeMovieApiService: FakeMovieApiService,
    ): MovieApiService

    @Binds
    @Singleton
    abstract fun bindMovieRepository(
        defaultMovieRepository: DefaultMovieRepository,
    ): MovieRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        defaultSettingsRepository: DefaultSettingsRepository,
    ): SettingsRepository
}
