package me.ziningzhu.uttimetable;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Zining on 12/28/2015.
 */
public class CourseListAdapter extends ArrayAdapter<Session> {
    private final Context mContext;
    private ArrayList<Session> mSessions;

    private final String TAG = "CourseListAdapter";

    public CourseListAdapter(Context context, ArrayList<Session> arraySessions) {
        super(context, R.layout.list_item_row, arraySessions);
        mContext = context;
        mSessions = arraySessions;
        // They are set to unselected by default. See Session.java constructor.


    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_row, parent, false);
        }
        Session c = mSessions.get(position);
        // Configure the line widgets
        TextView courseCodeTextView = (TextView) convertView.findViewById(R.id.course_code);
        TextView sessionNameTextView = (TextView) convertView.findViewById(R.id.session_name);
        TextView locationTextView = (TextView) convertView.findViewById(R.id.location);
        TextView profTextView = (TextView) convertView.findViewById(R.id.prof);
        final Button selectUnselectButton = (Button) convertView.findViewById(R.id.select_unselect_button);

        // Set up the appearance of the widgets
        courseCodeTextView.setText(c.getSession_name());
        sessionNameTextView.setText(c.getCourse_time());
        locationTextView.setText(c.getLocation());
        profTextView.setText(c.getProf_name());

        boolean selected = false;
        // read file and check if this one is selected.
        File file = new File(mContext.getFilesDir(), "selected_sessions.ser");
        try {
            FileInputStream fin = new FileInputStream(file);
            ObjectInputStream oin = new ObjectInputStream(fin);
            ArrayList<Session> sel = (ArrayList<Session>) oin.readObject();

            for (Session s:sel) {
                if (s.getSession_name().equals(mSessions.get(position).getSession_name()) &&
                        s.getSection_code().equals(mSessions.get(position).getSection_code())) {
                    selected = true;
                }
            }

            oin.close();
            fin.close();
        } catch(IOException e) {
            e.printStackTrace();
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (!selected) {
            selectUnselectButton.setText(R.string.select_button_text);
            selectUnselectButton.setBackgroundColor(mContext.getResources().getColor(R.color.button_clickable_background));
            selectUnselectButton.setTextColor(mContext.getResources().getColor(R.color.button_clickable_text));
        } else {
            selectUnselectButton.setText(R.string.unselect_button_text);
            selectUnselectButton.setBackgroundColor(mContext.getResources().getColor(R.color.course_droppable_background));
            selectUnselectButton.setTextColor(mContext.getResources().getColor(R.color.button_clickable_text));

        }
        // Wire up the widgets: onclick listener
        final int pos = position;
        selectUnselectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSessions.get(pos).setSelected(!mSessions.get(pos).getSelected());
                saveUserSelections();

                // Update the UI appearance.
                if(mSessions.get(pos).getSelected()) {
                    selectUnselectButton.setText(R.string.unselect_button_text);
                    selectUnselectButton.setBackgroundColor(mContext.getResources().getColor(R.color.course_droppable_background));
                    selectUnselectButton.setTextColor(mContext.getResources().getColor(R.color.black));
                } else {
                    selectUnselectButton.setText(R.string.select_button_text);
                    selectUnselectButton.setBackgroundColor(mContext.getResources().getColor(R.color.button_clickable_background));
                    selectUnselectButton.setTextColor(mContext.getResources().getColor(R.color.button_clickable_text));
                }


            }
        });

        return convertView;
    }

    private void saveUserSelections() {
        // Save the selected Sessions into internal storage

        // First see how many sessions are selected. temp_selected_sessions
        ArrayList<Session> temp_selected_sessions = new ArrayList<Session>();
        int j = 0;
        for (int i = 0; i < mSessions.size(); i++) {
            if (mSessions.get(i).getSelected()) {
                temp_selected_sessions.add(j, mSessions.get(i));
                j += 1;
            }
        }

        // Then convert to binary and store all the selected sessions.
        ArrayList<Session> all_select = new ArrayList<Session>();
        try {

            File file = new File(mContext.getFilesDir(), "selected_sessions.ser");
            FileInputStream fin = new FileInputStream(file);
            ObjectInputStream oin = new ObjectInputStream(fin);

            all_select = (ArrayList<Session>) oin.readObject();

            oin.close();
            fin.close();
        } catch(IOException e) {
            e.printStackTrace();
        } catch(ClassNotFoundException c) {
            c.printStackTrace();
        }

        for (Session s:temp_selected_sessions) {
            all_select.add(s);
        }
        try {
            // Starts appending to the new file.
            File file = new File(mContext.getFilesDir(), "selected_sessions.ser");
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(all_select);
            out.close();
            fos.close();
        } catch (IOException i) {
            i.printStackTrace();
            Log.e(TAG, "IOException at saveUserSelections(), when writing memory file.");
        }
    }
}
