package com.example.vent

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vent.network.UserApiService

val interFontFamily = FontFamily(
    Font(R.font.inter_24_regular, FontWeight.Normal),
    Font(R.font.inter_28_medium, FontWeight.Medium),
    Font(R.font.inter_24_light_italic, FontWeight.Normal, FontStyle.Italic),
)

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignUpLayout()
        }
    }

    @Preview
    @Composable
    private fun SignUpLayout() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFFFFF), Color(0xFFFF9C46))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.SignUpText),
                    fontSize = 32.sp,
                    fontFamily = interFontFamily,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 40.dp)
                )
                SignUpCard()
            }
        }
    }

    @Composable
    private fun SignUpCard() {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isEmailValid by remember { mutableStateOf(false) }
        var isPasswordValid by remember { mutableStateOf(false) }

        val isFormValid = isEmailValid && isPasswordValid

        val context = LocalContext.current
        val appContext = context.applicationContext
        var isLoading by remember { mutableStateOf(false) }

        // TODO: Add the animation for the sign up button to AnimationUtils.kt
        // Animate gradient offset
        val gradientOffset = remember { Animatable(0f) }

        LaunchedEffect(isLoading) {
            if (isLoading) {
                gradientOffset.animateTo(
                    targetValue = 200f,  // Adjust for desired animation speed/distance
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )
            }
        }

        val radialBrush = Brush.horizontalGradient(
            listOf(Color(0xFFFF8400), Color(0xFF003366)),
            startX = gradientOffset.value,
            endX = gradientOffset.value + 400f  // Dynamic endX to match button width
        )

        Box(
            modifier = Modifier
                .width(350.dp)
                .height(420.dp)
        ) {
            // Card with shadow
            Card(
                modifier = Modifier.fillMaxSize(),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 20.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xD9FFF5E1),
                )
            ) {
            }

            // Overlay to cancel bevels
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color(0xD9FFF5E1), RoundedCornerShape(20.dp))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.Center)
                ) {

                    //  Email
                    LoginInputs(
                        fieldLabel = "Email",
                        leadingIcon = Icons.Filled.Email,
                        iconDescription = "Email Icon",
                        isEmailField = true,
                        onInputChange = { email = it },
                        onValidationChange = { isEmailValid = it }
                    )

                    //  Password
                    LoginInputs(
                        fieldLabel = "Password",
                        leadingIcon = Icons.Filled.Lock,
                        iconDescription = "Password Icon",
                        isPasswordField = true,
                        onInputChange = { password = it },
                        onValidationChange = { isPasswordValid = it }
                    )

                    // Forgot Password
                    Text(
                        text = stringResource(R.string.ForgotPasswordText),
                        color = Color(0xFF003366),
                        fontSize = 14.sp,
                        fontFamily = interFontFamily,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .align(Alignment.End) // Align to the end of the Column
                            .padding(top = 8.dp, end = 10.dp)
                            .clickable {
                                // TODO: Navigate to the Forgot Password page
                                // This intent will start a new activity for "Forgot Password"
                                val intent = Intent(context, ForgotPasswordActivity::class.java)
                                context.startActivity(intent)
                            }
                    )

                    // Submit Button
                    Button(
                        onClick = {
                            if (isFormValid) {
                                isLoading = true

                                // Check user status
                                UserApiService.checkUserStatus(appContext, email) { status, role ->
                                    when (status) {
                                        "approved" -> {
                                            UserApiService.loginUser(appContext, email, password) { loginSuccess, loginMessage ->
                                                if (loginSuccess) {
                                                    // Success! Go to Dashboard
                                                    val intent = Intent(context, MainActivity::class.java)
                                                    context.startActivity(intent)
                                                    (context as? Activity)?.finish()
                                                } else {
                                                    // Login failed (wrong password, etc.)
                                                    isLoading = false // Reset loading so they can try again
                                                    Toast.makeText(context, "Login Failed: $loginMessage", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                        "pending" -> {
                                            // They exist but are waiting. Stop them here!
                                            isLoading = false
                                            Toast.makeText(context, "Account already awaiting approval.", Toast.LENGTH_SHORT).show()
                                            val intent = Intent(context, AwaitingApprovalActivity::class.java)
                                            context.startActivity(intent)
                                        }
                                        // If user doesn't exist (or any other status), attempt Sign Up
                                        else -> {
                                            UserApiService.signupUser(appContext, email, password) { isSuccess ->
                                                isLoading = false
                                                if (isSuccess) {
                                                    val intent = Intent(context, AwaitingApprovalActivity::class.java)
                                                    context.startActivity(intent)
                                                    (context as? Activity)?.finish()
                                                } else {
                                                    Toast.makeText(context, "Signup Failed", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        enabled = isFormValid && !isLoading,
                        modifier = Modifier
                            .width(200.dp)
                            .height(75.dp)
                            .padding(top = 25.dp)
                            .background(
                                brush = if (isLoading) radialBrush else SolidColor(
                                    if (isFormValid) Color(0xFF003366) else Color(0x0A808080) // ✅ Keeps validation logic intact
                                ),
                                shape = CircleShape
                            ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                    ) {
                        if (isLoading) {
                            Text(
                                text = "Loading...",
                                fontSize = 18.sp,
                                color = Color.White,
                                fontFamily = interFontFamily,
                                fontWeight = FontWeight.Normal
                            )
                        }
                        else{
                            Text(
                                text = "Sign Up",
                                fontSize = 18.sp,
                                color = Color.White,
                                fontFamily = interFontFamily,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun LoginInputs(
        fieldLabel: String,
        leadingIcon: ImageVector,
        iconDescription: String,
        isEmailField: Boolean = false,
        isPasswordField: Boolean = false,
        onInputChange: (String) -> Unit,
        onValidationChange: (Boolean) -> Unit
    ) {
        var input by remember { mutableStateOf("") }
        var isValid by remember { mutableStateOf(false) }
        var passwordVisible by remember { mutableStateOf(false) }
        val primaryBlue = Color(0xFF003366)

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

        OutlinedTextField(
            modifier = Modifier.padding(bottom = 10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryBlue,
                cursorColor = primaryBlue,
                focusedLabelColor = primaryBlue
            ),
            value = input,
            label = { Text(fieldLabel) },
            leadingIcon = { Icon(imageVector = leadingIcon, contentDescription = iconDescription) },
            singleLine = true,
            visualTransformation = when {
                isPasswordField && !passwordVisible -> PasswordVisualTransformation()
                else -> VisualTransformation.None
            },
            trailingIcon = {
                if (isPasswordField && input.isNotEmpty()) {
                    // If it's a password field and the input is not empty, show the eye icon to toggle visibility
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription =
                                "Toggle Password Visibility"
                        )
                    }
                } else if (input.isNotEmpty()) {
                    // Otherwise, show the validation icon (check or close)
                    Icon(
                        imageVector = if (isValid) Icons.Filled.Check else Icons.Filled.Close,
                        contentDescription = if (isValid) "Valid Input" else "Invalid Input",
                        tint = if (isValid) Color.Green else Color.Red
                    )
                }

            },
            onValueChange = { newValue ->
                input = newValue
                isValid = when {
                    isEmailField -> emailRegex.matches(newValue)
                    isPasswordField -> {
                        val result = isPasswordValid(newValue)
                        result
                    }

                    else -> false
                }
                onInputChange(input) // Pass input value to parent
                onValidationChange(isValid) // Pass validation status to parent
            },
            isError = input.isNotEmpty() && !isValid
        )

        if (input.isNotEmpty() && !isValid) {
            Text(
                text = when {
                    isEmailField -> "Invalid email format"
                    isPasswordField -> getPasswordErrorMessage(input)
                    else -> ""
                },
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }

    // Modular password validation functions
    fun hasMinimumLength(password: String) = password.length >= 8
    fun hasUppercase(password: String) = password.any { it.isUpperCase() }
    fun hasLowercase(password: String) = password.any { it.isLowerCase() }
    fun hasDigit(password: String) = password.any { it.isDigit() }
    fun hasSpecialChar(password: String) = password.any { "!@#$%^&*()_+[]{}:;<>?/".contains(it) }

    fun isPasswordValid(password: String): Boolean {
        return hasMinimumLength(password) and
                hasUppercase(password) and
                hasLowercase(password) and
                hasDigit(password) and
                hasSpecialChar(password)
    }

    fun getPasswordErrorMessage(password: String): String {
        return when {
            !hasMinimumLength(password) -> "Password must be at least 8 characters long"
            !hasUppercase(password) -> "Password must contain at least one uppercase letter"
            !hasLowercase(password) -> "Password must contain at least one lowercase letter"
            !hasDigit(password) -> "Password must contain at least one digit"
            !hasSpecialChar(password) -> "Password must contain at least one special character"
            else -> "Weak password"
        }
    }
}

