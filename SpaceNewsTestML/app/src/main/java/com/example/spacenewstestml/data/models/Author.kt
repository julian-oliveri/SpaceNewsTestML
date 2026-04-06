package com.example.spacenewstestml.data.models

import com.example.spacenewstestml.data.remote.dto.AuthorResponse
import com.example.spacenewstestml.data.remote.dto.SocialMediaResponse

data class Author(
    val name: String,
    val socials: SocialMedia?
)

data class SocialMedia(
    val x: String?,
    val youtube: String?,
    val instagram: String?,
    val linkedin: String?,
    val mastodon: String?,
    val bluesky: String?
) {
    // devuelvo solo los q no son nulls (ordenados en prioridad para mostrar)
    fun toList(): List<Pair<String, String>> = listOfNotNull(
        linkedin?.let { "LinkedIn" to it },
        x?.let { "X" to it },
        youtube?.let { "YouTube" to it },
        mastodon?.let { "Mastodon" to it },
        bluesky?.let { "Bluesky" to it },
        instagram?.let { "Instagram" to it }
    )}

fun AuthorResponse.toAuthorModel() = Author(
    name = name,
    socials = socials?.toSocialMediaModel()
)

fun SocialMediaResponse.toSocialMediaModel() = SocialMedia(
    x = x,
    youtube = youtube,
    instagram = instagram,
    linkedin = linkedin,
    mastodon = mastodon,
    bluesky = bluesky
)
