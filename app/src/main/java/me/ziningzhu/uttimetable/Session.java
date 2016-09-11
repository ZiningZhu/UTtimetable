package me.ziningzhu.uttimetable;

import android.util.Log;

import java.util.ArrayList;

/**
 * An instance of Session represents a session.
 */
public class Session implements java.io.Serializable{

    private final String TAG = "Session.java";
    private boolean mSelected;

    private String mCourse_name; // "CSC108H1F"

    private String mSection_code; // "F"

    private String mLocation; // "BA 1106"

    private String mSession_name; // "LEC0101"

    private String mCourse_time;
    /* "ONLINE" or "M3-5" or "MWF3-5". Note there may be different sessions having the same session name.
     * These are going to be handled separately (aka. drawn into different cells)
     * For each session, the String mCourse_time has two components.
     * String component indicates the day(s) in the week;
     * digit-with-hyphen component indicates the time.*/



    private int mStartTime;
    private int mEndTime;

    private String mProf_name;

    public Session() {
        mCourse_name = null;
        mLocation = null;
        mCourse_time = null;
        mProf_name = null;
    }
    public Session(String courseCode, String name, String loc, String time, String prof) {
        mCourse_name = courseCode;
        mSession_name = name;
        mLocation = loc;
        mCourse_time = time;
        mProf_name = prof;
        mSelected = false;


        int i;

        if (mCourse_time.equals("ONLINE")) {

            mStartTime = 0;
            mEndTime = 0;
            return;
        }
        for(i = 0; Character.isLetter(mCourse_time.charAt(i)); i++) {;}
        int index_first_num = i;
        int index_hyphen = mCourse_time.indexOf('-');
        if (index_hyphen < 0) {
            // mCourse_time is Something like "MTR3"
            mStartTime = Integer.parseInt(mCourse_time.substring(index_first_num, index_first_num+1));
            mEndTime = mStartTime + 1;
        } else {
            mStartTime = Integer.parseInt(mCourse_time.substring(index_first_num, index_hyphen));
            mEndTime = Integer.parseInt(mCourse_time.substring(index_hyphen + 1, mCourse_time.length()));
        }
        if (mStartTime < 9) {
            mStartTime += 12;
        }
        if(mEndTime < 9) {
            mEndTime += 12;
        }
    }


    public ArrayList<Integer> getDays() {
        ArrayList<Integer> days = new ArrayList<>();
        if(mCourse_time.equals("ONLINE")) {
            days.add(Integer.valueOf(0));
            return days;
        }
        int i;
        for(i = 0; Character.isLetter(mCourse_time.charAt(i)); i++) {;}
        String day_str = mCourse_time.substring(0, i);
        String allDaysString = "MTWRF";
        for(i = 0; i < day_str.length(); i++) {
            days.add(Integer.valueOf(allDaysString.indexOf(day_str.charAt(i)) + 1));
        }
        return days;
    }


    public String toString() {
        String s = mCourse_name + ", ";
        s += mSession_name + ", ";
        s += mLocation + ", ";
        s += mCourse_time + ", ";
        s += mProf_name + ";";
        return s;
    }

    public int getStartTime() {
        return mStartTime;
    }

    public int getEndTime() {
        return mEndTime;
    }
    public boolean getSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }

    public String getCourse_name() {
        return mCourse_name;
    }
    public String getShortCourse_name() {
        int i = 0;
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (i=0; i < mCourse_name.length(); i++) {
            if (Character.isLetter(mCourse_name.charAt(i)))
                hasLetter = true;
            if (Character.isDigit(mCourse_name.charAt(i)))
                hasDigit = true;
            if(hasLetter && hasDigit && Character.isLetter(mCourse_name.charAt(i))) {
                break;
            }
        }
        String s = mCourse_name.substring(0, i);
        return s;
    }

    public void setCourse_name(String course_name) {
        mCourse_name = course_name;
    }

    public String getSection_code() {
        return mCourse_name.substring(mCourse_name.length()-1);
    }

    public void setSection_code(String section_code) {
        mSection_code = section_code;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public String getSession_name() {
        return mSession_name;
    }

    public void setSession_name(String session_name) {
        mSession_name = session_name;
    }

    public String getCourse_time() {
        return mCourse_time;
    }

    public void setCourse_time(String course_time) {
        mCourse_time = course_time;
    }

    public String getProf_name() {
        return mProf_name;
    }

    public void setProf_name(String prof_name) {
        mProf_name = prof_name;
    }

}
