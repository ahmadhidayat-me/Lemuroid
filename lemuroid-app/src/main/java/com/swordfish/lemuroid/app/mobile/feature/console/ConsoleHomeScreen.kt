package com.swordfish.lemuroid.app.mobile.feature.console

import android.view.KeyEvent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.swordfish.lemuroid.R
import com.swordfish.lemuroid.app.mobile.shared.compose.ui.LemuroidEmptyView
import com.swordfish.lemuroid.app.shared.systems.MetaSystemInfo
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

/** Entries of the console layout menu, which replaces the bottom navigation bar. */
enum class ConsoleMenuEntry(val titleId: Int) {
    FAVORITES(R.string.favorites),
    SEARCH(R.string.title_search),
    SYSTEMS(R.string.title_systems),
    SETTINGS(R.string.title_settings),
}

/**
 * Home screen of the console layout: one system at a time in a full screen carousel, in the spirit of retro
 * handheld frontends. It works with touch, as well as with a gamepad or keyboard (D-pad/L/R to move, A to open,
 * Start/Y for the menu).
 */
@Composable
fun ConsoleHomeScreen(
    modifier: Modifier = Modifier,
    systems: List<MetaSystemInfo>,
    onSystemSelected: (MetaSystemInfo) -> Unit,
    onMenuEntrySelected: (ConsoleMenuEntry) -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.surfaceContainerHigh,
                            MaterialTheme.colorScheme.surface,
                        ),
                    ),
                )
                .systemBarsPadding(),
    ) {
        if (systems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize()) { LemuroidEmptyView() }
        } else {
            SystemsCarousel(
                systems = systems,
                onSystemSelected = onSystemSelected,
                onMenuRequested = { menuExpanded = true },
            )
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            ButtonHints(
                showOpen = systems.isNotEmpty(),
                onMenuClicked = { menuExpanded = true },
            )
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
            ) {
                ConsoleMenuEntry.entries.forEach { entry ->
                    DropdownMenuItem(
                        text = { Text(stringResource(entry.titleId)) },
                        onClick = {
                            menuExpanded = false
                            onMenuEntrySelected(entry)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun SystemsCarousel(
    systems: List<MetaSystemInfo>,
    onSystemSelected: (MetaSystemInfo) -> Unit,
    onMenuRequested: () -> Unit,
) {
    // Survives navigation to the games list and back.
    var savedPage by rememberSaveable { mutableStateOf(0) }
    val pagerState = rememberPagerState(initialPage = savedPage.coerceIn(0, systems.size - 1)) { systems.size }
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(pagerState.currentPage) { savedPage = pagerState.currentPage }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    fun moveBy(delta: Int) {
        val target = (pagerState.currentPage + delta).coerceIn(0, systems.size - 1)
        scope.launch { pagerState.animateScrollToPage(target) }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp)
                .focusRequester(focusRequester)
                .onPreviewKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                    when (event.nativeKeyEvent.keyCode) {
                        KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_BUTTON_L1 -> moveBy(-1)
                        KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_BUTTON_R1 -> moveBy(1)
                        KeyEvent.KEYCODE_BUTTON_A, KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER ->
                            onSystemSelected(systems[pagerState.currentPage])
                        KeyEvent.KEYCODE_BUTTON_START, KeyEvent.KEYCODE_BUTTON_Y, KeyEvent.KEYCODE_MENU ->
                            onMenuRequested()
                        else -> return@onPreviewKeyEvent false
                    }
                    true
                }
                .focusable(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.console_layout_header),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { moveBy(-1) }, enabled = pagerState.currentPage > 0) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null)
            }
            HorizontalPager(
                modifier = Modifier.weight(1f),
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 24.dp),
                pageSpacing = 8.dp,
            ) { page ->
                SystemPage(
                    system = systems[page],
                    pagerState = pagerState,
                    page = page,
                    onClick = {
                        if (page == pagerState.currentPage) {
                            onSystemSelected(systems[page])
                        } else {
                            scope.launch { pagerState.animateScrollToPage(page) }
                        }
                    },
                )
            }
            IconButton(onClick = { moveBy(1) }, enabled = pagerState.currentPage < systems.size - 1) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        val current = systems[pagerState.currentPage]
        val context = LocalContext.current
        Text(
            text = current.getName(context),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
        Text(
            text = stringResource(R.string.system_grid_details, current.count.toString()),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(24.dp))

        PageIndicator(count = systems.size, current = pagerState.currentPage)
    }
}

@Composable
private fun SystemPage(
    system: MetaSystemInfo,
    pagerState: PagerState,
    page: Int,
    onClick: () -> Unit,
) {
    val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
    val scale = 1f - (pageOffset.coerceIn(0f, 1f) * 0.2f)

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        ElevatedCard(
            modifier =
                Modifier
                    .widthIn(max = 320.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        alpha = 1f - (pageOffset.coerceIn(0f, 1f) * 0.5f)
                    },
            onClick = onClick,
            shape = RoundedCornerShape(24.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(Color(system.metaSystem.color())),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(0.75f),
                    painter = painterResource(id = system.metaSystem.imageResId),
                    contentDescription = stringResource(id = system.metaSystem.titleResId),
                    contentScale = ContentScale.Fit,
                )
            }
        }
    }
}

@Composable
private fun PageIndicator(
    count: Int,
    current: Int,
) {
    // Too many dots become noise, a counter reads better past a certain amount of systems.
    if (count > MAX_DOTS) {
        Text(
            text = "${current + 1} / $count",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        return
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(count) { index ->
            val color by animateColorAsState(
                if (index == current) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                },
                label = "dot",
            )
            Box(
                modifier =
                    Modifier
                        .size(if (index == current) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(color),
            )
        }
    }
}

@Composable
private fun ButtonHints(
    showOpen: Boolean,
    onMenuClicked: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (showOpen) {
                ButtonHint(symbol = "A", label = stringResource(R.string.console_layout_hint_open))
                ButtonHint(symbol = "◀▶", label = stringResource(R.string.console_layout_hint_select))
            }
            ButtonHint(
                symbol = "☰",
                label = stringResource(R.string.console_layout_hint_menu),
                modifier = Modifier.clickable(onClick = onMenuClicked),
            )
        }
    }
}

@Composable
private fun ButtonHint(
    symbol: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = symbol,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(text = label, style = MaterialTheme.typography.labelLarge)
    }
}

private const val MAX_DOTS = 12
