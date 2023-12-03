package values

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

/** Create a theme for the Compose application */
@Composable
fun PoliterTheme(content: @Composable () -> Unit) {

    val colors: Colors = lightColors(
        primary = Color(0xFF7669FF),
        secondary = Color(0xFF7669FF),
        onPrimary = Color.White,
        onSecondary = Color.White,
    )

    val typography = Typography(
        h4 = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
            shadow = Shadow(
                color = Color.Black, offset = Offset(x = 2f, y = 2f), blurRadius = 2f
            )
        ),
        h5 = TextStyle(
            fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 24.sp
        ),
        h6 = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            shadow = Shadow(
                color = Color.Gray, offset = Offset(1f, 1f), blurRadius = 2f
            )
        ),
        body1 = TextStyle(
            fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 16.sp
        )
    )

    MaterialTheme(
        colors = colors,
        typography = typography,
        content = content,
    )
}