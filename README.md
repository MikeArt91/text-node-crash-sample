# TextStringSimpleNode.draw crash in Jetpack Compose Version 1.6.7

## Description

There is a crash that might occur in certain circumstances within Jetpack Compose `TextStringSimpleNode` rendering.

The provided sample application aims to replicate the minimal conditions that trigger this crash with a high degree of probability.

We reported this issue to Google, and hope this sample helps with the further investigation: [Issue 308501489](https://issuetracker.google.com/issues/308501489)

Reproducing the crash consistently proved to be challenging. In our specific instance, removing the `Modifier.alignByBaseline()` mitigated the issue. However, this might not be the root cause of the problem.

## How to reproduce the crash

Required conditions for the crash are:
1. Using `LazyColumn`
2. A specific `Text` element implementation, such as `BasicText` wrapped within the `TextButton`
3. A subset of the `Text` elements reside outside the currently viewable portion of the screen.
4. Triggering rendering events by changing the text, using modifiers like `Modifier.alignByBaseline()` and scrolling through the list at the same time

All those conditions combined might result in this crash:
```
FATAL EXCEPTION: main
    Process: com.textnodecrashsample, PID: 28550
    java.lang.IllegalArgumentException: no paragraph
        at androidx.compose.foundation.text.modifiers.TextStringSimpleNode.draw(TextStringSimpleNode.kt:390)
        at androidx.compose.ui.node.LayoutNodeDrawScope.drawDirect-x_KDEd0$ui_release(LayoutNodeDrawScope.kt:105)
        at androidx.compose.ui.node.LayoutNodeDrawScope.draw-x_KDEd0$ui_release(LayoutNodeDrawScope.kt:86)
        at androidx.compose.ui.node.NodeCoordinator.drawContainedDrawModifiers(NodeCoordinator.kt:364)
        at androidx.compose.ui.node.NodeCoordinator.draw(NodeCoordinator.kt:353)
        at androidx.compose.ui.node.LayoutModifierNodeCoordinator.performDraw(LayoutModifierNodeCoordinator.kt:176)
        at androidx.compose.ui.node.NodeCoordinator.drawContainedDrawModifiers(NodeCoordinator.kt:361)
        at androidx.compose.ui.node.NodeCoordinator.access$drawContainedDrawModifiers(NodeCoordinator.kt:54)
```

In order to reproduce the crash, one just need to start the app.

It uses auto-scrolling and auto-state change that helps to trigger as much redraws as possible.

[Shortcut to MainActivity](app/src/main/java/com/textnodecrashsample/MainActivity.kt)

## Other mentions

Worth noting that there was a similar issue reported before, when `Text` element was wrapped inside a `Box` but it was fixed in JPC version 1.6: [Issue 295518534](https://issuetracker.google.com/issues/295518534), [Issue 295214720](https://issuetracker.google.com/issues/295214720)

This project is inspired by the sample app created to reproduce that previous issue: [reproduce_compose_text_bug_1.6.0-alpha03](https://github.com/samoylenkodmitry/reproduce_compose_text_bug_1.6.0-alpha03/) created by [samoylenkodmitry](https://github.com/samoylenkodmitry)
