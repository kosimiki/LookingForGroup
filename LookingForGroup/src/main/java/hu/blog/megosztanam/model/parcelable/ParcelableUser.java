package hu.blog.megosztanam.model.parcelable;

import android.os.Parcel;
import android.os.Parcelable;
import hu.blog.megosztanam.model.shared.Summoner;
import hu.blog.megosztanam.model.shared.User;

/**
 * Created by Miklós on 2017. 04. 22..
 */
public class ParcelableUser extends User implements Parcelable {
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.userId);
        dest.writeString(this.idTokenString);
        dest.writeString(this.email);
        dest.writeString(this.givenName);
        dest.writeString(this.profilePictureUrl);
        dest.writeValue(this.authenticated);
        dest.writeParcelable(new ParcelableSummoner(this.summoner), flags);
        dest.writeString(this.idToken);
    }

    public ParcelableUser(User user){
        this.userId = user.getUserId();
        this.idTokenString = user.getIdTokenString();
        this.email = user.getEmail();
        this.givenName = user.getGivenName();
        this.profilePictureUrl = user.getProfilePictureUrl();
        this.authenticated = user.getAuthenticated();
        this.summoner = user.getSummoner();
        this.idToken = user.getIdToken();
    }
    public ParcelableUser() {
    }

    protected ParcelableUser(Parcel in) {
        this.userId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.idTokenString = in.readString();
        this.email = in.readString();
        this.givenName = in.readString();
        this.profilePictureUrl = in.readString();
        this.authenticated = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.summoner = in.readParcelable(Summoner.class.getClassLoader());
        this.idToken = in.readString();
    }

    public static final Creator<ParcelableUser> CREATOR = new Creator<ParcelableUser>() {
        @Override
        public ParcelableUser createFromParcel(Parcel source) {
            return new ParcelableUser(source);
        }

        @Override
        public ParcelableUser[] newArray(int size) {
            return new ParcelableUser[size];
        }
    };
}
