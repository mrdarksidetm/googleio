# ProGuard/R8 Rules for Google I/O App

# --- General Android & Compose Rules ---
# R8 automatically handles most Compose rules, but we keep these for safety.
-keepattributes Signature, Annotation, SourceFile, LineNumberTable

# --- Room Database ---
# Room uses reflection to instantiate the generated Database and DAO implementations.
# We must keep these to prevent R8 from stripping them or changing their names.
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.Entity
-dontwarn androidx.room.paging.**

# --- Coroutines ---
# Coroutines use internal reflection for some optimizations.
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.coroutines.android.HandlerContext {
    private volatile y.b.a.a.g.h _h;
}

# --- Coil (Image Loading) ---
# Prevents stripping of Coil's internal classes.
-keep class coil.** { *; }
-dontwarn coil.**

# --- Media3 (ExoPlayer) ---
# Media3 requires certain classes to be kept for playback functionality.
-keep class androidx.media3.exoplayer.** { *; }
-keep class androidx.media3.ui.** { *; }
-dontwarn androidx.media3.**

# --- Webkit (WebView) ---
# Ensures JavaScript interfaces and internal WebView logic are preserved.
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# --- Kotlin Serialization (If used in future) ---
# Keep metadata for reflection-based serializers.
-keepclassmembernames class kotlinx.serialization.json.internal.JsonScanner {
    public private *;
}
