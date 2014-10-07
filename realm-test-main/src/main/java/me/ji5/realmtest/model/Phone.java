package me.ji5.realmtest.model;

import io.realm.RealmObject;

/**
 * Describe about this class here...
 *
 * @author ohjongin
 * @since 1.0
 * 14. 10. 7
 */
public class Phone extends RealmObject {
    private String phoneNumber;
    private int phoneType;

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public int getPhoneType() { return phoneType; }
    public void setPhoneType(int phoneType) { this.phoneType = phoneType; }
}
