/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sample.logging;

/**
 *
 * @author tlehe
 */
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import sample.entity.Music;

public class DefaultLogger {
    static Handler fileHandler = null;
    private static Logger logger; 
    private static DefaultLogger single_instance = null; 
    private static Formatter formatter;
    private DefaultLogger(){
        setup();
    }
    public static DefaultLogger getInstance() 
    { 
        if (single_instance == null) 
            single_instance = new DefaultLogger(); 
  
        return single_instance; 
    } 
    
    public static void setup() {

        try {
            fileHandler = new FileHandler("./logfile.log",true);//file
            logger = Logger.getLogger(DefaultLogger.class.getClass().getName());
            formatter = new Formatter() {
                @Override
                public String format(LogRecord record) {
                    DateFormat simple = new SimpleDateFormat("dd-MM-yyyy"); 
                    Date date = new Date(record.getMillis());
                    StringBuilder sb = new StringBuilder();
                    sb.append("date="+simple.format(date)).append(" : ");
                    sb.append(record.getMessage()).append('\n');
                    return sb.toString();
                }
            };
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);//adding Handler for file

        } catch (IOException e) {
            // TODO Auto-generated catch block
        }

    }
    public void logInfo(Music m){
        String logout = m.getLogOutString();
        if(logout != null){
            logger.info(logout);
        }
        
    }

}
