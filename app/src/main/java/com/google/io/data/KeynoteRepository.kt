package com.google.io.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class KeynoteRepository {

    /**
     * Simulates fetching keynotes from a remote API or local database.
     * In a real-world app, this would use Retrofit or Room.
     */
    fun getKeynotes(): Flow<List<Keynote>> = flow {
        // Simulate network latency for a dynamic feel
        delay(1500)
        
        emit(listOf(
            Keynote(
                id = "io26",
                year = "2026",
                date = "May 19, 2026",
                time = "10 AM PT",
                location = "Shoreline Amphitheatre",
                watchLink = "https://io.google/2026",
                keyFeatures = listOf("Gemini 3.5 Pro", "Android XR Audio Glasses", "Gemini Spark (Personal AI Agents)"),
                realWorldImpact = "Transitioned AI from chat-based interfaces to fully autonomous personal agents.",
                rolloutStatus = RolloutStatus.Upcoming
            ),
            Keynote(
                id = "io25",
                year = "2025",
                date = "May 20, 2025",
                time = "10 AM PT",
                location = "Shoreline Amphitheatre",
                watchLink = "https://io.google/2025",
                keyFeatures = listOf("Gemini 2.5 Pro", "Project Astra", "AI Overviews in Search"),
                realWorldImpact = "Enabled multi-modal real-time video understanding through Project Astra.",
                rolloutStatus = RolloutStatus.Beta
            ),
            Keynote(
                id = "io24",
                year = "2024",
                date = "May 14, 2024",
                time = "10 AM PT",
                location = "Shoreline Amphitheatre",
                watchLink = "https://io.google/2024",
                keyFeatures = listOf("Gemini 1.5 Pro", "Project Astra Preview", "Android 15"),
                realWorldImpact = "Brought 1-million-token context windows to developers worldwide.",
                rolloutStatus = RolloutStatus.Stable
            ),
            Keynote(
                id = "io23",
                year = "2023",
                date = "May 10, 2023",
                time = "10 AM PT",
                location = "Shoreline Amphitheatre",
                watchLink = "https://io.google/2023",
                keyFeatures = listOf("PaLM 2", "Bard Expansion", "Magic Editor (Photos)"),
                realWorldImpact = "Democratized generative AI by integrating it directly into Google Workspace and Search.",
                rolloutStatus = RolloutStatus.Stable
            ),
            Keynote(
                id = "io22",
                year = "2022",
                date = "May 11, 2022",
                time = "10 AM PT",
                location = "Shoreline Amphitheatre",
                watchLink = "https://io.google/2022",
                keyFeatures = listOf("Pixel 6a", "Pixel Watch", "Android 13"),
                realWorldImpact = "Consolidated Google's hardware ecosystem and expanded the reach of Material You.",
                rolloutStatus = RolloutStatus.Stable
            ),
            Keynote(
                id = "io21",
                year = "2021",
                date = "May 18, 2021",
                time = "10 AM PT",
                location = "Virtual (Shoreline)",
                watchLink = "https://io.google/2021",
                keyFeatures = listOf("Material You (Material 3)", "LaMDA", "Android 12"),
                realWorldImpact = "Revolutionized mobile UI with dynamic color extraction and introduced large language models (LaMDA).",
                rolloutStatus = RolloutStatus.Stable
            ),
            Keynote(
                id = "io19",
                year = "2019",
                date = "May 7, 2019",
                time = "10 AM PT",
                location = "Shoreline Amphitheatre",
                watchLink = "https://io.google/2019",
                keyFeatures = listOf("Pixel 3a", "Nest Hub Max", "Android 10"),
                realWorldImpact = "Made premium Pixel experiences affordable and introduced full gesture navigation to Android.",
                rolloutStatus = RolloutStatus.Stable
            ),
            Keynote(
                id = "io18",
                year = "2018",
                date = "May 8, 2018",
                time = "10 AM PT",
                location = "Shoreline Amphitheatre",
                watchLink = "https://io.google/2018",
                keyFeatures = listOf("Google Duplex", "Android P (Pie)", "Gmail Smart Compose"),
                realWorldImpact = "Showcased the potential of AI in daily life through natural-sounding voice calls and predictive text.",
                rolloutStatus = RolloutStatus.Stable
            ),
            Keynote(
                id = "io14",
                year = "2014",
                date = "June 25, 2014",
                time = "9 AM PT",
                location = "Moscone Center",
                watchLink = "https://io.google/archive/2014",
                keyFeatures = listOf("Material Design Launch", "Android Lollipop", "Android Wear"),
                realWorldImpact = "Standardized UI/UX across all Google platforms and introduced the card-based design language.",
                rolloutStatus = RolloutStatus.Stable
            )
        ))
    }
}
