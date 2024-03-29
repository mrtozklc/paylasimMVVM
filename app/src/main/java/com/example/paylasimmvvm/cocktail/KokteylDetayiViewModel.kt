package com.example.paylasimmvvm.cocktail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.paylasimmvvm.model.DrinkDetay
import com.example.paylasimmvvm.model.KokteylDetay
import com.example.paylasimmvvm.service.KokteylApiServis
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class KokteylDetayiViewModel:ViewModel() {

    val kokteyller= MutableLiveData<List<DrinkDetay>>()
    val kokteylYukleniyor= MutableLiveData<Boolean>()
    val kokteylHataMesaji=MutableLiveData<Boolean>()

    val disposable= CompositeDisposable()
    val kokteylApiServis= KokteylApiServis()



   fun verileriInternettenAl(kokteylId: String){


        disposable.add(
            kokteylApiServis.getKokteylDetayi(kokteylId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<KokteylDetay>() {

                    override fun onError(e: Throwable) {
                        Log.e("hataa","$e")

                        e.printStackTrace()
                    }




                    override fun onSuccess(t: KokteylDetay) {


                      kokteyller.value= t.drinks.filter{it.kokteylID==kokteylId}




                        kokteylYukleniyor.value=false
                    }

                })

        )


    }
}