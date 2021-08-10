package com.nerinos.marvelcharacters.di

import android.app.Application
import androidx.room.Room
import com.nerinos.marvelcharacters.api.MarvelApi
import com.nerinos.marvelcharacters.data.db.CharactersDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

        return Retrofit.Builder()
            .baseUrl(MarvelApi.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())

            .build()
    }

    @Provides
    @Singleton
    fun provideMarvelApi(retrofit: Retrofit): MarvelApi =
        retrofit.create(MarvelApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(
        appContext: Application,
    ) = Room.databaseBuilder(appContext, CharactersDatabase::class.java, "characters_database")
        .fallbackToDestructiveMigration()
        .build()


}