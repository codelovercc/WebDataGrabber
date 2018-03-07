package com.cool.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by codelover on 17/4/18.
 *
 */
@Component
public class TimestampFormatter implements Formatter<Timestamp> {
    @Autowired
    private MessageSource messageSource;
    @Override
    public Timestamp parse(String text, Locale locale) throws ParseException {
        final SimpleDateFormat dateFormat = StringUtil.createDateFormat(locale, messageSource);
        Date date = dateFormat.parse(text);
        return new Timestamp(date.getTime());
    }

    @Override
    public String print(Timestamp object, Locale locale) {
        final SimpleDateFormat dateFormat = StringUtil.createDateFormat(locale, messageSource);
        return dateFormat.format(object);
    }


}
