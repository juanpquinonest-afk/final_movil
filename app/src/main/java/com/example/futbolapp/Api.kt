package com.example.futbolapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface SportsApi {
    @GET("searchteams.php")
    suspend fun searchTeams(@Query("t") name: String): TeamsResponse

    @GET("lookup_all_players.php")
    suspend fun getPlayers(@Query("id") teamId: String): PlayersResponse

    @GET("lookuptable.php")
    suspend fun getTable(@Query("l") leagueId: String): TableResponse

    @GET("lookupplayer.php")
    suspend fun getPlayer(@Query("id") playerId: String): PlayerDetailResponse
}

object ApiClient {
    val api: SportsApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.thesportsdb.com/api/v1/json/123/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SportsApi::class.java)
    }
}
