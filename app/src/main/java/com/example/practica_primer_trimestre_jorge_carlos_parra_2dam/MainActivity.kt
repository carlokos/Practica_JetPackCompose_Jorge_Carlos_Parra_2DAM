package com.example.practica_primer_trimestre_jorge_carlos_parra_2dam

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.practica_primer_trimestre_jorge_carlos_parra_2dam.ui.theme.Practica_Primer_Trimestre_Jorge_Carlos_Parra_2ºDAMTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.navigation.compose.composable
import com.example.practica_primer_trimestre_jorge_carlos_parra_2dam.Controlador.EmpleadosInfo
import com.example.practica_primer_trimestre_jorge_carlos_parra_2dam.Controlador.UserInstance
import com.example.practica_primer_trimestre_jorge_carlos_parra_2dam.Modelos.JSONempleados
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.URL


class MainActivity : ComponentActivity() {
    @ExperimentalAnimationApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Practica_Primer_Trimestre_Jorge_Carlos_Parra_2ºDAMTheme {
                MainScreen()
            }
        }
    }

    @ExperimentalAnimationApi
    @ExperimentalFoundationApi
    @Composable
    fun MainScreen() {
        val scaffoldState = rememberScaffoldState(rememberDrawerState(initialValue = DrawerValue.Closed))
        val scope = rememberCoroutineScope()
        val navController = rememberNavController()

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { TopBar(scope = scope, scaffoldState = scaffoldState) },
            drawerContent = {
                Drawer(scope = scope, scaffoldState = scaffoldState, navController = navController)
            }

        ){
            Navigation(navController)
        }

    }

    @Composable
    fun TopBar(scope: CoroutineScope, scaffoldState: ScaffoldState){

        TopAppBar(
            title = { Text(text = "Navigation Drawer", fontSize = 18.sp)},
            navigationIcon = {
                IconButton(onClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                }) {
                    Icon(Icons.Filled.Menu, "")
                }
            },
            backgroundColor = Color.DarkGray,
            contentColor = Color.White
        )

    }

    @Composable
    fun Drawer(scope: CoroutineScope, scaffoldState: ScaffoldState, navController: NavController) {

        val items = listOf(
            NavegacionItem.Home,
            NavegacionItem.Add
        )

        Column(
            modifier = Modifier
                .background(color = Color.DarkGray)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color.DarkGray),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                Image(
                    painter = painterResource(id = R.drawable.persona /* aqui va la imagen */),
                    contentDescription = "",
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .padding(10.dp)
                )

            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
            )

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            items.forEach { items ->
                DrawerItem(item = items, selected = currentRoute == items.route, onItemClick = {

                    navController.navigate(items.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }

                    scope.launch {
                        scaffoldState.drawerState.close()
                    }

                })
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Jorge Carlos Parra 2ºDAM",
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.CenterHorizontally)
            )

        }
    }

    @Composable
    fun DrawerItem(item: NavegacionItem, selected: Boolean, onItemClick: (NavegacionItem) -> Unit) {
        val background = if (selected) Color.LightGray else Color.Transparent
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onItemClick(item) }
                .height(45.dp)
                .background(background)
                .padding(start = 10.dp)
        ) {

            Image(
                painter = painterResource(id = item.icon),
                contentDescription = item.title,
                colorFilter = ColorFilter.tint(Color.Black),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(24.dp)
                    .width(24.dp)
            )
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                text = item.title,
                fontSize = 16.sp,
                color = Color.White
            )

        }

    }
    @Composable
    fun cargarJSON() : EmpleadosInfo {
        var users by rememberSaveable { mutableStateOf(EmpleadosInfo()) }
        val user = UserInstance.userInterface.userInformation()

        user.enqueue(object : Callback<EmpleadosInfo> {
            override fun onResponse(
                call : Call<EmpleadosInfo>,
                response: Response<EmpleadosInfo>
            ) {
                val userInfo: EmpleadosInfo? = response.body()
                if (userInfo != null) {
                    users = userInfo
                }
            }

            override fun onFailure(call : Call<EmpleadosInfo>, t: Throwable)
            {
                //error
            }

        })

        return users
    }

    @ExperimentalAnimationApi
    @Composable
    fun EmpleadosList(itemList : List<JSONempleados>){
        val deletedItem = remember { mutableStateListOf<JSONempleados>()}
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color.DarkGray),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Lista de empleados", color=Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            }
            Column(){
                LazyColumn(modifier = Modifier.fillMaxWidth()){
                    itemsIndexed(
                        items = itemList,
                        itemContent = {_, item ->
                            AnimatedVisibility(
                                visible = !deletedItem.contains(item),
                                enter = expandVertically(),
                                exit = shrinkVertically(animationSpec = tween(durationMillis = 1000))
                            ) {
                                Card(modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .padding(10.dp, 5.dp, 10.dp, 5.dp)
                                    .background(Color.White),
                                    elevation = 20.dp,
                                    shape = RoundedCornerShape(5.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)){
                                        Row(modifier = Modifier.fillMaxWidth()){
                                            Image(painter = painterResource(id = R.drawable.usuario),
                                                contentDescription = "ItemImage",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(60.dp)
                                                    .clip(CircleShape))

                                            Column {
                                                Text(
                                                    text = item.Nombre,
                                                    style = TextStyle(Color.Black),
                                                    fontSize = 20.sp, textAlign = TextAlign.Center,
                                                )

                                                Text(
                                                    text = "ID: " + item.ID,
                                                    style = TextStyle(Color.LightGray),
                                                    fontSize = 15.sp
                                                )
                                            }

                                            Row(modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End,
                                                verticalAlignment = Alignment.CenterVertically){
                                                IconButton(onClick = {
                                                    deletedItem.add(item)
                                                    EliminarURL(item)
                                                }) {
                                                    Icon(Icons.Filled.Delete, "")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    fun AñadirURL(ID : String, Nombre: String){
        val url = "http://iesayala.ddns.net/Carlos/InsertEmpleado.php/?ID=$ID&Nombre=$Nombre"
        leerUrl(url)

    }

    fun EliminarURL(e : JSONempleados){
        val ID = e.ID
        val url = "http://iesayala.ddns.net/Carlos/DeleteEmpleado.php/?ID=$ID"
        leerUrl(url)
    }

    fun leerUrl(urlString:String){
        GlobalScope.launch(Dispatchers.IO)   {
            val response = try {
                URL(urlString)
                    .openStream()
                    .bufferedReader()
                    .use { it.readText() }
            } catch (e: IOException) {
                "Error with ${e.message}."
                Log.d("io", e.message.toString())
            } catch (e: Exception) {
                "Error with ${e.message}."
                Log.d("io", e.message.toString())
            }
        }

        return
    }


    @ExperimentalAnimationApi
    @Composable
    fun HomeScreen() {
        var list = cargarJSON()
        if(!list.isEmpty()) {
            EmpleadosList(itemList = list)
        }
    }

    @Composable
    fun ProfileScreen() {
        var textFieldID by rememberSaveable { mutableStateOf("") }
        var textFieldNombre by rememberSaveable { mutableStateOf("") }
        Column (modifier = Modifier.fillMaxSize()){

            TextField(
                value = textFieldID,
                onValueChange = { nuevo ->
                    textFieldID = nuevo
                },
                label = {
                    Text(text = "Introducir ID")
                },
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(textAlign = TextAlign.Right)
            )

            TextField(
                value = textFieldNombre,
                onValueChange = { nuevo ->
                    textFieldNombre = nuevo
                },
                label = {
                    Text(text = "Introducir Nombre")
                },
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                textStyle = TextStyle(textAlign = TextAlign.Right)
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(width = 100.dp, height = 50.dp),


                onClick = {
                    AñadirURL(textFieldID, textFieldNombre)
                    textFieldID = ""
                    textFieldNombre = ""
                }
            ) {
                Text(
                    text = "Insert"
                )
            }
        }
    }

    @ExperimentalAnimationApi
    @ExperimentalFoundationApi
    @Composable
    fun Navigation(navController: NavHostController) {

        NavHost(navController, startDestination = NavegacionItem.Home.route) {
            composable(NavegacionItem.Home.route){
                HomeScreen()
            }


            composable(NavegacionItem.Add.route){
                ProfileScreen()
            }


        }

    }

    @ExperimentalAnimationApi
    @ExperimentalFoundationApi
    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        Practica_Primer_Trimestre_Jorge_Carlos_Parra_2ºDAMTheme {
            MainScreen()
        }
    }
}
