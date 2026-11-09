package com.example.futbolapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                App()
            }
        }
    }
}

@Composable
fun App(vm: FootballViewModel = viewModel()) {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "search") {
        composable("search") { SearchScreen(vm, nav) }
        composable("team") { TeamScreen(vm, nav) }
        composable("players") { PlayersScreen(vm, nav) }
        composable("table") { TableScreen(vm, nav) }
        composable("player") { PlayerScreen(vm, nav) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen(
    title: String,
    nav: NavController?,
    vm: FootballViewModel,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (nav != null) {
                        IconButton(onClick = { nav.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (vm.loading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            vm.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            content()
        }
    }
}

// ---------- 1. Búsqueda de equipo ----------
@Composable
fun SearchScreen(vm: FootballViewModel, nav: NavController) {
    var query by remember { mutableStateOf("") }
    Screen("Buscar equipo", null, vm) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Nombre del equipo") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = { vm.searchTeam(query) }) { Text("Buscar") }
        }
        Spacer(Modifier.height(12.dp))
        if (vm.searched && vm.teams.isEmpty() && !vm.loading) {
            Text("No se encontraron equipos.")
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(vm.teams) { team ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            vm.selectTeam(team)
                            nav.navigate("team")
                        }
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = team.strTeamBadge,
                            contentDescription = team.strTeam,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(team.strTeam ?: "", fontWeight = FontWeight.Bold)
                            Text("${team.strLeague ?: ""} · ${team.strCountry ?: ""}")
                        }
                    }
                }
            }
        }
    }
}

// ---------- Detalle del equipo ----------
@Composable
fun TeamScreen(vm: FootballViewModel, nav: NavController) {
    val team = vm.selectedTeam ?: return
    Screen(team.strTeam ?: "Equipo", nav, vm) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = team.strTeamBadge,
                contentDescription = team.strTeam,
                modifier = Modifier.size(140.dp)
            )
            Text(team.strLeague ?: "", style = MaterialTheme.typography.titleMedium)
            Text("Estadio: ${team.strStadium ?: "N/D"}")
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                vm.loadPlayers()
                nav.navigate("players")
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Ver jugadores") }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = {
                vm.loadTable()
                nav.navigate("table")
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Ver tabla de posiciones") }
    }
}

// ---------- 2. Lista de jugadores ----------
@Composable
fun PlayersScreen(vm: FootballViewModel, nav: NavController) {
    Screen("Jugadores", nav, vm) {
        if (!vm.loading && vm.players.isEmpty()) Text("No hay jugadores disponibles.")
        LazyColumn {
            items(vm.players) { p ->
                ListItem(
                    headlineContent = { Text(p.strPlayer ?: "") },
                    supportingContent = { Text(p.strPosition ?: "") },
                    modifier = Modifier.clickable {
                        vm.selectPlayer(p)
                        nav.navigate("player")
                    }
                )
                HorizontalDivider()
            }
        }
    }
}

// ---------- 3. Tabla de posiciones ----------
@Composable
fun TableScreen(vm: FootballViewModel, nav: NavController) {
    Screen("Tabla de posiciones", nav, vm) {
        if (!vm.loading && vm.table.isEmpty()) Text("No hay tabla disponible para esta liga.")
        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Text("#", Modifier.width(28.dp), fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(28.dp))
            Text("Equipo", Modifier.weight(1f), fontWeight = FontWeight.Bold)
            listOf("PJ", "G", "E", "P", "DG", "Pts").forEach {
                Text(it, Modifier.width(34.dp), fontWeight = FontWeight.Bold)
            }
        }
        HorizontalDivider()
        val mine = vm.selectedTeam?.idTeam
        LazyColumn {
            items(vm.table) { r ->
                val weight = if (r.idTeam == mine) FontWeight.Bold else FontWeight.Normal
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(r.intRank ?: "", Modifier.width(28.dp), fontWeight = weight)
                    AsyncImage(
                        model = r.strBadge,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(r.strTeam ?: "", Modifier.weight(1f), fontWeight = weight, maxLines = 1)
                    listOf(
                        r.intPlayed, r.intWin, r.intDraw,
                        r.intLoss, r.intGoalDifference, r.intPoints
                    ).forEach {
                        Text(it ?: "-", Modifier.width(34.dp), fontWeight = weight)
                    }
                }
            }
        }
    }
}

// ---------- 4. Foto del jugador ----------
@Composable
fun PlayerScreen(vm: FootballViewModel, nav: NavController) {
    val p = vm.selectedPlayer ?: return
    val photo = p.photo()
    Screen(p.strPlayer ?: "Jugador", nav, vm) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            if (photo != null) {
                AsyncImage(
                    model = photo,
                    contentDescription = p.strPlayer,
                    modifier = Modifier.size(260.dp),
                    contentScale = ContentScale.Fit
                )
            } else if (!vm.loading) {
                Text("Este jugador no tiene foto disponible.")
            }
            Spacer(Modifier.height(12.dp))
            Text(p.strPlayer ?: "", style = MaterialTheme.typography.headlineSmall)
            Text("Posición: ${p.strPosition ?: "N/D"}")
            Text("Nacionalidad: ${p.strNationality ?: "N/D"}")
        }
    }
}
