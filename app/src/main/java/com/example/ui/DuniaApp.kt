package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.ContentPasteOff
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuniaApp(viewModel: DuniaViewModel) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 768

    // Coroutine Scope for in-app micro actions
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // State of transaction dialog sheet
    var showQuickAddDialog by remember { mutableStateOf(false) }

    MyApplicationTheme(darkTheme = viewModel.isDarkMode) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "🌍 DUNIA v2.0",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AccentIndigo,
                                    letterSpacing = 1.sp
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(BgSidebar)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    "Haikal × Ummu",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AccentDark
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        scrolledContainerColor = Color.White
                    ),
                    actions = {
                        // Digital Clock
                        var timeString by remember { mutableStateOf("10:35:00 UTC") }
                        LaunchedEffect(Unit) {
                            while (true) {
                                val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                                sdf.timeZone = TimeZone.getTimeZone("UTC")
                                timeString = sdf.format(Date()) + " UTC"
                                delay(1000L)
                            }
                        }
                        Text(
                            timeString,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(end = 12.dp)
                        )

                        // Private Mode Masking Toggle
                        IconButton(
                            onClick = { viewModel.privateMode = !viewModel.privateMode },
                            modifier = Modifier.testTag("private_mode_toggle")
                        ) {
                            Icon(
                                imageVector = if (viewModel.privateMode) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle Private Numbers",
                                tint = AccentIndigo
                            )
                        }

                        // Dark Mode Toggle Button
                        IconButton(
                            onClick = { viewModel.isDarkMode = !viewModel.isDarkMode },
                            modifier = Modifier.testTag("dark_mode_toggle")
                        ) {
                            Icon(
                                imageVector = if (viewModel.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Dark Mode",
                                tint = AccentIndigo
                            )
                        }

                        // Debug log trigger
                        IconButton(
                            onClick = { viewModel.showDebugPanel = !viewModel.showDebugPanel },
                            modifier = Modifier.testTag("debug_panel_toggle")
                        ) {
                            Icon(
                                imageVector = Icons.Default.BugReport,
                                contentDescription = "Toggle Debugger",
                                tint = if (viewModel.showDebugPanel) ColorSuccess else TextMuted
                            )
                        }
                    }
                )
            },
            bottomBar = {
                if (!isTablet) {
                    DuniaBottomNavigation(
                        currentTab = viewModel.currentTab,
                        onTabSelected = { viewModel.currentTab = it }
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showQuickAddDialog = true },
                    containerColor = AccentIndigo,
                    contentColor = Color.White,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .testTag("quick_add_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Transaction")
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = BgBase
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Wide screen side navigation bar
                if (isTablet) {
                    DuniaNavigationRail(
                        currentTab = viewModel.currentTab,
                        onTabSelected = { viewModel.currentTab = it }
                    )
                }

                // Main screen view switcher with anim crossfades
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    when (viewModel.currentTab) {
                        "Dashboard" -> DashboardScreen(viewModel)
                        "Transactions" -> TransactionsScreen(viewModel)
                        "SavingsGoals" -> SavingsGoalsScreen(viewModel)
                        "Simulator" -> SimulatorScreen(viewModel)
                        "AIAdvisor" -> AdvisorScreen(viewModel)
                        "More" -> MoreFeaturesScreen(viewModel)
                        else -> DashboardScreen(viewModel)
                    }

                    // Floating Overlay System debugger console
                    if (viewModel.showDebugPanel) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .align(Alignment.BottomCenter)
                                .background(Color(0xFF0F172A))
                                .border(1.dp, Color(0xFF1E293B))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "🐛 SYSTEM DEV DEV PANEL (DUNIA v2)",
                                        color = Color(0xFF94A3B8),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row {
                                        TextButton(onClick = { viewModel.resetDatabase() }) {
                                            Text("REBUILD SHEETS", color = Color.Yellow, fontSize = 11.sp)
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        TextButton(onClick = { viewModel.clearDebugLogs() }) {
                                            Text("CLEAR", color = Color.White, fontSize = 11.sp)
                                        }
                                        IconButton(onClick = { viewModel.showDebugPanel = false }) {
                                            Icon(Icons.Default.Close, "Close", tint = Color.LightGray)
                                        }
                                    }
                                }
                                Divider(color = Color(0xFF1E293B))
                                Spacer(modifier = Modifier.height(4.dp))
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0xFF020617))
                                        .padding(4.dp)
                                ) {
                                    items(viewModel.debugLogs.reversed()) { log ->
                                        val color = when {
                                            log.contains("[OK]") -> Color(0xFF10B981)
                                            log.contains("[WARN]") -> Color(0xFFF59E0B)
                                            log.contains("[ERROR]") -> Color(0xFFEF4444)
                                            log.contains("[INFO]") -> Color(0xFF3B82F6)
                                            else -> Color.Gray
                                        }
                                        Text(
                                            text = log,
                                            color = color,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Transactions Sheet / Dialog Trigger
        if (showQuickAddDialog) {
            QuickAddTransactionDialog(
                viewModel = viewModel,
                onDismiss = { showQuickAddDialog = false }
            )
        }
    }
}

// ── DOUBLE FORMATTING UTILS FOR MONIES ──
fun Double.toMaskedCurrency(masked: Boolean): String {
    return if (masked) {
        "Rp •••••••"
    } else {
        "Rp " + NumberFormat.getNumberInstance(Locale.GERMANY).format(this.toLong())
    }
}

// ── CUSTOM BOTTOM NAVIGATION BAR (MOBILE) ──
@Composable
fun DuniaBottomNavigation(
    currentTab: String,
    onTabSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        val items = listOf(
            Triple("Dashboard", Icons.Default.Dashboard, "Home"),
            Triple("Transactions", Icons.Default.ReceiptLong, "Catat"),
            Triple("SavingsGoals", Icons.Default.AccountBalanceWallet, "Tabungan"),
            Triple("Simulator", Icons.Default.Calculate, "Formula"),
            Triple("AIAdvisor", Icons.Default.SmartToy, "Asisten AI"),
            Triple("More", Icons.Default.Menu, "Lebih")
        )

        items.forEach { (tab, icon, label) ->
            NavigationBarItem(
                selected = currentTab == tab,
                onClick = { onTabSelected(tab) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentIndigo,
                    unselectedIconColor = TextMuted,
                    selectedTextColor = AccentIndigo,
                    unselectedTextColor = TextMuted,
                    indicatorColor = AccentLight
                ),
                modifier = Modifier.testTag("nav_item_${tab.lowercase()}")
            )
        }
    }
}

// ── CUSTOM NAVIGATION RAIL BAR (WIDE/TABLETS) ──
@Composable
fun DuniaNavigationRail(
    currentTab: String,
    onTabSelected: (String) -> Unit
) {
    NavigationRail(
        containerColor = Color.White,
        modifier = Modifier.fillMaxHeight()
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        val items = listOf(
            Triple("Dashboard", Icons.Default.Dashboard, "Home"),
            Triple("Transactions", Icons.Default.ReceiptLong, "Catat"),
            Triple("SavingsGoals", Icons.Default.AccountBalanceWallet, "Tabungan"),
            Triple("Simulator", Icons.Default.Calculate, "Simulator"),
            Triple("AIAdvisor", Icons.Default.SmartToy, "Gemini AI"),
            Triple("More", Icons.Default.Menu, "Menu Lain")
        )

        items.forEach { (tab, icon, label) ->
            NavigationRailItem(
                selected = currentTab == tab,
                onClick = { onTabSelected(tab) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = AccentIndigo,
                    unselectedIconColor = TextMuted,
                    selectedTextColor = AccentIndigo,
                    unselectedTextColor = TextMuted,
                    indicatorColor = AccentLight
                ),
                modifier = Modifier.testTag("rail_item_${tab.lowercase()}")
            )
        }
    }
}

// ──────────────────────────────────────────────────────────
// 1. DASHBOARD VIEW (DUAL / TRIPLE CONTEN SYSTEM)
// ──────────────────────────────────────────────────────────
@Composable
fun DashboardScreen(viewModel: DuniaViewModel) {
    val txs by viewModel.transactions.collectAsState()
    val goals by viewModel.savingsGoals.collectAsState()
    val insts by viewModel.installments.collectAsState()

    var showProfileEditor by remember { mutableStateOf(false) }

    // User values
    val currentFilter = viewModel.selectedUserFilter
    val healthScore = viewModel.calculateHealthScore(currentFilter)

    // Calculate sums
    val userCombinedIncome = viewModel.haikalSalary + viewModel.ummuSalary
    
    val scopedIncome = when (currentFilter) {
        "Haikal" -> viewModel.haikalSalary
        "Ummu" -> viewModel.ummuSalary
        else -> userCombinedIncome
    }

    val totalExpensesSum = txs.filter {
        it.type == "Pengeluaran" && (currentFilter == "Berdua" || it.userId == currentFilter || it.userId == "Bersama")
    }.sumOf { it.amount }

    val totalIncomeSum = txs.filter {
        it.type == "Pemasukan" && (currentFilter == "Berdua" || it.userId == currentFilter || it.userId == "Bersama")
    }.sumOf { it.amount }

    val remainingBudget = scopedIncome - totalExpensesSum

    val totalSavingsAmt = goals.filter {
        if (currentFilter == "Haikal") it.id.contains("haikal")
        else if (currentFilter == "Ummu") it.id.contains("ummu")
        else true
    }.sumOf { it.currentAmount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Double User Dual-Universe Selector Badge Row
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Kosmos Keuangan:",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Text(
                            when(currentFilter) {
                                "Haikal" -> "👨 Universe Haikal — Trainer Hara Chicken"
                                "Ummu" -> "👩 Universe Ummu — Ambitious Partner"
                                else -> "💑 Dua Langkah Berbeda, Satu Tujuan"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                    }
                    IconButton(onClick = { showProfileEditor = true }) {
                        Icon(Icons.Default.Edit, "Edit Profile context", tint = AccentIndigo)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Haikal", "Ummu", "Berdua").forEach { userScope ->
                        val selected = currentFilter == userScope
                        val color = when(userScope) {
                            "Haikal" -> HaikalCyan
                            "Ummu" -> UmmuRose
                            else -> TogetherViolet
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selected) color else BgBase)
                                .clickable { viewModel.selectedUserFilter = userScope }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (userScope == "Berdua") "💑 Bersama" else if (userScope == "Haikal") "👨 Haikal" else "👩 Ummu",
                                color = if (selected) Color.White else TextSecondary,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // ── PRAYER TIME COUNTDOWN & FINANCIAL REMINDER CARD ──
        Card(
            colors = CardDefaults.cardColors(containerColor = BgCard),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🕌", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ibadah & Pengingat Harian (DIY)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AccentIndigo)
                }
                
                Divider(color = BgBase)

                // 1. Sholat Countdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Jadwal Sholat terdekat: ${viewModel.nextPrayerName}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Mundur dalam ${viewModel.nextPrayerCountdown} (Yogyakarta)",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TextButton(
                            onClick = { viewModel.triggerPrayerNotification(viewModel.nextPrayerName) },
                            modifier = Modifier.testTag("test_prayer_notification")
                        ) {
                            Text("Tes Notif", fontSize = 9.sp, color = AccentIndigo, fontWeight = FontWeight.SemiBold)
                        }
                        Text("Adzan Alert", fontSize = 9.sp, color = TextMuted)
                        Switch(
                            checked = viewModel.sholatNotificationsEnabled,
                            onCheckedChange = { viewModel.sholatNotificationsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ColorSuccess,
                                checkedTrackColor = ColorSuccessLight
                            ),
                            modifier = Modifier.scale(0.8f)
                        )
                    }
                }

                // 2. Pencatat Keuangan Alert Reminder
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Evaluasi Keuangan Satu Sumbu",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = if (viewModel.dailyReminderEnabled) "Notifikasi pencatat aktif malam ini (20:00 WIB)" else "Notifikasi pencatat manual dimatikan",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Ingatkan", fontSize = 9.sp, color = TextMuted)
                        Switch(
                            checked = viewModel.dailyReminderEnabled,
                            onCheckedChange = { viewModel.dailyReminderEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = AccentIndigo,
                                checkedTrackColor = AccentLight
                            ),
                            modifier = Modifier.scale(0.8f)
                        )
                    }
                }
            }
        }

        // Financial Health Score Gauge & Countdowns (Side-by-side or stacked grid)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Circular Score Display Canvas (Custom DrawBehind)
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .testTag("health_score_gauge"),
                    contentAlignment = Alignment.Center
                ) {
                    val animatedScoreState = animateFloatAsState(
                        targetValue = healthScore / 100f,
                        animationSpec = tween(durationMillis = 1000)
                    )
                    
                    val colorAccent = when {
                        healthScore >= 80 -> ColorSuccess
                        healthScore >= 50 -> ColorWarning
                        else -> ColorDanger
                    }
                    
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Background circle arc
                        drawArc(
                            color = BgSidebar,
                            startAngle = 135f,
                            sweepAngle = 270f,
                            useCenter = false,
                            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                        )
                        // Progress Arc fill
                        drawArc(
                            color = colorAccent,
                            startAngle = 135f,
                            sweepAngle = 270f * animatedScoreState.value,
                            useCenter = false,
                            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = healthScore.toString(),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text("Health", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    }
                }

                // Health description
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Skor Kesehatan Keuangan",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when {
                            healthScore >= 80 -> "Luar biasa! Pengelolaan tabungan dan rasio hutang Anda berada dalam kondisi prima. ☀️"
                            healthScore >= 50 -> "Cukup sehat, namun banyaki tabungan dan pangkas cicilan impulsif akhir pekan! ⛅"
                            else -> "Waspada! Sisa dana sangat kecil. Sumbu finansial terancam bocor halus. 🌧️"
                        },
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // ── INTERACTIVE CHART.JS CARD ──
        InteractiveChartJsCard(txs = txs, isDarkMode = viewModel.isDarkMode)

        // Top Countdowns Grid & Weather Mood
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Marriage target
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("💍 Target Nikah", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                    Text("3 Tahun Lagi", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TogetherViolet)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Tahun target: 2029", fontSize = 10.sp, color = TextMuted)
                }
            }

            // House target
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🏠 Target Rumah", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                    Text("4 Tahun Lagi", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = HaikalCyan)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("DP: Rp 80 Juta (20%)", fontSize = 10.sp, color = TextMuted)
                }
            }
        }

        // FOUR MAIN STAT CARDS
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .height(180.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val stats = listOf(
                Triple("Gaji Pokok", scopedIncome, Color(0xFF6366F1)),
                Triple("Total Keluar", totalExpensesSum, Color(0xFFEF4444)),
                Triple("Sisa Anggaran", remainingBudget, Color(0xFF10B981)),
                Triple("Dana Tabungan", totalSavingsAmt, Color(0xFF8B5CF6))
            )

            stats.forEach { (label, value, color) ->
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth()
                        ) {
                            Text(label, fontSize = 10.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = value.toMaskedCurrency(viewModel.privateMode),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = color,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // MOTIVATIONAL COUPLE QUOTE & WEATHER MOOD
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AccentLight),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💡",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column {
                    Text(
                        "Dua langkah berbeda, satu arah yang sama. Konsistensi berdua adalah investasi terbesar masa depan kita.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = AccentDark
                    )
                }
            }
        }

        // MINI BAR CHART - Recent Daily Spend
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    "📊 Pengeluaran Berjalan (7 Hari)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val spendValues = listOf(15000, 25000, 550000, 15000, 50000, 40000, 100000)
                    val days = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
                    
                    val maxVal = spendValues.maxOrNull() ?: 100000
                    
                    spendValues.forEachIndexed { idx, valAmt ->
                        val ratio = valAmt.toFloat() / maxVal.toFloat()
                        
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight(ratio.coerceAtLeast(0.1f))
                                    .width(18.dp)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(if (valAmt > 100000) ColorDanger else AccentIndigo)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(days[idx], fontSize = 10.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }

        // PROFILE VIEW EDIT OVERLAY SHEET DIALOG
        if (showProfileEditor) {
            AlertDialog(
                onDismissRequest = { showProfileEditor = false },
                title = { Text("Set Up Konteks Profil", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        var editHaikalSalary by remember { mutableStateOf(viewModel.haikalSalary.toInt().toString()) }
                        var editUmmuSalary by remember { mutableStateOf(viewModel.ummuSalary.toInt().toString()) }
                        var editUmmuJob by remember { mutableStateOf(viewModel.ummuJob) }

                        OutlinedTextField(
                            value = editHaikalSalary,
                            onValueChange = { editHaikalSalary = it },
                            label = { Text("Gaji Haikal (Trainer Hara)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("edit_haikal_salary")
                        )

                        OutlinedTextField(
                            value = editUmmuSalary,
                            onValueChange = { editUmmuSalary = it },
                            label = { Text("Gaji Ummu") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("edit_ummu_salary")
                        )

                        OutlinedTextField(
                            value = editUmmuJob,
                            onValueChange = { editUmmuJob = it },
                            label = { Text("Pekerjaan Ummu") },
                            modifier = Modifier.fillMaxWidth().testTag("edit_ummu_job")
                        )

                        Button(
                            onClick = {
                                viewModel.updateSalaryConfigs(
                                    editHaikalSalary.toDoubleOrNull() ?: 2300000.0,
                                    editUmmuSalary.toDoubleOrNull() ?: 2300000.0,
                                    editUmmuJob
                                )
                                showProfileEditor = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
                            modifier = Modifier.fillMaxWidth().testTag("save_profile_settings")
                        ) {
                            Text("Simpan Perubahan")
                        }
                    }
                },
                confirmButton = {}
            )
        }
    }
}

// ──────────────────────────────────────────────────────────
// 2. TRANSACTION DIALOG SHEET & LIST
// ──────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(viewModel: DuniaViewModel) {
    val txs by viewModel.transactions.collectAsState()
    var selectedTabFilter by remember { mutableStateOf("Semua") } // "Semua", "Pemasukan", "Pengeluaran"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    "📝 Riwayat Transaksi Jurnal",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                // Filter Tab
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Semua", "Pemasukan", "Pengeluaran").forEach { tab ->
                        val active = selectedTabFilter == tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (active) AccentIndigo else BgBase)
                                .clickable { selectedTabFilter = tab }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                tab,
                                color = if (active) Color.White else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Transactions Table Rows
        val filteredTxs = txs.filter {
            selectedTabFilter == "Semua" || it.type == selectedTabFilter
        }

        if (filteredTxs.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.ContentPasteOff,
                        contentDescription = "Empty",
                        tint = TextMuted,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Jurnal transaksi kosong.", color = TextMuted, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredTxs) { tx ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("transaction_item_${tx.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                // Avatar circle with category initials
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(if (tx.type == "Pemasukan") ColorSuccessLight else ColorDangerLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = tx.category.take(1).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = if (tx.type == "Pemasukan") ColorSuccess else ColorDanger,
                                        fontSize = 14.sp
                                    )
                                }
                                Column {
                                    Text(
                                        tx.description,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            "${if (tx.userId == "Haikal") "👨 Haikal" else if (tx.userId == "Ummu") "👩 Ummu" else "💑 Bersama"} • ${tx.category}",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                        if (tx.tag.isNotEmpty()) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(BgSidebar)
                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                            ) {
                                                Text(tx.tag, fontSize = 9.sp, color = AccentIndigo)
                                            }
                                        }
                                    }
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = (if (tx.type == "Pemasukan") "+" else "-") + tx.amount.toMaskedCurrency(viewModel.privateMode),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (tx.type == "Pemasukan") ColorSuccess else ColorDanger
                                )
                                IconButton(
                                    onClick = { viewModel.deleteTransaction(tx) },
                                    modifier = Modifier.testTag("delete_tx_btn_${tx.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = ColorDanger.copy(alpha = 0.8f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Quick ADD Transaction Sheet Dialogue Action Overlay
@Composable
fun QuickAddTransactionDialog(
    viewModel: DuniaViewModel,
    onDismiss: () -> Unit
) {
    var userId by remember { mutableStateOf("Haikal") }
    var type by remember { mutableStateOf("Pengeluaran") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Makan") }
    var pos by remember { mutableStateOf("Fleksibel") }
    var description by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("#rutin") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "➕ Catat Aliran Kas",
                fontWeight = FontWeight.Bold,
                color = AccentIndigo,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                // Selector Haikal vs Ummu vs Bersama
                Text("Siapa Pengeluar/Penerima?", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Haikal", "Ummu", "Berdua").forEach { name ->
                        val selected = userId == name
                        val color = if (name == "Haikal") HaikalCyan else if (name == "Ummu") UmmuRose else TogetherViolet
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) color else BgBase)
                                .clickable { userId = name }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                name,
                                color = if (selected) Color.White else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Type selector
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Pemasukan", "Pengeluaran").forEach { t ->
                        val selected = type == t
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) (if (t == "Pemasukan") ColorSuccess else ColorDanger) else BgBase)
                                .clickable { type = t }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                t,
                                color = if (selected) Color.White else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Jumlah Dana (Rupiah)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("input_tx_amount")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi Catatan") },
                    modifier = Modifier.fillMaxWidth().testTag("input_tx_desc")
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Kategori (Makan, Transport, Tabungan...)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_tx_category")
                )

                OutlinedTextField(
                    value = tag,
                    onValueChange = { tag = it },
                    label = { Text("Tag (#rutin, #impulsif, #kencan, #darurat)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_tx_tag")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amtDouble = amount.toDoubleOrNull() ?: 0.0
                    if (amtDouble > 0.0 && description.isNotEmpty()) {
                        viewModel.addTransaction(userId, type, amtDouble, category, pos, description, tag)
                    }
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
                modifier = Modifier.testTag("submit_tx_btn")
            ) {
                Text("Tambahkan Jurnal")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

// ──────────────────────────────────────────────────────────
// 3. SAVINGS GOALS & INVESTMENT MODULE
// ──────────────────────────────────────────────────────────
@Composable
fun SavingsGoalsScreen(viewModel: DuniaViewModel) {
    val goals by viewModel.savingsGoals.collectAsState()
    val investments by viewModel.investments.collectAsState()

    var showAddGoalDialog by remember { mutableStateOf(false) }
    var selectedGoalForDeposit by remember { mutableStateOf<SavingGoal?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TOP HEADER CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    "🏦 Tabungan & Resolusi Impian",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
                Text(
                    "Membangun delapan pos dana aman dan portofolio investasi tepercaya berdua.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        // 8 SAVINGS GOALS GRIDS WITH PROGRESS BARS
        Text("8 Pos Tabungan Utama", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier
                .height(350.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(goals.size) { idx ->
                val goal = goals[idx]
                val ratio = if (goal.targetAmount > 0) goal.currentAmount / goal.targetAmount else 0.0
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(goal.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(goal.description, fontSize = 10.sp, color = TextSecondary)
                            }
                            IconButton(onClick = { selectedGoalForDeposit = goal }) {
                                Icon(Icons.Default.AddCircle, "Add fund", tint = AccentIndigo)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        // Progress bar track
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(BgSidebar)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(ratio.toFloat().coerceIn(0f, 1f))
                                    .fillMaxHeight()
                                    .clip(CircleShape)
                                    .background(if (ratio >= 1.0) ColorSuccess else TogetherViolet)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Terkumpul: " + goal.currentAmount.toMaskedCurrency(viewModel.privateMode),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentDark
                            )
                            Text(
                                "Target: " + goal.targetAmount.toMaskedCurrency(viewModel.privateMode) + " (${(ratio * 100).toInt()}%)",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // INVESTMENT TRACKER
        Text("📈 Portofolio Investasi", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(investments.size) { idx ->
                val invest = investments[idx]
                val profit = invest.currentValue - invest.capital
                val profitPercent = if (invest.capital > 0) (profit / invest.capital) * 100 else 0.0
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(invest.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Instrumen: ${invest.type} • modal: ${invest.capital.toMaskedCurrency(viewModel.privateMode)}", fontSize = 11.sp, color = TextSecondary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(invest.currentValue.toMaskedCurrency(viewModel.privateMode), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                text = "Profit: Rp ${profit.toInt()} (${"%.1f".format(profitPercent)}%)",
                                color = if (profit >= 0) ColorSuccess else ColorDanger,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Interactive Dialog for saving deposits
        selectedGoalForDeposit?.let { goal ->
            var depositAmt by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { selectedGoalForDeposit = null },
                title = { Text("Tabung Dana ke ${goal.name}", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(goal.description, fontSize = 11.sp, color = TextSecondary)
                        OutlinedTextField(
                            value = depositAmt,
                            onValueChange = { depositAmt = it },
                            label = { Text("Jumlah Deposit (Rupiah)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("deposit_input_amt")
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val amt = depositAmt.toDoubleOrNull() ?: 0.0
                            if (amt > 0) {
                                viewModel.addSavingsGoalFunds(goal.id, amt)
                                // Standard write down transaction logic helper
                                viewModel.addTransaction(
                                    userId = viewModel.selectedUserFilter,
                                    type = "Pengeluaran",
                                    amount = amt,
                                    category = "Tabungan",
                                    pos = "Investasi",
                                    description = "Top up tabungan: ${goal.name}",
                                    tag = "#darurat"
                                )
                            }
                            selectedGoalForDeposit = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
                        modifier = Modifier.testTag("submit_deposit_btn")
                    ) {
                        Text("Konfirmasi Tabung")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedGoalForDeposit = null }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}

// ──────────────────────────────────────────────────────────
// 4. INTERACTIVE SIMULATOR / FORECAST SCREEN
// ──────────────────────────────────────────────────────────
@Composable
fun SimulatorScreen(viewModel: DuniaViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    "🎯 Simulator Proyeksi Masa Depan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
                Text(
                    "Simulasikan target pencapaian tabungan pernikahan dan DP rumah secara interaktif.",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        // TAB 1: COMPOUND SAVINGS SIMULATOR
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    "💰 Simulasi Target Capaian",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TogetherViolet
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Selector goal
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Nikah (20jt)", "Rumah (80jt)").forEach { g ->
                        val active = (g.contains("Nikah") && viewModel.simGoalId == "nikah") || 
                                     (g.contains("Rumah") && viewModel.simGoalId == "dp_rumah")
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (active) TogetherViolet else BgBase)
                                .clickable {
                                    if (g.contains("Nikah")) {
                                        viewModel.simGoalId = "nikah"
                                        viewModel.simTargetAmount = 20000000.0
                                    } else {
                                        viewModel.simGoalId = "dp_rumah"
                                        viewModel.simTargetAmount = 8000000.0 * 10 // 80M IDR target
                                    }
                                }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(g, fontSize = 11.sp, color = if (active) Color.White else TextSecondary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Interactive sliders
                Text("Setoran Tabungan per Bulan: " + viewModel.simMonthlySavings.toMaskedCurrency(false), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Slider(
                    value = viewModel.simMonthlySavings.toFloat(),
                    onValueChange = { viewModel.simMonthlySavings = it.toDouble() },
                    valueRange = 100000f..2000000f,
                    steps = 19,
                    colors = SliderDefaults.colors(thumbColor = TogetherViolet, activeTrackColor = TogetherViolet),
                    modifier = Modifier.testTag("slider_monthly_savings")
                )

                Text("Tabungan Berjalan Sekarang: " + viewModel.simCurrentSavings.toMaskedCurrency(false), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Slider(
                    value = viewModel.simCurrentSavings.toFloat(),
                    onValueChange = { viewModel.simCurrentSavings = it.toDouble() },
                    valueRange = 0f..10000000f,
                    colors = SliderDefaults.colors(thumbColor = TogetherViolet, activeTrackColor = TogetherViolet)
                )

                // Mathematically calculate months needed
                val targetToFill = (viewModel.simTargetAmount - viewModel.simCurrentSavings).coerceAtLeast(0.0)
                val monthsNeeded = if (viewModel.simMonthlySavings > 0) (targetToFill / viewModel.simMonthlySavings).toInt() else 999

                Spacer(modifier = Modifier.height(14.dp))
                // Output UI block
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentLight)
                        .padding(14.dp)
                ) {
                    Column {
                        Text("Estimasi Waktu Tercapai:", fontSize = 11.sp, color = AccentDark, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (monthsNeeded <= 0) "TARGET SUDAH TERCAPAI! 🎉" else "$monthsNeeded Bulan ke depan",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = AccentDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        val calendar = Calendar.getInstance()
                        calendar.add(Calendar.MONTH, monthsNeeded)
                        val formatMonth = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
                        Text(
                            "Diproyeksikan tercapai penuh pada: ${formatMonth.format(calendar.time)}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // TAB 2: MULTI-SCENARIO ACCELERATION REMINDER (COUPLE INTEREST RATIOS)
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    "⚡ Simulator Percepatan Cicilan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = HaikalCyan
                )
                Text(
                    "Simulasikan percepatan cicilan motor Haikal dengan menambah setoran ekstra setiap bulannya.",
                    fontSize = 10.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text("Tambahan Ekstra Bayar per Bulan: " + viewModel.simInstallmentExtraPayment.toMaskedCurrency(false), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Slider(
                    value = viewModel.simInstallmentExtraPayment.toFloat(),
                    onValueChange = { viewModel.simInstallmentExtraPayment = it.toDouble() },
                    valueRange = 50000f..500000f,
                    steps = 9,
                    colors = SliderDefaults.colors(thumbColor = HaikalCyan, activeTrackColor = HaikalCyan)
                )

                val defaultMotorMonthly = 550000.0
                val defaultMonthsRemaining = 30
                val totalRemainingDebt = defaultMotorMonthly * defaultMonthsRemaining

                // Accelerated calculation
                val nextMonthlyTotal = defaultMotorMonthly + viewModel.simInstallmentExtraPayment
                val acceleratedMonths = if (nextMonthlyTotal > 0) (totalRemainingDebt / nextMonthlyTotal).toInt() else defaultMonthsRemaining
                val monthsSaved = (defaultMonthsRemaining - acceleratedMonths).coerceAtLeast(0)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(HaikalLight)
                        .padding(14.dp)
                ) {
                    Column {
                        Text("Percepatan Pelunasan Cicilan Motor:", fontSize = 11.sp, color = HaikalDark, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Lunas $acceleratedMonths Bulan (Hemat $monthsSaved Bulan lebih cepat!)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = HaikalDark
                        )
                        Text(
                            "Haikal bisa menghemat waktu cicilan motor dari rilis default 30 bulan.",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────
// 5. GEMINI CO-PILOT ADVISOR SCREEN
// ──────────────────────────────────────────────────────────
@Composable
fun AdvisorScreen(viewModel: DuniaViewModel) {
    var queryText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI COPILOT LOGO HEADER
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "AI",
                    tint = AccentIndigo,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        "🤖 AI Financial Advisor (Gemini)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                    Text(
                        "Gunakan model AI gemini-3.5-flash untuk mengevaluasi impian keuangan Anda berdua secara cerdas.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Presets prompt helper buttons
        Text("Tombol Pertanyaan Preset Instan:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val presets = listOf("Analisis Impian 2029", "Tips Hemat Karyawan", "Zakat & Sedekah")
            presets.forEach { preset ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BgSidebar)
                        .clickable { viewModel.askAIAdvisor(preset) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(preset, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AccentIndigo)
                }
            }
        }

        // Chat text container
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (viewModel.chatHistory.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Outlined.ChatBubbleOutline, "chat info", tint = TextMuted)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Belum ada percakapan. Tanyakan apa saja tentang target nikah atau cicilan Anda berdua!",
                                        fontSize = 11.sp,
                                        color = TextMuted,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 20.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        items(viewModel.chatHistory) { (msg, isUser) ->
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.85f)
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 12.dp,
                                                topEnd = 12.dp,
                                                bottomStart = if (isUser) 12.dp else 0.dp,
                                                bottomEnd = if (isUser) 0.dp else 12.dp
                                            )
                                        )
                                        .background(if (isUser) AccentIndigo else BgBase)
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = msg,
                                        color = if (isUser) Color.White else TextPrimary,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }

                    if (viewModel.isChatLoading) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(BgSidebar)
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = AccentIndigo)
                                        Text("Gemini sedang berpikir...", fontSize = 11.sp, color = TextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }

                Divider(color = BgBase, thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))

                // Chat Input bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = queryText,
                        onValueChange = { queryText = it },
                        placeholder = { Text("Ketik pesan di sini...", fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_text"),
                        shape = RoundedCornerShape(20.dp)
                    )
                    IconButton(
                        onClick = {
                            if (queryText.trim().isNotEmpty()) {
                                viewModel.askAIAdvisor(queryText)
                                queryText = ""
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(AccentIndigo)
                            .testTag("chat_send_button")
                    ) {
                        Icon(Icons.Default.Send, "Send", tint = Color.White)
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────
// 6. MORE TAB CONSOLE (LIFE ROADMAP, SHOLAT, MEETINGS, WISHLISTS)
// ──────────────────────────────────────────────────────────
@Composable
fun MoreFeaturesScreen(viewModel: DuniaViewModel) {
    var subMenu by remember { mutableStateOf("Index") } // "Index", "Roadmap", "Spiritual", "Rapat", "Wishlist"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Menu Back button helper
        if (subMenu != "Index") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { subMenu = "Index" }) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = AccentIndigo)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Kembali ke Menu", color = AccentIndigo, fontWeight = FontWeight.Bold)
                }
            }
        }

        when (subMenu) {
            "Index" -> MoreIndexMenu { subMenu = it }
            "Roadmap" -> LifeRoadmapSubScreen(viewModel)
            "Spiritual" -> SpiritualTrackerSubScreen(viewModel)
            "Rapat" -> MeetingNotulenSubScreen(viewModel)
            "Wishlist" -> WishlistSubScreen(viewModel)
            "Analytics" -> AnalyticsSubScreen(viewModel)
            "Export" -> ExportSubScreen(viewModel)
        }
    }
}

@Composable
fun MoreIndexMenu(onSelectSubMenu: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("🎯 Modul & Agenda Tambahan", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        
        val list = listOf(
            Triple("Lini Masa Hidup & Roadmap", "Struktur rencana 5 fase dari 2026-2030 pernikahan & rumah.", "Roadmap"),
            Triple("Ibadah Harian & Sholat Heatmap", "Tracker pencapaian spiritual, Quran, infaq, dan sholat berjamaah.", "Spiritual"),
            Triple("Rencana Rapat Evaluasi Bulanan", "Notulen agenda rapat audit bulanan Haikal & Ummu.", "Rapat"),
            Triple("Rencana Keinginan (Wishlist 30 Hari)", "Strategi impulse control menolak belanja sia-sia.", "Wishlist"),
            Triple("Analisis Finansial & Live Chart", "Visualisasi pengeluaran dan pemasukan harian, mingguan, bulanan, tahunan.", "Analytics"),
            Triple("Ekspor Laporan (PDF & Google Sheets)", "Cetak bukti PDF profesional atau sinkronisasi data online ke Google Sheets.", "Export")
        )

        list.forEach { (title, desc, key) ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectSubMenu(key) }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AccentIndigo)
                        Text(desc, fontSize = 11.sp, color = TextSecondary)
                    }
                    Icon(Icons.Default.ChevronRight, "select", tint = TextMuted)
                }
            }
        }
    }
}

// ── SUB SCREEN 1: LIFE ROADMAP 2026 - 2030 ──
@Composable
fun LifeRoadmapSubScreen(viewModel: DuniaViewModel) {
    val phases by viewModel.roadmapPhases.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("🗺️ Roadmap Hidup Haikal × Ummu", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text("Penyelarasan target hidup 5 tahun dari pacaran menuju rumah tangga mandiri.", fontSize = 11.sp, color = TextSecondary)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.height(450.dp)) {
            items(phases) { phase ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Fase ${phase.phaseId}: ${phase.title} (${phase.year})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            if (phase.isCompleted) {
                                Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(ColorSuccessLight).padding(4.dp)) {
                                    Text("LUNAS ACCED", fontSize = 9.sp, color = ColorSuccess, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Text(phase.description, fontSize = 11.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Milestones list checkable
                        val listMilestones = phase.milestones.split(";")
                        val checkedSet = if (phase.completedMilestones.isEmpty()) emptyList() else phase.completedMilestones.split(";")

                        listMilestones.forEachIndexed { index, ms ->
                            val isChecked = checkedSet.contains(index.toString())
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.toggleRoadmapMilestone(phase.phaseId, index) }
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { viewModel.toggleRoadmapMilestone(phase.phaseId, index) }
                                )
                                Text(ms, fontSize = 11.sp, color = if (isChecked) TextMuted else TextPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── SUB SCREEN 2: SPIRITUAL SHOLAT & DEVOTION HEATMAP ──
@Composable
fun SpiritualTrackerSubScreen(viewModel: DuniaViewModel) {
    val records by viewModel.ibadahRecords.collectAsState()
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val todayDate = sdf.format(Date())

    // UI Input field states
    var sedekahInp by remember { mutableStateOf("") }
    var quranInp by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Text("🕌 Jurnal Ibadah Harian & Sedekah", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text("Menjaga sumbu spiritual ketaatan dan keberkahan harta.", fontSize = 11.sp, color = TextSecondary)

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Hari Ini: $todayDate", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(10.dp))

                // Five sholat buttons h-layout
                val sholats = listOf("Subuh", "Zhuhur", "Ashar", "Maghrib", "Isya")
                val recordToday = records.find { it.date == todayDate } ?: IbadahRecord(todayDate)

                Text("Checklist Sholat Berjamaah:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    sholats.forEach { sh ->
                        val checked = when (sh.lowercase()) {
                            "subuh" -> recordToday.subuh
                            "zhuhur" -> recordToday.zhuhur
                            "ashar" -> recordToday.ashar
                            "maghrib" -> recordToday.maghrib
                            "isya" -> recordToday.isya
                            else -> false
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (checked) ColorSuccess else BgBase)
                                .clickable { viewModel.toggleIbadahPrayer(todayDate, sh) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(sh, fontSize = 9.sp, color = if (checked) Color.White else TextSecondary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sedekah and Quran inputs
                OutlinedTextField(
                    value = sedekahInp,
                    onValueChange = { sedekahInp = it },
                    label = { Text("Sedekah / Infaq (Rupiah)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = quranInp,
                    onValueChange = { quranInp = it },
                    label = { Text("Jumlah Halaman Qur'an Dibaca") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        val sed = sedekahInp.toDoubleOrNull() ?: 0.0
                        val qNum = quranInp.toIntOrNull() ?: 0
                        viewModel.updateIbadahSedekahAndQuran(todayDate, sed, qNum)
                        sedekahInp = ""
                        quranInp = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorSuccess),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Simpan Spiritual Record")
                }
            }
        }

        // GitHub style visual checklist box
        Text("Kalender Sholat Github Heatmap (30 Hari Terakhir):", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Render 30 boxes grid representing 30 days
                val dateHelper = Calendar.getInstance()
                val listDates = (0..29).map { minusDay ->
                    val helper = dateHelper.clone() as Calendar
                    helper.add(Calendar.DAY_OF_YEAR, -minusDay)
                    sdf.format(helper.time)
                }.reversed()

                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier
                        .height(110.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(listDates.size) { idx ->
                        val d = listDates[idx]
                        val rec = records.find { it.date == d }
                        val score = if (rec != null) {
                            (if (rec.subuh) 1 else 0) + (if (rec.zhuhur) 1 else 0) +
                            (if (rec.ashar) 1 else 0) + (if (rec.maghrib) 1 else 0) + (if (rec.isya) 1 else 0)
                        } else 0

                        val boxCl = when (score) {
                            5 -> Color(0xFF10B981)
                            4 -> Color(0xFF34D399)
                            3 -> Color(0xFF6EE7B7)
                            2 -> Color(0xFFA7F3D0)
                            1 -> Color(0xFFD1FAE5)
                            else -> BgBase
                        }

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(boxCl)
                                .border(0.5.dp, TextMuted.copy(alpha = 0.3f))
                        )
                    }
                }
            }
        }
    }
}

// ── SUB SCREEN 3: RAPAT EVALUASI BULANAN ──
@Composable
fun MeetingNotulenSubScreen(viewModel: DuniaViewModel) {
    val meetings by viewModel.meetings.collectAsState()

    var showWriteMinutes by remember { mutableStateOf(false) }

    var agendaInp by remember { mutableStateOf("") }
    var monthYearInp by remember { mutableStateOf("Juni 2026") }
    var notesInp by remember { mutableStateOf("") }
    var actionsInp by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(" Rapat Evaluasi Bulanan", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Button(
                onClick = { showWriteMinutes = !showWriteMinutes },
                colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
            ) {
                Text(if (showWriteMinutes) "Batal" else "Tulis Notulen")
            }
        }

        if (showWriteMinutes) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = monthYearInp,
                        onValueChange = { monthYearInp = it },
                        label = { Text("Bulan / Tahun (e.g., Juni 2026)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = agendaInp,
                        onValueChange = { agendaInp = it },
                        label = { Text("Agenda Rapat Utama") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = notesInp,
                        onValueChange = { notesInp = it },
                        label = { Text("Hasil Pembahasan (Notes)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = actionsInp,
                        onValueChange = { actionsInp = it },
                        label = { Text("Action Items (Pisahkan dengan titik koma ';')") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            if (notesInp.isNotEmpty()) {
                                viewModel.saveMeetingMinutes(monthYearInp, agendaInp, notesInp, actionsInp)
                                // Reset inputs
                                agendaInp = ""
                                notesInp = ""
                                actionsInp = ""
                                showWriteMinutes = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Simpan Bukti Rapat")
                    }
                }
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.height(450.dp)) {
            items(meetings) { mt ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Notulen: ${mt.monthYear}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AccentIndigo)
                            IconButton(onClick = { viewModel.deleteMeetingMinutes(mt) }) {
                                Icon(Icons.Default.Delete, "del", tint = ColorDanger)
                            }
                        }
                        Text("Agenda: ${mt.agenda}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(mt.notes, fontSize = 11.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))

                        // Action checklist items
                        val actionParsed = mt.actionItems.split(";")
                        val checkedParsed = if (mt.completedActions.isEmpty()) emptyList() else mt.completedActions.split(";")

                        if (mt.actionItems.isNotEmpty()) {
                            Text("Action Items (Pisahkan tugas berdua):", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            actionParsed.forEachIndexed { index, act ->
                                val complete = checkedParsed.contains(index.toString())
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.toggleMeetingActionItem(mt.id, index) }
                                ) {
                                    Checkbox(
                                        checked = complete,
                                        onCheckedChange = { viewModel.toggleMeetingActionItem(mt.id, index) }
                                    )
                                    Text(act, fontSize = 11.sp, color = if (complete) TextMuted else TextPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── SUB SCREEN 4: WISHLIST CONTROL ──
@Composable
fun WishlistSubScreen(viewModel: DuniaViewModel) {
    val wishes by viewModel.wishlist.collectAsState()

    var showAddWish by remember { mutableStateOf(false) }

    var editName by remember { mutableStateOf("") }
    var editPrice by remember { mutableStateOf("") }
    var editNotes by remember { mutableStateOf("") }
    var editUser by remember { mutableStateOf("Haikal") }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🛒 Wishlist Impulse Control", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Button(onClick = { showAddWish = !showAddWish }, colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)) {
                Text(if (showAddWish) "Batal" else "Tambah Wishlist")
            }
        }

        if (showAddWish) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Haikal", "Ummu").forEach { u ->
                            val active = editUser == u
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) AccentIndigo else BgBase)
                                    .clickable { editUser = u }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(u, color = if (active) Color.White else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Nama Barang") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPrice,
                        onValueChange = { editPrice = it },
                        label = { Text("Harga Barang (Rupiah)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editNotes,
                        onValueChange = { editNotes = it },
                        label = { Text("Kenapa Anda butuh barang ini?") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            val pr = editPrice.toDoubleOrNull() ?: 0.0
                            if (editName.isNotEmpty() && pr > 0) {
                                viewModel.saveWishlistItem(editUser, editName, pr, editNotes)
                                editName = ""
                                editPrice = ""
                                editNotes = ""
                                showAddWish = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Simpan Keinginan")
                    }
                }
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.height(450.dp)) {
            items(wishes) { wish ->
                val timeDifference = System.currentTimeMillis() - wish.addedDate
                val thirtyDaysInMs = 30L * 24 * 3600 * 1000L
                val isCoolDownOver = timeDifference >= thirtyDaysInMs
                val statusString = if (wish.status == "Belum 30 Hari" && isCoolDownOver) {
                    "Cocok Dibeli (Masa Tunggu 30 Hari Lulus ✅)"
                } else wish.status

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(wish.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(wish.price.toMaskedCurrency(viewModel.privateMode), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ColorDanger)
                        }
                        Text("Milik: ${wish.userId} • Catatan: ${wish.notes}", fontSize = 11.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (statusString.contains("Cocok") || statusString.contains("Dibeli")) ColorSuccessLight else BgSidebar)
                                .padding(4.dp)
                        ) {
                            Text(statusString, fontSize = 9.sp, color = if (statusString.contains("Cocok") || statusString.contains("Dibeli")) ColorSuccess else TextSecondary, fontWeight = FontWeight.Bold)
                        }

                        if (wish.status == "Belum 30 Hari" || wish.status == "Sudah 30 Hari") {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { viewModel.purchaseWishlistItem(wish.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = ColorSuccess),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Beli (Lulus)", fontSize = 11.sp)
                                }
                                Button(
                                    onClick = { viewModel.cancelWishlistItem(wish.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = ColorDanger),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Batalkan (Hemat)", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── SUB SCREEN 5: FINANCIAL DETAILED ANALYTICS (PIE CHART & HISTORICAL BARS) ──
@Composable
fun AnalyticsSubScreen(viewModel: DuniaViewModel) {
    val txs by viewModel.transactions.collectAsState()
    var selectedTimePeriod by remember { mutableStateOf("Bulanan") } // "Harian", "Mingguan", "Bulanan", "Tahunan"
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("📊 Analisis Keuangan Satu Sumbu", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AccentIndigo)
        Text("Evaluasi pengeluaran dan pemasukan berdasarkan kurun waktu secara realtime.", fontSize = 11.sp, color = TextSecondary)
        
        // Time Period Filter Tab Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("Harian", "Mingguan", "Bulanan", "Tahunan").forEach { period ->
                val active = selectedTimePeriod == period
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (active) AccentIndigo else Color.White)
                        .border(1.dp, if (active) AccentIndigo else BgSidebar, RoundedCornerShape(10.dp))
                        .clickable { selectedTimePeriod = period }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(period, color = if (active) Color.White else TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                }
            }
        }
        
        // Filter transactions according to date period
        val currentTimeMs = System.currentTimeMillis()
        val filteredTxs = remember(txs, selectedTimePeriod) {
            txs.filter { tx ->
                val diffMs = currentTimeMs - tx.timestamp
                when (selectedTimePeriod) {
                    "Harian" -> diffMs <= 7L * 24 * 3600 * 1000
                    "Mingguan" -> diffMs <= 4L * 7 * 24 * 3600 * 1000
                    "Bulanan" -> diffMs <= 12L * 30 * 24 * 3600 * 1000
                    "Tahunan" -> true
                    else -> true
                }
            }
        }
        
        val totalIncome = filteredTxs.filter { it.type == "Pemasukan" }.sumOf { it.amount }
        val totalExpense = filteredTxs.filter { it.type == "Pengeluaran" }.sumOf { it.amount }
        
        // Income vs Expense Summary card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Ringkasan Aliran Kas ($selectedTimePeriod)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Income Indicator
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Pemasukan", fontSize = 11.sp, color = TextSecondary)
                        Text(totalIncome.toMaskedCurrency(viewModel.privateMode), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ColorSuccess)
                    }
                    // Expense Indicator
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Pengeluaran", fontSize = 11.sp, color = TextSecondary)
                        Text(totalExpense.toMaskedCurrency(viewModel.privateMode), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ColorDanger)
                    }
                }
                
                // Progress representation bar (Inflow ratio)
                val totalTurnover = totalIncome + totalExpense
                if (totalTurnover > 0) {
                    val incomeRatio = (totalIncome / totalTurnover).toFloat()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(ColorDanger)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(incomeRatio)
                                .background(ColorSuccess)
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${(incomeRatio * 100).toInt()}% Hemat", fontSize = 9.sp, color = TextMuted)
                        Text("${(100 - (incomeRatio * 100)).toInt()}% Keluar", fontSize = 9.sp, color = TextMuted)
                    }
                }
            }
        }
        
        // ── PIE CHART - BREAKDOWN CATEGORIES ──
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("🍩 Distribusi Pengeluaran Kategori", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                
                val currentFilter = viewModel.selectedUserFilter
                val expensesFiltered = filteredTxs.filter {
                    it.type == "Pengeluaran" && (currentFilter == "Berdua" || it.userId == currentFilter || it.userId == "Bersama")
                }
                
                val categoryGroups = remember(expensesFiltered) {
                    expensesFiltered.groupBy { it.category }.mapValues { entry -> entry.value.sumOf { it.amount } }
                }
                
                val totalExpensesFiltered = categoryGroups.values.sum()
                
                if (categoryGroups.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Belum ada data pengeluaran tercatat.", fontSize = 12.sp, color = TextSecondary)
                    }
                } else {
                    val sliceColors = listOf(
                        Color(0xFFEF4444),
                        Color(0xFF3B82F6),
                        Color(0xFF10B981),
                        Color(0xFFF59E0B),
                        Color(0xFF8B5CF6),
                        Color(0xFFEC4899),
                        Color(0xFF06B6D4)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Drawing local custom Pie Chart Canvas
                        Canvas(
                            modifier = Modifier
                                .size(130.dp)
                                .testTag("expense_pie_chart")
                        ) {
                            var pivotAngle = -90f
                            categoryGroups.entries.forEachIndexed { idx, pair ->
                                val sweep = (pair.value / totalExpensesFiltered * 360f).toFloat()
                                drawArc(
                                    color = sliceColors[idx % sliceColors.size],
                                    startAngle = pivotAngle,
                                    sweepAngle = sweep,
                                    useCenter = true
                                )
                                pivotAngle += sweep
                            }
                            
                            // Donut hole mask for premium modern look
                            drawCircle(
                                color = Color.White,
                                radius = size.minDimension / 4f
                            )
                        }
                        
                        // Legends Column
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            categoryGroups.entries.forEachIndexed { index, entry ->
                                val pct = (entry.value / totalExpensesFiltered * 100).toInt()
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(sliceColors[index % sliceColors.size])
                                    )
                                    Text(
                                        text = "${entry.key} ($pct%)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // ── BAR CHART - INFLOW VS OUTFLOW TRENDS ──
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("📶 Perbandingan Pemasukan vs Pengeluaran ($selectedTimePeriod)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                
                // Draw manual double bar charts for high fidelity M3 style
                val maxVal = maxOf(totalIncome, totalExpense, 100000.0)
                val incHeightRatio = (totalIncome / maxVal).toFloat()
                val expHeightRatio = (totalExpense / maxVal).toFloat()
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(top = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Pemasukan Bar column
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = String.format("Rp %,d", totalIncome.toLong()),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorSuccess
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(incHeightRatio.coerceAtLeast(0.08f))
                                .width(36.dp)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(ColorSuccess)
                        )
                        Text("Pemasukan", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    }
                    
                    // Pengeluaran Bar column
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = String.format("Rp %,d", totalExpense.toLong()),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorDanger
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(expHeightRatio.coerceAtLeast(0.08f))
                                .width(36.dp)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(ColorDanger)
                        )
                        Text("Pengeluaran", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(30.dp))
    }
}

// ── SUB SCREEN 6: BULLETPROOF PDF EXPORTER & SHEET INTEGRATIONS ──
@Composable
fun ExportSubScreen(viewModel: DuniaViewModel) {
    val context = LocalContext.current
    var isCodeExpanded by remember { mutableStateOf(false) }
    
    val gasCodeString = """
// ── GOOGLE APPS SCRIPT WEBHOOK FOR DUNIA ──
function doPost(e) {
  try {
    var jsonString = e.postData.contents;
    var data = JSON.parse(jsonString);
    var sheet = SpreadsheetApp.getActiveSpreadsheet().getActiveSheet();
    
    sheet.clear();
    sheet.appendRow(["ID", "Tanggal", "User", "Tipe", "Pos", "Kategori", "Jumlah", "Deskripsi", "Tag"]);
    
    for (var i = 0; i < data.transactions.length; i++) {
      var tx = data.transactions[i];
      sheet.appendRow([
        tx.id,
        new Date(tx.timestamp).toISOString(),
        tx.userId,
        tx.type,
        tx.pos,
        tx.category,
        tx.amount,
        tx.description,
        tx.tag
      ]);
    }
    
    return ContentService.createTextOutput(JSON.stringify({"status": "success"}))
      .setMimeType(ContentService.MimeType.JSON);
  } catch (error) {
    return ContentService.createTextOutput(JSON.stringify({"status": "error", "message": error.toString()}))
      .setMimeType(ContentService.MimeType.JSON);
  }
}
    """.trimIndent()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("📁 Ekspor Jurnal & Integrasi Sheets", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AccentIndigo)
        Text("Mencetak laporan berformat PDF resmi atau menyinkronkan data jurnal satu sumbu secara digital.", fontSize = 11.sp, color = TextSecondary)
        
        // 1. PDF Report Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF3F4F6)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📄", fontSize = 22.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Laporan Keuangan Cetak (PDF)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                    Text("Membuat lembar bukti audit saku A4 lengkap dengan rincian pendapatan & belanja.", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.exportPdfReport(context) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                    ) {
                        Text("Cetak PDF Laporan", fontSize = 11.sp)
                    }
                }
            }
        }
        
        // 2. Google Sheets Sheet Export Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📊", fontSize = 22.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Ekspor Spreadsheet (CSV)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                    Text("Mengunduh data mentah transaksi berformat CSV kompatibel dengan Ms Excel & Google Sheets.", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.exportCsvReport(context) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Text("Download File Excel/CSV", fontSize = 11.sp)
                    }
                }
            }
        }

        // 3. Realtime Webhook Sync
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF3CD)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⚡", fontSize = 22.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Sinkronisasi Realtime Webhook", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                        Text("Konektivitas instan antara DB lokal Handphone dengan Google Drive Sheets Anda.", fontSize = 11.sp, color = TextSecondary)
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                if (viewModel.isSyncingSheets) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 3.dp, color = AccentIndigo)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mengunggah Jurnal Jaringan...", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentIndigo)
                    }
                } else {
                    Button(
                        onClick = { viewModel.syncWithGoogleSheets(context) },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Sinkronisasikan Live")
                    }
                }
                
                Divider(color = BgBase)
                
                // Apps Script expandable documentation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isCodeExpanded = !isCodeExpanded }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📜 Blueprint Kode Google Apps Script (Webhook)", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = AccentIndigo)
                    Icon(
                        imageVector = if (isCodeExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand CODE"
                    )
                }
                
                if (isCodeExpanded) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E293B))
                            .border(1.dp, Color(0xFF0F172A), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = gasCodeString,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = Color(0xFFE2E8F0),
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        )
                    }
                    Text(
                        text = "*Dapat disalin langsung ke Ekstensi -> Apps Script pada Google Sheet Anda untuk menghubungkan sinkronisasi realtime.",
                        fontSize = 9.sp,
                        color = TextSecondary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(35.dp))
    }
}

// ── GET CHART DATA FUNCTION (Fidelitas Tinggi) ──
fun getChartData(
    txs: List<com.example.data.ItemTransaction>,
    interval: String
): Triple<List<String>, List<Double>, List<Double>> {
    val labels = mutableListOf<String>()
    val incomes = mutableListOf<Double>()
    val expenses = mutableListOf<Double>()

    val calendar = Calendar.getInstance()

    when (interval) {
        "Harian" -> {
            val format = SimpleDateFormat("dd MMM", Locale.getDefault())
            for (i in 6 downTo 0) {
                val cal = Calendar.getInstance()
                cal.add(Calendar.DAY_OF_YEAR, -i)
                val label = format.format(cal.time)
                
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val startMs = cal.timeInMillis
                
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                cal.set(Calendar.MILLISECOND, 999)
                val endMs = cal.timeInMillis

                val dayTxs = txs.filter { it.timestamp in startMs..endMs }
                val inc = dayTxs.filter { it.type == "Pemasukan" }.sumOf { it.amount }
                val exp = dayTxs.filter { it.type == "Pengeluaran" }.sumOf { it.amount }

                labels.add(label)
                incomes.add(inc)
                expenses.add(exp)
            }
        }
        "Mingguan" -> {
            for (i in 4 downTo 0) {
                val label = "Minggu " + (5 - i)
                val cal = Calendar.getInstance()
                cal.add(Calendar.WEEK_OF_YEAR, -i)
                
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                val startMs = cal.timeInMillis
                
                cal.add(Calendar.DAY_OF_WEEK, 6)
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                val endMs = cal.timeInMillis

                val weekTxs = txs.filter { it.timestamp in startMs..endMs }
                val inc = weekTxs.filter { it.type == "Pemasukan" }.sumOf { it.amount }
                val exp = weekTxs.filter { it.type == "Pengeluaran" }.sumOf { it.amount }

                labels.add(label)
                incomes.add(inc)
                expenses.add(exp)
            }
        }
        "Bulanan" -> {
            val format = SimpleDateFormat("MMM", Locale.getDefault())
            for (i in 5 downTo 0) {
                val cal = Calendar.getInstance()
                cal.add(Calendar.MONTH, -i)
                val label = format.format(cal.time)
                
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                val startMs = cal.timeInMillis
                
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                val endMs = cal.timeInMillis

                val monthTxs = txs.filter { it.timestamp in startMs..endMs }
                val inc = monthTxs.filter { it.type == "Pemasukan" }.sumOf { it.amount }
                val exp = monthTxs.filter { it.type == "Pengeluaran" }.sumOf { it.amount }

                labels.add(label)
                incomes.add(inc)
                expenses.add(exp)
            }
        }
        "Tahunan" -> {
            val currentYear = calendar.get(Calendar.YEAR)
            for (year in (currentYear - 2)..currentYear) {
                val label = year.toString()
                
                val cal = Calendar.getInstance()
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, Calendar.JANUARY)
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                val startMs = cal.timeInMillis
                
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, Calendar.DECEMBER)
                cal.set(Calendar.DAY_OF_MONTH, 31)
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                val endMs = cal.timeInMillis

                val yearTxs = txs.filter { it.timestamp in startMs..endMs }
                val inc = yearTxs.filter { it.type == "Pemasukan" }.sumOf { it.amount }
                val exp = yearTxs.filter { it.type == "Pengeluaran" }.sumOf { it.amount }

                labels.add(label)
                incomes.add(inc)
                expenses.add(exp)
            }
        }
    }

    return Triple(labels, incomes, expenses)
}

// ── INTERACTIVE CHARTJS CARD IN DASHBOARD ──
@Composable
fun InteractiveChartJsCard(txs: List<com.example.data.ItemTransaction>, isDarkMode: Boolean) {
    var selectedInterval by remember { mutableStateOf("Bulanan") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(550.dp)
            .testTag("interactive_chartjs_card"),
        colors = CardDefaults.cardColors(containerColor = BgCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📊", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Grafik Interaktif (Chart.js)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                }

                // Small Switch Tabs
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Harian", "Mingguan", "Bulanan", "Tahunan").forEach { tag ->
                        val active = selectedInterval == tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (active) AccentIndigo else BgSidebar)
                                .clickable { selectedInterval = tag }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = tag,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (active) Color.White else TextPrimary
                            )
                        }
                    }
                }
            }

            // Calculations
            val (labels, incomes, expenses) = remember(txs, selectedInterval) {
                getChartData(txs, selectedInterval)
            }

            val intervalTxs = remember(txs, selectedInterval) {
                val cal = Calendar.getInstance()
                when (selectedInterval) {
                    "Harian" -> {
                        cal.add(Calendar.DAY_OF_YEAR, -6)
                        txs.filter { it.timestamp >= cal.timeInMillis && it.type == "Pengeluaran" }
                    }
                    "Mingguan" -> {
                        cal.add(Calendar.WEEK_OF_YEAR, -4)
                        txs.filter { it.timestamp >= cal.timeInMillis && it.type == "Pengeluaran" }
                    }
                    "Bulanan" -> {
                        cal.add(Calendar.MONTH, -5)
                        txs.filter { it.timestamp >= cal.timeInMillis && it.type == "Pengeluaran" }
                    }
                    else -> {
                        txs.filter { it.type == "Pengeluaran" }
                    }
                }
            }

            val categoryGroups = remember(intervalTxs) {
                intervalTxs.groupBy { it.category }
                    .mapValues { it.value.sumOf { item -> item.amount } }
            }

            val pieLabels = categoryGroups.keys.toList()
            val pieValues = categoryGroups.values.toList()

            val trendLabelsJson = labels.joinToString(prefix = "[", postfix = "]") { "\"$it\"" }
            val trendIncomeJson = incomes.joinToString(prefix = "[", postfix = "]") { it.toString() }
            val trendExpenseJson = expenses.joinToString(prefix = "[", postfix = "]") { it.toString() }

            val pieLabelsJson = if (pieLabels.isEmpty()) "[\"Sisa Budget\"]" else pieLabels.joinToString(prefix = "[", postfix = "]") { "\"$it\"" }
            val pieValuesJson = if (pieValues.isEmpty()) "[100]" else pieValues.joinToString(prefix = "[", postfix = "]") { it.toString() }

            // Theme Configuration
            val chartThemeBackground = if (isDarkMode) "#1E293B" else "#FFFFFF"
            val chartThemeText = if (isDarkMode) "#F8FAFC" else "#0F172A"
            val gridColor = if (isDarkMode) "#334155" else "#E2E8F0"

            val htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
                  <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
                  <style>
                    body {
                      background-color: $chartThemeBackground;
                      color: $chartThemeText;
                      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
                      margin: 0;
                      padding: 10px;
                      overflow-x: hidden;
                    }
                    .chart-container {
                      position: relative;
                      margin-bottom: 24px;
                      width: 100%;
                    }
                    canvas {
                      width: 100% !important;
                      height: 190px !important;
                    }
                    h3 {
                      font-size: 11px;
                      margin-top: 0;
                      margin-bottom: 8px;
                      color: $chartThemeText;
                      text-align: left;
                      font-weight: 700;
                      text-transform: uppercase;
                      letter-spacing: 0.5px;
                    }
                  </style>
                </head>
                <body>
                  <div class="chart-container">
                    <h3>📈 Tren Aliran Kas ($selectedInterval)</h3>
                    <div style="height: 190px;"><canvas id="trendChart"></canvas></div>
                  </div>
                  <div class="chart-container">
                    <h3>🍩 Distribusi Pengeluaran</h3>
                    <div style="height: 190px;"><canvas id="pieChart"></canvas></div>
                  </div>

                  <script>
                    var ctxTrend = document.getElementById('trendChart').getContext('2d');
                    var trendChart = new Chart(ctxTrend, {
                      type: 'line',
                      data: {
                        labels: $trendLabelsJson,
                        datasets: [
                          {
                            label: 'Masuk',
                            data: $trendIncomeJson,
                            borderColor: '#10B981',
                            backgroundColor: 'rgba(16, 185, 129, 0.1)',
                            borderWidth: 2,
                            tension: 0.3,
                            fill: true
                          },
                          {
                            label: 'Keluar',
                            data: $trendExpenseJson,
                            borderColor: '#EF4444',
                            backgroundColor: 'rgba(239, 68, 68, 0.1)',
                            borderWidth: 2,
                            tension: 0.3,
                            fill: true
                          }
                        ]
                      },
                      options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                          legend: {
                            labels: { color: '$chartThemeText', font: { size: 9, weight: 'bold' } }
                          }
                        },
                        scales: {
                          x: {
                            grid: { color: '$gridColor' },
                            ticks: { color: '$chartThemeText', font: { size: 8 } }
                          },
                          y: {
                            grid: { color: '$gridColor' },
                            ticks: { color: '$chartThemeText', font: { size: 8 } }
                          }
                        }
                      }
                    });

                    var ctxPie = document.getElementById('pieChart').getContext('2d');
                    var pieChart = new Chart(ctxPie, {
                      type: 'doughnut',
                      data: {
                        labels: $pieLabelsJson,
                        datasets: [{
                          data: $pieValuesJson,
                          backgroundColor: [
                            '#EF4444', '#3B82F6', '#10B981', '#F59E0B', '#8B5CF6', '#EC4899', '#06B6D4'
                          ],
                          borderWidth: 0
                        }]
                      },
                      options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                          legend: {
                            position: 'right',
                            labels: { color: '$chartThemeText', font: { size: 9, weight: 'bold' } }
                          }
                        }
                      }
                    });
                  </script>
                </body>
                </html>
            """.trimIndent()

            androidx.compose.ui.viewinterop.AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                factory = { context ->
                    android.webkit.WebView(context).apply {
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            useWideViewPort = true
                            loadWithOverviewMode = true
                        }
                        webViewClient = android.webkit.WebViewClient()
                    }
                },
                update = { webView ->
                    webView.loadDataWithBaseURL("https://chartjs-charts", htmlContent, "text/html", "UTF-8", null)
                }
            )
        }
    }
}
