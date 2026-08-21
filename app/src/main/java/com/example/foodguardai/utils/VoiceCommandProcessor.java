package com.example.foodguardai.utils;

import android.util.Log;

import com.example.foodguardai.logging.TestLog;
import com.example.foodguardai.logging.TestLogManager;

public class VoiceCommandProcessor {
    private static final String TAG = "VoiceCommandProcessor";

    public enum CommandType {
        SCAN_BARCODE,
        SCAN_LABEL,
        ADD_TO_DIET,
        SHOW_HISTORY,
        ASK_NUTRITION,
        UNKNOWN
    }

    public static class ProcessedCommand {
        public CommandType type;
        public String parameter;
        public String originalCommand;

        public ProcessedCommand(CommandType type, String parameter, String originalCommand) {
            this.type = type;
            this.parameter = parameter;
            this.originalCommand = originalCommand;
        }
    }

    public static ProcessedCommand processCommand(String command) {
        String lowercaseCommand = command.toLowerCase().trim();
        Log.d(TAG, "Processing command: " + lowercaseCommand);
        long startTime = System.currentTimeMillis();

        ProcessedCommand result;

        // Scan barcode
        if (lowercaseCommand.contains("scan") &&
                (lowercaseCommand.contains("barcode") || lowercaseCommand.contains("code"))) {
            result = new ProcessedCommand(CommandType.SCAN_BARCODE, null, command);
        }
        // Scan label
        else if (lowercaseCommand.contains("scan") &&
                (lowercaseCommand.contains("label") || lowercaseCommand.contains("text"))) {
            result = new ProcessedCommand(CommandType.SCAN_LABEL, null, command);
        }
        // Add to diet
        else if ((lowercaseCommand.contains("add") && lowercaseCommand.contains("diet")) ||
                lowercaseCommand.contains("track this") ||
                lowercaseCommand.contains("save")) {
            result = new ProcessedCommand(CommandType.ADD_TO_DIET, null, command);
        }
        // Show history
        else if (lowercaseCommand.contains("history") ||
                lowercaseCommand.contains("show") && lowercaseCommand.contains("scanned")) {
            result = new ProcessedCommand(CommandType.SHOW_HISTORY, null, command);
        }
        // Ask about nutrition (catch-all for questions)
        else if (lowercaseCommand.contains("what") ||
                lowercaseCommand.contains("how") ||
                lowercaseCommand.contains("is this") ||
                lowercaseCommand.contains("tell me")) {
            result = new ProcessedCommand(CommandType.ASK_NUTRITION, command, command);
        }
        else {
            result = new ProcessedCommand(CommandType.UNKNOWN, null, command);
        }

        long latency = System.currentTimeMillis() - startTime;
        boolean matched = result.type != CommandType.UNKNOWN;
        TestLog voiceLog = TestLogManager.getInstance().createVoiceLog(
                command,
                command,
                result.type.name(),
                result.type.name(),
                matched,
                latency
        );
        TestLogManager.getInstance().log(voiceLog);

        return result;
    }
}