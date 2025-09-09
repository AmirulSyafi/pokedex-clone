package com.amiruls.pokedex.di

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.amiruls.pokedex.data.remote.FakePokemonApi
import com.amiruls.pokedex.data.remote.PokemonApi
import com.amiruls.pokedex.data.repository.FakePokemonRepository
import com.amiruls.pokedex.data.repository.PokemonRepositoryInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
object FakeAppModule {

    @Provides
    @Singleton
    fun providePokemonApi(): PokemonApi = FakePokemonApi()


    @Provides
    @Singleton
    fun providePokemonRepository(api: PokemonApi): PokemonRepositoryInterface =
        FakePokemonRepository(api)
}