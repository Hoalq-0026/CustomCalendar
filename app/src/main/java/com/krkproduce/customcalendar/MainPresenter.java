package com.krkproduce.customcalendar;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import com.krkproduce.calendarlib.model.CalendarDay;
import java.util.ArrayList;

public class MainPresenter implements MainContract.Presenter {

    private Context mContext;

    private MainContract.View mMainView;

    private ArrayList<CalendarDay> mSelectedDays;

    private ObservableField<String> mListDayString;

    private ObservableField<String> selectedDateTitle;

    private ObservableBoolean isEnableMultiDateSelect;

    public MainPresenter(Context context, MainContract.View mainView) {

        mContext = context;
        mMainView = mainView;
        mainView.setPresenter(this);
        mSelectedDays = new ArrayList<>();
        selectedDateTitle = new ObservableField<>();
        mListDayString = new ObservableField<>();
        isEnableMultiDateSelect = new ObservableBoolean(false);
    }

    public ObservableField<String> getSelectedDateTitle() {
        return selectedDateTitle;
    }

    public void setSelectedDateTitle(ObservableField<String> selectedDateTitle) {
        this.selectedDateTitle = selectedDateTitle;
    }

    public ObservableBoolean getIsEnableMultiDateSelect() {
        return isEnableMultiDateSelect;
    }

    public void setIsEnableMultiDateSelect(ObservableBoolean isEnableMultiDateSelect) {
        this.isEnableMultiDateSelect = isEnableMultiDateSelect;
    }

    public ObservableField<String> getListDayString() {
        return mListDayString;
    }

    public void setListDayString(ObservableField<String> listDayString) {
        mListDayString = listDayString;
    }

    @Override
    public void handleDaySelected(CalendarDay selectedDay) {
        if (selectedDay == null) {
            mMainView.hideFooterLayout();

            return;
        }
        mSelectedDays.clear();
        selectedDateTitle.set(
                DateUtils.convertToString(selectedDay.getDate(), DateUtils.REVIEW_TIME_FORMAT));
        mSelectedDays.add(selectedDay);
    }

    @Override
    public void handleMultiDaySelected(ArrayList<CalendarDay> selectedDayList) {
        if (selectedDayList != null && selectedDayList.size() == 0) {
            mMainView.hideFooterLayout();
            return;
        }
        mSelectedDays.clear();
        mSelectedDays.addAll(selectedDayList);
        selectedDateTitle.set("Enable Multi Select");
        convertDayListToString();
    }

    public void enableMultiDateSelect() {
        isEnableMultiDateSelect.set(!isEnableMultiDateSelect.get());
        mMainView.enableMultiDateSelect(isEnableMultiDateSelect.get());
    }

    public void clearAllDateSelected() {
        mMainView.resetCalendar();
        mSelectedDays.clear();
    }

    private void convertDayListToString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (CalendarDay day : mSelectedDays) {
            String date = DateUtils.convertToString(day.getDate(), DateUtils.REVIEW_TIME_FORMAT);
            stringBuilder.append(date).append(",");
        }

        mListDayString.set(stringBuilder.toString());
    }
}
