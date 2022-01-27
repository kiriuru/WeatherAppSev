package com.kiriuru.weatherappsev.currentWeather.data.model

import com.google.gson.annotations.SerializedName


data class WeatherResponse(
    @SerializedName("current") val current: Current,
    @SerializedName("location") val location: Location
)

data class Current(
    @SerializedName("cloud") val cloud: Int,
    @SerializedName("condition") val condition: Condition,
    @SerializedName("feelslike_c") val feelslike_c: Double,
    @SerializedName("feelslike_f") val feelslike_f: Double,
    @SerializedName("gust_kph") val gust_kph: Double,
    @SerializedName("gust_mph") val gust_mph: Double,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("is_day") val is_day: Int,
    @SerializedName("last_updated") val last_updated: String,
    @SerializedName("last_updated_epoch") val last_updated_epoch: Int,
    @SerializedName("precip_in") val precip_in: Double,
    @SerializedName("precip_mm") val precip_mm: Double,
    @SerializedName("pressure_in") val pressure_in: Double,
    @SerializedName("pressure_mb") val pressure_mb: Double,
    @SerializedName("temp_c") val temp_c: Double,
    @SerializedName("temp_f") val temp_f: Double,
    @SerializedName("uv") val uv: Double,
    @SerializedName("vis_km") val vis_km: Double,
    @SerializedName("vis_miles") val vis_miles: Double,
    @SerializedName("wind_degree") val wind_degree: Int,
    @SerializedName("wind_dir") val wind_dir: String,
    @SerializedName("wind_kph") val wind_kph: Double,
    @SerializedName("wind_mph") val wind_mph: Double
)

data class Location(
    @SerializedName("country") val country: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("localtime") val localtime: String,
    @SerializedName("localtime_epoch") val localtime_epoch: Int,
    @SerializedName("lon") val lon: Double,
    @SerializedName("name") val name: String,
    @SerializedName("region") val region: String,
    @SerializedName("tz_id") val tz_id: String
)

data class Condition(
    @SerializedName("code") val code: Int,
    @SerializedName("icon") val icon: String,
    @SerializedName("text") val text: String
)