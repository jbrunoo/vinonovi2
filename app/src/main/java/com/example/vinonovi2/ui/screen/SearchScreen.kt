package com.example.vinonovi2.ui.screen

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.example.vinonovi2.network.ApiManager
import com.example.vinonovi2.network.DataItem
import com.example.vinonovi2.network.FirebaseStorageManager
import com.google.firebase.storage.FirebaseStorage
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch

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
