package com.company;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Daniel on 2016-07-18.
 */
public class Validators {

    public static Map<ValidatorKeys, Function> Validation;
    public enum ValidatorKeys { NONE, IS_ADULT, ALL_NUMBERS, POSTAL_CODE}

    public static void initMap(){
        Validation = new HashMap<>();
        Validation.put(ValidatorKeys.NONE, (o)-> Validators.noValidation(o));
        Validation.put(ValidatorKeys.IS_ADULT, (d)-> Validators.isAdult((Date)d));
        Validation.put(ValidatorKeys.ALL_NUMBERS, (s)-> Validators.allNumbers((String)s));
        Validation.put(ValidatorKeys.POSTAL_CODE, (s)-> Validators.isPostalCode((String)s));
    }

    public static boolean noValidation(Object o){
        return true;
    }

    public static boolean isAdult(Date date){
        Calendar cal = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        cal.setTime(date);

        int offset = cal.get(Calendar.DAY_OF_YEAR) > today.get(Calendar.DAY_OF_YEAR) ? 0 : 1;
        int age = today.get(Calendar.YEAR) - cal.get(Calendar.YEAR) + offset;
        return age >= 18;
    }

    public static boolean allNumbers(String str){
        return str.matches("\\d*");
    }

    public static boolean isPostalCode(String str){
        return str.matches("(\\p{Alpha}\\d){3}");
    }
}
