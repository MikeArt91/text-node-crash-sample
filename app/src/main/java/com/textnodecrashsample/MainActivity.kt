package com.textnodecrashsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ComposeView(this).apply {
            setContent {
                FailingSample()
            }
        })
    }
}

/**
 * Contains some crucial conditions to reproduce the crash:
 *
 * 1. LazyColumn - replacing with regular Column with enabled scroll fixes the crash
 * 2. Scroll - crash only appears on scrolling. This example uses AutoScroll,
 * feel free to delete it and reproduce the crash while manually scrolling
 * 3. Changing the state - switches the text inside the button,
 * could be done within the button element itself with the same outcome
 * 4. There should be some Text elements located outside the visible area of the screen
 *
 */
@Composable
fun FailingSample(
    countOfItems: Int = 200,
    autoSwitchState: Boolean = true,
    autoScroll: Boolean = true
) {
    val listState = rememberLazyListState()
    var isLoading by remember { mutableStateOf(true) }
    LazyColumn(
        modifier = Modifier.background(color = Color.White),
        state = listState
    ) {
        items(countOfItems) {
            Filler()
            FailingButton(isLoading = isLoading) {
                isLoading = !isLoading
            }
        }
    }

    if (autoSwitchState) {
        AutoSwitchState { newState ->
            isLoading = newState
        }
    }

    if (autoScroll) {
        AutoScroll(state = listState)
    }
}

/**
 * This element contains exact combination of elements that causes crash
 *
 * 1. Row with Modifier.alignByBaseline() - deleting this modifier, or removing the Row fixes the crash
 * 2. Usage of TextButton - replacing with simple text also fixes the crash
 *
 */
@Composable
fun FailingButton(isLoading: Boolean, onClick: () -> (Unit)) {
    Row {
        TextButton(
            modifier = Modifier.alignByBaseline(),
            onClick = onClick,
        ) {
            BasicText(
                text = if (isLoading) "Loading" else "Ready"
            )
        }
    }
}

@Composable
fun Filler() {
    Box(
        modifier = Modifier
            .background(Color.Cyan)
            .height((LocalConfiguration.current.screenHeightDp * 0.5).dp)
            .fillMaxWidth(),
    )
}

@Composable
fun AutoScroll(delayMs: Long = 200, scrollBy: Float = 100.0f, state: LazyListState) {
    var animateCount by remember { mutableStateOf(0) }
    LaunchedEffect(animateCount) {
        delay(delayMs)
        state.animateScrollBy(scrollBy)
        animateCount++
    }
}

@Composable
fun AutoSwitchState(delayMs: Long = 100, onSwitch: (Boolean) -> (Unit)) {
    var changedCount by remember { mutableStateOf(0) }
    LaunchedEffect(changedCount) {
        delay(delayMs)
        onSwitch(changedCount % 2 == 0)
        changedCount++
    }
}
