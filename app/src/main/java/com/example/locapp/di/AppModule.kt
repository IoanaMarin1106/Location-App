package com.example.locapp.di

import android.app.Application
import androidx.room.Room
import com.example.locapp.App
import com.example.locapp.MainActivity
import com.example.locapp.room.datasource.RoomDatabase
import com.example.locapp.room.repository.Repository
import com.example.locapp.room.repository.RepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideMyDataBase(app: Application): RoomDatabase {
        return Room.databaseBuilder(
            app,
            RoomDatabase::class.java,
            "locations_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideMyRepository(database: RoomDatabase): Repository {
        return RepositoryImpl(database.locationDao())
    }
}