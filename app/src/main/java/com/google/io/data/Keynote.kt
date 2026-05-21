package com.google.io.data

enum class RolloutStatus { Stable, Beta, Upcoming }

data class Keynote(
    val id: String,
    val year: String,
    val date: String,
    val time: String,
    val location: String,
    val watchLink: String,
    val keyFeatures: List<String>,
    val realWorldImpact: String,
    val rolloutStatus: RolloutStatus
)
