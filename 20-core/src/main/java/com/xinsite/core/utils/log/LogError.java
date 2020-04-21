package com.xinsite.core.utils.log;

import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.uitls.io.FileUtils;
import com.xinsite.common.uitls.lang.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LogError {
    private static Logger logger = LoggerFactory.getLogger(LogError.class);
    private static String _outName = "";

    public static String getLogPath() {
        //return PropsUtil.getString("log_file_path", "D:\\Java\\Log") + "\\";
        return LogError.class.getResource("/").getPath();
    }

    //public delegate void delegateAddExceptionLog(int org_id, int user_id, string log_fun, string log_message);
    public static void write(String log_fun, LogEnum type, Object logMessage) {
        LogError.write(type, logMessage);

        if (LogEnum.Error == type) {
            LogUtils.addExceptionLog(log_fun, logMessage.toString());
        }
    }

    public static void write(LogEnum type, Object logMessage) {
        FileOutputStream outSTr = null;
        BufferedOutputStream Buff = null;
        try {
            String path = getLogFileByType(type);
            outSTr = new FileOutputStream(new File(path), true);
            Buff = new BufferedOutputStream(outSTr);
            Buff.write(("Log : " + DateUtils.getDateTime() + "\r\n").getBytes());
            Buff.write(logMessage.toString().getBytes());
            Buff.write("\r\n---------------------------------------\r\n".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (Buff != null) Buff.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (Buff != null) Buff.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getLogFileByType(LogEnum i_type) {

        if (_outName.equals("")) _outName = FileUtils.getRootPath() + "logfile" + File.separator;
        File file = new File(_outName);
        if (!file.exists() && !file.isDirectory()) file.mkdir();
        String s_val = "";
        switch (i_type) {
            case Error: //异常记录日志
                s_val = _outName + String.format("LogException_%s.log", DateUtils.getDate("yyyMMdd"));
                break;
            case Warning: //数据交换日志
                s_val = _outName + String.format("LogWarning_%s.log", DateUtils.getDate("yyyMMdd"));
                break;
            case Information: //日常操作日志
                s_val = _outName + String.format("LogInformation_%s.log", DateUtils.getDate("yyyMMdd"));
                break;
            default:
                s_val = _outName + String.format("LogException_%s.log", DateUtils.getDate("yyyMMdd"));
                break;
        }
        return s_val;
    }
}
