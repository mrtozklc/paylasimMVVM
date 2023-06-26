package com.example.paylasimmvvm.model

import com.google.gson.annotations.SerializedName

data class Kokteyl(
     @SerializedName("drinks")
     val drinks: List<Drink>
)

data class Drink(
     @SerializedName("strTags")
     val kokteylTag:String?,

     @SerializedName("idDrink")
     val kokteylID:String?,

     @SerializedName("strDrink")
     val kokteylIsim:String?,

     @SerializedName("strCategory")
     val kokteylKategori:String?,

     @SerializedName("strDrinkThumb")
     val kokteylGorsel:String?,


     @SerializedName("strIngredient1")
     val kokteylicerik1:String?,

     @SerializedName("strIngredient2")
     val kokteylicerik2:String?,

     @SerializedName("strIngredient3")
     val kokteylicerik3:String?,

     @SerializedName("strIngredient4")
     val kokteylicerik4:String?,

     @SerializedName("strIngredient5")
     val kokteylicerik5:String?,

     @SerializedName("strIngredient6")
     val kokteylicerik6:String?,

     @SerializedName("strIngredient7")
     val kokteylicerik7:String?,

     @SerializedName("strIngredient8")
     val kokteylicerik8:String?,

     @SerializedName("strIngredient9")
     val kokteylicerik9:String?,

     @SerializedName("strIngredient10")
     val kokteylicerik10:String?,

     @SerializedName("strGlass")
      val kokteyGlass:String?,

     @SerializedName("strCategoryImage")
    val kokteylKategoriImage:String?,

)