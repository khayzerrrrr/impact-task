package com.impacttask.app.data.repository

import com.impacttask.Gain
import com.impacttask.app.data.db.GainDao
import com.impacttask.app.data.db.entity.GainEntity
import com.impacttask.app.data.identity.OwnerIdProvider
import com.impacttask.app.domain.model.GainProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface GainRepository {
    fun observeGains(): Flow<List<GainProgress>>
}

@Singleton
class GainRepositoryImpl @Inject constructor(
    private val gainDao: GainDao,
    private val ownerIdProvider: OwnerIdProvider,
) : GainRepository {

    override fun observeGains(): Flow<List<GainProgress>> = flow {
        val ownerId = ownerIdProvider.getOwnerId()
        // Idempotent: seeds the 6 Gains at zero EXP the first time this owner
        // is seen, no-ops on every later call.
        gainDao.insertIfAbsent(Gain.entries.map { GainEntity(id = it, ownerId = ownerId, totalExp = 0) })
        emitAll(gainDao.observeGains(ownerId))
    }.map { rows ->
        val byGain = rows.associateBy { it.id }
        Gain.entries.map { gain ->
            GainProgress(gain = gain, totalExp = byGain[gain]?.totalExp ?: 0)
        }
    }
}
