package com.example.practica_primer_trimestre_jorge_carlos_parra_2dam.Controlador

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

const val base_user = "http://iesayala.ddns.net/Carlos/"

interface UserInterface {
    @GET("SelectEmpleado.php/")
    fun userInformation(): Call<EmpleadosInfo>
}

object UserInstance {
    val userInterface: UserInterface

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(base_user)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        userInterface = retrofit.create(UserInterface::class.java)
    }

}