package hu.blog.megosztanam.model.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

import hu.blog.megosztanam.model.shared.LoginResponse;
import hu.blog.megosztanam.model.shared.LoginStatus;
import hu.blog.megosztanam.model.shared.User;

/**
 * Created by Miklós on 2017. 04. 22..
 */
public class ParcelableLoginResponse extends LoginResponse implements Parcelable {
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(new ParcelableUser(this.getUser()), flags);
        dest.writeInt(this.getLoginStatus() == null ? -1 : this.getLoginStatus().ordinal());
    }

    public ParcelableLoginResponse() {
    }

    public ParcelableLoginResponse(LoginResponse response) {
        this.user = response.getUser();
        this.loginStatus = response.getLoginStatus();
    }

    protected ParcelableLoginResponse(Parcel in) {
        this.user = in.readParcelable(User.class.getClassLoader());
        int tmpLoginStatus = in.readInt();
        this.loginStatus = tmpLoginStatus == -1 ? null : LoginStatus.values()[tmpLoginStatus];
    }

    public static final Creator<ParcelableLoginResponse> CREATOR = new Creator<ParcelableLoginResponse>() {
        @Override
        public ParcelableLoginResponse createFromParcel(Parcel source) {
            return new ParcelableLoginResponse(source);
        }

        @Override
        public ParcelableLoginResponse[] newArray(int size) {
            return new ParcelableLoginResponse[size];
        }
    };

    @Override
    public String toString() {
        return "ParcelableLoginResponse{" +
                "user=" + user +
                ", loginStatus=" + loginStatus +
                '}';
    }
}
