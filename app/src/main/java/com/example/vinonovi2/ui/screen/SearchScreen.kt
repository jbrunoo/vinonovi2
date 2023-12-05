@file:Suppress("DEPRECATION")

package com.example.vinonovi2.ui.screen

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.vinonovi2.network.ApiManager
import com.example.vinonovi2.network.DataItem
import com.example.vinonovi2.network.FirebaseStorageManager
import com.example.vinonovi2.ui.component.LoadingCirlce
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storageMetadata
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {
    val context = LocalContext.current
    val apiManager = ApiManager()
    val scope = rememberCoroutineScope()
//    var imageFileList: List<String> by remember {
//        mutableStateOf(emptyList())
//    }
    var question by remember {
        mutableStateOf("")
    }
//    var imageFileUriList: List<Uri> by remember {
//        mutableStateOf(emptyList())
//    }
    var dataItemList: List<DataItem> by remember {
        mutableStateOf(emptyList())
    }
    var firebaseStorageManager = FirebaseStorageManager()
    var storageUriList by remember {
        mutableStateOf<List<Uri?>>(emptyList())
    }
    var loading by remember {
        mutableStateOf(false)
    }

//
//    for (imageFile in imageFileList) {
//        val storageRef = FirebaseStorage.getInstance().getReference(imageFile)
//        storageRef.downloadUrl.addOnSuccessListener { uri ->
//            imageFileUriList += uri
//        }.addOnFailureListener {
//            Toast.makeText(context, "이미지 다운로드 실패", Toast.LENGTH_SHORT).show()
//        }
//    }
    if(loading) {
        LoadingCirlce("AI 검색이 실행중입니다...")
    } else {
        Column {
            Text(
                text = "SEARCH",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color.White)
            ) {
                TextField(
                    value = question,
                    label = { "검색어를 입력하세요" },
                    onValueChange = { question = it },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedLabelColor = Color.Black, containerColor = Color.LightGray
                    ),
                    modifier = Modifier
                        .background(Color.White)
                        .height(70.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, color = Color.LightGray)
                        .width(300.dp)
                )
                Spacer(Modifier.width(10.dp))

                IconButton(onClick = {
                    if (question != "") {
                        loading = true
                        scope.launch {
                            dataItemList = apiManager.uploadImage(context, question)
                            for (dataItem in dataItemList) {
                                if (dataItem.answer.equals("yes")) {
                                    storageUriList = emptyList()
//                            val fileName = dataItem.image.removePrefix("iamge/")
                                    storageUriList += firebaseStorageManager.downloadImage(dataItem.image)
                                }
                            }
                            loading = false
                        }
                    }
                },) {
                    Icon(
                        Icons.Filled.Search, contentDescription = "Localized description",
                        modifier = Modifier.size(50.dp)
                    )
                }

            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(10.dp)
            ) {
                if (storageUriList.isNotEmpty()) {
                    items(storageUriList) { storageUri ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(4.dp)
                                .clickable {
                                    // 항목을 클릭하여 선택을 토글합니다.
                                },
                        ) {
                            GlideImage(
                                modifier = Modifier
                                    .fillMaxSize(),
//                                .aspectRatio(1f)
//                                .clip(shape = RoundedCornerShape(16.dp)),
//                                .background(MaterialTheme.colorScheme.background),
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
    }
}


