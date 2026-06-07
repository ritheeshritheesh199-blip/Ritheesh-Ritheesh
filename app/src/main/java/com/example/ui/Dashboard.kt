package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.Destination
import com.example.data.DestinationsData
import com.example.data.DiaryEntry
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dashboard(
    viewModel: TravelViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val tabItems = listOf(
        TabItem("Explore", Icons.Filled.Explore, Icons.Outlined.Explore),
        TabItem("AI Guide", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
        TabItem("My Journal", Icons.Filled.EditCalendar, Icons.Outlined.EditCalendar)
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Explore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TN Tour Guide",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background,
                tonalElevation = 8.dp
            ) {
                tabItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.secondary,
                            unselectedIconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        ),
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == index) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (selectedTab) {
                0 -> ExploreTab(viewModel)
                1 -> AIGuideTab(viewModel)
                2 -> MyJournalTab(viewModel)
            }
        }
    }
}

data class TabItem(
    val title: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector
)

// ==================== TAB 0: EXPLORE DESTINATIONS ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreTab(viewModel: TravelViewModel) {
    var selectedDestination by remember { mutableStateOf<Destination?>(null) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Search bar
        OutlinedTextField(
            value = viewModel.searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .testTag("explore_search_bar"),
            placeholder = { Text("Search Temples, Hill Stations, Beaches...", fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search icon") },
            trailingIcon = {
                if (viewModel.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                        Icon(Icons.Filled.Close, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() })
        )

        // Category Horizontal row scroll
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(DestinationsData.categories) { category ->
                val isSelected = viewModel.selectedCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectCategory(category) },
                    label = { Text(category, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f),
                        enabled = true,
                        selected = isSelected
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Grid cards
        val destinations = viewModel.filteredDestinations
        if (destinations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Map,
                        contentDescription = "Empty list",
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No destinations matching your search",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(destinations) { dest ->
                    val isBookmarked = viewModel.bookmarkedIds.contains(dest.id)
                    DestinationCard(
                        destination = dest,
                        isBookmarked = isBookmarked,
                        onBookmarkToggle = { viewModel.toggleBookmark(dest.id) },
                        onClick = { selectedDestination = dest }
                    )
                }
            }
        }
    }

    // Detail overlay dialog
    selectedDestination?.let { dest ->
        DestinationDetailDialog(
            destination = dest,
            isBookmarked = viewModel.bookmarkedIds.contains(dest.id),
            onBookmarkToggle = { viewModel.toggleBookmark(dest.id) },
            onQuickPlan = {
                viewModel.setQuickPlan("Chennai", dest.name, 3, "Heritage Discovery")
            },
            onDismiss = { selectedDestination = null }
        )
    }
}

@Composable
fun DestinationCard(
    destination: Destination,
    isBookmarked: Boolean,
    onBookmarkToggle: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .clickable { onClick() }
            .testTag("destination_card_${destination.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Unsplash image loader
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(destination.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = destination.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Dynamic bottom dim gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.05f), Color.Black.copy(alpha = 0.85f)),
                            startY = 100f
                        )
                    )
            )

            // Bookmark icon button on top-right
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { onBookmarkToggle() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = if (isBookmarked) Color(0xFFFFB300) else Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Labels at bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = destination.name,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier.padding(top = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = destination.region,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = destination.category,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun DestinationDetailDialog(
    destination: Destination,
    isBookmarked: Boolean,
    onBookmarkToggle: () -> Unit,
    onQuickPlan: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(destination.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = destination.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                )
                            )
                    )

                    // Top Action Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.4f))
                        ) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        IconButton(
                            onClick = onBookmarkToggle,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.4f))
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (isBookmarked) Color(0xFFFFB300) else Color.White
                            )
                        }
                    }

                    // Floating name info on bottom of image
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = destination.name,
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = destination.coordinates,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Light
                        )
                    }
                }

                // Scrollable details section
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    item {
                        Text(
                            text = "Overview",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = destination.description,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                            modifier = Modifier.padding(vertical = 4.dp),
                            lineHeight = 18.sp
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.AccessTime, contentDescription = "Time", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Best Season to Visit",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Text(
                            text = destination.bestTimeToVisit,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            modifier = Modifier.padding(start = 22.dp, top = 2.dp)
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Must-See Attractions",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        destination.attractions.forEach { attraction ->
                            Row(
                                modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = attraction,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Local Culinary Specialties",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        destination.localFood.forEach { food ->
                            Row(
                                modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.RestaurantMenu,
                                    contentDescription = "Food",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = food,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.surface)
                                        .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.LiveHelp,
                                        contentDescription = "Insider tip",
                                        tint = Color(0xFFD97706),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Insider Local Tip",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFFD97706)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = destination.insiderTip,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }

                // CTA bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text("Close", color = MaterialTheme.colorScheme.primary)
                    }

                    Button(
                        onClick = {
                            onQuickPlan()
                            Toast.makeText(context, "Plan configurations synced. Opening AI Guide!", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = "Plan pointer", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Build Guide")
                    }
                }
            }
        }
    }
}

// ==================== TAB 1: AI ITINERARY PLANNER (GEMINI) ====================

@Composable
fun AIGuideTab(viewModel: TravelViewModel) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var isSavingSheetOpen by remember { mutableStateOf(false) }

    // Loading fun facts stream
    var loadingTrivia by remember { mutableStateOf("Consulting 'TN Tour AI Guide' experts...") }
    val loadingFacts = listOf(
        "Consulting 'TN Tour AI Guide' experts...",
        "Selecting Chola & Pallava historic points...",
        "Gathering Ooty & Palni mountain climates...",
        "Mapping coastal fish stalls & filter coffees...",
        "Arranging street market sunset walks...",
        "Drafting day-wise travel details..."
    )

    LaunchedEffect(viewModel.isPlanningLoading) {
        if (viewModel.isPlanningLoading) {
            var triviaIdx = 0
            while (viewModel.isPlanningLoading) {
                loadingTrivia = loadingFacts[triviaIdx % loadingFacts.size]
                delay(3000)
                triviaIdx++
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            Text(
                text = "Personalized AI Guide Coach",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
            Text(
                text = "Plan your tailored Tamil Nadu expedition using Gemini AI. Choose preferences, and watch your itinerary build live.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.60f),
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        // Form Inputs Box
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Start city
                    Text("Starting Point", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = viewModel.startCity,
                        onValueChange = { viewModel.updateStartCity(it) },
                        modifier = Modifier.fillMaxWidth().testTag("ai_start_city"),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
                    )

                    // Target destination dropdown simulated / chips
                    Text("Expedition Goal (Destinations)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val popularOptions = listOf("Madurai", "Ooty", "Thanjavur", "Mahabalipuram", "Chennai", "Kodaikanal", "Rameshwaram")
                        items(popularOptions) { opt ->
                            val isChosen = viewModel.selectedDestName.contains(opt)
                            val containerCol = if (isChosen) MaterialTheme.colorScheme.primary else Color.Transparent
                            val borderCol = if (isChosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                            val labelColor = if (isChosen) Color.White else MaterialTheme.colorScheme.onBackground

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(containerCol)
                                    .clickable {
                                        viewModel.updateSelectedDestName(opt)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(opt, fontSize = 11.sp, color = labelColor, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = viewModel.selectedDestName,
                        onValueChange = { viewModel.updateSelectedDestName(it) },
                        modifier = Modifier.fillMaxWidth().testTag("ai_destination_name"),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                        placeholder = { Text("Or specify extra: Madurai & Tanjore combo") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
                    )

                    // Duration Seek
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Duration", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { if (viewModel.numDays > 1) viewModel.updateNumDays(viewModel.numDays - 1) },
                                modifier = Modifier.size(28.dp).clip(CircleShape).background(MaterialTheme.colorScheme.background)
                            ) {
                                Icon(Icons.Filled.Remove, contentDescription = "Decrement", modifier = Modifier.size(16.dp))
                            }
                            Text(
                                text = "${viewModel.numDays} Days",
                                modifier = Modifier.padding(horizontal = 12.dp),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            IconButton(
                                onClick = { if (viewModel.numDays < 7) viewModel.updateNumDays(viewModel.numDays + 1) },
                                modifier = Modifier.size(28.dp).clip(CircleShape).background(MaterialTheme.colorScheme.background)
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = "Increment", modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    // Travel Style chips
                    Text("Journey Vibe", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val vibes = listOf("Cultural Heritage", "Nature & Lakes", "Budget Backpacker", "Culinary Trail", "Luxury Heritage")
                        items(vibes) { vibe ->
                            val isSelected = viewModel.travelStyle == vibe
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background)
                                    .clickable { viewModel.updateTravelStyle(vibe) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = vibe,
                                    fontSize = 11.sp,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Custom input
                    Text("Special Requests (Optional)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = viewModel.extraRequirements,
                        onValueChange = { viewModel.updateExtraRequirements(it) },
                        modifier = Modifier.fillMaxWidth().testTag("ai_extra_requests"),
                        placeholder = { Text("e.g. Vegetarian food, child friendly, photo gear focus...", fontSize = 12.sp) },
                        maxLines = 2,
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
                    )

                    // Button
                    Button(
                        onClick = { viewModel.generateItinerary() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("ai_submit_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(24.dp),
                        enabled = !viewModel.isPlanningLoading
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = "Spark key")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Draft Immersive Plan")
                    }
                }
            }
        }

        // Result displays
        if (viewModel.isPlanningLoading) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(44.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = loadingTrivia,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Drafting your journey directly with Gemini-3.5-Flash...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            modifier = Modifier.padding(top = 4.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        if (viewModel.itineraryResult.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Your Custom Guide",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(viewModel.itineraryResult))
                                        Toast.makeText(context, "Itinerary copied to clipboard", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(Icons.Filled.ContentCopy, contentDescription = "Copy itinerary", tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(
                                    onClick = { isSavingSheetOpen = true }
                                ) {
                                    Icon(Icons.Filled.BookmarkAdd, contentDescription = "Bookmark itinerary", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))

                        // Render AI text
                        Text(
                            text = viewModel.itineraryResult,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.90f),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Yellow.copy(alpha = 0.12f))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "⚠️ AI Prototype Notice: This custom plan is drafted live by Gemini. Be sure to double-check opening hours, temple codes, and season guidelines ahead of physical travel.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal sheet/Dialog to save a diary log from an itinerary
    if (isSavingSheetOpen) {
        Dialog(onDismissRequest = { isSavingSheetOpen = false }) {
            var saveTitle by remember { mutableStateOf("Guide Plan for ${viewModel.selectedDestName}") }
            var saveDate by remember { mutableStateOf("June 2026") }
            var saveRating by remember { mutableStateOf(5) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Pin Guide to Travel Tracker", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    
                    OutlinedTextField(
                        value = saveTitle,
                        onValueChange = { saveTitle = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Log Title") }
                    )
                    
                    OutlinedTextField(
                        value = saveDate,
                        onValueChange = { saveDate = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Visit Date / Intent") }
                    )

                    // Clickable Stars
                    Text("Intent Rating", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (i in 1..5) {
                            Icon(
                                imageVector = if (i <= saveRating) Icons.Filled.Star else Icons.Filled.StarBorder,
                                contentDescription = "$i Stars",
                                modifier = Modifier
                                    .size(28.dp)
                                    .clickable { saveRating = i },
                                tint = if (i <= saveRating) Color(0xFFFFB300) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { isSavingSheetOpen = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = MaterialTheme.colorScheme.primary)
                        }
                        Button(
                            onClick = {
                                viewModel.addDiaryEntry(
                                    title = saveTitle,
                                    destination = viewModel.selectedDestName,
                                    visitDate = saveDate,
                                    rating = saveRating,
                                    notes = "AI Guide generated for ${viewModel.numDays} days starting in ${viewModel.startCity}.\n\nItinerary Extract:\n" + if(viewModel.itineraryResult.length > 250) viewModel.itineraryResult.take(250) + "..." else viewModel.itineraryResult
                                )
                                isSavingSheetOpen = false
                                Toast.makeText(context, "Plan pinned to My Journal!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Save Entry")
                        }
                    }
                }
            }
        }
    }
}

// ==================== TAB 2: MY TRAVEL JOURNAL ====================

@Composable
fun MyJournalTab(viewModel: TravelViewModel) {
    val items by viewModel.diaryEntries.collectAsState()
    var isNewEntrySheetOpen by remember { mutableStateOf(false) }
    var entryToDelete by remember { mutableStateOf<DiaryEntry?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "My Travel Journal",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${items.size} memory logs saved locally",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }

                Button(
                    onClick = { isNewEntrySheetOpen = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("add_journal_button")
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add memo text", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Log", fontSize = 12.sp)
                }
            }

            if (items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.EditCalendar,
                            contentDescription = "Empty diary",
                            modifier = Modifier.size(72.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Memories Captured Yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "Write a review of places you visited or pin custom itineraries generated in the AI Guide section to save them here.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp).padding(horizontal = 12.dp),
                            lineHeight = 16.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items, key = { it.id }) { entry ->
                        JournalEntryCard(
                            entry = entry,
                            onDelete = { entryToDelete = entry }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }

    // Insert Log Sheet Modal Dialog
    if (isNewEntrySheetOpen) {
        Dialog(onDismissRequest = { isNewEntrySheetOpen = false }) {
            var inputTitle by remember { mutableStateOf("") }
            var inputDest by remember { mutableStateOf("Madurai") }
            var inputDate by remember { mutableStateOf("") }
            var inputRating by remember { mutableStateOf(5) }
            var inputNotes by remember { mutableStateOf("") }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Document New Visit",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text("Title of Journey", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = inputTitle,
                                onValueChange = { inputTitle = it },
                                modifier = Modifier.fillMaxWidth().testTag("add_log_title"),
                                placeholder = { Text("e.g. Ooty trekking memories") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
                            )
                        }

                        item {
                            Text("Destination", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                val locations = listOf("Madurai", "Ooty", "Thanjavur", "Mahabalipuram", "Chennai", "Kodaikanal", "Rameshwaram")
                                items(locations) { loc ->
                                    val isSelected = inputDest == loc
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                            .clickable { inputDest = loc }
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(loc, fontSize = 11.sp, color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground)
                                    }
                                }
                            }
                            OutlinedTextField(
                                value = inputDest,
                                onValueChange = { inputDest = it },
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                placeholder = { Text("Or custom destination name") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
                            )
                        }

                        item {
                            Text("Date of Visit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = inputDate,
                                onValueChange = { inputDate = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("e.g., June 2026") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
                            )
                        }

                        item {
                            Text("Rating", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                for (i in 1..5) {
                                    Icon(
                                        imageVector = if (i <= inputRating) Icons.Filled.Star else Icons.Filled.StarBorder,
                                        contentDescription = "$i Stars",
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clickable { inputRating = i },
                                        tint = if (i <= inputRating) Color(0xFFFFB300) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                                    )
                                }
                            }
                        }

                        item {
                            Text("Journal Notes / Thoughts", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = inputNotes,
                                onValueChange = { inputNotes = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp),
                                placeholder = { Text("What did you like? Weather guide, local spots, photography notes...") },
                                maxLines = 5,
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { isNewEntrySheetOpen = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Close", color = MaterialTheme.colorScheme.primary)
                        }
                        Button(
                            onClick = {
                                if (inputTitle.isNotEmpty() && inputDest.isNotEmpty()) {
                                    viewModel.addDiaryEntry(
                                        title = inputTitle,
                                        destination = inputDest,
                                        visitDate = inputDate.ifEmpty { "June 2026" },
                                        rating = inputRating,
                                        notes = inputNotes
                                    )
                                    isNewEntrySheetOpen = false
                                } else {
                                    // Title empty
                                }
                            },
                            modifier = Modifier.weight(1.5f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            enabled = inputTitle.isNotEmpty()
                        ) {
                            Text("Capture Memory")
                        }
                    }
                }
            }
        }
    }

    // Delete confirm dialogue
    entryToDelete?.let { entry ->
        AlertDialog(
            onDismissRequest = { entryToDelete = null },
            icon = { Icon(Icons.Filled.Delete, contentDescription = "Trash", tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Discard Memory?") },
            text = { Text("Are you sure you want to delete '${entry.title}' permanently from your Room storage?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteDiaryEntry(entry)
                        entryToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { entryToDelete = null }) {
                    Text("Keep Entry", color = MaterialTheme.colorScheme.onBackground)
                }
            }
        )
    }
}

@Composable
fun JournalEntryCard(
    entry: DiaryEntry,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("journal_card_${entry.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(entry.destination, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = entry.visitDate,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = entry.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp).offset(x = 6.dp, y = (-6).dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete entry",
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Stars
            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                for (i in 1..5) {
                    Icon(
                        imageVector = if (i <= entry.rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (i <= entry.rating) Color(0xFFFFB300) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                    )
                }
            }

            if (entry.notes.isNotEmpty()) {
                Text(
                    text = entry.notes,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                    modifier = Modifier.padding(top = 4.dp),
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
