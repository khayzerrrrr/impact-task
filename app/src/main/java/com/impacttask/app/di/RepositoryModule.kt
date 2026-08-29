package com.impacttask.app.di

import com.impacttask.app.data.identity.LocalOwnerIdProvider
import com.impacttask.app.data.identity.OwnerIdProvider
import com.impacttask.app.data.repository.GainRepository
import com.impacttask.app.data.repository.GainRepositoryImpl
import com.impacttask.app.data.repository.TaskRepository
import com.impacttask.app.data.repository.TaskRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository

    @Binds
    abstract fun bindGainRepository(impl: GainRepositoryImpl): GainRepository

    /** Swapped for a Firebase-Auth-backed implementation in Fase 2. */
    @Binds
    abstract fun bindOwnerIdProvider(impl: LocalOwnerIdProvider): OwnerIdProvider
}
