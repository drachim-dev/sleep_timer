package dr.achim.sleep_timer

import android.content.Context

class EntryPointCallbackHelper {
    companion object  {
        private const val ENTRY_POINT_HELPER_PREF = "ENTRY_POINT_HELPER_PREF"
        private const val CALLBACK_HANDLE_KEY = "CALLBACK_HANDLE_KEY"

        const val NO_HANDLE = -1L

        fun setHandle(context: Context, handle: Long) {
            context.getSharedPreferences(
                ENTRY_POINT_HELPER_PREF,
                Context.MODE_PRIVATE
            ).edit().apply {
                putLong(CALLBACK_HANDLE_KEY, handle)
                apply()
            }
        }

        fun getRawHandle(context: Context): Long {
            return context.getSharedPreferences(
                ENTRY_POINT_HELPER_PREF,
                Context.MODE_PRIVATE
            ).getLong(CALLBACK_HANDLE_KEY, NO_HANDLE)
        }
    }
}