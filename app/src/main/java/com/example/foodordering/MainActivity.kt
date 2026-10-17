package com.example.foodordering

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.foodordering.screens.auth.LoginScreen
import com.example.foodordering.screens.auth.SignupScreen
import com.example.foodordering.screens.cart.CartScreen
import com.example.foodordering.screens.checkout.CheckoutScreen
import com.example.foodordering.screens.checkout.OrderSuccessScreen
import com.example.foodordering.screens.dashboard.MainScreen
import com.example.foodordering.screens.detailFood.DetailScreen
import com.example.foodordering.screens.itemsList.ItemListScreen
import com.example.foodordering.screens.orders.OrderDetailScreen
import com.example.foodordering.screens.orders.OrderHistoryScreen
import com.example.foodordering.screens.profile.ProfileScreen
import com.example.foodordering.screens.profile.SavedAddressesScreen
import com.example.foodordering.screens.tracking.OrderTrackingScreen
import com.example.foodordering.ui.navigation.Screen
import com.example.foodordering.ui.theme.FoodOrderingTheme
import com.example.foodordering.viewModel.AuthUiState
import com.example.foodordering.viewModel.AuthViewModel
import com.example.foodordering.viewModel.CartViewModel
import com.example.foodordering.viewModel.CheckoutViewModel
import com.example.foodordering.viewModel.MainViewModel
import com.example.foodordering.viewModel.OrderTrackingViewModel
import com.example.foodordering.viewModel.OrderViewModel
import com.example.foodordering.viewModel.ProfileViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodOrderingTheme {
                AppNavHost()
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val mainVm: MainViewModel = viewModel()
    val authVm: AuthViewModel = viewModel()
    val cartVm: CartViewModel = viewModel()
    val checkoutVm: CheckoutViewModel = viewModel()
    val orderVm: OrderViewModel = viewModel()
    val trackingVm: OrderTrackingViewModel = viewModel()
    val profileVm: ProfileViewModel = viewModel()

    val authState by authVm.authState.collectAsState()
    val startDestination = if (authVm.currentUser != null) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(route = Screen.Login.route) {
            LoginScreen(
                authViewModel = authVm,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToSignup = {
                    navController.navigate(Screen.Signup.route)
                }
            )
        }

        composable(route = Screen.Signup.route) {
            SignupScreen(
                authViewModel = authVm,
                onSignupSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigateUp()
                }
            )
        }

        composable(route = Screen.Home.route) {
            val userEmail = (authState as? AuthUiState.Authenticated)?.user?.email
                ?: authVm.currentUser?.email.orEmpty()

            MainScreen(
                viewModel = mainVm,
                userEmail = userEmail,
                onLogout = {
                    authVm.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToCart = {
                    cartVm.loadCart()
                    navController.navigate(Screen.Cart.route)
                },
                onNavigateToOrders = {
                    orderVm.loadOrders()
                    navController.navigate(Screen.Orders.route)
                },
                onNavigateToProfile = {
                    profileVm.loadAddresses()
                    navController.navigate(Screen.Profile.route)
                },
                onOpenItems = { id, title ->
                    navController.navigate(Screen.Items.path(id, title))
                },
                onOpenDetail = { food ->
                    mainVm.selectedFood(food)
                    navController.navigate(Screen.Detail.route)
                }
            )
        }

        composable(
            route = Screen.Items.route,
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            val title = backStackEntry.arguments?.getString("title")

            ItemListScreen(
                viewModel = mainVm,
                id = id,
                title = title,
                onBackClick = { navController.navigateUp() },
                onAddToCart = { foodModel ->
                    cartVm.addToCart(foodModel)
                },
                onOpenDetail = { foodModel ->
                    mainVm.selectedFood(foodModel)
                    navController.navigate(Screen.Detail.route)
                }
            )
        }

        composable(route = Screen.Detail.route) {
            val food by mainVm.selectedFood.collectAsState()
            val currentFood = food ?: return@composable

            DetailScreen(
                item = currentFood,
                viewModel = mainVm,
                onBackClick = { navController.navigateUp() },
                onAddToCartClick = {
                    cartVm.loadCart()
                    navController.navigate(Screen.Cart.route)
                },
                onOpenDetail = { next ->
                    mainVm.selectedFood(next)
                    navController.navigate(Screen.Detail.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(route = Screen.Cart.route) {
            CartScreen(
                cartViewModel = cartVm,
                onBackClick = { navController.navigateUp() },
                onCheckoutClick = {
                    navController.navigate(Screen.Checkout.route)
                },
                onExploreClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateOrders = {
                    orderVm.loadOrders()
                    navController.navigate(Screen.Orders.route)
                },
                onNavigateProfile = {
                    profileVm.loadAddresses()
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        composable(route = Screen.Checkout.route) {
            CheckoutScreen(
                checkoutViewModel = checkoutVm,
                cartViewModel = cartVm,
                onBackClick = { navController.navigateUp() },
                onOrderPlaced = { orderId ->
                    navController.navigate(Screen.OrderSuccess.path(orderId)) {
                        popUpTo(Screen.Cart.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.OrderSuccess.route,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderSuccessScreen(
                orderId = orderId,
                onTrackOrder = { id ->
                    navController.navigate(Screen.OrderTracking.path(id))
                },
                onNavigateHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Orders.route) {
            OrderHistoryScreen(
                orderViewModel = orderVm,
                onBackClick = { navController.navigateUp() },
                onOrderClick = { order ->
                    orderVm.selectOrder(order)
                    navController.navigate(Screen.OrderDetail.path(order.orderId))
                },
                onNavigateHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateCart = {
                    cartVm.loadCart()
                    navController.navigate(Screen.Cart.route)
                },
                onNavigateProfile = {
                    profileVm.loadAddresses()
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        composable(
            route = Screen.OrderDetail.route,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderDetailScreen(
                orderId = orderId,
                orderViewModel = orderVm,
                cartViewModel = cartVm,
                onBackClick = { navController.navigateUp() },
                onTrackOrder = { id ->
                    navController.navigate(Screen.OrderTracking.path(id))
                },
                onReorderClick = {
                    cartVm.loadCart()
                    navController.navigate(Screen.Cart.route)
                }
            )
        }

        composable(
            route = Screen.OrderTracking.route,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderTrackingScreen(
                orderId = orderId,
                trackingViewModel = trackingVm,
                onBackClick = { navController.navigateUp() }
            )
        }

        composable(route = Screen.Profile.route) {
            ProfileScreen(
                profileViewModel = profileVm,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToCart = {
                    cartVm.loadCart()
                    navController.navigate(Screen.Cart.route)
                },
                onNavigateToOrders = {
                    orderVm.loadOrders()
                    navController.navigate(Screen.Orders.route)
                },
                onNavigateToSavedAddresses = {
                    profileVm.loadAddresses()
                    navController.navigate(Screen.SavedAddresses.route)
                },
                onLogout = {
                    authVm.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.SavedAddresses.route) {
            SavedAddressesScreen(
                profileViewModel = profileVm,
                onBackClick = { navController.navigateUp() }
            )
        }
    }
}
