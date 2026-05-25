package com.google.io

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.google.io.data.local.IoDatabase
import com.google.io.data.repository.MockIoRepository
import com.google.io.ui.GoogleIoApp
import com.google.io.ui.theme.GoogleIoTheme
import com.google.io.ui.viewmodel.IoViewModel
import com.google.io.ui.viewmodel.IoViewModelFactory
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.io.data.worker.SyncWorker
import java.util.concurrent.TimeUnit

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import com.google.io.util.NotificationHelper

/**
 * This is the front door of our app!
 */
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Initialize our local database
        val database = IoDatabase.getDatabase(this)
        
        // 2. Initialize our repository (the data middleman)
        val repository = MockIoRepository(database.sessionDao())
        
        // 3. Create the ViewModel (the brain)
        val viewModel: IoViewModel by viewModels {
            IoViewModelFactory(repository)
        }

        // 4. Background Sync Setup
        setupBackgroundSync()

        // TASK 3: Create the notification channel
        NotificationHelper.createNotificationChannel(this)

        // 5. Make the app look pretty and take up the whole screen
        enableEdgeToEdge()

        // 6. Start the UI!
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)

            // TASK 3: Notification Permission (Android 13+)
            // Starting with Android 13, apps MUST ask the user for permission 
            // before showing notifications. 
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted ->
                    if (isGranted) {
                        // User said yes! We can show reminders.
                    }
                }
            )

            // We ask for permission as soon as the app starts!
            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            
            GoogleIoTheme {
                GoogleIoApp(viewModel, windowSizeClass.widthSizeClass)
            }
        }
    }

    /**
     * TASK 2: Enqueue a background task to refresh data every 24 hours.
     */
    private fun setupBackgroundSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "SessionSync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}
