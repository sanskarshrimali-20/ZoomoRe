package com.zoomore.reelapp.presentation.ui.search.search_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zoomore.reelapp.repo.network.suggestions.DefaultSuggestionsRepo
import com.zoomore.reelapp.models.user.User
import com.zoomore.reelapp.repo.network.suggestions.SuggestionsRepo
import com.zoomore.reelapp.utils.architecture.BaseViewModel
import kotlinx.coroutines.*
import java.util.*

class SearchPageViewModel(
    private val suggestionsRepo: SuggestionsRepo = DefaultSuggestionsRepo()
) : BaseViewModel() {
    private var job: Job = Job()

    val liveQuery = MutableLiveData("")

    private val _liveSuggestions = MutableLiveData<List<User>>()
    val liveSuggestions: LiveData<List<User>> = _liveSuggestions

    private val onQueryChanged: (String?) -> Unit = {
        fetchSuggestions()
    }

    init {
        liveQuery.observeForever(onQueryChanged)
    }

    private fun fetchSuggestions() {
        val query = liveQuery.value?.toLowerCase(Locale.ENGLISH)
        if (query.isNullOrBlank()) return

        job.cancel()
        job = viewModelScope.launch {
            println("Before delay: Doing job")
            delay(1250)
            println("After delay: Doing job")
            _liveSuggestions.value = suggestionsRepo.fetchSuggestions(query).tryData() ?: listOf()
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
        liveQuery.removeObserver(onQueryChanged)
    }
}