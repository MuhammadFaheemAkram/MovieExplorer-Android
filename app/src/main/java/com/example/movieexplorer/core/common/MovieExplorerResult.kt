package com.example.movieexplorer.core.common

sealed interface MovieExplorerResult<out T> {
    data class Success<T>(val data: T) : MovieExplorerResult<T>
    data class Error(val message: String, val cause: Throwable? = null) : MovieExplorerResult<Nothing>
}
