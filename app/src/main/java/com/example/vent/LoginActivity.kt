package com.example.vent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
}

@Preview(
    showBackground = true,
    device = "spec:width=411dp,height=891dp"
)
@Composable
private fun SignUpLayout(){
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
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ){
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
    Box(
        modifier = Modifier
            .width(295.dp)
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
        ){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {

                //  Email
                LoginInputs("Email", Icons.Default.Email, "Email Icon")

                //  Password
                LoginInputs("Password", Icons.Default.Lock, "Password Icon")

                // Submit Button
                Button(
                    onClick = {  },
                    modifier = Modifier
                        .width(200.dp)
                        .height(75.dp)
                        .padding(top = 25.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF003366)),
                    elevation = ButtonDefaults.buttonElevation(10.dp)
                ){
                    Text(
                        text = "Sign Up",
                        fontSize = 18.sp,
                        color = Color.White,
                        fontFamily = interFontFamily,
                        fontWeight = FontWeight.Normal
                    )
                }

                // or Text
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = stringResource(R.string.SignUpOrText),
                    fontSize = 20.sp,
                    fontFamily = interFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                )

                // Google OAuth
                Button(
                    onClick = { },
                    modifier = Modifier
                        .width(200.dp)
                        .height(70.dp)
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(Color.White),
                    elevation = ButtonDefaults.buttonElevation(10.dp)
                ){
                    Row (
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Image(
                            painter = painterResource(id = R.drawable.google_logo),
                            modifier = Modifier
                                .width(24.dp)
                                .height(24.dp),
                            contentDescription = "Google Logo"
                        )
                        Text(
                            text = "Google",
                            fontSize = 18.sp,
                            color = Color.Black,
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
private fun LoginInputs(fieldLabel: String, leadingIcon: ImageVector, iconDescription: String){
    var input by remember { mutableStateOf("") }
    OutlinedTextField(
        modifier = Modifier.padding(bottom = 10.dp),
        value = input,
        label = { Text(fieldLabel) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = iconDescription
            )
        },
        onValueChange = {
                newValue -> input = newValue
        }
    )
}

