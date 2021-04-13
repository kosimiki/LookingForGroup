package hu.blog.megosztanam.model.parcelable;

import android.os.Parcel;
import android.os.Parcelable;
import hu.blog.megosztanam.model.shared.Summoner;

/**
 * Created by Miklós on 2017. 04. 22..
 */
public class ParcelableSummoner extends Summoner implements Parcelable {
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeValue(this.profileIconId);
        dest.writeValue(this.summonerLevel);
    }
    public ParcelableSummoner(Summoner summoner) {
        this.id = summoner.getId();
        this.name = summoner.getName();
        this.profileIconId = summoner.getProfileIconId();
        this.summonerLevel = summoner.getSummonerLevel();
    }

    public ParcelableSummoner() {
    }

    protected ParcelableSummoner(Parcel in) {
        this.id = (String) in.readValue(String.class.getClassLoader());
        this.name = in.readString();
        this.profileIconId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.summonerLevel = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<ParcelableSummoner> CREATOR = new Creator<ParcelableSummoner>() {
        @Override
        public ParcelableSummoner createFromParcel(Parcel source) {
            return new ParcelableSummoner(source);
        }

        @Override
        public ParcelableSummoner[] newArray(int size) {
            return new ParcelableSummoner[size];
        }
    };
}
