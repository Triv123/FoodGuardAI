package com.example.foodguardai.utils;

import android.util.Log;

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

        // Scan barcode
        if (lowercaseCommand.contains("scan") &&
                (lowercaseCommand.contains("barcode") || lowercaseCommand.contains("code"))) {
            return new ProcessedCommand(CommandType.SCAN_BARCODE, null, command);
        }

        // Scan label
        if (lowercaseCommand.contains("scan") &&
                (lowercaseCommand.contains("label") || lowercaseCommand.contains("text"))) {
            return new ProcessedCommand(CommandType.SCAN_LABEL, null, command);
        }

        // Add to diet
        if ((lowercaseCommand.contains("add") && lowercaseCommand.contains("diet")) ||
                lowercaseCommand.contains("track this") ||
                lowercaseCommand.contains("save")) {
            return new ProcessedCommand(CommandType.ADD_TO_DIET, null, command);
        }

        // Show history
        if (lowercaseCommand.contains("history") ||
                lowercaseCommand.contains("show") && lowercaseCommand.contains("scanned")) {
            return new ProcessedCommand(CommandType.SHOW_HISTORY, null, command);
        }

        // Ask about nutrition (catch-all for questions)
        if (lowercaseCommand.contains("what") ||
                lowercaseCommand.contains("how") ||
                lowercaseCommand.contains("is this") ||
                lowercaseCommand.contains("tell me")) {
            return new ProcessedCommand(CommandType.ASK_NUTRITION, command, command);
        }

        return new ProcessedCommand(CommandType.UNKNOWN, null, command);
    }
}