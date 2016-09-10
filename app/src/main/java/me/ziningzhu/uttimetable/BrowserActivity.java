package me.ziningzhu.uttimetable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

public class BrowserActivity extends AppCompatActivity {

    private Button mTimetableTabButton;
    private Button mBrowserTabButton;
    private EditText mInputCourseEditText;
    private Button mSearchCourseButton;
    private TextView mIntroductionTextView;
    private CourseListAdapter mCourseListAdapter;
    private ListView mCandidateCoursesListView;
    private String mCourseString;
    private final String TAG = "HandyCourse_Browser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        // Configure the widgets
        mTimetableTabButton = (Button)findViewById(R.id.timetable_tab_button);
        mBrowserTabButton = (Button)findViewById(R.id.browser_tab_button);
        mInputCourseEditText = (EditText)findViewById(R.id.inputCourseEditText);
        mSearchCourseButton = (Button)findViewById(R.id.searchCourseButton);
        mIntroductionTextView = (TextView)findViewById(R.id.introductionTextView);
        mIntroductionTextView.setMovementMethod(new ScrollingMovementMethod());
        mCandidateCoursesListView = (ListView)findViewById(R.id.candidate_courses);

        // Set Button styles
        mBrowserTabButton.setBackgroundColor(getResources().getColor(R.color.tab_selected));
        mBrowserTabButton.setTextColor(getResources().getColor(R.color.white));
        mTimetableTabButton.setBackgroundColor(getResources().getColor(R.color.tab_unselected));
        mTimetableTabButton.setTextColor(getResources().getColor(R.color.black));
        mSearchCourseButton.setClickable(false);
        mSearchCourseButton.setBackgroundColor(getResources().getColor(R.color.button_material_light));
        mSearchCourseButton.setTextColor(getResources().getColor(R.color.black));

        // Recover the EditText and TextView contents
        SharedPreferences settings = getSharedPreferences("BrowserSharedPreference", 0);
        if(settings.contains("SearchBarEditText")) {
            mCourseString = settings.getString("SearchBarEditText", "");
            mInputCourseEditText.setText(mCourseString);

            if(checkCourseCodeCorrectness(mCourseString)) {
                setSearchCourseButtonClickable(true);
            }
        } else {;}
        if(settings.contains("IntroductionTextView")) {
            mIntroductionTextView.setText(settings.getString("IntroductionTextView", ""));
        } else {mIntroductionTextView.setText(R.string.incomplete_search_area_introduction);}

        // Wire up the widgets
        mTimetableTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BrowserActivity.this, TimetableActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mInputCourseEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Only undergrad course are supported for the current time.
                if (checkCourseCodeCorrectness(s.toString())) {
                    setSearchCourseButtonClickable(true);
                    mCourseString = s.toString();
                } else {
                    // Set mSearchCourseButton to unclickable.
                    setSearchCourseButtonClickable(false);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                SharedPreferences settings = getSharedPreferences("BrowserSharedPreference", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("SearchBarEditText", mCourseString);
                editor.apply();
            }
        });
        mSearchCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),
                        "Getting info from online database...",
                        Toast.LENGTH_SHORT)
                        .show();

                new SearchCourseTask().execute(mCourseString);



            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_browser, menu);
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

    private boolean isLetter(CharSequence s, int startIndex, int endIndex) {
        for(int i = startIndex; i < endIndex; i++) {
            if(!Character.isLetter(s.charAt(i)))
                return false;
        }
        return true;
    }
    private boolean isDigit(CharSequence s, int startIndex, int endIndex) {
        for(int i = startIndex; i < endIndex; i++) {
            if(!Character.isDigit(s.charAt(i)))
                return false;
        }
        return true;
    }
    private boolean checkCourseCodeCorrectness(String courseString) {
        if(courseString.length() == 9) { // Undergrad courses
            return (isLetter(courseString, 0, 3) && isDigit(courseString, 3, 6) &&
                    isLetter(courseString, 6, 7) && isDigit(courseString, 7, 8) &&
                    isLetter(courseString, 8, 9));
        }

        return false;
    }
    private void setSearchCourseButtonClickable(boolean clickable) {
        if(clickable) {
            mSearchCourseButton.setClickable(true);
            mSearchCourseButton.setBackgroundColor(getResources().getColor(R.color.button_clickable_background));
            mSearchCourseButton.setTextColor(getResources().getColor(R.color.white));
        } else {
            mSearchCourseButton.setClickable(false);
            mSearchCourseButton.setBackgroundColor(getResources().getColor(R.color.button_material_light));
            mSearchCourseButton.setTextColor(getResources().getColor(R.color.black));
        }
    }

    private class SearchCourseTask extends AsyncTask<String, Void, Element> {
        @Override
        protected Element doInBackground(String... courses_argument) {

        /* Input the course code, output an Element containing all the information of a course.
        */
            final String myDatabase_header = "http://ziningzhu.me/res/HandyCourse_database/";
            final String myDatabase_ending = ".xml";
            final String TAG = "SearchCourseTask";

            String courseString = courses_argument[0];
            Log.d(TAG, "input courseString=" + courseString);
            courseString = courseString.substring(0, 3).toUpperCase() +
                    courseString.substring(3, 6) +
                    courseString.substring(6).toUpperCase() +
                    courseString.substring(7) +
                    courseString.substring(8);
            Elements courses = null;

            URL url;

            Document doc;
            try {
                url = new URL(myDatabase_header + courseString.substring(0, 3) + myDatabase_ending);
                Log.d("HandyCourse_Browser_url", url.toString());
            } catch (MalformedURLException e) {

                Log.e(TAG, "MalFormedURLException");
                return null;
            }
            try {
                doc = Jsoup.connect(url.toString()).userAgent("Mozilla").get();
                courses = doc.getElementsByTag("course");

            } catch (IOException e) {
                Log.e(TAG, "IOException at Jsoup.get().");

            } catch (RuntimeException e) {
                Log.e(TAG, "Runtime exception!");
            }
            if (courses != null) {
                Log.d("all csc courses", courses.text());
                Log.d("String is", "course#" + courseString.substring(0, 9));
                courses = courses.select("course#" + courseString.substring(0, 9));
                Log.d("csc108 course", courses.text());
            } else {

                Log.d(TAG, "courses is not found! Check the <course> tag!");
                return null;
            }
            return courses.first();
        }

        @Override
        protected void onPostExecute(Element course) {
            if(course == null) {
                Log.d(TAG, "doInBackground() gave me a null course!");
                return;
            }
            String intro =
                    course.getElementsByTag("title").first().text() + "\n" +
                            course.getElementsByTag("description").first().text();
            mIntroductionTextView.setText(intro);
            String courseCode = course.attr("id");

            Elements sessions = course.getElementsByTag("session");

            ArrayList<Session> arraySessions = new ArrayList<Session>();
            // todo - store all the information needed to display onto the calendar. Gonna be done in adapter.
            // grab the course time stuffs into an ArrayList.


            for(int i = 0; i < sessions.size(); i++) {
                Element session = sessions.get(i);
                String sessionName = session.select("name").first().text();
                String location = session.select("location").first().text();
                String time = session.select("time").first().text();
                String prof = session.select("prof").first().text();
                Log.d(TAG, "Grabbing info from a session");
                Session c = new Session(courseCode, sessionName, location, time, prof);
                arraySessions.add(c);
            }

            // Inflate the ListView
            mCourseListAdapter = new CourseListAdapter(getApplicationContext(), arraySessions);
            mCandidateCoursesListView.setAdapter(mCourseListAdapter);



        }
    }

}
