package com.example.myapplication.view.activity

import android.util.Log
import com.example.myapplication.App
import com.example.myapplication.network.IForecast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.lang.ref.WeakReference
import javax.inject.Inject

class ForecastPresenter(view: IForecastScreen) {
    @Inject
    lateinit var weatherApi: IForecast

    private val compositeDisposable = CompositeDisposable()
    private var requestDisposable: Disposable? = null
    private val view: WeakReference<IForecastScreen> = WeakReference(view)

    private val ERROR_NOT_FOUND = "404"

    fun init() {
        App.network.inject(this)
    }

    fun loadForecast(city: String) {
        requestDisposable?.dispose()
        view.get()?.onLoadingStart()
        val disp = weatherApi.getForecast(city)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ forecast ->
                view.get()?.onItemsLoaded(forecast)
            }, { error ->
                val msg = error.localizedMessage ?: ""
                Log.e("forecast request", msg)
                if (msg.contains(ERROR_NOT_FOUND)) {
                    view.get()?.onNotFound()
                } else {
                    view.get()?.onLoadingError(error)
                }
            })
        requestDisposable = disp
        compositeDisposable.add(disp)
    }

    fun destroy() {
        compositeDisposable.dispose()
    }
}