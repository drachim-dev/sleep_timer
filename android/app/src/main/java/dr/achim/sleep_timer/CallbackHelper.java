package dr.achim.sleep_timer;

import android.content.Context;
import android.content.SharedPreferences;

public class CallbackHelper {
    static String ENTRY_POINT_HELPER_PREF = "SLEEP_TIMER_CALLBACK_PREF";
    static String CALLBACK_HANDLE_KEY = "SLEEP_TIMER_CALLBACK_KEY";

    static Long NO_HANDLE = -1L;

    static void setHandle(Context context, Long handle) {
        final SharedPreferences.Editor editor= context.getSharedPreferences(
                ENTRY_POINT_HELPER_PREF,
                Context.MODE_PRIVATE
        ).edit();

        editor.putLong(CALLBACK_HANDLE_KEY, handle);
        editor.apply();
    }

    static Long getRawHandle(Context context) {
        return context.getSharedPreferences(
                ENTRY_POINT_HELPER_PREF,
                Context.MODE_PRIVATE
        ).getLong(CALLBACK_HANDLE_KEY, NO_HANDLE);
    }
}