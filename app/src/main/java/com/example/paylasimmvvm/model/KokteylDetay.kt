package com.example.paylasimmvvm.model

import com.google.gson.annotations.SerializedName

class KokteylDetay (
    @SerializedName("drinks")
    val drinks: List<DrinkDetay>
)

data class DrinkDetay(
    @SerializedName("idDrink")
    val kokteylID:String?,

    @SerializedName("strDrink")
    val kokteylIsim:String?,

    @SerializedName("strGlass")
    val kokteylbardak:String?,

    @SerializedName("strDrinkThumb")
    val kokteylGorsel:String?,


    @SerializedName("strIngredient1")
    val ingredient1: String?,

    @SerializedName("strIngredient2")
    val ingredient2: String?,

    @SerializedName("strIngredient3")
    val ingredient3: String?,

    @SerializedName("strInstructions")
    val kokteylTarif:String?
){
    val kokteylMalzemeler: List<String>
        get() {
            val ingredients = mutableListOf<String>()
            ingredient1?.let { ingredients.add(it) }
            ingredient2?.let { ingredients.add(it) }
            ingredient3?.let { ingredients.add(it) }
            return ingredients
        }}