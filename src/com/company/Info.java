package com.company;

import java.util.function.Function;
import com.company.Validators.ValidatorKeys;

/**
 * Created by Daniel on 2016-07-18.
 */
public class Info {
    public enum DataType {STRING, INTEGER, DOUBLE, BOOLEAN, DATE}
    public String key, text;
    public DataType type;
    public Function isValid;

    public Info(String key, String text, DataType type, ValidatorKeys validator) {
        this.key = key;
        this.text = text;
        this.type = type;
        this.isValid = Validators.Validation.get(validator);
    }

    public Info(String key, String text, DataType type) {
        this(key, text, type, ValidatorKeys.NONE);
    }
}
