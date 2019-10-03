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

    class ForecastAdapterItem(val date: String, weatherList: List<WeatherItem>) {
        val text: String
        @Inject
        lateinit var context: Context

        val MAX_CLOUDINNES_PERCENTAGE = 100

        private fun getMinimumTemp(weatherList: List<WeatherItem>) =
            weatherList.minBy { it.weatherData?.tempMin ?: 0.0 }?.weatherData?.tempMin

        private fun getMaximumTemp(weatherList: List<WeatherItem>) =
            weatherList.maxBy { it.weatherData?.tempMax ?: 0.0 }?.weatherData?.tempMax

        private fun getMaximumWind(weatherList: List<WeatherItem>) =
            weatherList.maxBy { it.wind?.speed ?: 0.0 }?.wind?.speed

        private fun getCloudLevelAvgValue(weatherList: List<WeatherItem>, totalLevels: Int): Int {
            val avgCloudPercent = weatherList.sumBy { it.clouds?.all ?: 0 } / weatherList.size
            val levelThreshold = MAX_CLOUDINNES_PERCENTAGE / totalLevels
            return (avgCloudPercent - levelThreshold / 2) / levelThreshold
        }


        init {
            App.app.inject(this)
            val temperatureMin = getMinimumTemp(weatherList)
            val temperatureMax = getMaximumTemp(weatherList)
            val windSpeedMax = getMaximumWind(weatherList)
            val cloudLevelNamesArray = context.resources.getStringArray(R.array.cloudiness_levels)
            val cloudLevelsCount = cloudLevelNamesArray.size
            val cloudLevelName =
                cloudLevelNamesArray[getCloudLevelAvgValue(weatherList, cloudLevelsCount)]
            text = context.getString(
                R.string.weather_format,
                temperatureMin.toString(),
                temperatureMax.toString(),
                windSpeedMax.toString(),
                cloudLevelName.capitalize()
            )
        }
    }

    private val itemList = mutableListOf<ForecastAdapterItem>()

    private fun unixDateToJavaDate(date: Long?) = date?.times(1000L) ?: 0L

    fun setItems(forecast: Forecast) {
        forecast.list?.let { list ->
            itemList.clear()
            val listOfWeathers = list.groupBy {
                val date = DateTime(unixDateToJavaDate(it.dt))
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