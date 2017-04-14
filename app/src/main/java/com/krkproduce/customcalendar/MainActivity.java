package com.krkproduce.customcalendar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.krkproduce.calendarlib.DatePickerController;
import com.krkproduce.calendarlib.DayPickerView;
import com.krkproduce.calendarlib.SimpleMonthAdapter;

public class MainActivity extends AppCompatActivity implements DatePickerController {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DayPickerView dayPickerView = (DayPickerView) findViewById(R.id.pickerView);
        dayPickerView.setController(this);

        SimpleMonthAdapter.CalendarDay closeDay = new SimpleMonthAdapter.CalendarDay(2017, 3, 7);
        SimpleMonthAdapter.CalendarDay inquiryDay = new SimpleMonthAdapter.CalendarDay(2017, 3, 18);
        dayPickerView.setReservationClosedDay(closeDay);
        dayPickerView.setReservationInquiryDay(inquiryDay);
    }

    @Override
    public void onDayOfMonthSelected(int year, int month, int day) {
        Toast.makeText(this, "Thai Bao:" + day + " / " + month + "/" + year, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onDateRangeSelected(
            SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> selectedDays) {

    }
}
