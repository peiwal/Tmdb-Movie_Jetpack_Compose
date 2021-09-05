/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package petrov.ivan.tmdb.ui

import android.app.Activity
import android.os.Bundle
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import petrov.ivan.tmdb.application.TmdbApplication
import petrov.ivan.tmdb.data.TmdbMovie
import petrov.ivan.tmdb.database.FavoritesDatabase
import petrov.ivan.tmdb.ui.MainDestinations.MOVIE_KEY
import petrov.ivan.tmdb.ui.movieInfo.ScreenMovieInfo
import petrov.ivan.tmdb.ui.movieInfo.MovieInfoViewModel
import petrov.ivan.tmdb.ui.movieInfo.MovieInfoViewModelFactory

object MainDestinations {
    const val HOME_ROUTE = "home"
    const val MOVIE_DETAIL_ROUTE = "movie_detail"
    const val MOVIE_KEY = "movie_key"
}

@Composable
fun TmdbNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    scaffoldState: ScaffoldState,
    startDestination: String =  MainDestinations.HOME_ROUTE,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        builder = {
        navigation(
            route = MainDestinations.HOME_ROUTE,
            startDestination = HomeSections.POPULAR.route
        ) {
            addHomeGraph(
                onMovieSelected = { movie: TmdbMovie, from: NavBackStackEntry ->
                    // In order to discard duplicated navigation events, we check the Lifecycle
                    if (from.lifecycleIsResumed()) {
                        navController.currentBackStackEntry?.arguments = Bundle().apply {
                            putSerializable(MOVIE_KEY, movie)
                        }
                        navController.navigate(MainDestinations.MOVIE_DETAIL_ROUTE)
                    }
                },
                scaffoldState = scaffoldState
            )
        }
        composable(
            route = MainDestinations.MOVIE_DETAIL_ROUTE
        ) { backStackEntry ->
            val movie = navController.previousBackStackEntry?.arguments?.getSerializable(
                MOVIE_KEY) as? TmdbMovie

            movie?.let {
                val application = ((LocalContext.current as Activity).application as TmdbApplication)
                val dataSource = FavoritesDatabase.invoke(application).favoritesDatabaseDao
                val movieInfoViewModel = viewModel<MovieInfoViewModel>(
                    factory = MovieInfoViewModelFactory(dataSource, application, movie = it))
                ScreenMovieInfo(viewModel = movieInfoViewModel,
                    upPress = {
                        navController.navigateUp()
                    }
                )
            }
        }
    })
}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED
