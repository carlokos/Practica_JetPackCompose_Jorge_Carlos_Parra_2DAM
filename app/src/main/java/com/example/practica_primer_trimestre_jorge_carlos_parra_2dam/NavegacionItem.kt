package com.example.practica_primer_trimestre_jorge_carlos_parra_2dam

import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavegacionItem (var route: String, var icon: Int, var title: String)
{
    object Home : NavegacionItem("home", R.drawable.home, "Home")
    object Add : NavegacionItem("add", R.drawable.poner, "Add")
    object Remove : NavegacionItem("remove", R.drawable.delete, "Remove")
}