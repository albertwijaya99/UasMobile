package id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class User implements Parcelable{
    private String name;
    private String position;
    private String subject;
    private String bio;
    private String oblTo;
    private String classes;
    private int salary;
    private String email;
    private String phone;
    private String uri;

    public User(){}

    public User( String name, String position, String subject, String bio, String oblTo, String classes, int salary, String email, String phone, String uri) {
        this.name = name;
        this.position = position;
        this.subject = subject;
        this.bio = bio;
        this.oblTo = oblTo;
        this.classes = classes;
        this.salary = salary;
        this.email = email;
        this.phone = phone;
        this.uri = uri;
    }


    protected User(Parcel in) {
        name = in.readString();
        position = in.readString();
        subject = in.readString();
        bio = in.readString();
        oblTo = in.readString();
        classes = in.readString();
        salary = in.readInt();
        email = in.readString();
        phone = in.readString();
        uri = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getName(){return this.name;}
    public void setName(String name){this.name = name;}

    public String getPosition(){return this.position;}
    public void setPosition(String position){this.position = position;}

    public String getSubject(){return this.subject;}
    public void setSubject(String subject){this.subject = subject;}

    public String getBio(){return this.bio;}
    public void setBio(String bio){this.bio = bio;}

    public String getOblTo(){return this.oblTo;}
    public void setOblTo(String oblTo){this.oblTo = oblTo;}

    public String getClasses(){return this.classes;}
    public void setClasses(String classes){this.classes = classes;}

    public int getSalary(){return this.salary;}
    public void setSalary(int salary){this.salary = salary;}

    public String getEmail(){return this.email;}
    public void setEmail(String email){this.email = email;}

    public String getPhone(){return this.phone;}
    public void setPhone(String phone){this.phone = phone;}

    public String getUri(){return this.uri;}
    public void setUri(String uri){this.uri = uri;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(position);
        dest.writeString(subject);
        dest.writeString(bio);
        dest.writeString(oblTo);
        dest.writeString(classes);
        dest.writeInt(salary);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(uri);
    }
}
