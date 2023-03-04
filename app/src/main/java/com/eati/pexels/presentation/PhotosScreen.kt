package com.eati.pexels.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.eati.pexels.domain.Photo

@Composable
fun PhotosScreen(viewModel: PhotosViewModel) {
    val result by viewModel.photosFlow.collectAsState()

    Column (
        modifier = Modifier
            .fillMaxSize()
    ) {
        SearchBar(updateResults = viewModel::updateResults)
        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(result.size) {
                ShowPhoto(photo = result[it])
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar (
    updateResults: (String) -> Unit
) {
    var input by remember {
        mutableStateOf("")
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    Row (
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.surface
            ),
            label = { Text("Search") },
            maxLines = 1,
            singleLine = true,
            leadingIcon = { Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier
                    .clickable(onClick = {updateResults(input)})
            )},
            modifier = Modifier
                .padding(20.dp)
                .heightIn(min = 56.dp)
                .fillMaxWidth(),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    updateResults(input)
                }),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )
    }
}

@Composable
fun ShowPhoto(
    photo: Photo
) {
    var liked by remember {
        mutableStateOf(photo.liked)
    }
    var expanded by remember(photo) {
        mutableStateOf(false)
    }
    val animF by animateFloatAsState(
        targetValue = if (expanded) 1f else 0.25f
    )

    Box(
        contentAlignment = Alignment.BottomEnd
    ) {
        AnimatedVisibility(
            visible = !expanded,
            modifier = Modifier
                .fillMaxHeight()
        ) {
            AsyncImage(
                model = photo.photoUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(250.dp)
                    .clickable(onClick = {
                        expanded = !expanded
                    })
            )
            IconButton(onClick = {
                liked = !liked
            }) {
                Icon(
                    imageVector = if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp),
                    tint = if (liked) Color.Red else Color.White
                )
            }
        }
        AnimatedVisibility(visible = expanded) {
            Column(
               // modifier = Modifier
               //     .background(color = Color(photo.avgColor.toInt()))
            ) {
                AsyncImage(
                    model = photo.photoUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize(animF)
                        .clickable(onClick = {
                            expanded = !expanded
                        })
                )
                Text(
                    text = photo.photographer,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}