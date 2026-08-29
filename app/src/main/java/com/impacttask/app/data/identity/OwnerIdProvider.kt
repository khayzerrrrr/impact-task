package com.impacttask.app.data.identity

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Scopes every Room row to an owner from day one so Fase 2 (real accounts,
 * Firestore sync) never needs a schema migration -- see prd.md section 14.
 *
 * Fase 1 has no backend: [getOwnerId] is a random UUID persisted locally,
 * generated once on first launch. Fase 2 replaces this implementation with
 * one backed by Firebase Auth anonymous sign-in (upgraded to Google/email on
 * login) behind the same interface, so callers never change.
 */
interface OwnerIdProvider {
    suspend fun getOwnerId(): String
}

private val Context.identityDataStore: DataStore<Preferences> by preferencesDataStore(name = "impact_task_identity")

@Singleton
class LocalOwnerIdProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) : OwnerIdProvider {

    override suspend fun getOwnerId(): String {
        val existing = context.identityDataStore.data.map { it[OWNER_ID_KEY] }.first()
        if (existing != null) return existing

        val fresh = UUID.randomUUID().toString()
        val prefs = context.identityDataStore.edit { mutable ->
            if (mutable[OWNER_ID_KEY] == null) {
                mutable[OWNER_ID_KEY] = fresh
            }
        }
        return prefs[OWNER_ID_KEY]!!
    }

    private companion object {
        val OWNER_ID_KEY = stringPreferencesKey("owner_id")
    }
}
