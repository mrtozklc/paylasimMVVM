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

    @SerializedName("strTags")
    val kokteylTag:String?,

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

    @SerializedName("strIngredient4")
    val ingredient4: String?,

    @SerializedName("strIngredient5")
    val ingredient5: String?,

    @SerializedName("strIngredient6")
    val ingredient6: String?,

    @SerializedName("strIngredient7")
    val ingredient7: String?,

    @SerializedName("strIngredient9")
    val ingredient9: String?,

    @SerializedName("strIngredient8")
    val ingredient8: String?,

    @SerializedName("strIngredient10")
    val ingredient10: String?,

    @SerializedName("strInstructions")
    val kokteylTarif:String?,

    @SerializedName("strMeasure1")
    val measure1: String?,

    @SerializedName("strMeasure2")
    val measure2: String?,

    @SerializedName("strMeasure3")
    val measure3: String?,

    @SerializedName("strMeasure4")
    val measure4: String?,

    @SerializedName("strMeasure5")
    val measure5: String?,

    @SerializedName("strMeasure6")
    val measure6: String?,

    @SerializedName("strMeasure7")
    val measure7: String?,

    @SerializedName("strMeasure8")
    val measure8: String?,

    @SerializedName("strMeasure9")
    val measure9: String?,

    @SerializedName("strMeasure10")
    val measure10: String?,

    @SerializedName("strCategory")
    val kokteylKategori:String?

){
    val kokteylMalzemeler: MutableList<Pair<String?, String?>>
        get() {
            val ingredients = mutableListOf<Pair<String?, String?>>()

            ingredient1?.let { ingredients.add(Pair(it, measure1 ?: "") )}
            ingredient2?.let { ingredients.add(Pair(it, measure2 ?: "") )}
            ingredient3?.let { ingredients.add(Pair(it, measure3 ?: "") )}
            ingredient4?.let { ingredients.add(Pair(it, measure4 ?: "") )}
            ingredient5?.let { ingredients.add(Pair(it, measure5 ?: "") )}
            ingredient6?.let { ingredients.add(Pair(it, measure6 ?: "") )}
            ingredient7?.let { ingredients.add(Pair(it, measure7 ?: "") )}
            ingredient8?.let { ingredients.add(Pair(it, measure8 ?: "") )}
            ingredient9?.let { ingredients.add(Pair(it, measure9 ?: "") )}
            ingredient10?.let { ingredients.add(Pair(it, measure10 ?: "") )}
            return ingredients
        }
}