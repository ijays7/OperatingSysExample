package com.ijays.operatonsysexample.utils;

import com.facebook.common.file.FileUtils;
import com.ijays.operatonsysexample.App;
import com.ijays.operatonsysexample.AppConstants;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by idhyt on 15/11/3.
 */


public class IOHelper {
    static String strReadContent = "";


    public static final String COMMAND_SU = "su";
    public static final String COMMAND_SH = "sh";
    public static final String COMMAND_EXIT = "exit\n";
    public static final String COMMAND_LINE_END = "\n";

    // I/O Scheduler
    // /sys/block/mmcblk0/queue/scheduler
    public static String[] getAvailableScheduler() {
        String[] availableScheduler = {"null"};
        try {
            availableScheduler = readFileContent(AppConstants.SCHEDULER).
                    replace("\n", "").split(" ");

        } catch (Exception ep) {
            ep.printStackTrace();
        }
        return availableScheduler;
    }

    public static String getCurScheduler() {
        String curScheduler = "";
        try {
            String strAvailableScheduler = readFileContent(AppConstants.SCHEDULER);
            curScheduler = strAvailableScheduler.substring(strAvailableScheduler.indexOf("[") + 1,
                    strAvailableScheduler.indexOf("]")).trim();

        } catch (Exception ep) {
            ep.printStackTrace();
        }
        return curScheduler;
    }

    public static void setCurScheduler(String data) {
//        try {
//            RCommand.setEnablePrivilege(Constants.SCHEDULER, true);
//            RCommand.writeFileContent(Constants.SCHEDULER, data);
//            RCommand.setEnablePrivilege(Constants.SCHEDULER, false);
//
//        }catch (Exception ep){
//            ep.printStackTrace();
//        }
    }

    // READ_AHEAD_KB
    public static String getCurReadAhead() {
//        String curReadAhead = "";
//        try {
//            curReadAhead = RCommand.readFileContent(Constants.READ_AHEAD_KB).
//                    replace("\n", "") + " kb";
//
//        } catch (Exception ep) {
//            ep.printStackTrace();
//        }
        return null;
    }

    public static void setCurReadAhead(String data) {
//        try {
//            RCommand.setEnablePrivilege(Constants.READ_AHEAD_KB, true);
//            RCommand.writeFileContent(Constants.READ_AHEAD_KB, data);
//            RCommand.setEnablePrivilege(Constants.READ_AHEAD_KB, false);
//        } catch (Exception ep) {
//            ep.printStackTrace();
//        }
    }

    // i/o scheduler status
    public static String getIOSchedulerStatusContent() {
        String ioSchedulerStatus = "get i/o scheduler status error";
        try {
            String curIOScheduler = getCurScheduler();
            String curReadAhead = getCurReadAhead();
            ioSchedulerStatus = "I/O Scheduler: " + curIOScheduler + "\n" +
                    "Read Ahead: " + curReadAhead;

        } catch (Exception ep) {
            ep.printStackTrace();
        }
        return ioSchedulerStatus;
    }

    public static String readFileContent(File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }

        if (file.canRead()) {
            strReadContent = org.apache.commons.io.FileUtils.readFileToString(file);
        } else {
            CommandResult cmdResult =
                    execCommand("cat " + file.getAbsolutePath(), true);
            strReadContent = cmdResult.successMsg;
        }
        return strReadContent;
    }

    public static class CommandResult {

        /**
         * result of command
         **/
        public int result;
        /**
         * success message of command result
         **/
        public String successMsg;
        /**
         * error message of command result
         **/
        public String errorMsg;

        public CommandResult(int result) {
            this.result = result;
        }

        public CommandResult(int result, String successMsg, String errorMsg) {
            this.result = result;
            this.successMsg = successMsg;
            this.errorMsg = errorMsg;
        }
    }
    public static CommandResult execCommand(String command, boolean isRoot) {
        return execCommand(new String[]{command}, isRoot, true);
    }

    /**
     * execute shell commands
     *
     * @param commands        command array
     * @param isRoot          whether need to run with root
     * @param isNeedResultMsg whether need result msg
     * @return <ul>
     * <li>if isNeedResultMsg is false, {@link CommandResult#successMsg} is null and
     * {@link CommandResult#errorMsg} is null.</li>
     * <li>if {@link CommandResult#result} is -1, there maybe some excepiton.</li>
     * </ul>
     */
    public static CommandResult execCommand(String[] commands, boolean isRoot, boolean isNeedResultMsg) {
        int result = -1;
        if (commands == null || commands.length == 0) {
            return new CommandResult(result, null, null);
        }

        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;

        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }

                // donnot use os.writeBytes(commmand), avoid chinese charset error
                os.write(command.getBytes());
                os.writeBytes(COMMAND_LINE_END);
                os.flush();
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();

            result = process.waitFor();
            // get command result
            if (isNeedResultMsg) {
                successMsg = new StringBuilder();
                errorMsg = new StringBuilder();
                successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String s;
                while ((s = successResult.readLine()) != null) {
                    successMsg.append(s);
                }
                while ((s = errorResult.readLine()) != null) {
                    errorMsg.append(s);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (process != null) {
                process.destroy();
            }
        }
        return new CommandResult(result, successMsg == null ? null : successMsg.toString(), errorMsg == null ? null
                : errorMsg.toString());
    }
}
