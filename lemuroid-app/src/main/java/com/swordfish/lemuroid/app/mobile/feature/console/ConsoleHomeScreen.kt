package com.swordfish.lemuroid.app.mobile.feature.console

import android.view.KeyEvent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.swordfish.lemuroid.R
import com.swordfish.lemuroid.app.mobile.shared.compose.ui.LemuroidEmptyView
import com.swordfish.lemuroid.app.shared.systems.MetaSystemInfo
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

/** Entries of the console layout menu, which replaces the app bars. */
enum class ConsoleMenuEntry(val titleId: Int) {
    FAVORITES(R.string.favorites),
    SEARCH(R.string.title_search),
    SYSTEMS(R.string.title_systems),
    SETTINGS(R.string.title_settings),
}

/**
 * Home screen of the console layout: one system at a time in a full screen carousel, in the spirit of retro
 * handheld frontends. It works with touch, as well as with a gamepad or keyboard (D-pad/L/R to move, A to open,
 * Start/Y for the menu). In landscape the system art and its details sit side by side.
 */
@Composable
fun ConsoleHomeScreen(
    modifier: Modifier = Modifier,
    systems: List<MetaSystemInfo>,
    onSystemSelected: (MetaSystemInfo) -> Unit,
    onMenuEntrySelected: (ConsoleMenuEntry) -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    // Survives navigation to the games list and back.
    var savedPage by rememberSaveable { mutableIntStateOf(0) }
    val pagerState =
        rememberPagerState(initialPage = savedPage.coerceIn(0, (systems.size - 1).coerceAtLeast(0))) { systems.size }
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(pagerState.currentPage) { savedPage = pagerState.currentPage }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    fun moveBy(delta: Int) {
        if (systems.isEmpty()) return
        val target = (pagerState.currentPage + delta).coerceIn(0, systems.size - 1)
        scope.launch { pagerState.animateScrollToPage(target) }
    }

    fun openCurrent() {
        systems.getOrNull(pagerState.currentPage)?.let(onSystemSelected)
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(consoleBackground())
                .systemBarsPadding()
                .focusRequester(focusRequester)
                .onPreviewKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                    when (event.nativeKeyEvent.keyCode) {
                        KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_BUTTON_L1 -> moveBy(-1)
                        KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_BUTTON_R1 -> moveBy(1)
                        KeyEvent.KEYCODE_BUTTON_A, KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> openCurrent()
                        KeyEvent.KEYCODE_BUTTON_START, KeyEvent.KEYCODE_BUTTON_Y, KeyEvent.KEYCODE_MENU ->
                            menuExpanded = true
                        else -> return@onPreviewKeyEvent false
                    }
                    true
                }
                .focusable(),
    ) {
        BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (systems.isEmpty()) {
                LemuroidEmptyView()
                return@BoxWithConstraints
            }

            val isLandscape = maxWidth > maxHeight
            // The card is sized from the space actually available, so it never gets cropped or squashed.
            val cardSize =
                if (isLandscape) {
                    min(maxHeight * 0.75f, maxWidth * 0.4f)
                } else {
                    min(maxWidth * 0.72f, maxHeight * 0.5f)
                }
            val current = systems[pagerState.currentPage]

            if (isLandscape) {
                Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                    Carousel(
                        modifier = Modifier.weight(1.2f).fillMaxHeight(),
                        systems = systems,
                        pagerState = pagerState,
                        cardSize = cardSize,
                        onMove = ::moveBy,
                        onOpen = ::openCurrent,
                    )
                    SystemDetails(
                        modifier = Modifier.weight(1f).padding(end = 24.dp),
                        system = current,
                        pageCount = systems.size,
                        currentPage = pagerState.currentPage,
                        textAlign = TextAlign.Start,
                        horizontalAlignment = Alignment.Start,
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Carousel(
                        modifier = Modifier.fillMaxWidth(),
                        systems = systems,
                        pagerState = pagerState,
                        cardSize = cardSize,
                        onMove = ::moveBy,
                        onOpen = ::openCurrent,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    SystemDetails(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        system = current,
                        pageCount = systems.size,
                        currentPage = pagerState.currentPage,
                        textAlign = TextAlign.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    )
                }
            }
        }

        Box {
            ConsoleButtonHints(
                hints =
                    listOfNotNull(
                        ConsoleButtonHint("A", stringResource(R.string.console_layout_hint_open), ::openCurrent)
                            .takeIf { systems.isNotEmpty() },
                        ConsoleButtonHint("◀▶", stringResource(R.string.console_layout_hint_select))
                            .takeIf { systems.size > 1 },
                        ConsoleButtonHint(
                            "☰",
                            stringResource(R.string.console_layout_hint_menu),
                        ) { menuExpanded = true },
                    ),
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
private fun Carousel(
    modifier: Modifier,
    systems: List<MetaSystemInfo>,
    pagerState: PagerState,
    cardSize: Dp,
    onMove: (Int) -> Unit,
    onOpen: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { onMove(-1) }, enabled = pagerState.currentPage > 0) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null)
        }
        HorizontalPager(
            modifier = Modifier.weight(1f),
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 8.dp,
        ) { page ->
            SystemPage(
                system = systems[page],
                pagerState = pagerState,
                page = page,
                size = cardSize,
                onClick = {
                    if (page == pagerState.currentPage) {
                        onOpen()
                    } else {
                        scope.launch { pagerState.animateScrollToPage(page) }
                    }
                },
            )
        }
        IconButton(onClick = { onMove(1) }, enabled = pagerState.currentPage < systems.size - 1) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}

@Composable
private fun SystemDetails(
    modifier: Modifier,
    system: MetaSystemInfo,
    pageCount: Int,
    currentPage: Int,
    textAlign: TextAlign,
    horizontalAlignment: Alignment.Horizontal,
) {
    val context = LocalContext.current
    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = stringResource(R.string.console_layout_header),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = system.getName(context),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = textAlign,
        )
        Text(
            text = stringResource(R.string.system_grid_details, system.count.toString()),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(12.dp))
        PageIndicator(count = pageCount, current = currentPage)
    }
}

@Composable
private fun SystemPage(
    system: MetaSystemInfo,
    pagerState: PagerState,
    page: Int,
    size: Dp,
    onClick: () -> Unit,
) {
    val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
    val fraction = pageOffset.coerceIn(0f, 1f)

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        ElevatedCard(
            modifier =
                Modifier
                    .size(size)
                    .graphicsLayer {
                        scaleX = 1f - fraction * 0.2f
                        scaleY = 1f - fraction * 0.2f
                        alpha = 1f - fraction * 0.5f
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

private const val MAX_DOTS = 12
