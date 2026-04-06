package com.example.spacenewstestml.navigation

import com.example.spacenewstestml.data.models.Article

sealed class ScreenNavState { // para que quede un poco mas limpio
    object List : ScreenNavState()
    data class Detail(val article: Article) : ScreenNavState()
    data class Web(val url: String, val article: Article) : ScreenNavState()
}