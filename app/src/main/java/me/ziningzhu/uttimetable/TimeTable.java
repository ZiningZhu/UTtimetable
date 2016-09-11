package me.ziningzhu.uttimetable;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * Created by Zining on 6/25/2016.
 */
public class TimeTable extends ViewGroup {
    private ArrayList<Session> mSessions = new ArrayList<Session>();
    private ArrayList<CellView> mCellViews = new ArrayList<CellView>();
    private final String TAG = "TimeTable";
    private boolean mShowText = false;

    private RectF mTableBounds = new RectF();
    private RectF mShadowBounds = new RectF();
    private Paint mShadowPaint;
    private Paint mTextPaint;
    private Paint mBackgroundPaint;


    public TimeTable(Context context) {
        super(context);
        init();
    }
    public TimeTable (Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TimeTable,
                0, 0
        );
        try {

        } finally {
            a.recycle();
        }

        setWillNotDraw(false);

        init();
    }

    private void init() {


        // Force the background to software rendering because otherwise the Blur
        // filter won't work.
        setLayerToSW(this);


        // Set up paint for the shadow
        mShadowPaint = new Paint(0);
        mShadowPaint.setColor(getResources().getColor(R.color.timetable_shadow));
        mShadowPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));

        // Set up paint for background
        mBackgroundPaint = new Paint(0);
        mBackgroundPaint.setColor(getResources().getColor(R.color.timetable_background));


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        // Draws background
        canvas.drawRect(mTableBounds, mBackgroundPaint);
        //Log.d(TAG, "background drawing; mTableBounds are " + mTableBounds.toString());
        canvas.drawRect(mShadowBounds, mShadowPaint);

        for (int i = 0; i < mCellViews.size(); i++) {
            mCellViews.get(i).draw(canvas);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // Do nothing. Do not call the superclass method--that would start a layout pass
        // on this view's children. TimeTable lays out its children in onSizeChanged().


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Account for padding
        float xpad = (float) (getPaddingLeft() + getPaddingRight());
        float ypad = (float) (getPaddingTop() + getPaddingBottom());

        float ww = (float) w - xpad;
        float hh = (float) h - ypad;


        mTableBounds = new RectF(0.0f, 0.0f, ww, hh);
        mTableBounds.offsetTo(getPaddingLeft(), getPaddingTop());

        //Log.d(TAG, "onSizeChanged: mTableBounds" + mTableBounds.toString());

        // Lay out the child view that actually draws the pie.
        for (int i=0; i < mCellViews.size(); i++) {
            RectF cellBound = calcCellBounds(mTableBounds, mCellViews.get(i));



            int ow = (int) mCellViews.get(i).getBounds().width();
            int oh = (int) mCellViews.get(i).getBounds().height();
            mCellViews.get(i).setBounds(cellBound);
            //mCellViews.get(i).onSizeChanged((int)cellBound.width(), (int)cellBound.height(), ow, oh);
            /*mCellViews.get(i).layout((int)cellBound.left,
                    (int)cellBound.top,
                    (int)cellBound.right,
                    (int)cellBound.bottom);
            */
        }

    }

    public void addSession(Session session) {
        mSessions.add(session);

        ArrayList<CellView> cvs = getSessionViews(session);
        for (int i = 0; i < cvs.size(); i++) {
            mCellViews.add(cvs.get(i));
        }
        Log.d(TAG, "session added: " + session.toString());

        invalidate();

        requestLayout();

    }

    private ArrayList<CellView> getSessionViews(Session session) {
        ArrayList<CellView> views = new ArrayList<>();
        ArrayList<Integer> days = session.getDays();

        int n = days.size();
        for (int i = 0; i < n; i++) {
            CellView cv = new CellView(getContext(), session, days.get(i));
            views.add(cv);
        }
        return views;
    }

    private RectF calcCellBounds(RectF mTableBounds, CellView cv) {
        final String TAG = "calcCellBounds";
        int start_time = cv.getSession().getStartTime();
        int end_time = cv.getSession().getEndTime();
        float CELL_WIDTH = mTableBounds.width() / 6;
        float CELL_HEIGHT = mTableBounds.height() / 12;
        float left = mTableBounds.left + (cv.getDay()) * CELL_WIDTH;
        float right = mTableBounds.left + (cv.getDay()+1) * CELL_WIDTH;
        float top = mTableBounds.top + (start_time - 9) * CELL_HEIGHT;
        float bottom = mTableBounds.top + (end_time - 9) * CELL_HEIGHT;
        RectF rectf = new RectF(left, top, right, bottom);
        //Log.d(TAG, "Session " + cv.getSession().toString() + "; day " + String.format("%d", cv.getDay())
        //        + ", rectf " + rectf.toString());
        return rectf;
    }
    private void setLayerToSW(View v) {
        if (!v.isInEditMode() && Build.VERSION.SDK_INT >= 11) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    private void setLayerToHW(View v) {
        if (!v.isInEditMode() && Build.VERSION.SDK_INT >= 11) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
    }

    private class CellView extends View {

        private final String TAG = "CellView";
        private RectF mBounds;


        private Session mSession;
        private int mDay;
        private String mText;
        private float mTextHeight;
        private Paint mCellBackgroundPaint;
        private Paint mTextPaint;
        private float mTextPaddingLeft;
        private float mTextPaddingBottom;

        public CellView (Context context, Session session, Integer day) {
            super(context);
            mBounds = new RectF();
            mText = session.getShortCourse_name();
            mSession = session;
            mDay = day;
            init();
        }

        private void init() {
            mCellBackgroundPaint = new Paint(0);
            mCellBackgroundPaint.setColor(getResources().getColor(R.color.timetable_session_cell_background));

            mTextPaint = new Paint(0);
            mTextPaint.setColor(getResources().getColor(R.color.timetable_cell_text));

            mTextPaddingBottom = 0;
            mTextPaddingLeft = 0;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawRect(mBounds, mCellBackgroundPaint);

            float startx = mBounds.left + mTextPaddingLeft;
            float starty = mBounds.bottom - mTextPaddingBottom;
            canvas.drawText(mText, startx, starty, mTextPaint);
            //Log.d(TAG, "session " + mSession.toString() +", mBounds are " + mBounds.toString());
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            mBounds = new RectF(0, 0, w, h);

        }

        public RectF getBounds() {
            return mBounds;

        }

        public void setBounds(RectF bounds) {

            mBounds = bounds;
            mTextHeight = mBounds.height() * (float)0.8;
            mTextPaint.setTextSize(mTextHeight);
            // Adjustment: shrink if width larger than bound width. Also set paddings.
            float mTextWidth = mTextPaint.measureText(mText);
            float suitableTextHeight;
            if (mTextWidth > mBounds.width()) {
                suitableTextHeight = mTextHeight * (mBounds.width() / mTextWidth);
                mTextPaddingBottom = (mBounds.height() - suitableTextHeight) / 2;
            } else {
                suitableTextHeight = mTextHeight;
                mTextPaddingLeft = (mBounds.width() - mTextWidth) / 2;
            }

            //Log.d(TAG, "setBounds(). suitableTextHeight = " + String.format("%2f", suitableTextHeight));
            mTextPaint.setTextSize(suitableTextHeight);
        }

        public String getText() {
            return mText;
        }

        public void setText(String text) {
            mText = text;
        }

        public Session getSession() {
            return mSession;
        }

        public void setSession(Session session) {
            mSession = session;
        }

        public int getDay() {
            return mDay;
        }

        public void setDay(int day) {
            mDay = day;
        }

    }


}
