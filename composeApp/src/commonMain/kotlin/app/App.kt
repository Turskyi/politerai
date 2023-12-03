package app

import androidx.compose.runtime.Composable
import modules.home.HomeView
import values.PoliterAiTheme

@Composable
fun App() {
    PoliterAiTheme {
        HomeView()
    }
}