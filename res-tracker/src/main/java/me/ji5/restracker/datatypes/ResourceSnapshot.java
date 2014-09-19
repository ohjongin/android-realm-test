package me.ji5.restracker.datatypes;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * Describe about this class here...
 *
 * @author ohjongin
 * @since 1.0
 * 14. 9. 18
 */
@RealmClass
public class ResourceSnapshot extends RealmObject {
    protected long RxUsage;
    protected long TxUsage;
    protected float Level;
    protected long Timestamp;

    public long getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(long timestamp) {
        Timestamp = timestamp;
    }

    public long getRxUsage() {
        return RxUsage;
    }

    public void setRxUsage(long rxUsage) {
        RxUsage = rxUsage;
    }

    public long getTxUsage() {
        return TxUsage;
    }

    public void setTxUsage(long txUsage) {
        TxUsage = txUsage;
    }


    public float getLevel() {
        return Level;
    }

    public void setLevel(float level) {
        Level = level;
    }
}
