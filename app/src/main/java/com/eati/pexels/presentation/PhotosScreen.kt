package com.eati.pexels.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.eati.pexels.domain.Photo


@Composable
fun PhotosScreen(viewModel: PhotosViewModel) {
    val result by viewModel.photosFlow.collectAsState()

    Column(
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
fun SearchBar(
    updateResults: (String) -> Unit
) {
    var input by remember {
        mutableStateOf("")
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    Row(
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
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier
                        .clickable(onClick = { updateResults(input) })
                )
            },
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
    val uriHandler = LocalUriHandler.current

    AnimatedVisibility(
        visible = !expanded,
        modifier = Modifier
            .fillMaxHeight(),
        enter = fadeIn(
            initialAlpha = 0.5f
        ),
        exit = fadeOut(
            targetAlpha = 0.5f
        )
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
    AnimatedVisibility(
        visible = expanded,
        enter = fadeIn(
            initialAlpha = 0.5f
        ),
        exit = fadeOut(
            targetAlpha = 0.5f
        )
    ) {
        AsyncImage(
            model = photo.photoUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth(animF)
                .clickable(onClick = {
                    expanded = !expanded
                }),
            colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
        )

        ClickableText(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            style = TextStyle(
                color = Color.Blue,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            ),
            text = AnnotatedString("More info"),
            onClick = {
                uriHandler.openUri(photo.photographerUrl)
            })
    }
}
