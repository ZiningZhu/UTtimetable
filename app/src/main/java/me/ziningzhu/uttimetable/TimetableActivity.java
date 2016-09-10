package me.ziningzhu.uttimetable;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TimetableActivity extends AppCompatActivity {

    private Button mTimetableTabButton;
    private Button mBrowserTabButton;
    private TimeTable mTimeTable;


    private SurfaceHolder mSurfaceHolder;
    private Thread mDrawingThread;
    private SharedPreferences selectedCourses;
    private final String TAG = "TimetableActivity";


    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        mTimetableTabButton = (Button)findViewById(R.id.timetable_tab_button);
        mBrowserTabButton = (Button)findViewById(R.id.browser_tab_button);

        mTimetableTabButton.setBackgroundColor(getResources().getColor(R.color.tab_selected));
        mTimetableTabButton.setTextColor(getResources().getColor(R.color.white));
        mBrowserTabButton.setBackgroundColor(getResources().getColor(R.color.tab_unselected));
        mBrowserTabButton.setTextColor(getResources().getColor(R.color.black));

        mBrowserTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimetableActivity.this, BrowserActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Read the selected_sessions from memory.
        ArrayList<Session> selectedSessions = null;
        try {
            File file = new File(getApplicationContext().getFilesDir(), "selected_sessions.ser");
            FileInputStream fin = new FileInputStream(file);
            ObjectInputStream oin = new ObjectInputStream(fin);
            selectedSessions = (ArrayList<Session>)oin.readObject();
            oin.close();
            fin.close();
        } catch(IOException i) {
            Log.e(TAG, "IOException at reading selected_sessions.ser");
        } catch(ClassNotFoundException c) {
            Log.e (TAG, "ClassNotFoundExceoption at reading selected_sessions.ser");
        }
        Log.d(TAG, "sessionsSet retrieved.");



        GridLayout gridLayout = (GridLayout)findViewById(R.id.timetableLayout);


        String[] weeks = {"", "Mon", "Tue", "Wed", "Thu", "Fri"};

        for (int i = 0; i < 6; i++) {
            TextView temp_cv = new TextView(getApplicationContext());
            temp_cv.setText(weeks[i]);
            temp_cv.setBackgroundColor(getResources().getColor(R.color.timetable_title_cell_background));

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.rowSpec = GridLayout.spec(0, ROWSPAN_BASIC);
            params.columnSpec = GridLayout.spec(i * COLSPAN_BASIC, COLSPAN_BASIC);
            // fixme - The cell size does not change as the @dimen numbers change. It does display though.
            // todo - where are the layout_width and layout_height attributes of the cell? Don't need.

            gridLayout.addView(temp_cv, params);
        }

        // Todo - determine the shape of cell by the size of courseStringSet.

        // Todo - determine the height and content of cell by the content of set.
    }
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        mTimetableTabButton = (Button) findViewById(R.id.timetable_tab_button);
        mBrowserTabButton = (Button) findViewById(R.id.browser_tab_button);

        mTimetableTabButton.setBackgroundColor(getResources().getColor(R.color.tab_selected));
        mTimetableTabButton.setTextColor(getResources().getColor(R.color.white));
        mBrowserTabButton.setBackgroundColor(getResources().getColor(R.color.tab_unselected));
        mBrowserTabButton.setTextColor(getResources().getColor(R.color.black));



        mBrowserTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimetableActivity.this, BrowserActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Read the selected_sessions from memory.
        ArrayList<Session> selectedSessions = null;
        try {
            File file = new File(getApplicationContext().getFilesDir(), "selected_sessions.ser");
            FileInputStream fin = new FileInputStream(file);
            ObjectInputStream oin = new ObjectInputStream(fin);
            selectedSessions = (ArrayList<Session>)oin.readObject();
            oin.close();
            fin.close();
        } catch(IOException i) {
            Log.e(TAG, "IOException at reading selected_sessions.ser");
        } catch(ClassNotFoundException c) {
            Log.e (TAG, "ClassNotFoundExceoption at reading selected_sessions.ser");
        }
        Log.d(TAG, "sessionsSet retrieved.");



        String selectedSessionsString = "";

        mTimeTable = (TimeTable) findViewById(R.id.timeTable);
        if (selectedSessions == null) {
            Log.e(TAG, "Selected sessions == null");
        } else {
            for(int i=0; i<selectedSessions.size(); i++) {
                mTimeTable.addSession(selectedSessions.get(i));
                selectedSessionsString += selectedSessions.get(i).toString();
            }
        }




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timetable, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
