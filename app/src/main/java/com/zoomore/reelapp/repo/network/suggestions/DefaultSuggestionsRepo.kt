package com.zoomore.reelapp.repo.network.suggestions

import com.zoomore.reelapp.models.user.User
import com.zoomore.reelapp.repo.network.utils.safeAccess
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class DefaultSuggestionsRepo(
    private val realFire: FirebaseDatabase = Firebase.database
) : SuggestionsRepo {

    fun dummy(input: String) = input

    override suspend fun fetchSuggestions(query: String) = safeAccess {
        realFire
            .getReference("users")
//            .orderByChild("basic-data/username")
            .startAt(query)
            .endBefore((query.first() + 1).toString())
            .orderByChild("basic-data/followers")
            .limitToFirst(12)
            .get()
            .await()
            .getValue<List<User>>() ?: listOf()
    }

}