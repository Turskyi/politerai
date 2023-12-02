import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.Typography
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

// Create a theme for the Compose application
@Composable
fun PoliterTheme(content: @Composable () -> Unit) {
    // Define the colors that you want to use
    val colors: Colors = lightColors(
        primary = Color(0xFF7669FF),
        secondary = Color(0xFF7669FF),
        onPrimary = Color.White,
        onSecondary = Color.White,
    )

    // Define the typography that you want to use
    val typography = Typography(
        h4 = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
            shadow = Shadow(
                color = Color.Black, offset = Offset(x = 2f, y = 2f), blurRadius = 2f
            )
        ), h5 = TextStyle(
            fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 24.sp
        ), h6 = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            shadow = Shadow(
                color = Color.Gray, offset = Offset(1f, 1f), blurRadius = 2f
            )
        ), body1 = TextStyle(
            fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 16.sp
        )
    )

    MaterialTheme(
        colors = colors, typography = typography, content = content
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    PoliterTheme {
        var prompt: String by remember { mutableStateOf("") }
        var message: String by remember { mutableStateOf("") }
        var messageLoading: Boolean by remember { mutableStateOf(false) }
        var messageLoadingError: Boolean by remember { mutableStateOf(false) }
        var showImage: Boolean by remember { mutableStateOf(true) }
        var isTextFieldFocused by remember { mutableStateOf(false) }

        fun handleSubmit() {
            message = ""
            if (prompt.isNotEmpty()) {
                try {
                    messageLoadingError = false
                    messageLoading = true
                    message = if (prompt.length < 5) {
                        "Message too short"
                    } else {
                        // Call the API to get the polite message
                        // For simplicity, I will use a mock function here
                        getPoliteMessage(prompt)
                    }
                } catch (error: Exception) {
                    messageLoadingError = true
                } finally {
                    messageLoading = false
                }
            }
        }

        // Colors for the gradient
        val colors: List<Color> = listOf(Color.White, Color(0xFFDFCAFF))
        // A linear gradient brush with the colors
        val gradient: Brush = Brush.linearGradient(
            colors = colors,
            start = Offset(x = 0f, y = 0f),
            end = Offset(x = 0f, y = 1000f),
        )

        Box(modifier = Modifier.background(gradient).fillMaxHeight()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Politer AI",
                    modifier = Modifier.padding(top = 20.dp, bottom = 12.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h4,
                    color = Color(0xFF7669ff)
                )
                Text(
                    text = "powered by GPT-3",
                    modifier = Modifier.padding(10.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h6
                )
                Text(
                    text = "Type your message below and click the button to make it more polite and friendly.",
                    modifier = Modifier.padding(top = 12.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body1
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { input -> prompt = input },
                    label = {
                        Box(
                            modifier = Modifier.background(
                                Color.White,
                                shape = RoundedCornerShape(4.dp)
                            ).border(
                                1.dp,
                                if (isTextFieldFocused) Color.Gray else Color.Transparent,
                                shape = RoundedCornerShape(2.dp)
                            ).padding(
                                vertical = if (isTextFieldFocused) 2.dp else 0.dp,
                                horizontal = if (isTextFieldFocused) 4.dp else 0.dp,
                            )
                        ) { Text("Type your message below") }
                    },
                    placeholder = {
                        Text("e.g. I do not have time for this nonsense. Stop bothering me.")
                    },
                    modifier = Modifier.padding(10.dp).widthIn(min = 420.dp, max = 440.dp)
                        .height(100.dp)
                        .onFocusChanged { state: FocusState ->
                            isTextFieldFocused = state.isFocused
                        },
                    maxLines = 3,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = Color.White,
                    ),
                )
                Button(
                    onClick = { handleSubmit() },
                    modifier = Modifier.padding(10.dp),
                    enabled = !messageLoading
                ) {
                    Text("Polite it up 😊")
                }
                if (messageLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(10.dp))
                }
                if (messageLoadingError) {
                    Text(
                        text = "We apologize for the inconvenience, but the OpenAI API is not available at the moment. It looks like we have reached our limit or quota for the API. Please wait for a while or switch to a different service.",
                        modifier = Modifier.padding(10.dp),
                        color = Color.Red
                    )
                }
                if (message.isNotEmpty()) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(10.dp),
                        style = MaterialTheme.typography.h5
                    )
                    showImage = false
                }
                if (showImage) {
                    Box(
                        modifier = Modifier.padding(10.dp)
                            .width(380.dp)
                            .height(198.5.dp)
                            .background(
                                shape = RoundedCornerShape(size = 40.dp),
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color.Gray,
                                        Color.Transparent
                                    )
                                )
                            )
                            .clip(RoundedCornerShape(size = 40.dp))
                            .shadow(
                                ambientColor = Color.Gray.copy(alpha = 0.2f),
                                spotColor = Color.Gray.copy(alpha = 0.2f),
                                elevation = 2.dp,
                                shape = RoundedCornerShape(size = 40.dp),
                                clip = false,
                            )
                    ) {
                        Image(
                            painter = painterResource("main_horizontal_image.png"),
                            contentDescription = "A picture of somebody holding the image of a smile",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.matchParentSize(),
                        )
                    }
                }
            }
        }
    }
}

// A mock function to get the polite message from the API
// You can replace this with your actual API call
fun getPoliteMessage(prompt: String): String {
    return when (prompt) {
        "I do not have time for this nonsense. Stop bothering me." -> "I'm sorry, but I'm really busy right now. Could you please contact me later? Thank you for your understanding."
        "Your work is terrible. You should be ashamed of yourself." -> "I'm afraid your work does not meet the expected standards. You might want to review it and make some improvements."
        "You are such a jerk. I hate you." -> "You are not very nice. I don't appreciate your behavior."
        else -> "I don't know how to make this message more polite. Maybe you can try to rephrase it or use more positive words."
    }
}