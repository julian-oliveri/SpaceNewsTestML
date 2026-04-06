package com.example.spacenewstestml.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AuthorResponse (
    @SerializedName("name") val name: String,
    @SerializedName("socials") val socials: SocialMediaResponse?
)

data class SocialMediaResponse (
    @SerializedName("x") val x: String?,
    @SerializedName("youtube") val youtube: String?,
    @SerializedName("instagram") val instagram: String?,
    @SerializedName("linkedin") val linkedin: String?,
    @SerializedName("mastodon") val mastodon: String?,
    @SerializedName("bluesky") val bluesky: String?
)
