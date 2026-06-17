package com.example.movieexplorer.core.network

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkConfig @Inject constructor() {
    var simulateErrors: Boolean = false
}
