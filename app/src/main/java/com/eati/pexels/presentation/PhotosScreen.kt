package com.eati.pexels.presentation

import android.provider.Contacts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.eati.pexels.domain.Photo

@Composable
fun PhotosScreen(viewModel: PhotosViewModel) {
    val result by viewModel.photosFlow.collectAsState()

    Column {
        SearchBar(updateResults = viewModel::updateResults)
        val mapPhotographerPhoto: MutableMap<String, MutableList<String>> =
            getMapPhotographerPhotos(result)

        val listUniquePhotographers : MutableList<UniquePhotographer> = mutableListOf()
        mapPhotographerPhoto.keys.forEach {
            listUniquePhotographers.add(UniquePhotographer(it, mapPhotographerPhoto[it]))
        }
        PhotographersRow(list = listUniquePhotographers)
    }

}

@Composable
fun SearchBar (
    updateResults: (String) -> Unit
) {
    var input by remember {
        mutableStateOf("")
    }
    Row (
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            placeholder = { Text("Search input") },
            value = input,
            onValueChange = { input = it },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.surface
            ),
            leadingIcon = { Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier
                    .clickable(onClick = {updateResults(input)})
            )},
            modifier = Modifier
                .heightIn(min = 56.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
fun Photos(results: List<Photo>, updateResults: (String) -> Unit) {





}

@Composable
private fun getMapPhotographerPhotos(results: List<Photo>): MutableMap<String, MutableList<String>> {
    var listLastIndex = results.size - 1
    val mapPhotographerPhoto: MutableMap<String, MutableList<String>> = mutableMapOf()
    for (i in 0..listLastIndex) {
        var photographer: String = results[i].photographer
        var listPhotos: MutableList<String>? = mapPhotographerPhoto.get(photographer)
        if (listPhotos == null) {
            var newList: MutableList<String> = mutableListOf(results[i].photoUrl)
            mapPhotographerPhoto.put(photographer, newList)
        } else {
            listPhotos.add(results[i].photoUrl)
            mapPhotographerPhoto[photographer] = listPhotos
        }
    }
    return mapPhotographerPhoto
}

data class UniquePhotographer(val photographerName: String, val photos: MutableList<String>?)

@Composable
fun PhotographerThing(
    photographerName: String,
    list: List<UniquePhotographer>,
    modifier: Modifier = Modifier
){
    val isSelected = remember {
        mutableStateOf(false)
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Row {
            Button(
                onClick = {
                    isSelected.value = !isSelected.value
                },
                shape = RoundedCornerShape(100.dp),
                modifier = Modifier
                    .size(100.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                    )
                    Text(
                        text = photographerName,
                        style = MaterialTheme.typography.h2,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp,
                        color = Color.White
                    )
                }
            }
        }
        var name: UniquePhotographer? = null
        list.forEach {
            if (it.photographerName.equals(photographerName))
                name = it
        }

        if (isSelected.value) {
            showPhotos(name)
        }
    }

}

@Composable
fun PhotographersRow(
    list: List<UniquePhotographer>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = modifier
    ) {
        items(list) { item ->
            PhotographerThing(
                photographerName = item.photographerName,
                list,
            )
        }
    }
}

@Composable
fun showPhotos(
    photographer: UniquePhotographer?
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val listPhotos = photographer?.photos
        if (listPhotos != null) {
            items(listPhotos.size) {
                AsyncImage(
                    model = listPhotos[it],
                    contentDescription = null,
                )
            }
        }
    }
}