package com.morfitime.application.injection

import android.content.Context
import androidx.room.Room
import com.google.gson.GsonBuilder
import com.morfitime.application.Constants.BASE_URL
import com.morfitime.data.local.database.AppDatabase
import com.morfitime.data.remote.WebService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideRoomInstance(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, "db_morfitime").fallbackToDestructiveMigration().build()
    @Singleton
    @Provides
    fun provideUserDao(appDatabase: AppDatabase) = appDatabase.userDao()
    @Singleton
    @Provides
    fun provideRetrofitInstance(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()
    @Singleton
    @Provides
    fun provideWebService(retrofit: Retrofit): WebService = retrofit.create(WebService::class.java)
}