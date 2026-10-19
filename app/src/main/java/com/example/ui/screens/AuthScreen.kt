package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserProfile
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandPrimaryVariant
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.ProductivityCategory

@Composable
fun AuthScreen(
  onLoginSuccess: (UserProfile) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Sign In, 1: Sign Up
  var name by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("user@chatwait.ai") }
  var password by remember { mutableStateOf("••••••••") }
  var passwordVisible by remember { mutableStateOf(false) }
  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  val scrollState = rememberScrollState()

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(
            DarkBackground,
            Color(0xFF0F172A),
            DarkSurface
          )
        )
      )
  ) {
    // Background Glow Orbs for aesthetic depth
    Box(
      modifier = Modifier
        .size(300.dp)
        .align(Alignment.TopEnd)
        .background(
          Brush.radialGradient(
            colors = listOf(BrandPrimary.copy(alpha = 0.25f), Color.Transparent),
            radius = 450f
          )
        )
    )

    Box(
      modifier = Modifier
        .size(240.dp)
        .align(Alignment.BottomStart)
        .background(
          Brush.radialGradient(
            colors = listOf(BrandSecondary.copy(alpha = 0.2f), Color.Transparent),
            radius = 350f
          )
        )
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 24.dp, vertical = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(16.dp))

      // Glowing Brand Icon Header
      Box(
        modifier = Modifier
          .size(72.dp)
          .clip(CircleShape)
          .background(
            Brush.linearGradient(
              listOf(BrandPrimary, BrandSecondary)
            )
          )
          .border(2.dp, Color.White.copy(alpha = 0.6f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = "ChatWait AI Logo",
          tint = Color.White,
          modifier = Modifier.size(38.dp)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Title & Subtitle
      Text(
        text = "ChatWait AI Studio",
        fontSize = 26.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color.White,
        letterSpacing = 0.5.sp
      )

      Text(
        text = "Multi-Model AI Chat & Smart Wait Discovery",
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF94A3B8),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 4.dp)
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Feature highlights row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Center
      ) {
        PillFeature(icon = Icons.Default.AutoAwesome, text = "Gemini 3.5")
        Spacer(modifier = Modifier.width(8.dp))
        PillFeature(icon = Icons.Default.Speed, text = "Wait Cards")
        Spacer(modifier = Modifier.width(8.dp))
        PillFeature(icon = Icons.Default.Shield, text = "Private & Fast")
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Auth Card Container
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(24.dp))
          .border(
            1.dp,
            Brush.verticalGradient(
              listOf(BrandPrimary.copy(alpha = 0.5f), Color.White.copy(alpha = 0.1f))
            ),
            RoundedCornerShape(24.dp)
          ),
        colors = CardDefaults.cardColors(
          containerColor = Color(0xFF131B2E).copy(alpha = 0.85f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          // Tab switcher (Sign In vs Sign Up)
          TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = BrandPrimary,
            indicator = { tabPositions ->
              TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = BrandPrimary,
                height = 3.dp
              )
            },
            divider = {}
          ) {
            Tab(
              selected = selectedTabIndex == 0,
              onClick = { selectedTabIndex = 0 },
              text = {
                Text(
                  "Sign In",
                  fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal,
                  color = if (selectedTabIndex == 0) Color.White else Color(0xFF64748B)
                )
              }
            )
            Tab(
              selected = selectedTabIndex == 1,
              onClick = { selectedTabIndex = 1 },
              text = {
                Text(
                  "Create Account",
                  fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal,
                  color = if (selectedTabIndex == 1) Color.White else Color(0xFF64748B)
                )
              }
            )
          }

          Spacer(modifier = Modifier.height(20.dp))

          // If Sign Up, show name field
          if (selectedTabIndex == 1) {
            OutlinedTextField(
              value = name,
              onValueChange = { name = it },
              label = { Text("Your Full Name", fontSize = 12.sp) },
              leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
              },
              singleLine = true,
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandPrimary,
                unfocusedBorderColor = Color(0xFF334155),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
              ),
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag("auth_name_field")
            )
          }

          // Email Field
          OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address", fontSize = 12.sp) },
            leadingIcon = {
              Icon(Icons.Default.Email, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = BrandPrimary,
              unfocusedBorderColor = Color(0xFF334155),
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("auth_email_field")
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Password Field
          OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", fontSize = 12.sp) },
            leadingIcon = {
              Icon(Icons.Default.Lock, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
            },
            trailingIcon = {
              IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                  imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                  contentDescription = "Toggle password",
                  tint = Color(0xFF94A3B8),
                  modifier = Modifier.size(18.dp)
                )
              }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = BrandPrimary,
              unfocusedBorderColor = Color(0xFF334155),
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("auth_password_field")
          )

          if (errorMessage != null) {
            Text(
              text = errorMessage!!,
              color = Color(0xFFEF4444),
              fontSize = 11.sp,
              modifier = Modifier.padding(top = 8.dp)
            )
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Primary Sign In Button
          Button(
            onClick = {
              isLoading = true
              val displayName = if (name.isNotBlank()) name.trim() else "Nitin Kumar"
              val userEmail = if (email.isNotBlank()) email.trim() else "nitin@chatwait.ai"
              val initials = displayName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase()
              
              onLoginSuccess(
                UserProfile(
                  id = "user_101",
                  name = displayName,
                  email = userEmail,
                  avatarInitials = if (initials.isNotEmpty()) initials else "AI",
                  isProUser = true
                )
              )
            },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("auth_submit_button")
          ) {
            if (isLoading) {
              CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
              Text(
                text = if (selectedTabIndex == 0) "Sign In to ChatWait" else "Create AI Account",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF334155))
            Text(
              text = "OR",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF64748B),
              modifier = Modifier.padding(horizontal = 12.dp)
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF334155))
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Continue with Google Button
          OutlinedButton(
            onClick = {
              onLoginSuccess(
                UserProfile(
                  id = "user_google_1",
                  name = "Nitin Kumar",
                  email = "nitinkumar14668@gmail.com",
                  avatarInitials = "NK",
                  isProUser = true
                )
              )
            },
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF1E293B)),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("auth_google_button")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              // Google G-style colored marker
              Text(
                text = "G ",
                color = Color(0xFF4285F4),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Continue with Google Account",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Guest Mode / Instant Try
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.Transparent,
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                onLoginSuccess(
                  UserProfile(
                    id = "guest_user",
                    name = "Guest Explorer",
                    email = "guest@chatwait.local",
                    avatarInitials = "GE",
                    isProUser = false
                  )
                )
              }
              .padding(vertical = 6.dp)
              .testTag("auth_guest_button")
          ) {
            Text(
              text = "Skip and continue as Guest ➔",
              color = BrandSecondary,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              textAlign = TextAlign.Center
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "By signing in, you agree to ChatWait's Terms of Service and Privacy Policy.",
        fontSize = 11.sp,
        color = Color(0xFF64748B),
        textAlign = TextAlign.Center,
        lineHeight = 16.sp
      )
    }
  }
}

@Composable
fun PillFeature(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
  Surface(
    shape = RoundedCornerShape(20.dp),
    color = Color(0xFF1E293B).copy(alpha = 0.8f),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = BrandSecondary,
        modifier = Modifier.size(13.dp)
      )
      Spacer(modifier = Modifier.width(5.dp))
      Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFFCBD5E1)
      )
    }
  }
}
