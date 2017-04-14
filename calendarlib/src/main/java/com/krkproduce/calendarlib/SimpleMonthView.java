package com.krkproduce.calendarlib;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.MotionEvent;
import android.view.View;
import java.security.InvalidParameterException;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class SimpleMonthView extends View {
    private static final String TAG = SimpleMonthView.class.getSimpleName();
    public static final String VIEW_PARAMS_HEIGHT = "height";
    public static final String VIEW_PARAMS_MONTH = "month";
    public static final String VIEW_PARAMS_YEAR = "year";
    public static final String VIEW_PARAMS_SELECTED_BEGIN_DAY = "selected_begin_day";
    public static final String VIEW_PARAMS_SELECTED_LAST_DAY = "selected_last_day";
    public static final String VIEW_PARAMS_SELECTED_BEGIN_MONTH = "selected_begin_month";
    public static final String VIEW_PARAMS_SELECTED_LAST_MONTH = "selected_last_month";
    public static final String VIEW_PARAMS_SELECTED_BEGIN_YEAR = "selected_begin_year";
    public static final String VIEW_PARAMS_SELECTED_LAST_YEAR = "selected_last_year";
    public static final String VIEW_PARAMS_WEEK_START = "week_start";

    private static final int SELECTED_CIRCLE_ALPHA = 128;
    protected static int DEFAULT_HEIGHT = 32;
    protected static final int DEFAULT_NUM_ROWS = 6;
    protected static int DAY_SELECTED_CIRCLE_SIZE;
    protected static int DAY_SEPARATOR_WIDTH = 1;
    protected static int MINI_DAY_NUMBER_TEXT_SIZE;
    protected static int MIN_HEIGHT = 10;
    protected static int MONTH_DAY_LABEL_TEXT_SIZE;
    protected static int MONTH_HEADER_SIZE;
    protected static int MONTH_LABEL_TEXT_SIZE;
    protected static int INQUIRY_STATUS_CIRCLE_SIZE;

    protected int mPadding = 0;

    private String mDayOfWeekTypeface;
    private String mMonthTitleTypeface;

    protected Paint mMonthDayLabelPaint;
    protected Paint mMonthNumPaint;
    protected Paint mReservationCloseStatusPaint;
    protected Paint mReservationInquiryStatusPaint;
    protected Paint mMonthTitlePaint;
    protected Paint mSelectedCirclePaint;
    protected int mCurrentDayTextColor;
    protected int mMonthTextColor;
    protected int mDayTextColor;
    protected int mDayNumColor;
    protected int mSelectedDayColor;
    protected int mPreviousDayColor;
    protected int mSelectedDaysColor;
    protected int mReservationStatusColor;

    private final StringBuilder mStringBuilder;

    protected boolean mHasToday = false;
    protected boolean mIsPrev = false;
    protected int mSelectedBeginDay = -1;
    protected int mSelectedLastDay = -1;
    protected int mSelectedBeginMonth = -1;
    protected int mSelectedLastMonth = -1;
    protected int mSelectedBeginYear = -1;
    protected int mSelectedLastYear = -1;
    protected int mToday = -1;
    protected int mWeekStart = 1;
    protected int mNumDays = 7;
    protected int mNumCells = mNumDays;
    private int mDayOfWeekStart = 0;
    protected int mMonth;
    protected Boolean mDrawRect;
    protected int mRowHeight = DEFAULT_HEIGHT;
    protected int mWidth;
    protected int mYear;
    final Time today;

    private final Calendar mCalendar;
    private final Calendar mDayLabelCalendar;
    private final Boolean isPrevDayEnabled;
    private Boolean isMultiSelectEnabled;

    private int mNumRows = DEFAULT_NUM_ROWS;

    private DateFormatSymbols mDateFormatSymbols = new DateFormatSymbols();

    private OnDayClickListener mOnDayClickListener;

    protected SimpleMonthAdapter.CalendarDay mReservationCloseDay;
    protected SimpleMonthAdapter.CalendarDay mReservationInquiryDay;

    protected Bitmap mCloseBitmap;

    public SimpleMonthView(Context context, TypedArray typedArray) {
        super(context);

        Resources resources = context.getResources();
        mDayLabelCalendar = Calendar.getInstance();
        mCalendar = Calendar.getInstance();
        today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        mDayOfWeekTypeface = resources.getString(R.string.sans_serif);
        mMonthTitleTypeface = resources.getString(R.string.sans_serif);

        mReservationStatusColor = resources.getColor(R.color.reservation_status);
        mCurrentDayTextColor = typedArray.getColor(R.styleable.DayPickerView_colorCurrentDay,
                resources.getColor(R.color.current_day));
        mMonthTextColor = typedArray.getColor(R.styleable.DayPickerView_colorMonthName,
                resources.getColor(R.color.current_day));
        mDayTextColor = typedArray.getColor(R.styleable.DayPickerView_colorDayName,
                resources.getColor(R.color.day_of_week_text));
        mDayNumColor = typedArray.getColor(R.styleable.DayPickerView_colorNormalDay,
                resources.getColor(R.color.normal_day));
        mPreviousDayColor = typedArray.getColor(R.styleable.DayPickerView_colorPreviousDay,
                resources.getColor(R.color.previous_day));
        mSelectedDaysColor =
                typedArray.getColor(R.styleable.DayPickerView_colorSelectedDayBackground,
                        resources.getColor(R.color.selected_days_background));
        mSelectedDayColor = typedArray.getColor(R.styleable.DayPickerView_colorSelectedDayText,
                resources.getColor(R.color.selected_day_text));

        mDrawRect = typedArray.getBoolean(R.styleable.DayPickerView_drawRoundRect, false);

        mStringBuilder = new StringBuilder(50);

        MINI_DAY_NUMBER_TEXT_SIZE =
                typedArray.getDimensionPixelSize(R.styleable.DayPickerView_textSizeDay,
                        resources.getDimensionPixelSize(R.dimen.text_size_day));
        MONTH_LABEL_TEXT_SIZE =
                typedArray.getDimensionPixelSize(R.styleable.DayPickerView_textSizeMonth,
                        resources.getDimensionPixelSize(R.dimen.text_size_month));
        MONTH_DAY_LABEL_TEXT_SIZE =
                typedArray.getDimensionPixelSize(R.styleable.DayPickerView_textSizeDayName,
                        resources.getDimensionPixelSize(R.dimen.text_size_day_name));
        MONTH_HEADER_SIZE =
                typedArray.getDimensionPixelOffset(R.styleable.DayPickerView_headerMonthHeight,
                        resources.getDimensionPixelOffset(R.dimen.header_month_height));
        DAY_SELECTED_CIRCLE_SIZE =
                typedArray.getDimensionPixelSize(R.styleable.DayPickerView_selectedDayRadius,
                        resources.getDimensionPixelOffset(R.dimen.selected_day_radius));

        INQUIRY_STATUS_CIRCLE_SIZE =
                typedArray.getDimensionPixelSize(R.styleable.DayPickerView_inquiryStatusRadius,
                        resources.getDimensionPixelOffset(R.dimen.inquiry_status_radius_size));

        mRowHeight = ((typedArray.getDimensionPixelSize(R.styleable.DayPickerView_calendarHeight,
                resources.getDimensionPixelOffset(R.dimen.calendar_height)) - MONTH_HEADER_SIZE)
                / 6);

        isPrevDayEnabled = typedArray.getBoolean(R.styleable.DayPickerView_enablePreviousDay, true);

        isMultiSelectEnabled =
                typedArray.getBoolean(R.styleable.DayPickerView_enableMultiSelect, false);

        initView();
    }

    private int calculateNumRows() {
        int offset = findDayOffset();
        int dividend = (offset + mNumCells) / mNumDays;
        int remainder = (offset + mNumCells) % mNumDays;
        return (dividend + (remainder > 0 ? 1 : 0));
    }

    private void drawMonthDayLabels(Canvas canvas) {
        int y = MONTH_HEADER_SIZE - (MONTH_DAY_LABEL_TEXT_SIZE / 2);
        int dayWidthHalf = (mWidth - mPadding * 2) / (mNumDays * 2);

        for (int i = 0; i < mNumDays; i++) {
            int calendarDay = (i + mWeekStart) % mNumDays;
            int x = (2 * i + 1) * dayWidthHalf + mPadding;
            mDayLabelCalendar.set(Calendar.DAY_OF_WEEK, calendarDay);
            if (i == 0) {
                mMonthDayLabelPaint.setColor(
                        getContext().getResources().getColor(R.color.sunday_text));
            } else if (i == mNumDays - 1) {
                mMonthDayLabelPaint.setColor(
                        getContext().getResources().getColor(R.color.saturday_text));
            } else {
                mMonthDayLabelPaint.setColor(mDayTextColor);
            }

            String dayOfWeek = mDateFormatSymbols.getShortWeekdays()[mDayLabelCalendar.get(
                    Calendar.DAY_OF_WEEK)].toUpperCase(Locale.getDefault());
           /* canvas.drawText(mDateFormatSymbols.getShortWeekdays()[mDayLabelCalendar.get(
                    Calendar.DAY_OF_WEEK)].toUpperCase(Locale.getDefault()), x, y,
                    mMonthDayLabelPaint);*/

            canvas.drawText(dayOfWeek.substring(0, 1), x, y, mMonthDayLabelPaint);
        }
    }

    private void drawMonthTitle(Canvas canvas) {
        int x = (mWidth + 2 * mPadding) / 2;
        int y = (MONTH_HEADER_SIZE - MONTH_DAY_LABEL_TEXT_SIZE) / 2 + (MONTH_LABEL_TEXT_SIZE / 3);
        /*        StringBuilder stringBuilder = new StringBuilder(getMonthAndYearString().toLowerCase());
                stringBuilder.setCharAt(0, Character.toUpperCase(stringBuilder.charAt(0)));*/
        canvas.drawText(getMonthAndYearString(), x, y, mMonthTitlePaint);
    }

    private int findDayOffset() {
        return (mDayOfWeekStart < mWeekStart ? (mDayOfWeekStart + mNumDays) : mDayOfWeekStart)
                - mWeekStart;
    }

    private String getMonthAndYearString() {
        int flags = DateUtils.FORMAT_SHOW_DATE
                | DateUtils.FORMAT_SHOW_YEAR
                | DateUtils.FORMAT_NO_MONTH_DAY;
        mStringBuilder.setLength(0);
        //        long millis = mCalendar.getTimeInMillis();
        //        return DateUtils.formatDateRange(getContext(), millis, millis, flags);
        Date date = mCalendar.getTime();
        return CalendarUtils.convertToString(date, CalendarUtils.DATE_FORMAT_YYYY_MM_JAPANESE);
    }

    private void onDayClick(SimpleMonthAdapter.CalendarDay calendarDay) {
        if (mOnDayClickListener != null && (isPrevDayEnabled || !((calendarDay.month == today.month)
                && (calendarDay.year == today.year)
                && calendarDay.day < today.monthDay))) {
            mOnDayClickListener.onDayClick(this, calendarDay);
        }
    }

    private boolean sameDay(int monthDay, Time time) {
        return (mYear == time.year) && (mMonth == time.month) && (monthDay == time.monthDay);
    }

    private boolean prevDay(int monthDay, Time time) {
        return ((mYear < time.year)) || (mYear == time.year && mMonth < time.month) || (mMonth
                == time.month && monthDay < time.monthDay);
    }

    protected void drawMonthNums(Canvas canvas) {
        int y = (mRowHeight + MINI_DAY_NUMBER_TEXT_SIZE) / 2 - DAY_SEPARATOR_WIDTH
                + MONTH_HEADER_SIZE;
        int paddingDay = (mWidth - 2 * mPadding) / (2 * mNumDays);
        int dayOffset = findDayOffset();
        int day = 1;

        while (day <= mNumCells) {
            int x = paddingDay * (1 + dayOffset * 2) + mPadding;
            if ((mMonth == mSelectedBeginMonth
                    && mSelectedBeginDay == day
                    && mSelectedBeginYear == mYear) || (mMonth == mSelectedLastMonth
                    && mSelectedLastDay == day
                    && mSelectedLastYear == mYear)) {
                if (mDrawRect) {
                    RectF rectF = new RectF(x - DAY_SELECTED_CIRCLE_SIZE,
                            (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) - DAY_SELECTED_CIRCLE_SIZE,
                            x + DAY_SELECTED_CIRCLE_SIZE,
                            (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) + DAY_SELECTED_CIRCLE_SIZE);
                    canvas.drawRoundRect(rectF, 10.0f, 10.0f, mSelectedCirclePaint);
                } else {
                    canvas.drawCircle(x, y - MINI_DAY_NUMBER_TEXT_SIZE / 3,
                            DAY_SELECTED_CIRCLE_SIZE, mSelectedCirclePaint);
                }
            }

            if (mHasToday && (mToday == day)) {
                mMonthNumPaint.setColor(mCurrentDayTextColor);
                mMonthNumPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            } else {
                mMonthNumPaint.setColor(mDayNumColor);
                mMonthNumPaint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            }

            if ((mMonth == mSelectedBeginMonth
                    && mSelectedBeginDay == day
                    && mSelectedBeginYear == mYear) || (mMonth == mSelectedLastMonth
                    && mSelectedLastDay == day
                    && mSelectedLastYear == mYear)) {
                mMonthNumPaint.setColor(mSelectedDayColor);
            }

            if ((mSelectedBeginDay != -1
                    && mSelectedLastDay != -1
                    && mSelectedBeginYear == mSelectedLastYear
                    && mSelectedBeginMonth == mSelectedLastMonth
                    && mSelectedBeginDay == mSelectedLastDay
                    && day == mSelectedBeginDay
                    && mMonth == mSelectedBeginMonth
                    && mYear == mSelectedBeginYear)) {
                mMonthNumPaint.setColor(mSelectedDaysColor);
            }

            if ((mSelectedBeginDay != -1
                    && mSelectedLastDay != -1
                    && mSelectedBeginYear == mSelectedLastYear
                    && mSelectedBeginYear == mYear) && (((mMonth == mSelectedBeginMonth
                    && mSelectedLastMonth == mSelectedBeginMonth) && ((mSelectedBeginDay
                    < mSelectedLastDay && day > mSelectedBeginDay && day < mSelectedLastDay) || (
                    mSelectedBeginDay > mSelectedLastDay
                            && day < mSelectedBeginDay
                            && day > mSelectedLastDay))) || ((mSelectedBeginMonth
                    < mSelectedLastMonth
                    && mMonth == mSelectedBeginMonth
                    && day > mSelectedBeginDay) || (mSelectedBeginMonth < mSelectedLastMonth
                    && mMonth == mSelectedLastMonth
                    && day < mSelectedLastDay)) || ((mSelectedBeginMonth > mSelectedLastMonth
                    && mMonth == mSelectedBeginMonth
                    && day < mSelectedBeginDay) || (mSelectedBeginMonth > mSelectedLastMonth
                    && mMonth == mSelectedLastMonth
                    && day > mSelectedLastDay)))) {
                mMonthNumPaint.setColor(mSelectedDaysColor);
            }

            // Drawing  the closed day icon
            if (mReservationCloseDay != null && (day == mReservationCloseDay.day
                    && mMonth == mReservationCloseDay.month
                    && mYear == mReservationCloseDay.year)) {
                canvas.drawBitmap(mCloseBitmap, x - MINI_DAY_NUMBER_TEXT_SIZE / 3,
                        y + MINI_DAY_NUMBER_TEXT_SIZE / 4, mReservationCloseStatusPaint);
                mMonthNumPaint.setColor(mReservationStatusColor);
            }

            // Drawing  the inquiry day icon
            if (mReservationInquiryDay != null && (day == mReservationInquiryDay.day
                    && mMonth == mReservationInquiryDay.month
                    && mYear == mReservationInquiryDay.year)) {

                canvas.drawCircle(x + MINI_DAY_NUMBER_TEXT_SIZE / 8,
                        y + MINI_DAY_NUMBER_TEXT_SIZE / 2, INQUIRY_STATUS_CIRCLE_SIZE,
                        mReservationInquiryStatusPaint);
            }


/*            if ((mSelectedBeginDay != -1
                    && mSelectedLastDay != -1
                    && mSelectedBeginYear != mSelectedLastYear
                    && ((mSelectedBeginYear == mYear && mMonth == mSelectedBeginMonth) || (
                    mSelectedLastYear == mYear
                            && mMonth == mSelectedLastMonth))
                    && (((mSelectedBeginMonth < mSelectedLastMonth
                    && mMonth == mSelectedBeginMonth
                    && day < mSelectedBeginDay) || (mSelectedBeginMonth < mSelectedLastMonth
                    && mMonth == mSelectedLastMonth
                    && day > mSelectedLastDay)) || ((mSelectedBeginMonth > mSelectedLastMonth
                    && mMonth == mSelectedBeginMonth
                    && day > mSelectedBeginDay) || (mSelectedBeginMonth > mSelectedLastMonth
                    && mMonth == mSelectedLastMonth
                    && day < mSelectedLastDay))))) {
                mMonthNumPaint.setColor(mSelectedDaysColor);
            }*/

/*            if ((mSelectedBeginDay != -1
                    && mSelectedLastDay != -1
                    && mSelectedBeginYear == mSelectedLastYear
                    && mYear == mSelectedBeginYear) && ((mMonth > mSelectedBeginMonth
                    && mMonth < mSelectedLastMonth
                    && mSelectedBeginMonth < mSelectedLastMonth) || (mMonth < mSelectedBeginMonth
                    && mMonth > mSelectedLastMonth
                    && mSelectedBeginMonth > mSelectedLastMonth))) {
                mMonthNumPaint.setColor(mSelectedDaysColor);
            }*/

       /*     if ((mSelectedBeginDay != -1
                    && mSelectedLastDay != -1
                    && mSelectedBeginYear != mSelectedLastYear) && ((mSelectedBeginYear
                    < mSelectedLastYear && ((mMonth > mSelectedBeginMonth
                    && mYear == mSelectedBeginYear) || (mMonth < mSelectedLastMonth
                    && mYear == mSelectedLastYear))) || (mSelectedBeginYear > mSelectedLastYear && (
                    (mMonth < mSelectedBeginMonth && mYear == mSelectedBeginYear)
                            || (mMonth > mSelectedLastMonth && mYear == mSelectedLastYear))))) {
                mMonthNumPaint.setColor(mSelectedDaysColor);
            }*/

            if (!isPrevDayEnabled
                    && prevDay(day, today)
                    && today.month == mMonth
                    && today.year == mYear) {
                mMonthNumPaint.setColor(mPreviousDayColor);
                mMonthNumPaint.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
            }

            canvas.drawText(String.format("%d", day), x, y, mMonthNumPaint);

            dayOffset++;
            if (dayOffset == mNumDays) {
                dayOffset = 0;
                y += mRowHeight;
            }
            day++;
        }
    }

    public SimpleMonthAdapter.CalendarDay getDayFromLocation(float x, float y) {
        int padding = mPadding;
        if ((x < padding) || (x > mWidth - mPadding)) {
            return null;
        }

        int yDay = (int) (y - MONTH_HEADER_SIZE) / mRowHeight;
        int day = 1 + ((int) ((x - padding) * mNumDays / (mWidth - padding - mPadding))
                - findDayOffset()) + yDay * mNumDays;

        if (mMonth > 11
                || mMonth < 0
                || CalendarUtils.getDaysInMonth(mMonth, mYear) < day
                || day < 1) {
            return null;
        }

        return new SimpleMonthAdapter.CalendarDay(mYear, mMonth , day);
    }

    protected void initView() {
        mCloseBitmap =
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_reservation_close);

        mMonthTitlePaint = new Paint();
        mMonthTitlePaint.setFakeBoldText(true);
        mMonthTitlePaint.setAntiAlias(true);
        mMonthTitlePaint.setTextSize(MONTH_LABEL_TEXT_SIZE);
        mMonthTitlePaint.setTypeface(Typeface.create(mMonthTitleTypeface, Typeface.NORMAL));
        mMonthTitlePaint.setColor(mMonthTextColor);
        mMonthTitlePaint.setTextAlign(Paint.Align.CENTER);
        mMonthTitlePaint.setStyle(Paint.Style.FILL);

        mReservationCloseStatusPaint = new Paint();
        mReservationCloseStatusPaint.setFakeBoldText(false);
        mReservationCloseStatusPaint.setAntiAlias(true);
        mReservationCloseStatusPaint.setColor(mReservationStatusColor);
        mReservationCloseStatusPaint.setTextSize(MINI_DAY_NUMBER_TEXT_SIZE);
        mReservationCloseStatusPaint.setTextAlign(Paint.Align.CENTER);
        mReservationCloseStatusPaint.setStyle(Paint.Style.FILL);

        mReservationInquiryStatusPaint = new Paint();
        mReservationInquiryStatusPaint.setFakeBoldText(true);
        mReservationInquiryStatusPaint.setAntiAlias(true);
        mReservationInquiryStatusPaint.setColor(mReservationStatusColor);
        mReservationInquiryStatusPaint.setTextAlign(Paint.Align.CENTER);
        mReservationInquiryStatusPaint.setStyle(Paint.Style.FILL);
        mReservationInquiryStatusPaint.setAlpha(SELECTED_CIRCLE_ALPHA);

        mSelectedCirclePaint = new Paint();
        mSelectedCirclePaint.setFakeBoldText(true);
        mSelectedCirclePaint.setAntiAlias(true);
        mSelectedCirclePaint.setColor(mSelectedDaysColor);
        mSelectedCirclePaint.setTextAlign(Paint.Align.CENTER);
        mSelectedCirclePaint.setStyle(Paint.Style.FILL);
        // mSelectedCirclePaint.setAlpha(SELECTED_CIRCLE_ALPHA);

        mMonthDayLabelPaint = new Paint();
        mMonthDayLabelPaint.setAntiAlias(true);
        mMonthDayLabelPaint.setTextSize(MONTH_DAY_LABEL_TEXT_SIZE);
        mMonthDayLabelPaint.setColor(mDayTextColor);
        mMonthDayLabelPaint.setTypeface(Typeface.create(mDayOfWeekTypeface, Typeface.NORMAL));
        mMonthDayLabelPaint.setStyle(Paint.Style.FILL);
        mMonthDayLabelPaint.setTextAlign(Paint.Align.CENTER);
        mMonthDayLabelPaint.setFakeBoldText(true);

        mMonthNumPaint = new Paint();
        mMonthNumPaint.setAntiAlias(true);
        mMonthNumPaint.setTextSize(MINI_DAY_NUMBER_TEXT_SIZE);
        mMonthNumPaint.setStyle(Paint.Style.FILL);
        mMonthNumPaint.setTextAlign(Paint.Align.CENTER);
        mMonthNumPaint.setFakeBoldText(false);
    }

    protected void onDraw(Canvas canvas) {
        drawMonthTitle(canvas);
        drawMonthDayLabels(canvas);
        drawMonthNums(canvas);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                mRowHeight * mNumRows + MONTH_HEADER_SIZE);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            SimpleMonthAdapter.CalendarDay calendarDay =
                    getDayFromLocation(event.getX(), event.getY());
            if (calendarDay != null) {
                onDayClick(calendarDay);
            }
        }
        return true;
    }

    public void reuse() {
        mNumRows = DEFAULT_NUM_ROWS;
        requestLayout();
    }

    public void setMonthParams(HashMap<String, Integer> params) {
        if (!params.containsKey(VIEW_PARAMS_MONTH) && !params.containsKey(VIEW_PARAMS_YEAR)) {
            throw new InvalidParameterException("You must specify month and year for this view");
        }
        setTag(params);

        if (params.containsKey(VIEW_PARAMS_HEIGHT)) {
            mRowHeight = params.get(VIEW_PARAMS_HEIGHT);
            if (mRowHeight < MIN_HEIGHT) {
                mRowHeight = MIN_HEIGHT;
            }
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_DAY)) {
            mSelectedBeginDay = params.get(VIEW_PARAMS_SELECTED_BEGIN_DAY);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_LAST_DAY)) {
            mSelectedLastDay = params.get(VIEW_PARAMS_SELECTED_LAST_DAY);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_MONTH)) {
            mSelectedBeginMonth = params.get(VIEW_PARAMS_SELECTED_BEGIN_MONTH);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_LAST_MONTH)) {
            mSelectedLastMonth = params.get(VIEW_PARAMS_SELECTED_LAST_MONTH);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_YEAR)) {
            mSelectedBeginYear = params.get(VIEW_PARAMS_SELECTED_BEGIN_YEAR);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_LAST_YEAR)) {
            mSelectedLastYear = params.get(VIEW_PARAMS_SELECTED_LAST_YEAR);
        }

        mMonth = params.get(VIEW_PARAMS_MONTH);
        mYear = params.get(VIEW_PARAMS_YEAR);

        mHasToday = false;
        mToday = -1;

        mCalendar.set(Calendar.MONTH, mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);
        mDayOfWeekStart = mCalendar.get(Calendar.DAY_OF_WEEK);

        if (params.containsKey(VIEW_PARAMS_WEEK_START)) {
            mWeekStart = params.get(VIEW_PARAMS_WEEK_START);
        } else {
            mWeekStart = mCalendar.getFirstDayOfWeek();
        }

        mNumCells = CalendarUtils.getDaysInMonth(mMonth, mYear);
        for (int i = 0; i < mNumCells; i++) {
            final int day = i + 1;
            if (sameDay(day, today)) {
                mHasToday = true;
                mToday = day;
            }

            mIsPrev = prevDay(day, today);
        }

        mNumRows = calculateNumRows();
    }

    public void setOnDayClickListener(OnDayClickListener onDayClickListener) {
        mOnDayClickListener = onDayClickListener;
    }

    public void setReservationCloseDay(SimpleMonthAdapter.CalendarDay closeDay) {
        this.mReservationCloseDay = closeDay;
    }

    public void setReservationInquiryDay(SimpleMonthAdapter.CalendarDay inquiryDay) {
        this.mReservationInquiryDay = inquiryDay;
    }

    public interface OnDayClickListener {
        void onDayClick(SimpleMonthView simpleMonthView,
                SimpleMonthAdapter.CalendarDay calendarDay);
    }
}
