package com.krkproduce.calendarlib;

public interface DatePickerController {

    void onDayOfMonthSelected(int year, int month, int day);

    void onDateRangeSelected(
            final SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> selectedDays);
}
