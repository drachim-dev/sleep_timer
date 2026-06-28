package dr.achim.sleep_timer.common

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class UiMessageManager {
    private val _messages = Channel<String>(Channel.BUFFERED)
    val messages = _messages.receiveAsFlow()

    fun emitMessage(message: String) {
        _messages.trySend(message)
    }
}