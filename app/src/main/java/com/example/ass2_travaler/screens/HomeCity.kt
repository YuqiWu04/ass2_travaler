package com.example.ass2_travaler.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

import coil3.compose.AsyncImage
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController

import com.example.ass2_travaler.componentTool.formatCurrency
import com.example.ass2_travaler.model.City
import com.example.ass2_travaler.routes.CityScreen
import com.example.ass2_travaler.viewmodel.HomeCityViewModel
import androidx.compose.ui.text.font.FontWeight
import com.example.ass2_travaler.R
import kotlinx.coroutines.launch

@Composable
fun UserHeader(username: String, onAvatarClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.touxiang),
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                // 点击头像时触发回调
                .clickable { onAvatarClick() },
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = username,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Let's travel now",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = "The Now Lets Travel Store specialises\nin great deals on holidays",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        modifier = Modifier.padding(horizontal = 16.dp)
    )
    Spacer(modifier = Modifier.height(16.dp))
}
@Composable
fun CityItem(
    city: City,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    Card(
        modifier = Modifier
            .width(200.dp)
            .padding(8.dp)
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .border(2.dp, borderColor, shape = RoundedCornerShape(16.dp))
                .padding(8.dp)
        ) {
            AsyncImage(
                model = city.imageUrl[0],
                contentDescription = city.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = city.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// 横向城市列表
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun CityRecommend(
    viewModel: HomeCityViewModel,
    navController: NavController
) {
    val cities = SharedTravelData.cities.value

    if (cities.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cities) { city ->
                CityItem(
                    city = city,
                    isSelected = city.id == viewModel.selectedId.value,
                    onSelect = {
                        navController.navigate("${CityScreen.Detail.route}/${city.id}")
                    }
                )
            }
        }
    }
}


@Composable
fun CitySearchBar(
    cities: List<City>,
    navController: NavController
) {
    var query by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                errorMessage = ""
            },
            placeholder = { Text("Search...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search Icon"
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                try {
                    if (query.isBlank()) {
                        throw Exception("Please input the city which You want to view")
                    }
                    val foundCity = cities.find { it.title.contains(query, ignoreCase = true) }
                    if (foundCity != null) {
                        navController.navigate("${CityScreen.Detail.route}/${foundCity.id}")
                    } else {
                        errorMessage = "City not found"
                    }
                } catch (e: Exception) {
                    errorMessage = e.message ?: "An error occurred"
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Confirm Search",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}


@Composable
fun StatisticsView(
    travelPlanCount: Int,
    totalBudget: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Travel Plans 卡片
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Travel Plans",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$travelPlanCount",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Total Budget",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatCurrency(totalBudget),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun DashboardView(
    navController: NavController,
    username: String,
    modifier: Modifier = Modifier,
    onAvatarClick: () -> Unit
) {
    val viewModel: HomeCityViewModel = viewModel(factory = HomeCityViewModel.Factory)
    val cities = SharedTravelData.cities.value
    val travelPlans by viewModel.travelPlans.observeAsState(emptyList())
    val totalBudget by viewModel.totalSpending.observeAsState(0.0)

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFFE5F6F5)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp)
        ) {
            UserHeader(username = username, onAvatarClick = onAvatarClick)
            Spacer(modifier = Modifier.height(16.dp))
            CitySearchBar(cities = cities, navController = navController)
            Spacer(modifier = Modifier.height(16.dp))
            StatisticsView(
                travelPlanCount = travelPlans.size,
                totalBudget = totalBudget
            )
            Spacer(modifier = Modifier.height(35.dp))
            Text(
                text = "Recommendation City:",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            CityRecommend(
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}

@Composable
fun DrawerContent(
    navController: NavController,
    username: String,
    onCloseDrawer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = username,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            IconButton(onClick = onCloseDrawer) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Drawer"
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))


        Text(
            text = "My Favorites",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .clickable {

                }
                .padding(vertical = 8.dp)
        )
        Text(
            text = "My Follows",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .clickable {

                }
                .padding(vertical = 8.dp)
        )
        Text(
            text = "Browsing History",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .clickable {

                }
                .padding(vertical = 8.dp)
        )


        Spacer(modifier = Modifier.height(24.dp))


        Button(
            onClick = {

                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Log out")
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeCity(
    navController: NavHostController,
    viewModel: HomeCityViewModel,
    username: String
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surface)
            ){
                DrawerContent(
                    navController = navController,
                    username = username,
                    onCloseDrawer = {
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
            }

        }
    ) {

        DashboardView(
            navController = navController,
            username = username,

            onAvatarClick = {
                scope.launch {
                    drawerState.open()
                }
            }
        )
    }
}