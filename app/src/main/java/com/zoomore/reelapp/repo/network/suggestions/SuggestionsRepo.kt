package com.zoomore.reelapp.repo.network.suggestions

import com.zoomore.reelapp.models.TheResult
import com.zoomore.reelapp.models.user.User

interface SuggestionsRepo {
    suspend fun fetchSuggestions(query: String): TheResult<List<User>>
}