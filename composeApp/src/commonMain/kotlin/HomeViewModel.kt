import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

//@HiltViewMfodel
//class HomeViewModel @Inject constructor() : ViewModel() {
//
//    // Define your state variables here
//    var prompt: String by mutableStateOf(value = "")
//    var message: String by mutableStateOf(value = "")
//    var messageLoading: Boolean by mutableStateOf(value = false)
//    var messageLoadingError: Boolean by mutableStateOf(value = false)
//    var showImage: Boolean by mutableStateOf(value = true)
//    var isTextFieldFocused: Boolean by mutableStateOf(value = false)
//
//    // A mock function to get the polite message from the API
//// You can replace this with your actual API call
//    fun getPoliteMessage(prompt: String): String {
//        return when (prompt) {
//            "I do not have time for this nonsense. Stop bothering me." -> "I'm sorry, but I'm really busy right now. Could you please contact me later? Thank you for your understanding."
//            "Your work is terrible. You should be ashamed of yourself." -> "I'm afraid your work does not meet the expected standards. You might want to review it and make some improvements."
//            "You are such a jerk. I hate you." -> "You are not very nice. I don't appreciate your behavior."
//            else -> "I don't know how to make this message more polite. Maybe you can try to rephrase it or use more positive words."
//        }
//    }
//
//    // Define your business logic functions here
//    fun handleSubmit() {
//        message = ""
//        if (prompt.isNotEmpty()) {
//            try {
//                messageLoadingError = false
//                messageLoading = true
//                message = if (prompt.length < 5) {
//                    "Message too short"
//                } else {
//                    // Call the API to get the polite message
//                    // For simplicity, I will use a mock function here
//                    getPoliteMessage(prompt)
//                }
//            } catch (error: Exception) {
//                messageLoadingError = true
//            } finally {
//                messageLoading = false
//            }
//        }
//    }
//}
