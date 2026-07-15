package dr.achim.sleep_timer.common

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration

/**
 * Find the closest Activity in a given Context.
 */
fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Must only be called in the context of an Activity")
}

fun Long.toDays(): Long = this / (1000 * 60 * 60 * 24)

val Any.TAG: String
    get() {
        val tag = javaClass.simpleName
        return if (tag.length <= 23) tag else tag.take(23)
    }

inline fun <T1, T2, T3, T4, T5, T6, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    crossinline transform: suspend (T1, T2, T3, T4, T5, T6) -> R
): Flow<R> {
    return combine(flow, flow2, flow3, flow4, flow5, flow6) { args: Array<*> ->
        @Suppress("UNCHECKED_CAST")
        transform(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
            args[3] as T4,
            args[4] as T5,
            args[5] as T6,
        )
    }
}

inline fun <T1, T2, T3, T4, T5, T6, T7, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    crossinline transform: suspend (T1, T2, T3, T4, T5, T6, T7) -> R
): Flow<R> {
    return combine(flow, flow2, flow3, flow4, flow5, flow6, flow7) { args: Array<*> ->
        @Suppress("UNCHECKED_CAST")
        transform(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
            args[3] as T4,
            args[4] as T5,
            args[5] as T6,
            args[6] as T7,
        )
    }
}

/**
 * Extension for ViewModel to launch a loading task.
 */
fun <T> ViewModel.launchLoading(
    loadingState: MutableStateFlow<Boolean>,
    minDuration: Duration = Constants.MIN_LOADING_DURATION,
    previousJob: Job? = null,
    block: suspend CoroutineScope.() -> T,
    onSuccess: (T) -> Unit
): Job = viewModelScope.launchLoading(loadingState, minDuration, previousJob, block, onSuccess)

/**
 * Launches a coroutine that sets a loading state to true for at least [minDuration].
 *
 * @param loadingState The StateFlow to update with the loading status.
 * @param minDuration The minimum time the loading state should remain true.
 * @param previousJob An optional job to cancel and join before starting this one.
 * @param block The suspend block to execute.
 * @param onSuccess The callback to execute with the block's result after minDuration has passed.
 * @return The launched Job.
 */
fun <T> CoroutineScope.launchLoading(
    loadingState: MutableStateFlow<Boolean>,
    minDuration: Duration = Constants.MIN_LOADING_DURATION,
    previousJob: Job? = null,
    block: suspend CoroutineScope.() -> T,
    onSuccess: (T) -> Unit
): Job = launch {
    previousJob?.cancelAndJoin()
    loadingState.value = true
    val minDurationJob = launch { delay(minDuration) }
    try {
        val result = block()
        minDurationJob.join()
        onSuccess(result)
    } finally {
        if (isActive) {
            loadingState.value = false
        }
    }
}
