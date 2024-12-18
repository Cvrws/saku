package cc.unknown.util.client.security;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import lombok.experimental.UtilityClass;

@UtilityClass
public class HardwareUtil {
    private String uuid;

    public String getUuid() {
        if (uuid != null) return uuid;

        String output = executeCommand("wmic csproduct get uuid");
        uuid = extractValue(output);

        if (uuid == null) {
            uuid = "Not found uuid";
        }

        return uuid;
    }

    private String executeCommand(String command) {
        StringBuilder output = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    private String extractValue(String output) {
        if (output == null || output.isEmpty()) return null;
        String[] lines = output.split("\n");
        StringBuilder result = new StringBuilder();
        boolean isValue;
        for (String line : lines) {
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty()) continue;
            isValue = !trimmedLine.contains(":") && !trimmedLine.equalsIgnoreCase("Caption") && !trimmedLine.equalsIgnoreCase("Name") && !trimmedLine.equalsIgnoreCase("SerialNumber") && !trimmedLine.equalsIgnoreCase("UUID") && !trimmedLine.equalsIgnoreCase("Version");
            if (isValue) {
                result.append(trimmedLine).append("\n");
            }
        }

        return result.toString().trim();
    }
}