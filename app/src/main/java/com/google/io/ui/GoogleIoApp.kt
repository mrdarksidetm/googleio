package com.google.io.ui

import android.app.Activity
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import coil.compose.AsyncImage
import com.google.io.data.model.Session
import com.google.io.ui.components.AdaptiveScaffold
import com.google.io.ui.components.NavigationItem
import com.google.io.ui.components.VideoPlayer
import com.google.io.ui.screens.GameScreen
import com.google.io.ui.viewmodel.IoViewModel

/**
 * These are the screens in our app!
 */
sealed class Screen(val route: String, val title: String = "", val icon: ImageVector? = null) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Archives : Screen("archives", "Archives", Icons.Default.Archive)
    object You : Screen("you", "You", Icons.Default.Person)
    object SessionDetail : Screen("session_detail/{sessionId}") {
        fun createRoute(sessionId: String) = "session_detail/$sessionId"
        // Task 3: Define the pattern for the deep link URL
        const val DEEP_LINK_URI = "https://io.google.com/session/{sessionId}"
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GoogleIoApp(viewModel: IoViewModel, widthSizeClass: WindowWidthSizeClass) {
    val navController = rememberNavController()
    
    // We keep track of which screen we are currently on
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    // These are the items for our side rail and bottom bar
    val navItems = listOf(
        NavigationItem(Screen.Home.route, "Home", Icons.Default.Home),
        NavigationItem(Screen.Archives.route, "Archives", Icons.Default.Archive),
        NavigationItem(Screen.You.route, "You", Icons.Default.Person)
    )

    SharedTransitionLayout {
        // TASK 2: Using our new AdaptiveScaffold!
        AdaptiveScaffold(
            widthSizeClass = widthSizeClass,
            currentRoute = currentRoute,
            navigationItems = navItems,
            onNavigate = { route ->
                navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            "Google I/O 2026",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        ) 
                    },
                    actions = {
                        if (isLoading) {
                            LoadingIndicator(
                                modifier = Modifier.size(32.dp).padding(end = 16.dp)
                            )
                        }
                    }
                )
            }
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        viewModel = viewModel,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@composable,
                        onSessionClick = { session ->
                            navController.navigate(Screen.SessionDetail.createRoute(session.id))
                        }
                    )
                }
                composable(Screen.Archives.route) {
                    ArchivesScreen(
                        viewModel = viewModel,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@composable,
                        onSessionClick = { session ->
                            navController.navigate(Screen.SessionDetail.createRoute(session.id))
                        }
                    )
                }
                composable(Screen.You.route) {
                    YouScreen(
                        viewModel = viewModel,
                        widthSizeClass = widthSizeClass, // Still need this for YouScreen logic
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@composable,
                        onSessionClick = { session ->
                            navController.navigate(Screen.SessionDetail.createRoute(session.id))
                        }
                    )
                }
                composable(
                    route = Screen.SessionDetail.route,
                    arguments = listOf(navArgument("sessionId") { type = NavType.StringType }),
                    deepLinks = listOf(
                        navDeepLink { uriPattern = Screen.SessionDetail.DEEP_LINK_URI }
                    )
                ) { backStackEntry ->
                    val sessionId = backStackEntry.arguments?.getString("sessionId")
                    val sessions by viewModel.allSessions.collectAsStateWithLifecycle()
                    val session = sessions.find { it.id == sessionId }
                    
                    session?.let {
                        SessionDetailScreen(
                            session = it,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this@composable,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: IoViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onSessionClick: (Session) -> Unit
) {
    // TASK 1: Observe search and filter state
    val sessions by viewModel.filteredSessions.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    
    // We only want the sessions for 2026 on the Home screen
    val currentSessions = sessions.filter { it.year == 2026 }

    Column(modifier = Modifier.fillMaxSize()) {
        // TASK 1: Material 3 SearchBar
        // This is the new, modern search bar. It stays pinned at the top.
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            inputField = {
                SearchBarDefaults.InputField(
                    query = searchQuery,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    onSearch = { /* Handle search action */ },
                    expanded = false,
                    onExpandedChange = { },
                    placeholder = { Text("Search sessions...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                )
            },
            expanded = false,
            onExpandedChange = { },
        ) { }

        // TASK 1: Filter Chips
        // A scrollable row of chips to filter by category.
        val categories = listOf("All", "Mobile", "AI", "Cloud", "History")
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories.size) { index ->
                val category = categories[index]
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { viewModel.updateCategory(category) },
                    label = { Text(category) }
                )
            }
        }

        VideoPlayer(videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Featured Sessions",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // TASK 3: Responsive Content Grids
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 300.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(currentSessions) { session ->
                SessionCard(
                    session = session,
                    onBookmarkToggle = { viewModel.toggleBookmark(session) },
                    onSessionClick = { onSessionClick(session) },
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ArchivesScreen(
    viewModel: IoViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onSessionClick: (Session) -> Unit
) {
    // TASK 1: Observe search and filter state for Archives
    val sessions by viewModel.filteredSessions.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    val pastSessions = sessions.filter { it.year < 2026 }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            inputField = {
                SearchBarDefaults.InputField(
                    query = searchQuery,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    onSearch = { },
                    expanded = false,
                    onExpandedChange = { },
                    placeholder = { Text("Search archives...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                )
            },
            expanded = false,
            onExpandedChange = { },
        ) { }

        val categories = listOf("All", "Mobile", "AI", "Cloud", "History")
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories.size) { index ->
                val category = categories[index]
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { viewModel.updateCategory(category) },
                    label = { Text(category) }
                )
            }
        }

        // TASK 3: Adaptive grids here too!
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 300.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(pastSessions) { session ->
                SessionCard(
                    session = session,
                    onBookmarkToggle = { viewModel.toggleBookmark(session) },
                    onSessionClick = { onSessionClick(session) },
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun YouScreen(
    viewModel: IoViewModel,
    widthSizeClass: WindowWidthSizeClass,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onSessionClick: (Session) -> Unit
) {
    val bookmarkedSessions by viewModel.bookmarkedSessions.collectAsStateWithLifecycle()

    val columns = when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> 1
        WindowWidthSizeClass.Medium -> 2
        else -> 3
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Your Bookmarks",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(16.dp)
        )

        if (bookmarkedSessions.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = "You haven't saved any sessions yet!",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier.weight(1f)
            ) {
                items(bookmarkedSessions) { session ->
                    SessionCard(
                        session = session,
                        onBookmarkToggle = { viewModel.toggleBookmark(session) },
                        onSessionClick = { onSessionClick(session) },
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Play I/O Adventure",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(16.dp)
        )
        GameScreen()
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SessionCard(
    session: Session,
    onBookmarkToggle: () -> Unit,
    onSessionClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    // Task 2: Material 3 Expressive Card with custom styling
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onSessionClick() },
        shape = RoundedCornerShape(24.dp) // More rounded for Expressive look
    ) {
        Column {
            // Task 3: The Image that will animate!
            // We use sharedElement modifier with a unique key (session.id).
            with(sharedTransitionScope) {
                AsyncImage(
                    model = session.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .sharedElement(
                            rememberSharedContentState(key = "image-${session.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    with(sharedTransitionScope) {
                        Text(
                            text = session.title,
                            // Task 2: Expressive type scale
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .weight(1f)
                                .sharedElement(
                                    rememberSharedContentState(key = "title-${session.id}"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                        )
                    }
                    
                    IconButton(onClick = onBookmarkToggle) {
                        Icon(
                            imageVector = if (session.isBookmarked) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Bookmark",
                            tint = if (session.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "By ${session.speaker}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Task 2: Category tag with vibrant tertiary colors
                    SuggestionChip(
                        onClick = { },
                        label = { Text(session.category) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            labelColor = MaterialTheme.colorScheme.onTertiaryContainer
                        ),
                        border = null,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailScreen(
    session: Session,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Session Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Task 3: The matching shared element image!
            with(sharedTransitionScope) {
                AsyncImage(
                    model = session.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .sharedElement(
                            rememberSharedContentState(key = "image-${session.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        ),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(24.dp)) {
                with(sharedTransitionScope) {
                    Text(
                        text = session.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "title-${session.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Speaker: ${session.speaker}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Year: ${session.year}",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                // Category tag
                SuggestionChip(
                    onClick = { },
                    label = { Text(session.category) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        labelColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = session.description,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
