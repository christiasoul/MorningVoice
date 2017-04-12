package edu.cogswell.morningvoice;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;


/**
 * Created by Christian on 4/12/2017.
 */

public class SetTime implements View.OnFocusChangeListener, TimePickerDialog.OnTimeSetListener {
    private EditText editText;
    private Calendar myCalendar;
    private Context ctx;

    public SetTime(EditText editText, Context newCtx){
        this.editText = editText;
        this.editText.setOnFocusChangeListener(this);
        this.myCalendar = Calendar.getInstance();
        ctx = newCtx;

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus){
            int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = myCalendar.get(Calendar.MINUTE);
            // Make sure context is proper
            new TimePickerDialog(ctx, this, hour, minute, true).show();
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.editText.setText( hourOfDay + ":" + minute);
    }

}
