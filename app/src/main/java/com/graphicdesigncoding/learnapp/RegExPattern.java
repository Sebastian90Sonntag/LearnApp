package com.graphicdesigncoding.learnapp;

import java.util.regex.Pattern;

//COPYRIGHT BY GraphicDesignCoding
public class RegExPattern {

    public boolean Email(String email){
        return Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+"
                + "(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)"
                + "*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])"
                + "*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])"
                + "?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}"
                + "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:"
                + "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])").matcher(email).matches();
    }

    public boolean Password(String password){
        return Pattern.compile("^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=!])"
                + "(?=\\S+$).{8,20}$").matcher(password).matches();
    }

    public boolean Name(String name){
        return Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{3,50}$").matcher(name).matches();
    }
}
