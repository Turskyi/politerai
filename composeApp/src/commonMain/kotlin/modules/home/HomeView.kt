package modules.home

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import entity.Entity
import interactors.GetPoliteMessageUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class, ExperimentalComposeUiApi::class)
@Composable
fun HomeView() {
    val useCase = GetPoliteMessageUseCase()
    // Returns a scope that's cancelled when F is removed from composition
    val scope: CoroutineScope = rememberCoroutineScope()
    var prompt: String by remember { mutableStateOf("") }
    var message: String by remember { mutableStateOf("") }
    var messageLoading: Boolean by remember { mutableStateOf(false) }
    var messageLoadingError: Boolean by remember { mutableStateOf(false) }
    var showImage: Boolean by remember { mutableStateOf(true) }
    var isTextFieldFocused: Boolean by remember { mutableStateOf(false) }
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current

    fun handleSubmit() {
        keyboardController?.hide()
        message = ""
        if (prompt.isNotEmpty()) {
            try {
                isTextFieldFocused = true
                messageLoadingError = false
                messageLoading = true
                if (prompt.length < 5) {
                    message = "Message too short"
                    messageLoading = false
                } else {
                    // Call the API to get the polite message
                    // Launch a coroutine
                    scope.launch {
                        // Call the suspend function on a background thread
                        withContext(Dispatchers.IO) {
                            val entity: Entity = useCase.getPoliteMessage(prompt)
                            // Update the state with the message
                            message = entity.politerMessage
                            messageLoading = false
                        }
                    }
                }
            } catch (error: Exception) {
                messageLoadingError = true
                messageLoading = false
            }
        } else {
            // Launch a coroutine to show the Snackbar
            scope.launch {
                // Show the Snackbar with a message
                snackbarHostState.showSnackbar(
                    message = "Please enter some text before clicking the button",
                    duration = SnackbarDuration.Short,
                )
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
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
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
                onValueChange = { input ->
                    prompt = input
                    if (input.isEmpty()) {
                        message = ""
                        showImage = true
                    }
                },
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
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    },
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text
                ),
            )
            Button(
                onClick = { handleSubmit() },
                modifier = Modifier.padding(10.dp),
                enabled = !messageLoading
            ) {
                Text("Polite it up 😊")
            }
            AnimatedVisibility(visible = messageLoading) {
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
                    modifier = Modifier.padding(
                        start = 10.dp,
                        top = 10.dp,
                        end = 10.dp,
                        bottom = 40.dp,
                    ),
                    style = MaterialTheme.typography.h5
                )
                showImage = false
            }
            AnimatedVisibility(visible = showImage) {
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
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}