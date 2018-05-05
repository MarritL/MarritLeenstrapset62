package marrit.marritleenstra_pset62;

import java.io.Serializable;


/**
 * Created by Marrit on 4-12-2017.
 * Class containing all User data
 */

public class User implements Serializable{

    private String mUID;
    private String mEmail;
    private String mDisplayName;
    private int mRunStreak;
    private int mDaysVegetarian;
    private double mCO2Avoided;
    private double mAnimalsSaved;
    private Boolean mClickedToday;
    private Boolean mOnLaunch;

    // default constructor for FireBase
    public User() {}

    // constructor
    public User(String UID, String email, String displayName) {
        mUID = UID;
        mEmail = email;
        mDisplayName = displayName;
        mRunStreak = 0;
        mDaysVegetarian = 0;
        mAnimalsSaved = 0.0;
        mCO2Avoided = 0.0;
        mClickedToday = false;
        mOnLaunch = true;
    }

    // getters and setters
    public String getUID() {
        return mUID;
    }

    public void setUID(String UID) {
        mUID = UID;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String displayName) {
        mDisplayName = displayName;
    }

    public int getRunStreak() {
        return mRunStreak;
    }

    public void setRunStreak(int runStreak) {
        mRunStreak = runStreak;
    }

    public int getDaysVegetarian() {
        return mDaysVegetarian;
    }

    public void setDaysVegetarian(int daysVegetarian) {
        mDaysVegetarian = daysVegetarian;
    }

    public double getAnimalsSaved() {
        return mAnimalsSaved;
    }

    public void setAnimalsSaved(double animalsSaved) {
        mAnimalsSaved = animalsSaved;
    }

    public double getCO2Avoided() {
        return mCO2Avoided;
    }

    public void setCO2Avoided(double CO2Avoided) {
        mCO2Avoided = CO2Avoided;
    }

    public Boolean getClickedToday() {
        return mClickedToday;
    }

    public void setClickedToday(Boolean clickedToday) {
        mClickedToday = clickedToday;
    }

    public Boolean getOnLaunch() {
        return mOnLaunch;
    }

    public void setOnLaunch(Boolean onLaunch) {
        mOnLaunch = onLaunch;
    }
}
