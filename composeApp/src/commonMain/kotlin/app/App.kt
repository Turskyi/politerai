package app

import androidx.compose.runtime.Composable
import modules.home.HomeView
import values.PoliterTheme

@Composable
fun App() {
    PoliterTheme {
        HomeView()
    }
}