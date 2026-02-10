import android.os.Parcel;
import android.os.Parcelable;

public class Model implements Parcelable {
    private String id;
    private String name;
    private String type;
    private String participants;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;

    // Constructor
    public Model(String id, String name, String type, String participants, String startDate, String endDate, String startTime, String endTime) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.participants = participants;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters
    public String getId() { return id; }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getParticipants() { return participants; }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    // Parcelable implementation
    protected Model(Parcel in) {
        id = in.readString();
        name = in.readString();
        type = in.readString();
        participants = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        startTime = in.readString();
        endTime = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(participants);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeString(startTime);
        dest.writeString(endTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Model> CREATOR = new Creator<Model>() {
        @Override
        public Model createFromParcel(Parcel in) {
            return new Model(in);
        }

        @Override
        public Model[] newArray(int size) {
            return new Model[size];
        }
    };
}
