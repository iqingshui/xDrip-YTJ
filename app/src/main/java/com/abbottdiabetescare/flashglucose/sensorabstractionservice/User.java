package com.abbottdiabetescare.flashglucose.sensorabstractionservice;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes.dex */
public final class User implements Parcelable {
    public static Parcelable.Creator<User> CREATOR;
    private final String name;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    User(String str) {
        this.name = str;
    }

    public String getName() {
        return this.name;
    }

    public int hashCode() {
        String str = this.name;
        return (str == null ? 0 : str.hashCode()) + 31;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && getClass() == obj.getClass()) {
            User user = (User) obj;
            String str = this.name;
            return str == null ? user.name == null : str.equals(user.name);
        }
        return false;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
    }
}
