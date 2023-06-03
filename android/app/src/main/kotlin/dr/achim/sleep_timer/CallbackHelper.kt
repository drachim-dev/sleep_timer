package dr.achim.sleep_timer

import android.content.Context

object CallbackHelper {
    const val ENTRY_POINT_HELPER_PREF = "SLEEP_TIMER_CALLBACK_PREF"
    const val CALLBACK_HANDLE_KEY = "SLEEP_TIMER_CALLBACK_KEY"
    const val NO_HANDLE = -1L
    fun setHandle(context: Context, handle: Long?) {
        val editor = context.getSharedPreferences(
                ENTRY_POINT_HELPER_PREF,
                Context.MODE_PRIVATE
        ).edit()
        editor.putLong(CALLBACK_HANDLE_KEY, handle!!)
        editor.apply()
    }

    fun getRawHandle(context: Context): Long {
        return context.getSharedPreferences(
                ENTRY_POINT_HELPER_PREF,
                Context.MODE_PRIVATE
        ).getLong(CALLBACK_HANDLE_KEY, NO_HANDLE)
    }
}