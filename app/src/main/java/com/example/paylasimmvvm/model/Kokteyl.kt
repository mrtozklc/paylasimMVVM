package com.example.paylasimmvvm.model

import com.google.gson.annotations.SerializedName

data class Kokteyl(
     @SerializedName("drinks")
     val drinks: List<Drink>
)

data class Drink(
     @SerializedName("idDrink")
     val kokteylID:String?,

     @SerializedName("strDrink")
     val kokteylIsim:String?,

     @SerializedName("strCategory")
     val kokteylKategori:String?,

     @SerializedName("strDrinkThumb")
     val kokteylGorsel:String?
)