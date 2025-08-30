package max.project.camerases

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil.compose.AsyncImage
import coil.request.ImageRequest
import max.project.camerases.Nav.CameraScreen
import max.project.camerases.Nav.PhotosOfSession
import max.project.camerases.Nav.SearchScreen
import max.project.camerases.Nav.SessionCreateScreen
import max.project.camerases.Nav.SessionViewScreen
import max.project.camerases.dataBase.SessionDB
import max.project.camerases.dataBase.Sessions
import max.project.camerases.ui.theme.CameraSESTheme
import max.project.camerases.util.BottomBar
import max.project.camerases.util.CameraPreview
import max.project.camerases.util.TopBar
import max.project.camerases.viewmodels.CameraView
import max.project.camerases.viewmodels.SessionViewModelFactory
import max.project.camerases.viewmodels.SessionViews
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!requestCameraPermissions()) {
            ActivityCompat.requestPermissions(this, cameraxPermissions, 0)
        }
        enableEdgeToEdge()
        setContent {
            CameraSESTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = SessionCreateScreen) {
                    composable<SessionCreateScreen> {
                        SessionCreate({
                            navController.navigate(
                                CameraScreen
                            )
                        }) {
                            navController.navigate(SessionViewScreen)
                        }
                    }
                    composable<CameraScreen> {
                        val args = it.toRoute<CameraScreen>()
                        Camera(
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            navController.navigate(SessionCreateScreen){
                                navController.popBackStack()
                            }
                        }
                    }
                    composable<SessionViewScreen> {
                        SessionsView(onSearch = {
                            navController.navigate(SearchScreen)
                        },onBack = {
                             navController.popBackStack()
                        }, onClickingAnyItem = {
                            Log.d("TAG", "onCreate: $it")
                            navController.navigate(
                                PhotosOfSession(
                                    sessionId = it.sessionId,
                                    name = it.name,
                                    age = it.age
                                )
                            ) {
                            }
                        })
                    }
                    composable<PhotosOfSession> {
                            val args = it.toRoute<PhotosOfSession>()
                            PhotoViewOfSession(args.name, args.age, args.sessionId, onBack = {
                                navController.popBackStack()
                            })
                    }
                    composable<SearchScreen> {
                        SearchPage(
                            onBack = {
                                navController.popBackStack()
                            }
                            , onClickingAnyItem = {
                            navController.navigate(
                                PhotosOfSession(
                                    sessionId = it.sessionId,
                                    name = it.name,
                                    age = it.age
                                )
                            )}
                        )
                    }

                }
            }
        }
    }
    private fun requestCameraPermissions(): Boolean {
        return cameraxPermissions.all {
            ContextCompat.checkSelfPermission(
                applicationContext ,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private val cameraxPermissions = arrayOf(
            Manifest.permission.CAMERA
        )
    }

}


@Composable
fun Camera(modifier: Modifier,  viewmodel: CameraView= viewModel(), onBack:()->Unit={}) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var endSession by remember { mutableStateOf(false) }
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE
            )
        }
    }
    val sessionInfo = viewmodel.bitmapImages.collectAsState()
    var isNameEntered by remember { mutableStateOf(true) }
    var isAgeEntered by remember { mutableStateOf(true) }
//    val data = SessionDB.getDatabase(context)
    CameraPreview(
        controller = controller,
        modifier = modifier
    )
    Box(modifier = Modifier
        .padding()
        .fillMaxSize()) {
        TopBar(modifier = Modifier
            .padding(start = 10.dp, top = 20.dp)
            .align(Alignment.TopStart)){

            endSession = true
        }
        AnimatedVisibility(
            visible = endSession,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Column(modifier = Modifier
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(10.dp))
                .padding(15.dp),verticalArrangement = Arrangement.Center,horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(
                    value = sessionInfo.value.name,
                    onValueChange = {
                        viewmodel.updateName(it)
                    },
                    label = {
                        Text(text = "Name")
                    }
                    , keyboardActions = KeyboardActions(
                        onNext =  { focusManager.moveFocus(FocusDirection.Down) }
                ), isError = !isNameEntered
                )
                OutlinedTextField(
                    value = when(sessionInfo.value.age){
                        0 -> ""
                        else -> sessionInfo.value.age.toString()
                    },
                    onValueChange = { newValue ->
                        // Only allow digits
                        if (newValue.all { it.isDigit() }) {
                            viewmodel.updateAge(newValue.toIntOrNull() ?: 0)
                        }
                    },
                    isError = !isAgeEntered,
                    label = { Text("Age") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = {
                            endSession=false
                            viewmodel.updateName("")
                            viewmodel.updateAge(0)
                        }
                    ) {
                        Text(text = "Cancel")
                    }

                    Button(onClick = {
                        isNameEntered = sessionInfo.value.name.isNotEmpty()
                        isAgeEntered = sessionInfo.value.age!=0
                        if( sessionInfo.value.photos.isEmpty()){
                            Toast.makeText(context, "No photos taken", Toast.LENGTH_SHORT).show()
                        }
                        else if (isNameEntered && isAgeEntered)
                        {
                            Log.d("TAG", "Session saved")
                            sessionInfo.value.sessionId="${sessionInfo.value.name}_${sessionInfo.value.age}_${System.currentTimeMillis()}"
                            Log.d("TAG", "Session saved: ${sessionInfo.value.sessionId}")
//                        endSession=fa
                            viewmodel.save(sessionInfo.value, context = context)
                            onBack()
                        }

                    }) {
                        Text(text = "Save")
                    }
                }
            }
        }
        BottomBar(modifier=Modifier.align(Alignment.BottomCenter),
            shutterClick = {
                viewmodel.takePicture(controller,context)
            })
    }


}

@Composable
fun SessionCreate(onClick:()-> Unit={},onClickToView:()-> Unit={}){
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),verticalArrangement = Arrangement.Center,horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = onClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Create Session")
        }
        Button(
            onClick = onClickToView,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "view Sessions")
        }
    }
}

@Composable
fun SessionsView(sessionViews: SessionViews = viewModel(factory = SessionViewModelFactory(
    LocalContext.current
)
),onSearch: () -> Unit={},onBack: () -> Unit={},onClickingAnyItem:(session: Sessions)-> Unit={}){
    val context = LocalContext.current
    val sessions = sessionViews.allSessions.collectAsState()
    Scaffold(
        topBar = {
            TopBar(modifier = Modifier
                .padding(start = 10.dp, top = 20.dp), onBack = onBack)
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            item {
                IconButton(
                    onSearch, modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .padding(horizontal = 150.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .shadow(2.dp),
                ) {Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Search, contentDescription = "Search", modifier = Modifier)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Search")
                }
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .border(2.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(10.dp)),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(text = "SessionID")
                    Text(text = "Name")

                }
            }
            items(sessions.value,key = {it.sessionId}) {
                Row(modifier = Modifier.clickable {
                    onClickingAnyItem(it)
                    Toast.makeText(context, it.name, Toast.LENGTH_SHORT).show()
                }.fillMaxWidth()) {

                    Text(
                        text = it.sessionId,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = it.name,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


@Composable
fun PhotoViewOfSession(name: String, age: Int, sessionId: String,cameraView: CameraView = viewModel (),onBack: () -> Unit={}){
    val photos = cameraView.getAllPhotos(sessionId,LocalContext.current)
    Scaffold(modifier = Modifier
        .padding(3.dp)
        .fillMaxSize(),
        topBar = {
            TopBar(modifier = Modifier
                .padding(start = 10.dp, top = 20.dp), onBack = {
                    Log.d("TAG", "PhotoViewOfSession: $sessionId")
                    onBack()
            })
        }
    ) { paddingValues ->
    Column(modifier = Modifier
        .padding(paddingValues)
        .fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(2.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(10.dp)),horizontalArrangement = Arrangement.SpaceAround,verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Name: $name")
            Text(text = "Age: $age")
        }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 120.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(photos) { photo ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(File(photo.path))
                        .crossfade(true)
                        .size(500)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.shadow(2.dp, RoundedCornerShape(10.dp))// keep square images

                )
            }
        }
    }
    }
}

@Composable
fun SearchPage(sessionViews: SessionViews = viewModel(factory = SessionViewModelFactory(
    LocalContext.current
)),onBack: () -> Unit={},onClickingAnyItem:(session:Sessions)-> Unit={}){
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }
    val searches by sessionViews.searches.collectAsState()
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar(modifier = Modifier
                .padding(start = 10.dp, top = 20.dp), onBack = onBack)
        }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                OutlinedTextField(
                    value = text,
                    onValueChange = {
                        text = it
                        sessionViews.updateSearchQuery(it)
                    },
                    label = {
                        Text(text = "Search")
                    },
                    trailingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
                    }
                )
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .border(2.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(10.dp)),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(text = "SessionID")
                    Text(text = "Name")

                }
            }
            items(searches,key = {it.sessionId}) {
                Row(modifier = Modifier.clickable {
                    onClickingAnyItem(it)
                    Toast.makeText(context, it.name, Toast.LENGTH_SHORT).show()
                }.fillMaxWidth()) {

                    Text(
                        text = it.sessionId,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = it.name,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}