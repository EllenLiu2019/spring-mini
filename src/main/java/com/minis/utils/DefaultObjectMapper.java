package com.minis.utils;


import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;

public class DefaultObjectMapper implements ObjectMapper {
    String dateFormat = "yyyy-MM-dd";
    DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
    String decimalFormat = "###.##";
    DecimalFormat decimalFormatter = new DecimalFormat(decimalFormat);

    @Override
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
        this.datetimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
    }

    @Override
    public void setDecimalFormat(String decimalFormat) {
        this.decimalFormat = decimalFormat;
        this.decimalFormatter = new DecimalFormat(decimalFormat);
    }

    @Override
    public String writeValuesAsString(Object item) {
        StringBuilder sb = new StringBuilder();
        buildJson(item, sb);
        return sb.toString();
    }

    private void buildJson(Object item, StringBuilder sb) {
        if (item == null) {
            sb.append("null");
            return;
        }

        if (item instanceof Collection<?>) {
            sb.append("[");
            final boolean[] first = {true};
            ((Collection<?>) item).forEach(obj -> {
                if (!first[0]) {
                    sb.append(",");
                }
                buildJson(obj, sb);
                first[0] = false;
            });
            sb.append("]");
        } else {
            sb.append("{");
            Class<?> clz = item.getClass();
            Field[] fields = clz.getDeclaredFields();
            boolean firstField = true;
            for (Field field : fields) {
                Object value;
                try {
                    field.setAccessible(true);
                    value = field.get(item);
                    String strValue = formatValue(value);

                    if (!firstField) {
                        sb.append(",");
                    }
                    sb.append("\"").append(field.getName()).append("\":\"").append(strValue).append("\"");
                    firstField = false;

                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new RuntimeException("Error serializing field: " + field.getName(), e);
                }
            }
            sb.append("}");
        }
    }

    private String formatValue(Object value) {
        if (value == null) {
            return "null";
        }

        if (value instanceof Date) {
            LocalDate localDate = ((Date) value).toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            return localDate.format(this.datetimeFormatter);
        } else if (value instanceof BigDecimal || value instanceof Double || value instanceof Float) {
            return this.decimalFormatter.format(value);
        } else {
            return value.toString().replace("\\", "\\\\").replace("\"", "\\\"");
        }
    }

}
