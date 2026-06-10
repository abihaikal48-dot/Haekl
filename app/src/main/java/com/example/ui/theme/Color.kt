package com.example.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

// Global state for Dark Mode theme toggle
var isAppDarkMode by mutableStateOf(false)

// Backgrounds & Base Surfaces
val BgBase: Color
    get() = if (isAppDarkMode) Color(0xFF0F172A) else Color(0xFFF8FAFF)       // Soft ice melt blue-white background
val BgCard: Color
    get() = if (isAppDarkMode) Color(0xFF1E293B) else Color(0xFFFFFFFF)       // Pure white card background
val BgSidebar: Color
    get() = if (isAppDarkMode) Color(0xFF334155) else Color(0xFFF0F4FF)    // Ice soft blue-grey
val BgInput: Color
    get() = if (isAppDarkMode) Color(0xFF334155) else Color(0xFFF5F7FF)      // Ultra-light input background

// Text Colors
val TextPrimary: Color
    get() = if (isAppDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)  // Ink slate black
val TextSecondary: Color
    get() = if (isAppDarkMode) Color(0xFF94A3B8) else Color(0xFF475569)// Medium slate grey
val TextMuted: Color
    get() = if (isAppDarkMode) Color(0xFF64748B) else Color(0xFF94A3B8)    // Muted grey

// Accent Palette
val AccentIndigo: Color
    get() = if (isAppDarkMode) Color(0xFF818CF8) else Color(0xFF6366F1) // Indigo Electric
val AccentLight: Color
    get() = if (isAppDarkMode) Color(0xFF312E81) else Color(0xFFEEF2FF)  // Indigo light glow
val AccentDark: Color
    get() = if (isAppDarkMode) Color(0xFFC7D2FE) else Color(0xFF4F46E5)   // Indigo deep sapphire
val AccentGlow: Color
    get() = if (isAppDarkMode) Color(0x33818CF8) else Color(0x266366F1)   // Glow 15% opacity

// Dynamic Personas
val HaikalCyan: Color
    get() = if (isAppDarkMode) Color(0xFF38BDF8) else Color(0xFF0EA5E9)   // Haikal's Electric Cyan
val HaikalLight: Color
    get() = if (isAppDarkMode) Color(0xFF0C4A6E) else Color(0xFFE0F7FF)
val HaikalDark: Color
    get() = if (isAppDarkMode) Color(0xFF7DD3FC) else Color(0xFF0284C7)

val UmmuRose: Color
    get() = if (isAppDarkMode) Color(0xFFF472B6) else Color(0xFFEC4899)     // Ummu's Rose Gold / Pink
val UmmuLight: Color
    get() = if (isAppDarkMode) Color(0xFF4C0519) else Color(0xFFFDF2F8)
val UmmuDark: Color
    get() = if (isAppDarkMode) Color(0xFFF9A8D4) else Color(0xFFDB2777)

val TogetherViolet: Color
    get() = if (isAppDarkMode) Color(0xFFA78BFA) else Color(0xFF8B5CF6) // Combined Violet Accent
val TogetherLight: Color
    get() = if (isAppDarkMode) Color(0xFF2E1065) else Color(0xFFF5F3FF)
val TogetherDark: Color
    get() = if (isAppDarkMode) Color(0xFFC4B5FD) else Color(0xFF7C3AED)

// Semantic status colors
val ColorSuccess = Color(0xFF10B981) // Emerald green
val ColorSuccessLight: Color
    get() = if (isAppDarkMode) Color(0xFF064E3B) else Color(0xFFECFDF5)
val ColorDanger = Color(0xFFEF4444)  // Ruby red
val ColorDangerLight: Color
    get() = if (isAppDarkMode) Color(0xFF7F1D1D) else Color(0xFFFEF2F2)
val ColorWarning = Color(0xFFF59E0B) // Amber yellow
val ColorWarningLight: Color
    get() = if (isAppDarkMode) Color(0xFF78350F) else Color(0xFFFFFBEB)
val ColorInfo = Color(0xFF3B82F6)    // Sky blue
val ColorInfoLight: Color
    get() = if (isAppDarkMode) Color(0xFF1E3A8A) else Color(0xFFEFF6FF)
