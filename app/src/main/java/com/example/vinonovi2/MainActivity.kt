package com.example.vinonovi2

import android.content.ContentResolver
import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vinonovi2.data.Image
import com.example.vinonovi2.data.ImageDatabase
import com.example.vinonovi2.ui.theme.Vinonovi2Theme
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Vinonovi2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "gallery") {
                        composable("gallery") { GalleryScreen(navController = navController) }
                        composable("upload") { UploadScreen(navController = navController) }
                        composable("search") { SearchScreen(navController = navController) }
                    }
                }
            }
        }
    }
}

@Composable
fun UploadScreen(navController: NavController) {
    val context = LocalContext.current
    val contentResolver = context.contentResolver
    val db = remember {
        ImageDatabase.getDatabase(context)
    }
    var selectUriList by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val scope = rememberCoroutineScope()

    val launcher = // 갤러리 이미지 런쳐
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia(),
            onResult = { uris ->
                if (uris.isNotEmpty()) {
                    selectUriList = uris

                }
            }
        )

    Column {
        LazyVerticalGrid(columns = GridCells.Adaptive(128.dp)) {
            items(selectUriList) { selectUri ->
                GlideImage(
                    modifier = Modifier.size(250.dp),
                    imageModel = { selectUri }, // loading a network image using an URL.
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                )
            }
        }
        Button(onClick = {
            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }) {
            Text(text = "이미지")
        }
        Button(onClick = {
            scope.launch(Dispatchers.IO) {
                try {
                    val firebaseStorageManager = FirebaseStorageManager()

                    for (selectUri in selectUriList) {
                        // 이미지를 업로드하고 다운로드된 URL을 얻음
                        val imageUrl =
                            firebaseStorageManager.uploadImage(contentResolver, selectUri)
                        imageUrl?.let { uri ->
                            val newImage = Image(imageUrl = uri.toString())
                            db.imageDao().insertAll(newImage)
                        }
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "이미지 업로드 성공", Toast.LENGTH_SHORT).show()
                        navController.navigate("gallery")
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text(text = "저장")
        }
    }
}

@Composable
fun GalleryScreen(navController: NavController) {
    val context = LocalContext.current
    val db = remember {
        ImageDatabase.getDatabase(context)
    }
    val imageUriList = db.imageDao().getAll().collectAsState(initial = emptyList())

    Column {
        LazyVerticalGrid(columns = GridCells.Adaptive(128.dp)) {
            items(imageUriList.value) { image ->
                GlideImage(
                    modifier = Modifier.size(128.dp),
                    imageModel = { Uri.parse(image.imageUrl) },
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                )
            }
        }

        Row {
            Button(onClick = { navController.navigate("upload") }) {
                Text("사진 추가")
            }
            Button(onClick = { navController.navigate("search") }) {
                Text("검색")
            }
        }
    }
}

@Composable
fun SearchScreen(navController: NavController) {
    val context = LocalContext.current
    val apiManager = ApiManager()
    val scope = rememberCoroutineScope()
    var imageFileList: List<String> by remember {
        mutableStateOf(emptyList())
    }
    var question by remember {
        mutableStateOf("")
    }
    var imageFileUriList: List<Uri> by remember {
        mutableStateOf(emptyList())
    }
    var dataItemList: List<DataItem> by remember {
        mutableStateOf(emptyList())
    }
    var firebaseStorageManager = FirebaseStorageManager()
    var storageUriList by remember {
        mutableStateOf<List<Uri?>>(emptyList())
    }

    for(imageFile in imageFileList) {
        val storageRef = FirebaseStorage.getInstance().getReference(imageFile)
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            imageFileUriList += uri
        }.addOnFailureListener {
            Toast.makeText(context, "이미지 다운로드 실패", Toast.LENGTH_SHORT).show()
        }
    }
    Column {
        TextField(value = question, onValueChange = { question = it })
        Button(onClick = {
            if (question != "") {
                scope.launch {
                    dataItemList = apiManager.uploadImage(context, question)
                    for(dataItem in dataItemList) {
                        if(dataItem.answer.equals("yes")) {
//                            val fileName = dataItem.image.removePrefix("iamge/")
                            storageUriList += firebaseStorageManager.downloadImage(dataItem.image)
                        }
                    }
                }
            }
        }) {
            Text("검색")
        }
        LazyVerticalGrid(columns = GridCells.Adaptive(128.dp)) {
            if(storageUriList.isNotEmpty()) {
                items(storageUriList) { storageUri ->
                    GlideImage(
                        modifier = Modifier.size(128.dp),
                        imageModel = { storageUri },
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    )
                }
            }
        }

    }
}

class FirebaseStorageManager {
    private val storageRef = Firebase.storage.reference
    private val storageReference: StorageReference =
        FirebaseStorage.getInstance().reference.child("images")

    suspend fun uploadImage(contentResolver: ContentResolver, imageUri: Uri): Uri? {
        // 이미지 파일의 MIME 타입을 얻기
        val mimeType = contentResolver.getType(imageUri)

        // 이미지 파일의 확장자 추출
        val fileExtension =
            mimeType?.let { MimeTypeMap.getSingleton().getExtensionFromMimeType(it) }

        // Firebase Storage에 저장할 이미지 파일 이름 생성
        val imageName = "${UUID.randomUUID()}.$fileExtension"

        // Firebase Storage에 이미지 업로드
        val imageRef = storageReference.child(imageName)
        val uploadTask = imageRef.putFile(imageUri)

        return try {
            // 업로드 완료를 기다림
            val result = uploadTask.await()

            // 업로드된 이미지의 다운로드 URL 반환
            result.storage.downloadUrl.await()
        } catch (e: Exception) {
            // 업로드 실패 시 예외 처리
            null
        }
    }

    suspend fun downloadImage(imageFileName: String): Uri? {
        // Storage Reference 생성
        val imageRef = storageRef.child(imageFileName)

        return try {
            // 이미지 다운로드 URL 반환
            imageRef.downloadUrl.await()
        } catch (e: Exception) {
            // 다운로드 실패 시 예외 처리
            null
        }
    }
}