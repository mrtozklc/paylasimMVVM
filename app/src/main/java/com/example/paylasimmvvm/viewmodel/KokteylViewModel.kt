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

    val disposable=CompositeDisposable()
    val kokteylApiServis=KokteylApiServis()

    fun refreshData(){
        verileriInternettenAl()
    }

    private fun verileriInternettenAl(){
        kokteylYukleniyor.value=true

        disposable.add(
            kokteylApiServis.getData()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Kokteyl>() {

                    override fun onError(e: Throwable) {
                        Log.e("hataa","$e")

                        e.printStackTrace()
                    }




                    override fun onSuccess(t: Kokteyl) {
                        kokteyller.value= t.drinks
                        kokteylYukleniyor.value=false                        }

                })

        )


    }

}