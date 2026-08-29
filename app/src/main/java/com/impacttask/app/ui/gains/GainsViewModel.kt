package com.impacttask.app.ui.gains

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.impacttask.app.data.repository.GainRepository
import com.impacttask.app.domain.model.GainProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class GainsViewModel @Inject constructor(
    gainRepository: GainRepository,
) : ViewModel() {

    val gains: StateFlow<List<GainProgress>> = gainRepository.observeGains()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
