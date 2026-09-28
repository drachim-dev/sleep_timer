package dr.achim.sleep_timer.common

import androidx.annotation.StringRes
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

sealed interface UiMessage {
    data class Dynamic(val value: String) : UiMessage
    data class Resource(
        @StringRes val resId: Int,
        val args: List<Any> = emptyList(),
    ) : UiMessage
}

class UiMessageManager {
    private val _messages = Channel<UiMessage>(Channel.BUFFERED)
    val messages = _messages.receiveAsFlow()

    fun emitMessage(message: String) {
        _messages.trySend(UiMessage.Dynamic(message))
    }

    fun emitMessage(@StringRes resId: Int, vararg args: Any) {
        _messages.trySend(UiMessage.Resource(resId, args.toList()))
    }
}
