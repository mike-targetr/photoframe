package net.targetr.httpd.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Formats java.util.logging into something that is useful.
 * @author mike-targetr
 */
public class NiceLogFormatter extends Formatter {

    @Override 
    public String format(LogRecord record) {
        
        StringBuilder b = new StringBuilder();

        b.append(record.getLevel());
        b.append(" ");
        b.append(Thread.currentThread().getName());
        b.append(" ");
        //b.append(record.getLoggerName());
        //b.append(" ");
        b.append(record.getMessage());
        
        if (record.getThrown() != null) {
            b.append("\r\n\r\n");
            b.append(prettyPrintStackTrace(record.getThrown()));
        }
        
        b.append("\r\n");
        
        return b.toString();
    }
    
    public static String prettyPrintStackTrace(Throwable ex) {
        
        StringWriter stackWriter = new StringWriter();
        PrintWriter stackPrinter = new PrintWriter(stackWriter);
        ex.printStackTrace(stackPrinter);
        stackPrinter.close();
        
        return stackWriter.toString();
    }
}
