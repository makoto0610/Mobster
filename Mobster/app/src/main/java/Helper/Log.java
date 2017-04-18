package Helper;

/**
 * Wrapper class around Android Logger for enabling/disabling logger (just set enable)
 *
 * @author Ani
 * @version 1.0
 */
public class Log {
    private static boolean ENABLE_DEBUG = true;
    private static boolean ENABLE_INFO = true;

    /**
     * Enable the debug flag and output message
     *
     * @param tag of the message
     * @param message content of the message
     */
    public static void d(String tag, String message) {
        if (ENABLE_DEBUG) android.util.Log.d(tag, message);
    }

    /**
     * Enable the info flag and output message
     * @param tag of the message
     * @param message content of the message
     */
    public static void i(String tag, String message) {
        if (ENABLE_INFO) android.util.Log.i(tag, message);
    }
}
