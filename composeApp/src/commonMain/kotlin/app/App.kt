package app

import androidx.compose.runtime.Composable
import modules.home.HomeView
import values.PoliterAiTheme

@Composable
fun App() {
    PoliterAiTheme {
        // Set current Koin instance to Compose context
//        KoinContext() {
        HomeView()
//        }
    }
}