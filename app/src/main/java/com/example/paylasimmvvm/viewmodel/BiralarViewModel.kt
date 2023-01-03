package com.example.paylasimmvvm.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.paylasimmvvm.model.BiralarModel
import com.example.paylasimmvvm.service.BiraApiServis
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class BiralarViewModel:ViewModel() {
    val biralar = MutableLiveData<List<BiralarModel>>()
    val hataMesaji = MutableLiveData<Boolean>()
    val yukleniyor = MutableLiveData<Boolean>()

    private val disposable = CompositeDisposable()
    private val biraApiServis = BiraApiServis()

    fun refreshData() {
        verileriInternettenAl()
    }

    private fun verileriInternettenAl() {
        disposable.add(
            biraApiServis.getData()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<BiralarModel>>() {

                    override fun onError(e: Throwable) {
                        Log.e("hataa","$e")
                        e.printStackTrace()
                        hataMesaji.value = true
                        yukleniyor.value = false
                    }

                    override fun onSuccess(t: List<BiralarModel>) {
                        yukleniyor.value = true
                        hataMesaji.value = false
                        biralar.value = t


                    }
                })
        )
    }
}


