package android.text;

/**
 * Created by shaxiboy on 2017/3/31 0031.
 */

public class TextUtils {
    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.equals("")) {
            return true;
        }
        return false;
    }
}