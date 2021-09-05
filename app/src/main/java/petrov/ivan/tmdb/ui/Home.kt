/*
 * Copyright 2020 The Android Open Source Project
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
import androidx.activity.ComponentActivity
import androidx.annotation.FloatRange
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.core.os.ConfigurationCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.navigationBarsPadding
import petrov.ivan.tmdb.R
import petrov.ivan.tmdb.application.TmdbApplication
import petrov.ivan.tmdb.data.TmdbMovie
import petrov.ivan.tmdb.database.FavoritesDatabase
import petrov.ivan.tmdb.ui.components.TmdbSurface
import petrov.ivan.tmdb.ui.favorites.ScreenFavorites
import petrov.ivan.tmdb.ui.favorites.FavoritesViewModel
import petrov.ivan.tmdb.ui.favorites.FavoritesViewModelFactory
import petrov.ivan.tmdb.ui.popularMovies.ScreenPopularMovies
import petrov.ivan.tmdb.ui.popularMovies.PopularMoviesViewModel
import petrov.ivan.tmdb.ui.popularMovies.PopularMoviesViewModelFactory
import petrov.ivan.tmdb.ui.search.ScreenSearch
import petrov.ivan.tmdb.ui.search.SearchViewModel
import petrov.ivan.tmdb.ui.search.SearchViewModelFactory
import petrov.ivan.tmdb.ui.theme.TmdbTheme

fun NavGraphBuilder.addHomeGraph(
    onMovieSelected: (TmdbMovie, NavBackStackEntry) -> Unit,
    scaffoldState: ScaffoldState
) {
    composable(HomeSections.POPULAR.route) { from ->
        val application = ((LocalContext.current as Activity).application as TmdbApplication)
        val viewModel: PopularMoviesViewModel = viewModel(factory =
        PopularMoviesViewModelFactory(movieService = application.getTmdbComponent().getTmdbService(),
            application = application))

        ScreenPopularMovies(
            viewModel = viewModel,
            onMovieClick = { movie -> onMovieSelected(movie, from) },
            scaffoldState = scaffoldState)
    }
    composable(HomeSections.SEARCH.route) { from ->
        val activity = LocalContext.current as ComponentActivity
        val application = (activity.application as TmdbApplication)
        val viewModel: SearchViewModel = viewModel(factory =
        SearchViewModelFactory(movieService = application.getTmdbComponent().getTmdbService(),
            application = application))

        ScreenSearch(viewModel = viewModel,
            onMovieClick = { movie -> onMovieSelected(movie, from) }
        )
    }
    composable(HomeSections.FAVORITE.route) { from ->
        val application = ((LocalContext.current as Activity).application as TmdbApplication)
        val dataSource = FavoritesDatabase.invoke(application).favoritesDatabaseDao
        val viewModel: FavoritesViewModel = viewModel(factory =
        FavoritesViewModelFactory(database = dataSource,
            application = application))

        ScreenFavorites(
            viewModel = viewModel,
            onMovieClick = { movie -> onMovieSelected(movie, from) },
        )
    }
    composable(HomeSections.INFO.route) {
        ScrenAbout()
    }
}

enum class HomeSections(
    @StringRes val title: Int,
    val icon: ImageVector,
    val route: String
) {
    POPULAR(R.string.home_popular, Icons.Outlined.Home, "home/popular"),
    SEARCH(R.string.home_search, Icons.Outlined.Search, "home/search"),
    FAVORITE(R.string.home_favorite, Icons.Outlined.Star, "home/favorite"),
    INFO(R.string.home_info, Icons.Outlined.Info, "home/info")
}

@Composable
fun TmdbBottomBar(
    navController: NavController,
    tabs: Array<HomeSections>,
    color: Color = TmdbTheme.colors.iconPrimary,
    contentColor: Color = TmdbTheme.colors.iconInteractive
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val sections = remember { HomeSections.values() }
    val routes = remember { sections.map { it.route } }
    if (currentRoute in routes) {
        val currentSection = sections.first { it.route == currentRoute }
        TmdbSurface(
            color = color,
            contentColor = contentColor
        ) {
            val springSpec = SpringSpec<Float>(
                // Determined experimentally
                stiffness = 800f,
                dampingRatio = 0.8f
            )
            TmdbBottomNavLayout(
                selectedIndex = currentSection.ordinal,
                itemCount = routes.size,
                indicator = { TmdbBottomNavIndicator() },
                animSpec = springSpec,
                modifier = Modifier.navigationBarsPadding(start = false, end = false)
            ) {
                tabs.forEach { section ->
                    val selected = section == currentSection
                    val tint by animateColorAsState(
                        if (selected) {
                            TmdbTheme.colors.iconInteractive
                        } else {
                            TmdbTheme.colors.iconInteractiveInactive
                        }
                    )

                    TmdbBottomNavigationItem(
                        icon = {
                            Icon(
                                imageVector = section.icon,
                                tint = tint,
                                contentDescription = null
                            )
                        },
                        text = {
                            Text(
                                text = stringResource(section.title).uppercase(
                                    ConfigurationCompat.getLocales(
                                        LocalConfiguration.current
                                    ).get(0)
                                ),
                                color = tint,
                                style = MaterialTheme.typography.button,
                                maxLines = 1
                            )
                        },
                        selected = selected,
                        onSelected = {
                            if (section.route != currentRoute) {
                                navController.navigate(section.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(findStartDestination(navController.graph).id) {
                                        saveState = true
                                    }
                                }
                            }
                        },
                        animSpec = springSpec,
                        modifier = BottomNavigationItemPadding
                            .clip(BottomNavIndicatorShape)
                    )
                }
            }
        }
    }
}

internal val NavGraph.startDestination: NavDestination?
    get() = findNode(startDestinationId)

/**
 * Copied from similar function in NavigationUI.kt
 *
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:navigation/navigation-ui/src/main/java/androidx/navigation/ui/NavigationUI.kt
 */
private tailrec fun findStartDestination(graph: NavDestination): NavDestination {
    return if (graph is NavGraph) findStartDestination(graph.startDestination!!) else graph
}

@Composable
private fun TmdbBottomNavLayout(
    selectedIndex: Int,
    itemCount: Int,
    animSpec: AnimationSpec<Float>,
    indicator: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Track how "selected" each item is [0, 1]
    val selectionFractions = remember(itemCount) {
        List(itemCount) { i ->
            Animatable(if (i == selectedIndex) 1f else 0f)
        }
    }
    selectionFractions.forEachIndexed { index, selectionFraction ->
        val target = if (index == selectedIndex) 1f else 0f
        LaunchedEffect(target, animSpec) {
            selectionFraction.animateTo(target, animSpec)
        }
    }

    // Animate the position of the indicator
    val indicatorIndex = remember { Animatable(0f) }
    val targetIndicatorIndex = selectedIndex.toFloat()
    LaunchedEffect(targetIndicatorIndex) {
        indicatorIndex.animateTo(targetIndicatorIndex, animSpec)
    }

    Layout(
        modifier = modifier.height(BottomNavHeight),
        content = {
            content()
            Box(Modifier.layoutId("indicator"), content = indicator)
        }
    ) { measurables, constraints ->
        check(itemCount == (measurables.size - 1)) // account for indicator

        // Divide the width into n+1 slots and give the selected item 2 slots
        val unselectedWidth = constraints.maxWidth / (itemCount + 1)
        val selectedWidth = 2 * unselectedWidth
        val indicatorMeasurable = measurables.first { it.layoutId == "indicator" }

        val itemPlaceables = measurables
            .filterNot { it == indicatorMeasurable }
            .mapIndexed { index, measurable ->
                // Animate item's width based upon the selection amount
                val width = lerp(unselectedWidth, selectedWidth, selectionFractions[index].value)
                measurable.measure(
                    constraints.copy(
                        minWidth = width,
                        maxWidth = width
                    )
                )
            }
        val indicatorPlaceable = indicatorMeasurable.measure(
            constraints.copy(
                minWidth = selectedWidth,
                maxWidth = selectedWidth
            )
        )

        layout(
            width = constraints.maxWidth,
            height = itemPlaceables.maxByOrNull { it.height }?.height ?: 0
        ) {
            val indicatorLeft = indicatorIndex.value * unselectedWidth
            indicatorPlaceable.placeRelative(x = indicatorLeft.toInt(), y = 0)
            var x = 0
            itemPlaceables.forEach { placeable ->
                placeable.placeRelative(x = x, y = 0)
                x += placeable.width
            }
        }
    }
}

@Composable
fun TmdbBottomNavigationItem(
    icon: @Composable BoxScope.() -> Unit,
    text: @Composable BoxScope.() -> Unit,
    selected: Boolean,
    onSelected: () -> Unit,
    animSpec: AnimationSpec<Float>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.selectable(selected = selected, onClick = onSelected),
        contentAlignment = Alignment.Center
    ) {
        // Animate the icon/text positions within the item based on selection
        val animationProgress by animateFloatAsState(if (selected) 1f else 0f, animSpec)
        TmdbBottomNavItemLayout(
            icon = icon,
            text = text,
            animationProgress = animationProgress
        )
    }
}

@Composable
private fun TmdbBottomNavItemLayout(
    icon: @Composable BoxScope.() -> Unit,
    text: @Composable BoxScope.() -> Unit,
    @FloatRange(from = 0.0, to = 1.0) animationProgress: Float
) {
    Layout(
        content = {
            Box(
                modifier = Modifier
                    .layoutId("icon")
                    .padding(horizontal = TextIconSpacing),
                content = icon
            )
            val scale = lerp(0.6f, 1f, animationProgress)
            Box(
                modifier = Modifier
                    .layoutId("text")
                    .padding(horizontal = TextIconSpacing)
                    .graphicsLayer {
                        alpha = animationProgress
                        scaleX = scale
                        scaleY = scale
                        transformOrigin = BottomNavLabelTransformOrigin
                    },
                content = text
            )
        }
    ) { measurables, constraints ->
        val iconPlaceable = measurables.first { it.layoutId == "icon" }.measure(constraints)
        val textPlaceable = measurables.first { it.layoutId == "text" }.measure(constraints)

        placeTextAndIcon(
            textPlaceable,
            iconPlaceable,
            constraints.maxWidth,
            constraints.maxHeight,
            animationProgress
        )
    }
}

private fun MeasureScope.placeTextAndIcon(
    textPlaceable: Placeable,
    iconPlaceable: Placeable,
    width: Int,
    height: Int,
    @FloatRange(from = 0.0, to = 1.0) animationProgress: Float
): MeasureResult {
    val iconY = (height - iconPlaceable.height) / 2
    val textY = (height - textPlaceable.height) / 2

    val textWidth = textPlaceable.width * animationProgress
    val iconX = (width - textWidth - iconPlaceable.width) / 2
    val textX = iconX + iconPlaceable.width

    return layout(width, height) {
        iconPlaceable.placeRelative(iconX.toInt(), iconY)
        if (animationProgress != 0f) {
            textPlaceable.placeRelative(textX.toInt(), textY)
        }
    }
}

@Composable
private fun TmdbBottomNavIndicator(
    strokeWidth: Dp = 2.dp,
    color: Color = TmdbTheme.colors.iconInteractive,
    shape: Shape = BottomNavIndicatorShape
) {
    Spacer(
        modifier = Modifier
            .fillMaxSize()
            .then(BottomNavigationItemPadding)
            .border(strokeWidth, color, shape)
    )
}

private val TextIconSpacing = 2.dp
private val BottomNavHeight = 56.dp
private val BottomNavLabelTransformOrigin = TransformOrigin(0f, 0.5f)
private val BottomNavIndicatorShape = RoundedCornerShape(percent = 50)
private val BottomNavigationItemPadding = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)

@Preview
@Composable
private fun TmdbBottomNavPreview() {
    TmdbTheme {
        TmdbBottomBar(
            navController = rememberNavController(),
            tabs = HomeSections.values()
        )
    }
}
