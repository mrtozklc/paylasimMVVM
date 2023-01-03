package com.example.paylasimmvvm.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.paylasimmvvm.model.Drink
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
                        kokteyller.value= t.drinks
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
                        kokteyller.value= t.drinks
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
                        kokteyller.value= t.drinks
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
                        kokteyller.value= t.drinks
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
                        kokteyller.value= t.drinks
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
                        kokteyller.value= t.drinks
                        kokteylYukleniyor.value=false

                    }

                })
        )
    }


}