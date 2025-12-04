package com.munchmatch.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.FieldValue

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val error = remember { mutableStateOf("") }
    val showRegister = remember { mutableStateOf(false) }
    val showForgot = remember { mutableStateOf(false) }
    val auth = remember { FirebaseAuth.getInstance() }
    val db = remember { Firebase.firestore }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Welcome", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = username.value,
                    onValueChange = { username.value = it },
                    label = { Text("Username") },
                    leadingIcon = { androidx.compose.material3.Icon(Icons.Outlined.Person, null) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    leadingIcon = { androidx.compose.material3.Icon(Icons.Outlined.Lock, null) },
                    modifier = Modifier.fillMaxWidth()
                )
                if (error.value.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(text = error.value, color = MaterialTheme.colorScheme.error)
                }
                Spacer(Modifier.height(16.dp))
                ElevatedButton(
                    onClick = {
                    error.value = ""
                    val uname = username.value.trim().lowercase()
                    val pwd = password.value
                    if (uname.isEmpty() || pwd.isEmpty()) {
                        error.value = "Enter username and password"
                    } else {
                        db.collection("users").document(uname).get()
                            .addOnSuccessListener { snap ->
                                val email = snap.getString("email")
                                if (snap.exists() && !email.isNullOrBlank()) {
                                    auth.signInWithEmailAndPassword(email, pwd)
                                        .addOnSuccessListener { onLoginSuccess() }
                                        .addOnFailureListener { e -> error.value = e.localizedMessage ?: "Login failed" }
                                } else {
                                    error.value = "Username not found"
                                }
                            }
                            .addOnFailureListener { e -> error.value = e.localizedMessage ?: "Network error" }
                    }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) { Text("Sign In") }
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = { showRegister.value = true }, modifier = Modifier.fillMaxWidth()) { Text("New Registration") }
                TextButton(onClick = { showForgot.value = true }, modifier = Modifier.fillMaxWidth()) { Text("Forgot Password") }
            }
        }
    }

    if (showRegister.value) RegistrationDialog(onDismiss = { showRegister.value = false })
    if (showForgot.value) ForgotDialog(onDismiss = { showForgot.value = false })
}

@Composable
private fun RegistrationDialog(onDismiss: () -> Unit) {
    val auth = remember { FirebaseAuth.getInstance() }
    val db = remember { Firebase.firestore }
    val username = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val error = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                error.value = ""
                val uname = username.value.trim().lowercase()
                val mail = email.value.trim()
                val pwd = password.value
                if (uname.isEmpty() || mail.isEmpty() || pwd.isEmpty()) {
                    error.value = "All fields are required"
                } else {
                    db.collection("users").document(uname).get()
                        .addOnSuccessListener { snap ->
                            if (snap.exists()) {
                                error.value = "Username already taken"
                            } else {
                                auth.createUserWithEmailAndPassword(mail, pwd)
                                    .addOnSuccessListener { res ->
                                        val uid = res.user?.uid ?: ""
                                        val data = hashMapOf(
                                            "username" to uname,
                                            "email" to mail,
                                            "uid" to uid,
                                            "createdAt" to FieldValue.serverTimestamp()
                                        )
                                        db.collection("users").document(uname).set(data)
                                            .addOnSuccessListener { onDismiss() }
                                            .addOnFailureListener { e -> error.value = e.localizedMessage ?: "Failed saving user" }
                                    }
                                    .addOnFailureListener { e -> error.value = e.localizedMessage ?: "Registration failed" }
                            }
                        }
                        .addOnFailureListener { e -> error.value = e.localizedMessage ?: "Network error" }
                }
            }) { Text("Submit") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text("New Registration") },
        text = {
            Column {
                OutlinedTextField(value = username.value, onValueChange = { username.value = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = email.value, onValueChange = { email.value = it }, label = { Text("Email") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = password.value, onValueChange = { password.value = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                if (error.value.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(text = error.value, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    )
}

@Composable
private fun ForgotDialog(onDismiss: () -> Unit) {
    val existing = remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text("Submit") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text("Forgot Password") },
        text = { OutlinedTextField(value = existing.value, onValueChange = { existing.value = it }, label = { Text("Existing Username") }, modifier = Modifier.fillMaxWidth()) }
    )
}
