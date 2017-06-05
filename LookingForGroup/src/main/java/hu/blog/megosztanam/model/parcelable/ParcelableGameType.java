package hu.blog.megosztanam.model.parcelable;

import android.os.Parcel;
import android.os.Parcelable;
import hu.blog.megosztanam.model.shared.GameMap;
import hu.blog.megosztanam.model.shared.GameType;

/**
 * Created by Miklós on 2017. 04. 22..
 */
public class ParcelableGameType extends GameType implements Parcelable {
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.getMap() == null ? -1 : this.getMap().ordinal());
        dest.writeByte(this.isRanked() ? (byte) 1 : (byte) 0);
    }

    public ParcelableGameType() {
    }

    public ParcelableGameType(GameType gameType) {
        this.setRanked(gameType.isRanked());
        this.setMap(gameType.getMap());
    }

    protected ParcelableGameType(Parcel in) {
        int tmpMap = in.readInt();
        this.setMap(tmpMap == -1 ? null : GameMap.values()[tmpMap]);
        this.setRanked(in.readByte() != 0);
    }

    public static final Creator<ParcelableGameType> CREATOR = new Creator<ParcelableGameType>() {
        @Override
        public ParcelableGameType createFromParcel(Parcel source) {
            return new ParcelableGameType(source);
        }

        @Override
        public ParcelableGameType[] newArray(int size) {
            return new ParcelableGameType[size];
        }
    };
}
