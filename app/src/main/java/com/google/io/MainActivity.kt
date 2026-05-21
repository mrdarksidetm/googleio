package com.google.io

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.io.data.Keynote
import com.google.io.data.RolloutStatus
import com.google.io.ui.theme.*
import com.google.io.ui.viewmodel.IoUiState
import com.google.io.ui.viewmodel.IoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GoogleIoTheme {
                GoogleIoExpressiveApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GoogleIoExpressiveApp(viewModel: IoViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Google I/O",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.width(8.dp))
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ) {
                                Text("2026")
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.fetchKeynotes() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier.background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                IoDarkSurface,
                                IoDarkSurface.copy(alpha = 0.8f)
                            )
                        )
                    )
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                // Gemini-inspired animated background pulse
                val infiniteTransition = rememberInfiniteTransition(label = "GeminiPulse")
                val pulseAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.05f,
                    targetValue = 0.15f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(3000, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "PulseAlpha"
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    GeminiPurple.copy(alpha = pulseAlpha),
                                    GeminiBlue.copy(alpha = pulseAlpha * 0.5f),
                                    Color.Transparent
                                ),
                                radius = 1000f
                            )
                        )
                )

                AnimatedContent(
                    targetState = uiState,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(500)) + scaleIn(initialScale = 0.92f)) togetherWith
                                (fadeOut(animationSpec = tween(500)) + scaleOut(targetScale = 1.08f))
                    },
                    label = "UiStateTransition"
                ) { state ->
                    when (state) {
                        is IoUiState.Loading -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = IoBlue)
                            }
                        }
                        is IoUiState.Success -> {
                            KeynoteList(keynotes = state.keynotes)
                        }
                        is IoUiState.Error -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = state.message,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KeynoteList(
    keynotes: List<Keynote>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(keynotes, key = { it.id }) { keynote ->
            KeynoteCard(keynote = keynote)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun KeynoteCard(keynote: Keynote) {
    val context = LocalContext.current
    
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = keynote.year,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = when (keynote.rolloutStatus) {
                        RolloutStatus.Upcoming -> IoBlue
                        RolloutStatus.Beta -> IoYellow
                        RolloutStatus.Stable -> IoGreen
                    }
                )

                Badge(
                    containerColor = when (keynote.rolloutStatus) {
                        RolloutStatus.Upcoming -> IoBlue.copy(alpha = 0.2f)
                        RolloutStatus.Beta -> IoYellow.copy(alpha = 0.2f)
                        RolloutStatus.Stable -> IoGreen.copy(alpha = 0.2f)
                    },
                    contentColor = when (keynote.rolloutStatus) {
                        RolloutStatus.Upcoming -> IoBlue
                        RolloutStatus.Beta -> IoYellow
                        RolloutStatus.Stable -> IoGreen
                    }
                ) {
                    Text(
                        keynote.rolloutStatus.name.uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "${keynote.date} • ${keynote.time}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = keynote.location,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Real-world Impact",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.secondary,
                        letterSpacing = androidx.compose.ui.unit.TextUnit.Unspecified // To avoid issues
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = keynote.realWorldImpact,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2f
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                SplitButtonLayout(
                    leadingButton = {
                        SplitButtonDefaults.LeadingButton(
                            onClick = { 
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(keynote.watchLink))
                                context.startActivity(intent)
                            },
                        ) {
                            Icon(
                                Icons.Default.PlayArrow, 
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.size(8.dp))
                            Text("Watch Keynote", fontWeight = FontWeight.Bold)
                        }
                    },
                    trailingButton = {
                        SplitButtonDefaults.TrailingButton(
                            onClick = { 
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Check out Google I/O ${keynote.year}: ${keynote.watchLink}")
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, null)
                                context.startActivity(shareIntent)
                            },
                        ) {
                            Icon(
                                Icons.Default.Share, 
                                contentDescription = "Share",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                )
            }
        }
    }
}

