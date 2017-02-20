package Helper;

/**
 * Created by anireddy on 2/19/17.
 */

/**
 * Wrapper class around Android Logger for enabling/disabling logger (just set enable)
 */
public class Log {
    private static boolean ENABLE_DEBUG = true;
    private static boolean ENABLE_INFO = true;


    public static void d(String tag, String message) {
        if (ENABLE_DEBUG) android.util.Log.d(tag, message);
    }

    public static void i(String tag, String message) {
        if (ENABLE_INFO) android.util.Log.i(tag, message);
    }
}
