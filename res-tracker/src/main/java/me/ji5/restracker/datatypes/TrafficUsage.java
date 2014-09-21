package me.ji5.restracker.datatypes;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.text.TextUtils;

import java.util.List;

import io.realm.RealmObject;
import me.ji5.restracker.utils.Log;

/**
 * Describe about this class here...
 *
 * @author ohjongin
 * @since 1.0
 * 14. 9. 17
 */
public class TrafficUsage extends RealmObject {
    protected Context mContext;
    protected String mPackageName;
    protected int mUid;
    protected long mRxUsage;
    protected long mTxUsage;

    public TrafficUsage(Context context) {
        initialize(context, context.getPackageName());
    }

    public TrafficUsage(Context context, String packageName) {
        initialize(context, packageName);
    }

    protected void initialize(Context context, String packageName) {
        mContext = context;
        mPackageName = packageName;

        if (TextUtils.isEmpty(mPackageName)) {
            Log.e("Package name is NULL!!");
        }

        mUid = findUid(context);
    }

    public int getUid() {
        return mUid;
    }

    public int findUid(Context context) {
        int uid = -1;

        PackageManager pm = context.getPackageManager();
        // get a list of installed apps.
        List<ApplicationInfo> appInfoList = pm.getInstalledApplications(0);

        // loop through the list of installed packages and see if the selected
        // app is in the list
        for (ApplicationInfo ai : appInfoList) {
            if (!mPackageName.equals(ai.packageName)) {
                continue;
            } else {
                uid = ai.uid;
                break;
            }
        }

        return uid;
    }

    public TrafficUsage calcUsage() {
        if (TextUtils.isEmpty(mPackageName)) {
            Log.e("Package name is NULL!!");
            return this;
        }

        if (mUid < 0) {
            Log.e("There is no proper UID(" + mUid + ")");
            return this;
        }

        mRxUsage = TrafficStats.getUidRxBytes(mUid);
        mTxUsage = TrafficStats.getUidTxBytes(mUid);

        Log.d("TrafficUsage [" + mPackageName + "] - Rx:" + mRxUsage + ", Tx:" + mTxUsage);

        return this;
    }

    public long getRxUsage() {
        return mRxUsage;
    }

    public long getTxUsage() {
        return mTxUsage;
    }
}
