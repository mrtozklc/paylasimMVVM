package com.example.paylasimmvvm.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.paylasimmvvm.model.Drink
import com.example.paylasimmvvm.model.DrinkDetay
import com.example.paylasimmvvm.model.Kokteyl
import com.example.paylasimmvvm.service.KokteylApiServis
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers


class KokteylViewModel:ViewModel() {
    val kokteyller=MutableLiveData<List<Drink>>()
    val kokteylYukleniyor=MutableLiveData<Boolean>()
    val kokteylHataMesaji=MutableLiveData<Boolean>()
    val disposable=CompositeDisposable()
    val kokteylApiServis=KokteylApiServis()
    val kokteylKategorileriLiveData = MutableLiveData<List<String?>>()
    val kokteylBardaklariLiveData = MutableLiveData<List<String?>>()
    val kokteylIcerikleriLiveData = MutableLiveData<List<String?>>()


    fun getCategoryList(category:String) {
        disposable.add(
            kokteylApiServis.getCategoryList(category)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Kokteyl>() {

                    override fun onError(e: Throwable) {
                        Log.e("hataa","$e")
                        e.printStackTrace()
                    }

                    override fun onSuccess(t: Kokteyl) {
                        val kokteylKategorileri = t.drinks.map { it.kokteylKategori }.distinctBy { it }

                        kokteylKategorileriLiveData.value = kokteylKategorileri

                        Log.e("gelen veri",""+ kokteylKategorileriLiveData.value!!.size)


                        kokteylYukleniyor.value=false

                    }

                })
        )
    }

    fun getGlassList(glass:String) {
        disposable.add(
            kokteylApiServis.getGlassList(glass)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Kokteyl>() {

                    override fun onError(e: Throwable) {
                        Log.e("hataa","$e")
                        e.printStackTrace()
                    }

                    override fun onSuccess(t: Kokteyl) {
                        val kokteylBardaklari = t.drinks.map { it.kokteyGlass }.distinctBy { it }

                        kokteylBardaklariLiveData.value = kokteylBardaklari
                        kokteylYukleniyor.value=false


                    }

                })
        )
    }

    fun getIngredientList(ingredient:String) {
        disposable.add(
            kokteylApiServis.getIngredientList(ingredient)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Kokteyl>() {

                    override fun onError(e: Throwable) {
                        Log.e("hataa","$e")
                        e.printStackTrace()
                    }

                    override fun onSuccess(t: Kokteyl) {
                        val kokteylIcerikleri = t.drinks.flatMap { listOf(it.kokteylicerik1, it.kokteylicerik2,
                            it.kokteylicerik3, it.kokteylicerik4, it.kokteylicerik5, it.kokteylicerik6,
                            it.kokteylicerik7, it.kokteylicerik8, it.kokteylicerik9, it.kokteylicerik10) }.filterNotNull().distinct()

                        kokteylIcerikleriLiveData.value = kokteylIcerikleri
                        Log.e("geleniceriksayısı",""+kokteylIcerikleri.size)
                        kokteylYukleniyor.value=false


                    }

                })
        )
    }

    fun getCategoriesItem(c:String) {
        disposable.add(
            kokteylApiServis.getCategoriesItem(c)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Kokteyl>() {

                    override fun onError(e: Throwable) {
                        Log.e("hataa","$e")
                        e.printStackTrace()
                    }

                    override fun onSuccess(t: Kokteyl) {
                        kokteyller.value= t.drinks.filter{it.kokteylKategori==c}
                        Log.e("kategory",""+kokteyller.value)



                        kokteylYukleniyor.value=false

                    }



                })
        )
    }

    fun getGlassItem(g:String) {
        disposable.add(
            kokteylApiServis.getGlassItem(g)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Kokteyl>() {

                    override fun onError(e: Throwable) {
                        Log.e("hataa","$e")
                        e.printStackTrace()
                    }

                    override fun onSuccess(t: Kokteyl) {
                        kokteyller.value= t.drinks.filter{it.kokteyGlass==g}
                        Log.e("bardak",""+kokteyller.value)



                        kokteylYukleniyor.value=false

                    }

                })
        )
    }

    fun getIngredientsItem(i:String) {
        disposable.add(
            kokteylApiServis.getIngredientsItem(i)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Kokteyl>() {

                    override fun onError(e: Throwable) {
                        Log.e("hataa","$e")
                        e.printStackTrace()
                    }

                    override fun onSuccess(t: Kokteyl) {
                        kokteyller.value= t.drinks.filter{it.kokteylicerik1==i||it.kokteylicerik2==i||it.kokteylicerik3==i||it.kokteylicerik4==i
                                ||it.kokteylicerik5==i||it.kokteylicerik6==i||it.kokteylicerik7==i||it.kokteylicerik8==i
                                ||it.kokteylicerik9==i||it.kokteylicerik10==i}.distinct()
                        Log.e("içerik",""+kokteyller.value)
                        kokteylYukleniyor.value=false

                    }

                })
        )
    }


}