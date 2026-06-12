package edu.uph.m24si3.demouts

import android.app.DatePickerDialog
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.uph.m24si3.demouts.ui.theme.DemoUTSTheme
import kotlinx.coroutines.delay
import java.io.InputStream
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DemoUTSTheme {
                MainApp()
            }
        }
    }
}

enum class Screen {
    Splash, Login, Dashboard, EditProfile
}

data class UserData(
    val fullName: String = "Edward Narto",
    val username: String = "admin",
    val placeOfBirth: String = "Medan",
    val dateOfBirth: String = "06/04/2006",
    val hobbies: String = "Programming, Desain Web,",
    val bio: String = "Full Stack Developer",
    val profileImageUri: String? = null
)

@Composable
fun MainApp() {
    var currentScreen by remember { mutableStateOf(Screen.Splash) }
    var userData by remember { mutableStateOf(UserData()) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (currentScreen) {
            Screen.Splash -> SplashScreen { currentScreen = Screen.Login }
            Screen.Login -> LoginScreen(onLoginSuccess = { currentScreen = Screen.Dashboard })
            Screen.Dashboard -> DashboardScreen(
                userData = userData,
                onUpdateProfile = { currentScreen = Screen.EditProfile },
                onLogout = { currentScreen = Screen.Login }
            )
            Screen.EditProfile -> EditProfileScreen(
                currentData = userData,
                onSave = { newData ->
                    userData = newData
                    currentScreen = Screen.Dashboard
                }
            )
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000)
        onTimeout()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF311B92)), // Deep Purple
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Logo placeholder based on screenshot 1
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1A237E))
                    .border(2.dp, Color.Gray, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "24SI3",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "24SI3 UTS",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Login UTS",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(48.dp))
        
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            )
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                Toast.makeText(context, "Login Berhasil", Toast.LENGTH_SHORT).show()
                onLoginSuccess()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
        ) {
            Text("LOGIN", color = Color.Black)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    userData: UserData,
    onUpdateProfile: () -> Unit,
    onLogout: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2196F3)),
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Text("⋮", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Update Profile") },
                            onClick = {
                                showMenu = false
                                onUpdateProfile()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                showMenu = false
                                onLogout()
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Welcome, ${userData.fullName}!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 24.dp)
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        val context = LocalContext.current
                        val bitmap = remember(userData.profileImageUri) {
                            userData.profileImageUri?.let { uriString ->
                                try {
                                    val inputStream: InputStream? = context.contentResolver.openInputStream(Uri.parse(uriString))
                                    BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
                                } catch (e: Exception) {
                                    null
                                }
                            }
                        }

                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap,
                                contentDescription = "Profile Image",
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color.Gray, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF263238))
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    ProfileInfoRow("Nama Lengkap", userData.fullName)
                    ProfileInfoRow("Username", userData.username)
                    ProfileInfoRow("Tempat Lahir", userData.placeOfBirth)
                    ProfileInfoRow("Tanggal Lahir", userData.dateOfBirth)
                    ProfileInfoRow("Hobi", userData.hobbies)
                    ProfileInfoRow("Bio", userData.bio)
                }
            }
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    currentData: UserData,
    onSave: (UserData) -> Unit
) {
    var fullName by remember { mutableStateOf(currentData.fullName) }
    var placeOfBirth by remember { mutableStateOf(currentData.placeOfBirth) }
    var dateOfBirth by remember { mutableStateOf(currentData.dateOfBirth) }
    var hobbies by remember { mutableStateOf(currentData.hobbies) }
    var bio by remember { mutableStateOf(currentData.bio) }
    var profileImageUri by remember { mutableStateOf(currentData.profileImageUri) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    
    // Image Picker Launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileImageUri = uri?.toString()
    }

    // Date Picker Dialog
    val datePickerDialog = remember {
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                dateOfBirth = "$dayOfMonth/${month + 1}/$year"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", color = Color.DarkGray) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val bitmap = remember(profileImageUri) {
                profileImageUri?.let { uriString ->
                    try {
                        val inputStream: InputStream? = context.contentResolver.openInputStream(Uri.parse(uriString))
                        BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
                    } catch (e: Exception) {
                        null
                    }
                }
            }

            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1A237E))
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { launcher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("CHANGE IMAGE", color = Color.Black, fontSize = 12.sp)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            EditField(value = fullName, onValueChange = { fullName = it }, label = "Nama")
            EditField(value = placeOfBirth, onValueChange = { placeOfBirth = it }, label = "Tempat Lahir")
            
            // Date of Birth with Calendar Trigger
            Box(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = dateOfBirth,
                    onValueChange = { dateOfBirth = it },
                    label = { Text("Tanggal Lahir") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent
                    )
                )
                // Transparent box over the TextField to catch clicks
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { datePickerDialog.show() }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            EditField(value = hobbies, onValueChange = { hobbies = it }, label = "Hobi")
            EditField(value = bio, onValueChange = { bio = it }, label = "Bio")
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    onSave(currentData.copy(
                        fullName = fullName,
                        placeOfBirth = placeOfBirth,
                        dateOfBirth = dateOfBirth,
                        hobbies = hobbies,
                        bio = bio,
                        profileImageUri = profileImageUri
                    ))
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // Green
            ) {
                Text("SAVE PROFILE", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun EditField(value: String, onValueChange: (String) -> Unit, label: String) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent
        )
    )
    Spacer(modifier = Modifier.height(8.dp))
}
