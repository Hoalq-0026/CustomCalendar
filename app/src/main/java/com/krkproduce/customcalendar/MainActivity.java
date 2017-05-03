package com.krkproduce.customcalendar;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import com.krkproduce.calendarlib.DatePickerController;
import com.krkproduce.calendarlib.DayPickerView;
import com.krkproduce.calendarlib.model.CalendarDay;
import com.krkproduce.customcalendar.databinding.ActivityMainBinding;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements DatePickerController, MainContract.View {
    private ActivityMainBinding mMainBinding;
    private MainPresenter mPresenter;
    private DayPickerView mPickerView;
    private RelativeLayout mFooterLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mPickerView = mMainBinding.dayPickerView;
        mPickerView.setController(this);
        mFooterLayout = mMainBinding.footerLayout;

        mPresenter = new MainPresenter(this, this);
        mMainBinding.setPresenter(mPresenter);
    }

    @Override
    public void onDayOfMonthSelected(CalendarDay selectedDay) {

        mPresenter.handleDaySelected(selectedDay);
        if (!mFooterLayout.isShown()) {
            showFooterLayout();
        }
    }

    @Override
    public void onDaysSelected(ArrayList<CalendarDay> selectedDays) {
        mPresenter.handleMultiDaySelected(selectedDays);
        if (!mFooterLayout.isShown()) {
            showFooterLayout();
        }
    }

    @Override
    public void onFirstDayOfMonthScrolled(CalendarDay firstDay) {

    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {

    }

    @Override
    public void showFooterLayout() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.move_in);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFooterLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mFooterLayout.startAnimation(animation);
    }

    @Override
    public void hideFooterLayout() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.move_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFooterLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mFooterLayout.startAnimation(animation);
    }

    @Override
    public void resetCalendar() {
        mPickerView.removeAllSelectedDays();
        if (mFooterLayout.isShown()) {
            hideFooterLayout();
        }
    }

    @Override
    public void enableMultiDateSelect(boolean enable) {
        final boolean multiDaySelect = mPresenter.getIsEnableMultiDateSelect().get();
        if (!multiDaySelect) {
            if (mFooterLayout.isShown()) {
                hideFooterLayout();
            }
        }
        mPickerView.setIsEnableMultiDaySelected(enable);
    }
}
