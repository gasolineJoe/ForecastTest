package com.example.myapplication.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.App
import com.example.myapplication.R
import com.example.myapplication.network.IForecast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var weatherApi: IForecast
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.component.inject(this)
        setContentView(R.layout.activity_main)
        initSearch()
        initList()
    }

    private fun getWeatherInCity(city: String) {
        val disp = weatherApi.getForecast(city)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ forecast ->
                //todo
            }, { error ->
                //todo
            })
        compositeDisposable.add(disp)
    }

    private fun initSearch() {
        btn_search.setOnClickListener {
            getWeatherInCity(edit_city_name.text.toString())
        }
    }

    private fun initList() {
        //todo
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}
