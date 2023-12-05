package com.example.vinonovi2.ui.screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.vinonovi2.data.Image
import com.example.vinonovi2.data.ImageDatabase
import com.example.vinonovi2.network.FirebaseStorageManager
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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