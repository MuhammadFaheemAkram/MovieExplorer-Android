package co.bitfuse.movieexplorer.di

import co.bitfuse.movieexplorer.data.remote.FakeMovieApiService
import co.bitfuse.movieexplorer.data.remote.MovieApiService
import co.bitfuse.movieexplorer.data.repository.DefaultMovieRepository
import co.bitfuse.movieexplorer.data.repository.DefaultSettingsRepository
import co.bitfuse.movieexplorer.domain.repository.MovieRepository
import co.bitfuse.movieexplorer.domain.repository.SettingsRepository
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
