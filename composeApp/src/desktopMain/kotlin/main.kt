import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import app.App

fun main() = application {
    Window(
        // This is the window title
        title = "Politer AI",
        onCloseRequest = ::exitApplication
    ) {
        App()
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    App()
}