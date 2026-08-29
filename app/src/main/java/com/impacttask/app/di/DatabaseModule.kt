package com.impacttask.app.di

import android.content.Context
import androidx.room.Room
import com.impacttask.app.data.db.ExpLedgerDao
import com.impacttask.app.data.db.GainDao
import com.impacttask.app.data.db.ImpactDatabase
import com.impacttask.app.data.db.TaskDao
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
    fun provideDatabase(@ApplicationContext context: Context): ImpactDatabase =
        Room.databaseBuilder(context, ImpactDatabase::class.java, "impact_task.db").build()

    @Provides
    fun provideTaskDao(db: ImpactDatabase): TaskDao = db.taskDao()

    @Provides
    fun provideGainDao(db: ImpactDatabase): GainDao = db.gainDao()

    @Provides
    fun provideExpLedgerDao(db: ImpactDatabase): ExpLedgerDao = db.expLedgerDao()
}
