package com.example.android_dev_test.di

import android.content.Context
import com.example.android_dev_test.data.local.PokemonDao
import com.example.android_dev_test.data.local.PokemonDatabase
import com.example.android_dev_test.data.network.ApiService
import com.example.android_dev_test.data.network.PokemonRetrofit
import com.example.android_dev_test.data.repository.PokemonRepository
import com.example.android_dev_test.data.repository.PokemonRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providePokemonService(): ApiService {
        return PokemonRetrofit.pokemonApiService
    }

    @Provides
    @Singleton
    fun providePokemonRepository(
        pokemonService: ApiService,
        pokemonDatabase: PokemonDatabase
    ): PokemonRepository {
        return PokemonRepositoryImpl(pokemonService, pokemonDatabase)
    }

    @Provides
    @Singleton
    fun providePokemonDatabase(@ApplicationContext context: Context): PokemonDatabase {
        return PokemonDatabase.getDatabase(context)
    }

}