package com.example.vinonovi2.ui.screen

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vinonovi2.data.ImageDatabase
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage

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