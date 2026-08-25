package ru.application.homemedkit.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.*
import androidx.navigation3.runtime.serialization.NavKeySerializer
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer

class NavigationState(
    val startRoute: NavKey,
    topLevelRoute: MutableState<NavKey>,
    val backStacks: Map<NavKey, NavBackStack<NavKey>>
) {
    var topLevelRoute: NavKey by topLevelRoute

    val currentStack: List<NavKey>
        get() = backStacks[topLevelRoute] ?: emptyList()

    val currentRoute: NavKey?
        get() = currentStack.lastOrNull()

    val previousRoute: NavKey?
        get() {
            val currentStack = backStacks[topLevelRoute] ?: return null
            return if (currentStack.size > 1) {
                currentStack[currentStack.size - 2]
            } else {
                null
            }
        }

    val stacksInUse: List<NavKey>
        get() = if (topLevelRoute == startRoute) {
            listOf(startRoute)
        } else {
            listOf(startRoute, topLevelRoute)
        }
}

private fun buildBackStack(startKey: NavKey) = buildList {
    var node: NavKey? = startKey

    while (node != null) {
        add(0, node)
        val parent = (node as? Screen.NavDeepLink)?.parent
        node = parent
    }
}

@Composable
fun rememberNavigationState(
    startRoute: NavKey,
    topLevelRoutes: Set<NavKey>,
    deepLink: NavKey? = null
): NavigationState {
    val initialTab = remember(deepLink) {
        if (deepLink is Screen.NavDeepLink) deepLink.parent else startRoute
    }

    val topLevelRoute = rememberSerializable(
        inputs = arrayOf(initialTab, topLevelRoutes),
        serializer = MutableStateSerializer(NavKeySerializer()),
        init = { mutableStateOf(initialTab) }
    )

    val backStacks = topLevelRoutes.associateWith { key ->
        val isNested = (deepLink as? Screen.NavDeepLink)?.parent == key || deepLink == key

        val initialList = if (isNested) {
            buildBackStack(deepLink).ifEmpty { listOf(key) }
        } else {
            listOf(key)
        }

        rememberNavBackStack(*initialList.toTypedArray())
    }

    return remember(initialTab, topLevelRoutes) {
        NavigationState(
            startRoute = initialTab,
            topLevelRoute = topLevelRoute,
            backStacks = backStacks
        )
    }
}

@Composable
fun NavigationState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>
): SnapshotStateList<NavEntry<NavKey>> {
    val decoratedEntries = backStacks.mapValues { (_, stack) ->
        val decorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
            rememberViewModelStoreNavEntryDecorator()
        )
        rememberDecoratedNavEntries(
            backStack = stack,
            entryDecorators = decorators,
            entryProvider = entryProvider
        )
    }

    return stacksInUse
        .flatMap { decoratedEntries[it] ?: emptyList() }
        .toMutableStateList()
}