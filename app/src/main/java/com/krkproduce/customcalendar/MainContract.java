package com.krkproduce.customcalendar;

import com.krkproduce.calendarlib.model.CalendarDay;
import com.krkproduce.customcalendar.base.BasePresenter;
import com.krkproduce.customcalendar.base.BaseView;
import java.util.ArrayList;

public class MainContract {

    interface View extends BaseView<Presenter> {

        void showFooterLayout();

        void hideFooterLayout();

        void resetCalendar();

        void enableMultiDateSelect(boolean enable);
    }

    interface Presenter extends BasePresenter {

        void handleDaySelected(CalendarDay selectedDay);

        void handleMultiDaySelected(ArrayList<CalendarDay> selectedDayList);
    }
}
