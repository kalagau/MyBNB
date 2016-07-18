package com.company;

/**
 * Created by Daniel on 2016-07-18.
 */
public class Info {
    public enum DataType {STRING, INTEGER, DOUBLE, BOOLEAN, DATE}
    public String key, text;
    public DataType type;

    public Info(String key, String text, DataType type) {
        this.key = key;
        this.text = text;
        this.type = type;
    }
}
