package petrov.ivan.tmdb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ProvideWindowInsets
import petrov.ivan.tmdb.ui.HomeSections
import petrov.ivan.tmdb.ui.TmdbBottomBar
import petrov.ivan.tmdb.ui.TmdbNavGraph
import petrov.ivan.tmdb.ui.components.TmdbScaffold
import petrov.ivan.tmdb.ui.theme.TmdbTheme


class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This app draws behind the system bars, so we want to handle fitting system windows
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            CreateScreen()
        }
    }
}

@Composable
fun CreateScreen() {
    ProvideWindowInsets {
        TmdbTheme {
            val tabs = remember { HomeSections.values() }
            val navController = rememberNavController()
            val scaffoldState = rememberScaffoldState()
            TmdbScaffold(
                bottomBar = { TmdbBottomBar(navController = navController, tabs = tabs) },
                scaffoldState = scaffoldState
            ) { innerPaddingModifier ->
                TmdbNavGraph(
                    navController = navController,
                    scaffoldState = scaffoldState,
                    modifier = Modifier.padding(innerPaddingModifier)
                )
            }
        }
    }
}