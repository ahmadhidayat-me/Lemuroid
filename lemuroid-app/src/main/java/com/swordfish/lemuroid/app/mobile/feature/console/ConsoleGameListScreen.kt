package com.swordfish.lemuroid.app.mobile.feature.console

import android.view.KeyEvent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.paging.compose.LazyPagingItems
import com.swordfish.lemuroid.R
import com.swordfish.lemuroid.app.mobile.shared.compose.ui.LemuroidEmptyView
import com.swordfish.lemuroid.app.mobile.shared.compose.ui.LemuroidGameImage
import com.swordfish.lemuroid.lib.library.GameSystem
import com.swordfish.lemuroid.lib.library.db.entity.Game
import kotlinx.coroutines.launch

/**
 * Games list of the console layout: titles on one side and the highlighted game's cover on the other, like
 * retro handheld frontends. With a gamepad, the D-pad moves the highlight, A plays, Y toggles the favorite and
 * B goes back. With touch, the first tap highlights a game and a second tap plays it.
 */
@Composable
fun ConsoleGameListScreen(
    title: String,
    games: LazyPagingItems<Game>,
    onGameClick: (Game) -> Unit,
    onGameLongClick: (Game) -> Unit,
    onGameFavoriteToggle: (Game, Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    val itemCount = games.itemCount
    val selectedGame = if (itemCount > 0) games.peek(selectedIndex.coerceIn(0, itemCount - 1)) else null

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    fun select(index: Int) {
        if (itemCount == 0) return
        selectedIndex = index.coerceIn(0, itemCount - 1)
        scope.launch {
            val visible = listState.layoutInfo.visibleItemsInfo
            val isVisible =
                visible.any { it.index == selectedIndex } &&
                    visible.first().index != selectedIndex &&
                    visible.last().index != selectedIndex
            if (!isVisible) listState.animateScrollToItem((selectedIndex - 2).coerceAtLeast(0))
        }
    }

    val playSelected = { selectedGame?.let(onGameClick) }
    val toggleFavorite = { selectedGame?.let { onGameFavoriteToggle(it, !it.isFavorite) } }

    BoxWithConstraints(
        modifier =
            modifier
                .fillMaxSize()
                .background(consoleBackground())
                .systemBarsPadding()
                .focusRequester(focusRequester)
                .onPreviewKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                    when (event.nativeKeyEvent.keyCode) {
                        KeyEvent.KEYCODE_DPAD_UP -> select(selectedIndex - 1)
                        KeyEvent.KEYCODE_DPAD_DOWN -> select(selectedIndex + 1)
                        KeyEvent.KEYCODE_BUTTON_L1, KeyEvent.KEYCODE_PAGE_UP -> select(selectedIndex - PAGE_JUMP)
                        KeyEvent.KEYCODE_BUTTON_R1, KeyEvent.KEYCODE_PAGE_DOWN -> select(selectedIndex + PAGE_JUMP)
                        in CONFIRM_KEYS -> playSelected()
                        KeyEvent.KEYCODE_BUTTON_Y -> toggleFavorite()
                        KeyEvent.KEYCODE_BUTTON_X -> selectedGame?.let(onGameLongClick)
                        KeyEvent.KEYCODE_BUTTON_B -> onBack()
                        else -> return@onPreviewKeyEvent false
                    }
                    true
                }
                .focusable(),
    ) {
        val isLandscape = maxWidth > maxHeight

        Column(modifier = Modifier.fillMaxSize()) {
            Header(title = title, count = itemCount)

            Box(modifier = Modifier.weight(1f)) {
                if (itemCount == 0) {
                    LemuroidEmptyView()
                } else if (isLandscape) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        GamesList(
                            modifier = Modifier.weight(1f),
                            games = games,
                            listState = listState,
                            selectedIndex = selectedIndex,
                            onRowClick = {
                                    index,
                                    game,
                                ->
                                if (index == selectedIndex) onGameClick(game) else select(index)
                            },
                            onRowLongClick = onGameLongClick,
                        )
                        CoverPreview(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            game = selectedGame,
                            maxSize = min(this@BoxWithConstraints.maxHeight * 0.6f, 360.dp),
                            onClick = { playSelected() },
                        )
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        CoverPreview(
                            modifier = Modifier.fillMaxWidth(),
                            game = selectedGame,
                            maxSize =
                                min(
                                    this@BoxWithConstraints.maxWidth * 0.55f,
                                    this@BoxWithConstraints.maxHeight * 0.3f,
                                ),
                            onClick = { playSelected() },
                        )
                        GamesList(
                            modifier = Modifier.weight(1f),
                            games = games,
                            listState = listState,
                            selectedIndex = selectedIndex,
                            onRowClick = {
                                    index,
                                    game,
                                ->
                                if (index == selectedIndex) onGameClick(game) else select(index)
                            },
                            onRowLongClick = onGameLongClick,
                        )
                    }
                }
            }

            ConsoleButtonHints(
                hints =
                    listOfNotNull(
                        ConsoleButtonHint("A", stringResource(R.string.console_layout_hint_play), { playSelected() })
                            .takeIf { itemCount > 0 },
                        ConsoleButtonHint(
                            "Y",
                            stringResource(R.string.console_layout_hint_favorite),
                            { toggleFavorite() },
                        )
                            .takeIf { itemCount > 0 },
                        ConsoleButtonHint("B", stringResource(R.string.console_layout_hint_back), onBack),
                    ),
            )
        }
    }
}

@Composable
private fun Header(
    title: String,
    count: Int,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = title.uppercase(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = stringResource(R.string.system_grid_details, count.toString()),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GamesList(
    modifier: Modifier,
    games: LazyPagingItems<Game>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    selectedIndex: Int,
    onRowClick: (Int, Game) -> Unit,
    onRowLongClick: (Game) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxHeight(),
        state = listState,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        items(games.itemCount, key = { games.peek(it)?.id ?: it }) { index ->
            val game = games[index] ?: return@items
            val selected = index == selectedIndex
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (selected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surface.copy(
                                    alpha = 0f,
                                )
                            },
                        )
                        .combinedClickable(
                            onClick = { onRowClick(index, game) },
                            onLongClick = { onRowLongClick(game) },
                        )
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val contentColor =
                    if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                Text(
                    modifier = Modifier.weight(1f),
                    text = (if (selected) "▶ " else "   ") + game.displayName(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (game.isFavorite) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = contentColor,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CoverPreview(
    modifier: Modifier,
    game: Game?,
    maxSize: Dp,
    onClick: () -> Unit,
) {
    BoxWithConstraints(modifier = modifier.padding(16.dp), contentAlignment = Alignment.Center) {
        if (game == null) return@BoxWithConstraints
        // Leave room for the title and details below the cover, whatever the space this panel received.
        val coverSize = min(maxSize, min(maxWidth, maxHeight - COVER_TEXT_SPACE)).coerceAtLeast(0.dp)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(coverSize)
                        .clip(RoundedCornerShape(16.dp))
                        .combinedClickable(onClick = onClick),
            ) {
                LemuroidGameImage(game = game)
            }
            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = game.displayName(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            val details =
                listOfNotNull(
                    GameSystem.findById(game.systemId).shortTitleResId.let { stringResource(it) },
                    game.developer,
                )
            Text(
                text = details.joinToString(" · "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private const val PAGE_JUMP = 8

private val COVER_TEXT_SPACE = 88.dp

private val CONFIRM_KEYS = setOf(KeyEvent.KEYCODE_BUTTON_A, KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER)
