package com.minis.beans.propertyeditors;

import com.minis.utils.StringUtils;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CustomDateEditor extends PropertyEditorSupport {
    private DateTimeFormatter datetimeFormatter;
    private boolean allowEmpty;
    private Date value;


    public CustomDateEditor(String pattern, boolean allowEmpty) throws IllegalArgumentException {
        this.datetimeFormatter = DateTimeFormatter.ofPattern(pattern);
        this.allowEmpty = allowEmpty;
    }

    @Override
    public void setAsText(String text) {
        if (this.allowEmpty && !StringUtils.hasText(text)) {
            // Treat empty String as null value.
            setValue(null);
        } else {
            // Use default valueOf methods for parsing text.
            LocalDate date = LocalDate.parse(text, this.datetimeFormatter);
            setValue(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
    }

    @Override
    public void setValue(Object value) {
        this.value = (Date) value;
    }

    @Override
    public String getAsText() {
        Date value = this.value;
        if (value == null) {
            return "";
        } else {
            LocalDate localDate = value.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return localDate.format(datetimeFormatter);
        }
    }

    @Override
    public Object getValue() {
        return this.value;
    }
}
