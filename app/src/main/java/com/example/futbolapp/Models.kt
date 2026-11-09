package com.example.futbolapp

data class TeamsResponse(val teams: List<Team>?)

data class Team(
    val idTeam: String?,
    val strTeam: String?,
    val strLeague: String?,
    val idLeague: String?,
    val strCountry: String?,
    val strStadium: String?,
    val strTeamBadge: String?
)

data class PlayersResponse(val player: List<Player>?)
data class PlayerDetailResponse(val players: List<Player>?)

data class Player(
    val idPlayer: String?,
    val strPlayer: String?,
    val strPosition: String?,
    val strNationality: String?,
    val strCutout: String?,
    val strThumb: String?,
    val strRender: String?
) {
    /** Mejor foto disponible del jugador. */
    fun photo(): String? = strThumb ?: strCutout ?: strRender
}

data class TableResponse(val table: List<TableRow>?)

data class TableRow(
    val idTeam: String?,
    val intRank: String?,
    val strTeam: String?,
    val strBadge: String?,
    val intPlayed: String?,
    val intWin: String?,
    val intDraw: String?,
    val intLoss: String?,
    val intGoalDifference: String?,
    val intPoints: String?
)
