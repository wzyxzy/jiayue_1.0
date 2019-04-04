package com.jiayue.vr;

/**
 * Created by MRKING on 2016/6/29.
 */

import android.content.Context;
import android.os.Environment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogcatFileManager {
    private static volatile LogcatFileManager INSTANCE = null;
    private static String PATH_LOGCAT;
    private LogDumper mLogDumper = null;
    private int mPId;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private boolean isFirstStart = true;

    public static LogcatFileManager getInstance() {
        if (INSTANCE == null) {
            synchronized (LogcatFileManager.class) {
                if (INSTANCE == null)
                    INSTANCE = new LogcatFileManager();
            }
        }
        return INSTANCE;
    }

    private LogcatFileManager() {
        mPId = android.os.Process.myPid();
    }

    public void startLogcatManager(Context context) {
        isFirstStart = true;
        String folderPath = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "SnailVRPlayer/Logcat";
        } else {
            folderPath = context.getFilesDir().getAbsolutePath() + File.separator + "SnailVRPlayer/Logcat";
        }
        start(folderPath);
    }

    public void stopLogcatManager() {
        stop();
    }

    private void setFolderPath(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("The logcat folder path is not a directory: " + folderPath);
        }

        PATH_LOGCAT = folderPath.endsWith("/") ? folderPath : folderPath + "/";
    }

    private void start(String saveDirectoy) {
        setFolderPath(saveDirectoy);
        if (mLogDumper == null) {
            mLogDumper = new LogDumper(String.valueOf(mPId), PATH_LOGCAT);
            mLogDumper.start();
        }
    }

    private void stop() {
        if (mLogDumper != null) {
            mLogDumper.stopLogs();
            mLogDumper = null;
        }
    }

    private class LogDumper extends Thread {
        private Process logcatProc;
        private BufferedReader mReader = null;
        private boolean mRunning = true;
        private String cmds = null;
        private String mPID;
        private FileOutputStream out = null;

        public LogDumper(String pid, String dir) {
            mPID = pid;
            try {
                File file = new File(dir, "logcat.log");
                if (isFirstStart) {
                    isFirstStart = false;
                    FileOutputStream outputStream = new FileOutputStream(file, false);
                    try {
                        outputStream.write("".getBytes());
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (outputStream != null) {
                            try {
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                out = new FileOutputStream(file, true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        /**
         *
         * 日志等级：*:v , *:d , *:w , *:e , *:f , *:s
         *
         * 显示当前mPID程序的 E和W等级的日志.
         *
         * */

            // cmds = "logcat *:e *:w *:i | grep \"(" + mPID + ")\"";
            // cmds = "logcat  | grep \"(" + mPID + ")\"";//打印所有日志信息
            // cmds = "logcat -s way";//打印标签过滤信息
            cmds = "logcat | grep \"(" + mPID + ")\"";
        }

        public void stopLogs() {
            mRunning = false;
        }

        @Override
        public void run() {
            try {
                logcatProc = Runtime.getRuntime().exec(cmds);
                mReader = new BufferedReader(new InputStreamReader(logcatProc.getInputStream()), 1024);
                String line = null;
                while (mRunning) {

                    if (!mRunning) {
                        break;
                    }
                    if ((line = mReader.readLine()) == null || line.length() == 0) {
                        continue;
                    }
                   // Log.d("Test", line);
                    if (out != null && line.contains(mPID)) {
                        out.write((simpleDateFormat.format(new Date()) + "：" + line + "\n").getBytes());
                        out.flush();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (logcatProc != null) {
                    logcatProc.destroy();
                    logcatProc = null;
                }
                if (mReader != null) {
                    try {
                        mReader.close();
                        mReader = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out = null;
                }
            }
        }

    }
}