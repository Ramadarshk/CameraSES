package max.project.camerases.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.time.debounce
import max.project.camerases.dataBase.SessionDB
import max.project.camerases.dataBase.Sessions

class SessionViews(context: Context): ViewModel()  {


    private val sessionDao = SessionDB.getDatabase(context).sessionDao()
    private val _searchQuery = MutableStateFlow("")

    val searches: StateFlow<List<Sessions>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                sessionDao.getAllSessions() // Show all if search empty
            } else {
                sessionDao.searchSessions(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    val allSessions: StateFlow<List<Sessions>> =
        sessionDao.getAllSessions()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
class SessionViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SessionViews::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SessionViews(context.applicationContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}