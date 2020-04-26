package net.targetr.httpd.util;

import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Formats java.util.logging into something that is useful.
 * @author mike-targetr
 */
public class NiceLogFormatter extends Formatter {

    private static final MessageFormat messageFormat = new MessageFormat("{3,date,HH:mm:ss.SSS} {1} {2} {4}\n");

    @Override
    public String format(LogRecord record) {
        Object[] arguments = new Object[6];
        arguments[0] = record.getLoggerName();
        arguments[1] = record.getLevel();
        arguments[2] = Thread.currentThread().getName();
        arguments[3] = new Date(record.getMillis());
        arguments[4] = record.getMessage();
        arguments[5] = record.getSourceMethodName();
        return messageFormat.format(arguments);
    }
}
