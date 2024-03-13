package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Jabalpur")
        searchCity()
    }

    private fun searchCity() {
        val search = binding.searchView
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityname:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(APIInterface::class.java)
        val response = retrofit.getWeatherData(cityname,"dbb8e08e81afad00ecba8279cb391ab2","metric")
        response.enqueue(object : Callback<weatherapp> {
            override fun onResponse(call: Call<weatherapp>, response: Response<weatherapp>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunrise = responseBody.sys.sunrise.toLong()
                    val sunset = responseBody.sys.sunset.toLong()
                    val sealevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxtemp = responseBody.main.temp_max
                    val mintemp = responseBody.main.temp_min
                    binding.temp.text= "$temperature °C"
                    binding.weather.text = condition
                    binding.max.text = "Max temp: $maxtemp °C"
                    binding.min.text = "Min temp: $mintemp °C"
                    binding.humidity.text ="$humidity %"
                    binding.windspeed.text = "$windSpeed m/s"
                    binding.sunrisetime.text = "${time(sunrise)}"
                    binding.sunsettime.text = "${time(sunset)}"
                    binding.condition.text = condition
                    binding.cityname.text = "$cityname"
                    binding.sea.text = "$sealevel hPa"
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = DateFun()
//                    Log.d("TAG", "onResponse: $temperature")
//                    Toast.makeText(this@MainActivity, "$temperature", Toast.LENGTH_SHORT).show()
                }
                //else Toast.makeText(this@MainActivity, "Wrong", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<weatherapp>, t: Throwable) {
                //TODO("Not yet implemented")
            }

        })

    }
    fun DateFun():String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
    fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))
    }
    fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }
}