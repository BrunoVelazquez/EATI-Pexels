package com.eati.pexels.presentation

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import com.eati.pexels.domain.Photo


@Composable
fun PhotosScreen(viewModel: PhotosViewModel) {
    val result by viewModel.photosFlow.collectAsState()

    Photos(result, viewModel::updateResults)
}

@Composable
fun Photos(results: List<Photo>, updateResults: (String) -> Unit) {

    LaunchedEffect(Unit) {
        updateResults("architecture")
    }

    Text(text = results.toString())
}