package info.nightscout.androidaps.plugins.PumpInsight.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Map;

import info.nightscout.androidaps.MainApp;

/**
 * Created by jamorham on 24/01/2018.
 *
 * Useful utility methods from xDrip+
 *
 */

public class Helpers {

    private static final String TAG = "InsightHelpers";

    private static final Map<String, Long> rateLimits = new HashMap<>();
    // singletons to avoid repeated allocation
    private static DecimalFormatSymbols dfs;
    private static DecimalFormat df;

    // return true if below rate limit
    public static synchronized boolean ratelimit(String name, int seconds) {
        // check if over limit
        if ((rateLimits.containsKey(name)) && (tsl() - rateLimits.get(name) < (seconds * 1000))) {
            Log.d(TAG, name + " rate limited: " + seconds + " seconds");
            return false;
        }
        // not over limit
        rateLimits.put(name, tsl());
        return true;
    }

    public static long tsl() {
        return System.currentTimeMillis();
    }

    public static long msSince(long when) {
        return (tsl() - when);
    }

    public static long msTill(long when) {
        return (when - tsl());
    }

    public static boolean checkPackageExists(Context context, String TAG, String packageName) {
        try {
            final PackageManager pm = context.getPackageManager();
            final PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi.packageName.equals(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        } catch (Exception e) {
            Log.wtf(TAG, "Exception trying to determine packages! " + e);
            return false;
        }
    }

    public static boolean runOnUiThreadDelayed(Runnable theRunnable, long delay) {
        return new Handler(MainApp.instance().getMainLooper()).postDelayed(theRunnable, delay);
    }

    public static PowerManager.WakeLock getWakeLock(final String name, int millis) {
        final PowerManager pm = (PowerManager) MainApp.instance().getSystemService(Context.POWER_SERVICE);
        if (pm == null) return null;
        final PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, name);
        wl.acquire(millis);
        return wl;
    }

    public static void releaseWakeLock(PowerManager.WakeLock wl) {
        if (wl == null) return;
        if (wl.isHeld()) wl.release();
    }

    public static String niceTimeSince(long t) {
        return niceTimeScalar(msSince(t));
    }

    public static String niceTimeTill(long t) {
        return niceTimeScalar(-msSince(t));
    }

    public static String niceTimeScalar(long t) {
        String unit = "second";
        t = t / 1000;
        if (t > 59) {
            unit = "minute";
            t = t / 60;
            if (t > 59) {
                unit = "hour";
                t = t / 60;
                if (t > 24) {
                    unit = "day";
                    t = t / 24;
                    if (t > 28) {
                        unit = "week";
                        t = t / 7;
                    }
                }
            }
        }
        if (t != 1) unit = unit + "s";
        return qs((double) t, 0) + " " + unit;
    }

    public static String qs(double x, int digits) {

        if (digits == -1) {
            digits = 0;
            if (((int) x != x)) {
                digits++;
                if ((((int) x * 10) / 10 != x)) {
                    digits++;
                    if ((((int) x * 100) / 100 != x)) digits++;
                }
            }
        }

        if (dfs == null) {
            final DecimalFormatSymbols local_dfs = new DecimalFormatSymbols();
            local_dfs.setDecimalSeparator('.');
            dfs = local_dfs; // avoid race condition
        }

        final DecimalFormat this_df;
        // use singleton if on ui thread otherwise allocate new as DecimalFormat is not thread safe
        if (Thread.currentThread().getId() == 1) {
            if (df == null) {
                final DecimalFormat local_df = new DecimalFormat("#", dfs);
                local_df.setMinimumIntegerDigits(1);
                df = local_df; // avoid race condition
            }
            this_df = df;
        } else {
            this_df = new DecimalFormat("#", dfs);
        }

        this_df.setMaximumFractionDigits(digits);
        return this_df.format(x);
    }

    public static String niceTimeScalarRedux(long t) {
        return niceTimeScalar(t).replaceFirst("^1 ", "");
    }


}
