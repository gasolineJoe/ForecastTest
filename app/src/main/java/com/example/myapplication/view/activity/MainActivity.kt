package com.example.myapplication.view.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.App
import com.example.myapplication.R
import com.example.myapplication.network.IForecast
import com.example.myapplication.network.pojo.Forecast
import com.example.myapplication.view.adapter.ForecastAdapter
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var weatherApi: IForecast
    private val compositeDisposable = CompositeDisposable()

    private val adapter = ForecastAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.network.inject(this)
        setContentView(R.layout.activity_main)
        initSearch()
        initList()
    }

    private var requestDisposable: Disposable? = null

    private fun getWeatherInCity(city: String) {
        requestDisposable?.dispose()
        startLoading()
        val disp = weatherApi.getForecast(city)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ forecast ->
                setListItems(forecast)
            }, { error ->
                Log.e("forecast request", error.localizedMessage ?: "error")
                showEmptyView()
            })
        requestDisposable = disp
        compositeDisposable.add(disp)
    }

    private fun setListItems(forecast: Forecast) {
        adapter.setItems(forecast)
        empty_view.visibility = View.GONE
        progress.visibility = View.GONE
    }

    private fun showEmptyView() {
        empty_view.visibility = View.VISIBLE
        progress.visibility = View.GONE
    }

    private fun startLoading() {
        adapter.clear()
        progress.visibility = View.VISIBLE
        empty_view.visibility = View.GONE
    }

    private fun initSearch() {
        val disp = RxTextView.textChanges(edit_city_name)
            .debounce(800, TimeUnit.MILLISECONDS)
            .filter { it.length > 2 }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val cityName = it.toString().trim()
                getWeatherInCity(cityName)
            }
        compositeDisposable.add(disp)
    }

    private fun initList() {
        recycler_forecast.layoutManager = LinearLayoutManager(this)
        recycler_forecast.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}
