package com.example.mapsapp.util

sealed class States<T> {
    class Idle<T>() : States<T>()
    class Success<T>(data: String) : States<T>()
    class Error<T>(message: String) : States<T>()
    class Loading<T>() : States<T>()
}