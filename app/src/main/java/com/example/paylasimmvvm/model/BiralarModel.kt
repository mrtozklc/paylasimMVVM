package com.example.paylasimmvvm.model

import com.google.gson.annotations.SerializedName


data class BiralarModel (
    @SerializedName("id")
    val biraID: String,
    @SerializedName("name")
    val biraIsim: String,
    @SerializedName("tagline")
    val biraTag: String,
    @SerializedName("abv")
    val biraAlkolOrani: String,
    @SerializedName("ph")
    val biraPHOrani: String
)
