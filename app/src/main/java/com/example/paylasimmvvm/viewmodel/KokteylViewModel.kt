package com.example.paylasimmvvm.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.paylasimmvvm.model.Drink
import com.example.paylasimmvvm.model.DrinkDetay
import com.example.paylasimmvvm.model.Kokteyl
import com.example.paylasimmvvm.model.KokteylDetay
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
    val kokteylIsimleriLiveData= MutableLiveData<List<String?>>()

    fun getCocktailNameList(name:String){
        disposable.add(
            kokteylApiServis.getCategoryList(name)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Kokteyl>() {

                    override fun onError(e: Throwable) {
                        Log.e("hataa","$e")
                        e.printStackTrace()
                    }

                    override fun onSuccess(t: Kokteyl) {
                        val kokteylIsimleri = t.drinks.map { it.kokteylIsim }.distinctBy { it }

                        kokteylIsimleriLiveData.value = kokteylIsimleri



                        kokteylYukleniyor.value=false

                    }

                })
        )

    }
    fun getNameItem(name:String){

        disposable.add(
            kokteylApiServis.getKokteylName(name)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Kokteyl>() {

                    override fun onError(e: Throwable) {
                        Log.e("hataa","$e")

                        e.printStackTrace()
                    }




                    override fun onSuccess(t: Kokteyl) {

                        kokteyller.value= t.drinks.filter{it.kokteylIsim==name}


                        kokteylYukleniyor.value=false
                    }

                })

        )
    }


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

                        kokteylYukleniyor.value=false

                    }

                })
        )
    }




}