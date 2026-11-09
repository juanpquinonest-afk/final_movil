package com.example.futbolapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class FootballViewModel : ViewModel() {

    var teams by mutableStateOf<List<Team>>(emptyList())
        private set
    var selectedTeam by mutableStateOf<Team?>(null)
        private set
    var players by mutableStateOf<List<Player>>(emptyList())
        private set
    var table by mutableStateOf<List<TableRow>>(emptyList())
        private set
    var selectedPlayer by mutableStateOf<Player?>(null)
        private set
    var loading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var searched by mutableStateOf(false)
        private set

    private val api = ApiClient.api

    private fun load(block: suspend () -> Unit) {
        viewModelScope.launch {
            loading = true
            error = null
            try {
                block()
            } catch (e: Exception) {
                error = "Error de conexión: ${e.message}"
            }
            loading = false
        }
    }

    // Punto 1: buscar equipo por nombre
    fun searchTeam(name: String) {
        if (name.isBlank()) return
        load {
            teams = api.searchTeams(name.trim()).teams ?: emptyList()
            searched = true
        }
    }

    fun selectTeam(team: Team) {
        selectedTeam = team
        players = emptyList()
        table = emptyList()
    }

    // Punto 2: jugadores del equipo
    fun loadPlayers() {
        val id = selectedTeam?.idTeam ?: return
        load { players = api.getPlayers(id).player ?: emptyList() }
    }

    // Punto 3: tabla de posiciones de la liga del equipo
    fun loadTable() {
        val id = selectedTeam?.idLeague ?: return
        load { table = api.getTable(id).table ?: emptyList() }
    }

    // Punto 4: detalle y foto del jugador
    fun selectPlayer(player: Player) {
        selectedPlayer = player
        val id = player.idPlayer ?: return
        load {
            api.getPlayer(id).players?.firstOrNull()?.let { selectedPlayer = it }
        }
    }
}
