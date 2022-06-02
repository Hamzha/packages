
package com.smart.agriculture.industrial.solutions.packages.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserAttributes implements Parcelable {

    @SerializedName("roles")
    @Expose
    private String roles;
    public final static Parcelable.Creator<UserAttributes> CREATOR = new Creator<UserAttributes>() {


        @SuppressWarnings({
                "unchecked"
        })
        public UserAttributes createFromParcel(Parcel in) {
            return new UserAttributes(in);
        }

        public UserAttributes[] newArray(int size) {
            return (new UserAttributes[size]);
        }

    }
            ;

    protected UserAttributes(Parcel in) {
        this.roles = ((String) in.readValue((String.class.getClassLoader())));
    }

    public UserAttributes() {
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(roles);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "UserAttributes{" +
                "roles='" + roles + '\'' +
                '}';
    }
}
