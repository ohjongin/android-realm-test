package me.ji5.restracker.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class Log {
    protected static final int DEPTH_OF_STACK = 4;

    public static String TAG = "IB"; // should be replaced with package

    public static boolean LOG_VERBOSE = true;
    public static boolean LOG_INFO = true;
    public static boolean LOG_DEBUG = true;
    public static boolean LOG_WARNING = true;
    public static boolean LOG_ERROR = true;

    public static boolean SHOW_LOGCAT = true;

    protected static boolean mIsDebugMode = true;

    // 보안상의 목적 또는 기타 사유로 사용자 DDMS에는 log 메시지를 출력하지 않으면서
    // 필요에 따라서 내부적으로 log message를 수집하기 위해서 queue를 관리한다.
    protected static LinkedList<LogItem> mLogMsgQueue = new LinkedList<LogItem>();
    protected static int mMaxQueueSize = -1;
    protected static boolean mIsUsingQueue = false;

    /**
     * @return 현재 Log 출력이 가능한 상태인지 flag
     */
    public static boolean isDebugMode() {
        return mIsDebugMode;
    }

    /**
     * Debug Mode를 끄면 모든 Log 메시지 출력을 하지 않는다.
     *
     * @param isDebug
     */
    public static void setDebugMode(boolean isDebug) {
        mIsDebugMode = isDebug;
    }

    /**
     * setMaxQueueSize()를 설정하면 모든 출력되는 Log를 Queue에 쌓게 되는데, DDMS logcat으로 실제 logcat message를 출력할지 여부를 결정한다.
     * 사용자에게는 logcat 메시지를 보여주지 않으면서 내부적으로 log를 관리하는 용도로 사용한다.
     *
     * @param isShow DDMS logcat message를 출력할지 여부
     */
    public static void showLogcat(boolean isShow) {
        SHOW_LOGCAT = isShow;
    }

    public static void setLogTag(String tag) {
        TAG = tag;
    }

    public static void setMaxQueueSize(int max) {
        mIsUsingQueue = (max > 0);
        mMaxQueueSize = max;
        if (max < 0) {
            mLogMsgQueue.clear();
        }
    }

    // only for compatibility with ib.android.util.Log class
    public static void v(String tag, String log) {
        if (mIsDebugMode && LOG_VERBOSE) {
            String realLog = getPrefix(tag) + log;
            if (SHOW_LOGCAT) android.util.Log.v(tag, realLog);
            addQueue(tag, realLog);
        }
    }

    public static void i(String tag, String log) {
        if (mIsDebugMode && LOG_INFO) {
            String realLog = getPrefix(tag) + log;
            if (SHOW_LOGCAT) android.util.Log.i(tag, realLog);
            addQueue(tag, realLog);
        }
    }

    public static void d(String tag, String log) {
        if (mIsDebugMode && LOG_DEBUG) {
            String realLog = getPrefix(tag) + log;
            if (SHOW_LOGCAT) android.util.Log.d(tag, realLog);
            addQueue(tag, realLog);
        }
    }

    public static void w(String tag, String log) {
        if (mIsDebugMode && LOG_WARNING) {
            String realLog = getPrefix(tag) + log;
            if (SHOW_LOGCAT) android.util.Log.w(tag, realLog);
            addQueue(tag, realLog);
        }
    }

    public static void e(String tag, String log) {
        if (mIsDebugMode && LOG_ERROR) {
            String realLog = getPrefix(tag) + log;
            if (SHOW_LOGCAT) android.util.Log.e(tag, realLog);
            addQueue(tag, realLog);
        }
    }

    public static void v(String log) {
        if (mIsDebugMode && LOG_VERBOSE) {
            String tag = getClassName(4);
            if (TextUtils.isEmpty(tag)) tag = TAG;
            String realLog = "[" + getLineNumber(DEPTH_OF_STACK) + "] " + log;
            if (SHOW_LOGCAT) android.util.Log.v(tag, realLog);
            addQueue(tag, realLog);
        }
    }

    public static void i(String log) {
        if (mIsDebugMode && LOG_INFO) {
            String tag = getClassName(4);
            if (TextUtils.isEmpty(tag)) tag = TAG;
            String realLog = "[" + getLineNumber(DEPTH_OF_STACK) + "] " + log;
            if (SHOW_LOGCAT) android.util.Log.i(tag, realLog);
            addQueue(tag, realLog);
        }
    }

    public static void d(String log) {
        if (mIsDebugMode && LOG_DEBUG) {
            String tag = getClassName(4);
            if (TextUtils.isEmpty(tag)) tag = TAG;
            String realLog = "[" + getLineNumber(DEPTH_OF_STACK) + "] " + log;
            if (SHOW_LOGCAT) android.util.Log.d(tag, realLog);
            addQueue(tag, realLog);
        }
    }

    public static void w(String log) {
        if (mIsDebugMode && LOG_WARNING) {
            String tag = getClassName(4);
            if (TextUtils.isEmpty(tag)) tag = TAG;
            String realLog = "[" + getLineNumber(DEPTH_OF_STACK) + "] " + log;
            if (SHOW_LOGCAT) android.util.Log.w(tag, realLog);
            addQueue(tag, realLog);
        }
    }

    public static void e(String log) {
        if (mIsDebugMode && LOG_ERROR) {
            String tag = getClassName(4);
            if (TextUtils.isEmpty(tag)) tag = TAG;
            String realLog = "[" + getLineNumber(DEPTH_OF_STACK) + "] " + log;
            if (SHOW_LOGCAT) android.util.Log.e(tag, realLog);
            addQueue(tag, realLog);
        }
    }

    public static void vv(Object... args) {
        if (mIsDebugMode && LOG_VERBOSE) {
            String tag = getClassName(4);
            if (TextUtils.isEmpty(tag)) tag = TAG;
            String realLog = "[" + getLineNumber(DEPTH_OF_STACK) + "] " + buildVariableArguments(args);
            if (SHOW_LOGCAT) android.util.Log.v(tag, realLog);
            addQueue(tag, realLog);
        }
    }

    public static void ii(Object... args) {
        if (mIsDebugMode && LOG_INFO) {
            String tag = getClassName(4);
            if (TextUtils.isEmpty(tag)) tag = TAG;
            String realLog = "[" + getLineNumber(DEPTH_OF_STACK) + "] " + buildVariableArguments(args);
            if (SHOW_LOGCAT) android.util.Log.i(tag, realLog);
            addQueue(tag, realLog);
        }
    }

    public static void dd(Object... args) {
        if (mIsDebugMode && LOG_DEBUG) {
            String tag = getClassName(4);
            if (TextUtils.isEmpty(tag)) tag = TAG;
            String realLog = "[" + getLineNumber(DEPTH_OF_STACK) + "] " + buildVariableArguments(args);
            if (SHOW_LOGCAT) android.util.Log.d(tag, realLog);
            addQueue(tag, realLog);
        }
    }

    public static void ww(Object... args) {
        if (mIsDebugMode && LOG_WARNING) {
            String tag = getClassName(4);
            if (TextUtils.isEmpty(tag)) tag = TAG;
            String realLog = "[" + getLineNumber(DEPTH_OF_STACK) + "] " + buildVariableArguments(args);
            if (SHOW_LOGCAT) android.util.Log.w(tag, realLog);
            addQueue(tag, realLog);
        }
    }

    public static void ee(Object... args) {
        if (mIsDebugMode && LOG_ERROR) {
            String tag = getClassName(4);
            if (TextUtils.isEmpty(tag)) tag = TAG;
            String realLog = "[" + getLineNumber(DEPTH_OF_STACK) + "] " + buildVariableArguments(args);
            if (SHOW_LOGCAT) android.util.Log.e(tag, realLog);
            addQueue(tag, realLog);
        }
    }

    public static void e(Throwable e) {
        if (mIsDebugMode) {
            e(getStringFromThrowable(e));
        }
    }

    public static String getStringFromThrowable(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public static String buildVariableArguments(Object... agrs) {
        StringBuilder sb = new StringBuilder();
        // sb.append(" [").append(Thread.currentThread().getId()).append("] ");
        for (Object item : agrs) {
            sb.append(item);
        }
        return sb.toString();
    }

    protected static void addQueue(String tag, String msg) {
        if (!mIsUsingQueue) return;

        mLogMsgQueue.add(new LogItem(tag, msg));
        while (mLogMsgQueue.size() > mMaxQueueSize) {
            mLogMsgQueue.poll();
        }
    }

    protected static String getPrefix(String tag) {
        String prefix = getClassNameAndLineNumber(DEPTH_OF_STACK + 1);
        if (!TextUtils.isEmpty(tag) && tag.equals(getClassName(DEPTH_OF_STACK + 1))) {
            prefix = "[" + getLineNumber(DEPTH_OF_STACK + 1) + "] ";
        }

        return prefix;
    }

    public static void fileLog(String log) {
        Date now = new Date();

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd,HH:mm:ss.SSS");
        String temp_log = format.format(now) + "    " + log + "\n";

        format = new SimpleDateFormat("yyyyMMdd");
        String fileName = TAG + format.format(now) + ".txt";
        FileOutputStream fos = null;
        try {
            File root = Environment.getExternalStorageDirectory();

            if (root.canWrite()) {
                fos = new FileOutputStream(new File(root, fileName), true);
                fos.write(temp_log.getBytes());
                fos.close();
                fos.flush();
            }
        } catch (IOException e) {
            Log.e(e);
            StackTraceElement[] elem = e.getStackTrace();
            for (int i = 0; i < elem.length; i++) {
                android.util.Log.e(TAG, "fileLog() -> " + elem[i]);
            }
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                Log.e(e);
            }
        }
    }

    public static String getClassNameAndLineNumber(int depth) {
        final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        StringBuffer tempBuf = new StringBuffer();

        if (depth < 0 || depth >= stack.length) {
            android.util.Log.e(TAG, "Invalid depth of call stack! - " + depth);
            return "";
        }

        String[] temp = stack[depth].getClassName().split("\\.");
        tempBuf.append("[" + temp[temp.length - 1]);
        tempBuf.append(":" + stack[depth].getLineNumber() + "] ");

        return tempBuf.toString();
    }

    public static String getClassName(int depth) {
        final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        String[] temp = stack[depth].getClassName().split("\\.");
        return temp[temp.length - 1];
    }

    public static int getLineNumber(int depth) {
        final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        return stack[depth].getLineNumber();
    }

    public static String getMethodName(int depth) {
        final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        return stack[depth].getMethodName();
    }

    public static String getAppDefaultStoragePath(String packageName) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + packageName;
    }

    public static String getCaller() {
        int depth = DEPTH_OF_STACK;

        final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        String[] temp = stack[depth].getClassName().split("\\.");

        String className = temp[temp.length - 1];
        int lineNumber = stack[depth].getLineNumber();

        /* // FIXME: Debbug purpose only
        int count = 0;
        for(StackTraceElement e : stack) {
            Log.e("[" + count++ + "] " + e.getClassName() + ", " + e.getMethodName() + ", " + e.getLineNumber());
        }*/

        return "[" + className + ":" + lineNumber + "]";
    }

    public static String getCallerClassName() {
        int depth = DEPTH_OF_STACK;

        final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        String[] temp = stack[depth].getClassName().split("\\.");
        return temp[temp.length - 1];
    }

    public static void catchException(Exception e) {
        e.printStackTrace();
    }

    public static void logException(Context context, Object... args) {
        String tag = getClassName(4);
        if (TextUtils.isEmpty(tag)) tag = TAG;
        String realLog = "[" + getLineNumber(DEPTH_OF_STACK) + "] " + buildVariableArguments(args);
        if (SHOW_LOGCAT) android.util.Log.e(tag, realLog);
        addQueue(tag, realLog);
    }

    public static void saveQueuedMessage(Context context) {
        saveQueuedMessage(context, "");
    }

    public static void saveQueuedMessage(Context context, String postFix) {
        android.util.Log.e(TAG, "saveQueuedMessage mLogMsgQueue.size(): " + mLogMsgQueue.size());

        String cacheDir = getApplicationCacheDir(context);
        File dir = new File(cacheDir);
        boolean success = dir.mkdirs();
        if (!dir.exists() && !success) {
            Log.e("Can't create base directory: " + dir.getPath());
        }

        String filename = getFilenameByDate(null, postFix, ".txt");
        File file = new File(dir, filename);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            while (mLogMsgQueue.size() > 0) {
                try {
                    LogItem item = mLogMsgQueue.poll();
                    if (item == null) continue;

                    // Write the string to the file
                    osw.write(item.mTag + " " + item.mMsg + "\n");
                } catch (NoSuchElementException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            osw.flush();
            osw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        android.util.Log.e(TAG, "saveQueuedMessage done!");
    }

    public static void saveLogcatMessage(Context context, String postFix) {
        boolean has_permission = true;

        /*
          JellyBean 부터는 READ_LOG 퍼미션이 필요 없고, 다른 application의 log를 읽을 수 없다.
          http://stackoverflow.com/questions/16795582/not-all-data-shown-when-android-logcat-is-read-programatically/16795874#16795874
         */
        int res = context.getApplicationContext().checkCallingOrSelfPermission("android.permission.READ_LOGS");
        if (res != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            String tag = getClassName(4);
            if (TextUtils.isEmpty(tag)) tag = TAG;
            String realLog = "[" + getLineNumber(DEPTH_OF_STACK) + "] " + "android.permission.READ_LOGS permission is missing!";
            android.util.Log.e(tag, realLog);
            addQueue(tag, realLog);

            has_permission = false;
        }

        res = context.getApplicationContext().checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
        if (res != PackageManager.PERMISSION_GRANTED) {
            String tag = getClassName(4);
            if (TextUtils.isEmpty(tag)) tag = TAG;
            String realLog = "[" + getLineNumber(DEPTH_OF_STACK) + "] " + "android.permission.WRITE_EXTERNAL_STORAGE permission is missing!";
            android.util.Log.e(tag, realLog);
            addQueue(tag, realLog);

            has_permission = false;
        }

        if (has_permission) {
            saveLogcatMessage(getAppDefaultStoragePath(context.getPackageName()), postFix);
        } else {
            android.util.Log.e(TAG, "Saving logcat message to external storage is FAIL!!");
        }
    }

    public static void saveLogcatMessage(String path, String postFix) {
        try {
            Process process = Runtime.getRuntime().exec("logcat -v time -d");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));

            StringBuilder log = new StringBuilder();
            String line = "";
            // String separator = System.getProperty("line.separator");
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line);
                log.append("\r\n");
            }

            // to create a Text file name "logcat.txt" in SDCard
            if (log != null && log.toString().length() > 0) {
                String ext_path = getExternalStoragePath(path);
                File dir = new File(ext_path);
                boolean success = dir.mkdirs();
                if (!dir.exists() && !success) {
                    Log.e("Can't create base directory: " + dir.getPath());
                }
                String filename = getFilenameByDate(null, postFix, ".txt");
                File file = new File(dir, filename);
                // to write logcat in text file
                FileOutputStream fOut = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(fOut);

                // Write the string to the file
                osw.write(log.toString());
                osw.flush();
                osw.close();
            } else {
                Log.e("Could not capture logcat text.. Check 'android.permission.READ_LOGS' in your AndroidManifest.xml");
            }

            limitCheckNRemove();
            // i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new
            // File(Environment.getExternalStorageDirectory() +
            // DATA_STORAGE_PATH_BASE + "/logcat","file name")));
        } catch (FileNotFoundException e) {
            Log.e(e);
        } catch (IOException e) {
            Log.e(e);
        }
    }

    public static String getApplicationCacheDir(Context context) {
        boolean success = true;
        String cache_dir = getExternalStoragePath() + "/Android/data" + File.separator + context.getPackageName();

        File file = new File(cache_dir);
        if (!file.exists()) {
            success = file.mkdirs();
        }

        if (!success) {
            cache_dir = null;
        }

        return cache_dir;
    }

    public static String getExternalStoragePath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    public static String getExternalStoragePath(String default_path) {
        String ext_path = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (default_path.startsWith(ext_path)) {
            ext_path = default_path;
        } else {
            if (!default_path.startsWith(File.separator)) default_path = File.separator + default_path;
            ext_path = Environment.getExternalStorageDirectory().getAbsolutePath() + default_path;
        }

        return ext_path;
    }

    public static String getFilenameByDate(String prefix, String postfix, String ext) {
        if (prefix == null)
            prefix = "";
        if (postfix == null)
            postfix = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss.SSS");
        String filename = prefix + sdf.format(new Date()) + postfix + ext;
        filename = filename.replace(" ", "_");
        filename = filename.replace(":", "-");

        return filename;
    }

    /**
     * Features about file write
     */
    private static final int MAX_COUNT_FILE_LOG = 10;
    /*private static SimpleDateFormat sdf = new SimpleDateFormat();

	private static String getDateString() {
		//2014.03.24 JuL StringIndexOutOfBoundsException 에러에 대한 대응. sdf가 thread safe 하지 않아서 pattern 충돌이 나는 듯 함.
		synchronized(sdf){
			long timemillis = System.currentTimeMillis();
			Date date = new Date(timemillis);
			sdf.applyPattern("yyyyMMdd");
			return sdf.format(date);
		}
	}

	private static void appendLogToFile(String text) {
		//2014.03.24 JuL StringIndexOutOfBoundsException 에러에 대한 대응. sdf가 thread safe 하지 않아서 pattern 충돌이 나는 듯 함.
		synchronized(sdf){
			File folder = new File(IBNConstants.MN_TONG_LOG_PATH);
			if (folder.exists() == false) {
				folder.mkdirs();
			}
			File logFile = new File(IBNConstants.MN_TONG_LOG_PATH + "/log_"
					+ getDateString() + ".txt");
			if (!logFile.exists()) {
				try {
					logFile.createNewFile();
				} catch (IOException e) {
				    e.printStackTrace();
				}
				limitCheckNRemove();
			}
			try {
				// BufferedWriter for performance, true to set append to file flag
				BufferedWriter buf = new BufferedWriter(new FileWriter(logFile,
						true));
				long curTime = System.currentTimeMillis();
				Date d = new Date(curTime);
				sdf.applyPattern("HH:mm:ss.SSS");
				String timeStr = sdf.format(d);
				buf.append(timeStr);
				buf.append(" : ");
				buf.append(text);
				buf.newLine();
				buf.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (StringIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}
	}*/

    private static void limitCheckNRemove() {
        String ext_path = getExternalStoragePath("log");
        File root = new File(ext_path);
        File[] files = root.listFiles();

        if (files != null && files.length > MAX_COUNT_FILE_LOG) {
            ArrayList<File> file_list = new ArrayList<File>();
            for (File f : files) {
                file_list.add(f);
            }
            Collections.sort(file_list, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    return Long.valueOf(f2.lastModified()).compareTo(
                            f1.lastModified());
                }
            });

            for (int i = file_list.size() - 1; i >= MAX_COUNT_FILE_LOG; i--) {
                File f = file_list.get(i);
                f.delete();
            }
        }
    }

    public static String getPhoneState(Context context) {
        StringBuffer log = new StringBuffer();

//        TelephonyManager tManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
//        log.append("phone number: " + tManager.getLine1Number() + " \r\n");

        ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        log.append("mobile network connected: " + mobile.isConnected() + " \r\n");
        log.append("wifi network connected: " + wifi.isConnected() + " \r\n");

        return log.toString();
    }

    public static String formatLog(Object... message) {
        StringBuilder sb = new StringBuilder();
        sb.append(" [")
                .append(Thread.currentThread().getId())
                .append("] ");
        for (Object item : message) {
            sb.append(item);
        }
        return sb.toString();
    }

    protected static class LogItem {
        public String mTag;
        public String mMsg;

        public LogItem(String tag, String msg) {
            mTag = tag;
            mMsg = msg;
        }
    }
}
