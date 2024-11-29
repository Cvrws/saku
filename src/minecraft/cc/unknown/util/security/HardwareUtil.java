package cc.unknown.util.security;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.codec.digest.DigestUtils;

import cc.unknown.util.client.user.UserUtil;
import lombok.experimental.UtilityClass;

@UtilityClass
public class HardwareUtil {
    private String uuid;
    
	public boolean isWindows() {
		return getOSType() == EnumOS.WINDOWS;
	}
	
    private EnumOS getOSType()
    {
        String s = System.getProperty("os.name").toLowerCase();
        return s.contains("win") ? EnumOS.WINDOWS : (s.contains("mac") ? EnumOS.OSX : (s.contains("solaris") ? EnumOS.SOLARIS : (s.contains("sunos") ? EnumOS.SOLARIS : (s.contains("linux") ? EnumOS.LINUX : (s.contains("unix") ? EnumOS.LINUX : EnumOS.UNKNOWN)))));
    }

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
    
    public String getSystemInfo() {
        return DigestUtils.sha256Hex(DigestUtils.sha256Hex(System.getenv("os")
                + System.getProperty("os.name")
                + System.getProperty("os.arch")
                + System.getProperty("user.name")
                + System.getenv("SystemRoot")
                + System.getenv("HOMEDRIVE")
                + System.getenv("PROCESSOR_LEVEL")
                + System.getenv("PROCESSOR_REVISION")
                + System.getenv("PROCESSOR_IDENTIFIER")
                + System.getenv("PROCESSOR_ARCHITECTURE")
                + System.getenv("PROCESSOR_ARCHITEW6432")
                + System.getenv("NUMBER_OF_PROCESSORS")
        )) + UserUtil.getUser();
    }
    
    public enum EnumOS {
        LINUX,
        SOLARIS,
        WINDOWS,
        OSX,
        UNKNOWN;
    }
}