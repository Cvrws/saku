package net.minecraft.util;

import java.util.Arrays;
import java.util.regex.Pattern;

import cc.unknown.util.render.font.Font;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {
    private final Pattern patternControlCode = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

    public String ticksToElapsedTime(final int ticks) {
        int i = ticks / 20;
        final int j = i / 60;
        i = i % 60;
        return i < 10 ? j + ":0" + i : j + ":" + i;
    }

    public String stripControlCodes(final String p_76338_0_) {
        return patternControlCode.matcher(p_76338_0_).replaceAll("");
    }

    public boolean isNullOrEmpty(final String string) {
        return org.apache.commons.lang3.StringUtils.isEmpty(string);
    }

    public String getToFit(Font font, String string, double length) {
        double l = 0;
        int index = 0;
        StringBuilder stringBuilder = new StringBuilder();

        while (l < length && index < string.length()) {
            String character = String.valueOf(string.charAt(index));
            l += font.width(character);
            index++;
            stringBuilder.append(character);
        }

        return stringBuilder.toString();
    }
    
    public double getSimilarity(String str1, String str2) {
        int len1 = str1.length();
        int len2 = str2.length();

        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            for (int j = 0; j <= len2; j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = min(dp[i - 1][j - 1]
                                    + costOfSubstitution(str1.charAt(i - 1), str2.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1);
                }
            }
        }

        int maxLen = Math.max(len1, len2);
        return 1 - (double) dp[len1][len2] / maxLen;
    }

    private int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    private int min(int... numbers) {
        return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
    }
}