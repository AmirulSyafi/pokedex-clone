package com.amiruls.pokedex.di

import com.amiruls.pokedex.data.remote.PokemonApi
import com.amiruls.pokedex.data.repository.PokemonRepository
import com.amiruls.pokedex.data.repository.PokemonRepositoryInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "https://pokeapi.co/api/v2/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder().build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun providePokemonApi(retrofit: Retrofit): PokemonApi =
        retrofit.create(PokemonApi::class.java)

    @Provides
    @Singleton
    fun providePokemonRepository(api: PokemonApi): PokemonRepositoryInterface =
        PokemonRepository(api)
}
