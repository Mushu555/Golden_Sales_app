
package com.goldensales.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

private object Routes {
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
}

@Composable
fun GoldenSalesApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME
    ) {
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onGetStartedClick = {
                    navController.navigate(Routes.LOGIN)
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginClick = { email, password ->
                    // Backend authentication will be connected next.
                    // Do not navigate to Home until authentication succeeds.
                },
                onRegisterClick = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
    RegisterScreen(
        onRegisterClick = { name, email, password ->
            // Connect to Auth Service after confirming its API contract.
        },
        onBackClick = {
            navController.popBackStack()
        }
    )
}
        composable(Routes.HOME) {
            HomePlaceholder()
        }
    }
}

@Composable
private fun WelcomeScreen(onGetStartedClick: () -> Unit) {
    androidx.compose.foundation.layout.Column(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        androidx.compose.material3.Text(
            text = "GOLDEN SALES",
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
        )
        androidx.compose.foundation.layout.Spacer(
            androidx.compose.ui.Modifier.height(16.dp)
        )
        androidx.compose.material3.Text(
            text = "Manage products, customers, sales and payments in one place."
        )
        androidx.compose.foundation.layout.Spacer(
            androidx.compose.ui.Modifier.height(24.dp)
        )
        androidx.compose.material3.Button(onClick = onGetStartedClick) {
            androidx.compose.material3.Text("Get Started")
        }
    }
}

@Composable
private fun RegisterPlaceholder(onBackClick: () -> Unit) {
    androidx.compose.foundation.layout.Column(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        androidx.compose.material3.Text("Create account")
        androidx.compose.foundation.layout.Spacer(
            androidx.compose.ui.Modifier.height(16.dp)
        )
        androidx.compose.material3.Text(
            "Registration form will be implemented next."
        )
        androidx.compose.material3.TextButton(onClick = onBackClick) {
            androidx.compose.material3.Text("Back to Sign In")
        }
    }
}

@Composable
private fun HomePlaceholder() {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Text("Golden Sales Home")
    }
}
