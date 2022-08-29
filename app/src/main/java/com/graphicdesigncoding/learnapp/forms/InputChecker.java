package com.graphicdesigncoding.learnapp.forms;

import android.graphics.Color;
import android.text.Editable;
import android.widget.EditText;

import com.graphicdesigncoding.learnapp.R;
import com.graphicdesigncoding.learnapp.api.RegExPattern;

public class InputChecker{
    public boolean editText(EditText editText, String str, RegExPattern regExPattern){
        editText.getBackground().setTint(R.style.Theme_LearnApp_EditText);
        if(str.length() > 0) {
            if (regExPattern.getAction(str)) {
                editText.getBackground().setTint(Color.GREEN);
                return true;
            } else {
                editText.requestFocus();
                editText.getBackground().setTint(Color.RED);
                return false;
            }
        }else{
            return false;
        }
    }
}
