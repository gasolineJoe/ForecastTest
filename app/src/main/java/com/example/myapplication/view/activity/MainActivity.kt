package com.example.myapplication.view.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.network.pojo.Forecast
import com.example.myapplication.view.adapter.ForecastAdapter
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

interface IForecastScreen {
    fun onItemsLoaded(forecast: Forecast)
    fun onLoadingError(error: Throwable)
    fun onNotFound()
    fun onLoadingStart()
}

class MainActivity : AppCompatActivity(), IForecastScreen {
    override fun onItemsLoaded(forecast: Forecast) {
        setListItems(forecast)
    }

    override fun onLoadingError(error: Throwable) {
        val msg =
            if (error.localizedMessage.isNullOrEmpty())
                this.getString(R.string.internalError)
            else
                error.localizedMessage
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        setProgressVisibility(false)
    }

    override fun onNotFound() {
        showEmptyView()
    }

    override fun onLoadingStart() {
        startLoading()
    }

    private val adapter = ForecastAdapter()
    private val presenter = ForecastPresenter(this)
    private var searchDisposable: Disposable? = null

    private val SEARCH_TIMEOUT_MS = 800L
    private val SEARCH_MIN_TEXT = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initSearch()
        initList()
        presenter.init()
    }

    private fun setListItems(forecast: Forecast) {
        adapter.setItems(forecast)
        empty_view.visibility = View.GONE
        setProgressVisibility(false)
        progress.visibility = View.GONE
    }

    private fun showEmptyView() {
        empty_view.visibility = View.VISIBLE
        setProgressVisibility(false)
    }

    private fun setProgressVisibility(isVisible: Boolean) {
        progress.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun startLoading() {
        adapter.clear()
        setProgressVisibility(true)
        empty_view.visibility = View.GONE
    }

    private fun initSearch() {
        searchDisposable = RxTextView.textChanges(edit_city_name)
            .debounce(SEARCH_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .filter { it.length > SEARCH_MIN_TEXT }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val cityName = it.toString().trim()
                presenter.loadForecast(cityName)
            }
    }

    private fun initList() {
        recycler_forecast.layoutManager = LinearLayoutManager(this)
        recycler_forecast.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroy()
        searchDisposable?.dispose()
    }
}
