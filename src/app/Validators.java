package app;

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
    public enum ValidatorKeys { NONE, IS_ADULT, ALL_NUMBERS, POSTAL_CODE, FUTURE_START_DATE,
        FUTURE_END_DATE, START_DATE, END_DATE, CC_EXPIRY, CC_NUMBER, CCV, LONGITUDE, LATITUDE,
    SIN, NOT_NEGATIVE, NUM_BEDROOMS}
    private static Calendar startDate;

    public static void initMap(){
        Validation = new HashMap<>();
        Validation.put(ValidatorKeys.NONE, (o)-> Validators.noValidation(o));
        Validation.put(ValidatorKeys.IS_ADULT, (d)-> Validators.isAdult((Date)d));
        Validation.put(ValidatorKeys.ALL_NUMBERS, (s)-> Validators.allNumbers((String)s));
        Validation.put(ValidatorKeys.POSTAL_CODE, (s)-> Validators.isPostalCode((String)s));
        Validation.put(ValidatorKeys.FUTURE_START_DATE, (d)-> Validators.isFutureStartDate((Date)d));
        Validation.put(ValidatorKeys.FUTURE_END_DATE, (d)-> Validators.isFutureEndDate((Date)d));
        Validation.put(ValidatorKeys.START_DATE, (d)-> Validators.isStartDate((Date)d));
        Validation.put(ValidatorKeys.END_DATE, (d)-> Validators.isEndDate((Date)d));
        Validation.put(ValidatorKeys.CC_NUMBER, (s)-> Validators.CCNumber((String) s));
        Validation.put(ValidatorKeys.CCV, (s)-> Validators.CCV((String) s));
        Validation.put(ValidatorKeys.LONGITUDE, (d)-> Validators.longitude((Double) d));
        Validation.put(ValidatorKeys.LATITUDE, (d)-> Validators.latitude((Double) d));
        Validation.put(ValidatorKeys.SIN, (s)-> Validators.SIN((String) s));
        Validation.put(ValidatorKeys.NOT_NEGATIVE, (d)-> Validators.notNegative((Double) d));
        Validation.put(ValidatorKeys.NUM_BEDROOMS, (i)-> Validators.numBedrooms((int) i));
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
        return str.matches("(\\p{Alpha}\\d){3}");//A1A1A1
    }

    public static boolean isFutureStartDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        startDate = cal;
        return !cal.before(Calendar.getInstance());
    }

    public static boolean isFutureEndDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.after(startDate);
    }

    public static boolean isStartDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        startDate = cal;
        return true;
    }

    public static boolean isEndDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.after(startDate);
    }

    public static boolean isCCExpiry(String str){
        return str.matches("((0?[1-9])|(1[0-2]))/\\d{4}"); // mm/yyyy
    }

    public static boolean CCNumber(String str){
        return allNumbers(str) && str.length() >= 13 && str.length() <= 19;
    }

    public static boolean CCV(String str){
        return allNumbers(str) && str.length() <= 4 && (str.length() > 1 || !str.equals("0"));
    }

    public static boolean longitude(Double d){
        return d > -180. && d < 180.;
    }

    public static boolean latitude(Double d){
        return d > -90. && d < 90.;
    }

    public static boolean SIN(String str){
        return allNumbers(str) && str.charAt(0) != '0' && str.length() <= 9;
    }

    public static boolean notNegative(Double d){
        return d >= 0;
    }

    public static boolean numBedrooms(int d){
        return d > 0 && d <25.;
    }







}
