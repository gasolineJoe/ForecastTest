package com.example.myapplication.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.App
import com.example.myapplication.R
import com.example.myapplication.network.pojo.Forecast
import com.example.myapplication.network.pojo.WeatherItem
import kotlinx.android.synthetic.main.item_forecast.view.*
import org.joda.time.DateTime
import javax.inject.Inject

class ForecastAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ForecastAdapterItem(val date: String, w: List<WeatherItem>) {
        val text: String
        @Inject
        lateinit var context: Context

        init {
            App.app.inject(this)
            val min = w.minBy { it.weatherData?.tempMin ?: 0.0 }?.weatherData?.tempMin
            val max = w.maxBy { it.weatherData?.tempMax ?: 0.0 }?.weatherData?.tempMax
            val wind = w.maxBy { it.wind?.speed ?: 0.0 }?.wind?.speed
            val clouds = context.getString(
                when (w.sumBy { it.clouds?.all ?: 0 } / w.size / 25) {
                    0 -> R.string.weather_clear
                    1 -> R.string.partially_cloudy
                    2 -> R.string.mostly_cloudy
                    3 -> R.string.weather_cloudy
                    else -> R.string.weather_cloudy
                }
            )
            text = context.getString(
                R.string.weather_format,
                min.toString(),
                max.toString(),
                wind.toString(),
                clouds.capitalize()
            )
        }
    }

    private val itemList = mutableListOf<ForecastAdapterItem>()

    fun setItems(forecast: Forecast) {
        forecast.list?.let { list ->
            itemList.clear()
            val listOfWeathers = list.groupBy {
                val date = DateTime(it.dt?.times(1000L))
                return@groupBy "${date.monthOfYear().asShortText} ${date.dayOfMonth().asText}"
            }
            for (w in listOfWeathers) {
                itemList.add(ForecastAdapterItem(w.key, w.value))
            }
            notifyDataSetChanged()
        }
    }

    fun clear() {
        itemList.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ForecastHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_forecast,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = itemList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ForecastHolder).bind(itemList[position], position % 2 == 0)
    }

    private class ForecastHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: ForecastAdapterItem, highlight: Boolean) {
            itemView.date.text = data.date
            itemView.description.text = data.text
            val currentColor =
                if (highlight) R.color.colorBackgroundHighlight else R.color.colorBackground
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, currentColor))
        }
    }
}