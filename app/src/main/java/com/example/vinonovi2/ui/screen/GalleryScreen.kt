@file:Suppress("DEPRECATION")

package com.example.vinonovi2.ui.screen

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.vinonovi2.data.Image
import com.example.vinonovi2.data.ImageDatabase
import com.example.vinonovi2.network.FirebaseStorageManager
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(navController: NavController) {
    val context = LocalContext.current
    val db = remember {
        ImageDatabase.getDatabase(context)
    }
    val imageUriList = db.imageDao().getAll().collectAsState(initial = emptyList())
//    var isSelectionMode by remember { mutableStateOf(false) }
//    var selectedImages by remember { mutableStateOf<Image>(Image(imageUrl = "")) }
    val scope = rememberCoroutineScope()
    Column(
        Modifier
            .fillMaxSize()
    )
    //  .background(Color.White))
    {
        Row(
            verticalAlignment = Alignment.CenterVertically,

            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color.White)
        ) {
            Text(
                text = "HOME",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
            Spacer(Modifier.width(100.dp))

            IconButton(
               onClick = {
//                   isSelectionMode = !isSelectionMode
               })

             {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Localized description",

                    )
            }
            IconButton(onClick = { navController.navigate("search") }) {
                Icon(Icons.Filled.Search, contentDescription = "Localized description")
            }
            IconButton(onClick = {}) {
                Icon(Icons.Filled.QuestionMark, contentDescription = "Localized description")
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
            items(imageUriList.value) { image ->
                Card(
                    modifier = Modifier
                        .size(180.dp)
                        .padding(4.dp)
                        .clickable {
//                            if (isSelectionMode) {
//                                val imageUri = Uri.parse(image.imageUrl)
//                                 이미지 선택/해제 토글
//                                if (selectedImages.contains(imageUri)) {
//                                    selectedImages - imageUri
//                                } else {
//                                    selectedImages + imageUri
//                                }
//                            }
                        },
                ) {
                    GlideImage(
                        modifier = Modifier
                            .fillMaxSize(),
//                                .aspectRatio(1f)
//                                .clip(shape = RoundedCornerShape(16.dp)),
//                                .background(MaterialTheme.colorScheme.background),
                        imageModel = { image.imageUrl },
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )

                    )
//                    if (selectedImages.contains(Uri.parse(image.imageUrl))) {
//                        Icon(
//                            Icons.Filled.Check,
//                            contentDescription = "Localized description",
//                            tint = Color.White,
//                            modifier = Modifier
//                                .align(Alignment.End)
//                                .padding(4.dp)
//                        )
//                    }
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        ExtendedFloatingActionButton(

            icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
            text = { Text("Add") },
            onClick = {
//                if (isSelectionMode && selectedImages.isNotEmpty()) {

                // 삭제 버튼 클릭 시 선택된 이미지 삭제 로직 추가
                // 삭제 후 isSelectionMode 초기화 및 selectedImages 비우기
//                isSelectionMode = false
//                selectedImages.forEach { selectedImage ->
//                    scope.launch(Dispatchers.IO) {
//                        try {
//                            val firebaseStorageManager = FirebaseStorageManager()
//
//                            for (selectedUri in selectedImages) {
//                                 TODO: 선택된 이미지를 삭제하는 작업 수행
//                                firebaseStorageManager.deleteImage(selectedUri.toString())
//
//                                 선택된 이미지 리스트에서 제거
//                                db.imageDao().deleteByImageUrI(selectedUri.toString())
//                            }
//
//                            withContext(Dispatchers.Main) {
//                                Toast.makeText(context, "이미지 삭제 성공", Toast.LENGTH_SHORT).show()
//
//                                 선택된 이미지 삭제 후 상태 초기화
//                                selectedImages = emptySet()
//                            }
//                        } catch (e: Exception) {
//                            withContext(Dispatchers.Main) {
//                                Toast.makeText(context, "이미지 삭제 실패", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    }
//                }
//                selectedImages = emptySet()
//
//            } else {
                // 선택된 이미지가 없으면 추가 버튼 표시
                navController.navigate("upload")

//           }
      },
            modifier = Modifier
                .padding(16.dp)
                .background(color = Color.Transparent)
                .clip(CircleShape) // 원 모양으로 클리핑
                .size(56.dp) // 지정된 크기
        )
    }
}
