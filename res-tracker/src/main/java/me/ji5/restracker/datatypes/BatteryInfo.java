/*
 * Project: android-ngm-gradle
 * module: ibiz-test-main
 *
 * Copyright (C) 2013~2014, Infobank. Corp. All Right Reserved.
 *
 * DO NOT COPY OR DISTRIBUTE WITHOUT PERMISSION OF THE AUTHOR
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 * Revision History
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
 * 2014/09/17 ohjongin 1.0.0 Initial creation with these template
 *
 * last modified : 2014/09/17 03:41PM
 */

package me.ji5.restracker.datatypes;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * Describe about this class here...
 *
 * @author ohjongin
 * @since 1.0
 * 14. 9. 17
 */
@RealmClass
public class BatteryInfo extends RealmObject {
    private int plugType;
    private int level;
    private int scale;
    private int voltage;
    private int health;
    private float temperature;
    private int status;
    private String technology;
    private boolean isPresent;

    public BatteryInfo(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        fromIntent(intent);
    }

    public BatteryInfo(Intent intent) {
        fromIntent(intent);
    }

    public void fromIntent(Intent intent) {
        plugType = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
        level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
        temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10;
        health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN);
        status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
        technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
        isPresent = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
    }

    public int getPlugType() {
        return plugType;
    }

    public int getLevel() {
        return level;
    }

    public int getScale() {
        return scale;
    }

    public int getVoltage() {
        return voltage;
    }

    public int getHealthState() {
        return health;
    }

    public int getStatus() {
        return status;
    }

    public String getTechnology() {
        return technology;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public String getStatusString() {
        String statusString = "Unknown";

        if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
            statusString = ("Charging");
            if (plugType > 0) {
                statusString = statusString + " " + (
                        (plugType == BatteryManager.BATTERY_PLUGGED_AC)
                                ? "(AC)"
                                : "(USB)");
            }
        } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
            statusString = ("Discharging");
        } else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
            statusString = ("Not charging");
        } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
            statusString = ("Full");
        }

        return statusString;
    }

    public String getPlugTypeName() {
        String strPlug = "Unknown";

        switch (plugType) {
            case 0:
                strPlug = "Unplugged";
                break;
            case BatteryManager.BATTERY_PLUGGED_AC:
                strPlug = "AC";
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                strPlug = "USB";
                break;
            case (BatteryManager.BATTERY_PLUGGED_AC | BatteryManager.BATTERY_PLUGGED_USB):
                strPlug = "AC and USB";
                break;
            default:
                strPlug = "Unknown";
                break;
        }

        return strPlug;
    }

    public String getHealthString() {
        String healthString = "Unknown";

        if (health == BatteryManager.BATTERY_HEALTH_GOOD) {
            healthString = "Good";
        } else if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT) {
            healthString = "OverHeat";
        } else if (health == BatteryManager.BATTERY_HEALTH_DEAD) {
            healthString = "Dead";
        } else if (health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE) {
            healthString = "Over voltage";
        } else if (health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE) {
            healthString = "Unknown error";
        } else if (health == BatteryManager.BATTERY_HEALTH_COLD) {
            healthString = "Cold";
        }

        return healthString;
    }

    public float getScaledLevel() {
        // Error checking that probably isn't needed but I added just in case.
        if (level == -1 || scale == -1 || scale == 0) {
            return 50.0f;
        }

        return ((float) level * 100.0f / (float) scale);
    }

    public String toString() {
        String batteryInfo;
        batteryInfo = "\nBattery Voltage : " + voltage + " mV\n";
        batteryInfo += "Battery Level : " + level + "\n";
        batteryInfo += "Battery Scale : " + scale + "\n";

        batteryInfo += String.format("Battery Charge Status: %d%% ", (level * 100 / scale));
        batteryInfo += getStatusString() + "\n";
        batteryInfo += "Battery Temperature : " + temperature + "˚C \n";
        batteryInfo += "Battery technology : " + technology + "\n";

        batteryInfo += "Battery PlugType : " + getPlugTypeName() + "\n";
        batteryInfo += "Battery Health : " + getHealthString() + "\n";

        return batteryInfo;
    }
}
