package com.maplabs.mapprotect.utils;

import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("<#([A-Fa-f0-9]{6})>");
    private static final Pattern GRADIENT_PATTERN = Pattern.compile("<gradient:#([A-Fa-f0-9]{6}):#([A-Fa-f0-9]{6})>(.*?)</gradient>");

    public static String color(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }

        Matcher gradientMatcher = GRADIENT_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();
        while (gradientMatcher.find()) {
            String hexStart = gradientMatcher.group(1);
            String hexEnd = gradientMatcher.group(2);
            String text = gradientMatcher.group(3);
            gradientMatcher.appendReplacement(buffer, generateGradient(text, hexStart, hexEnd));
        }
        gradientMatcher.appendTail(buffer);
        message = buffer.toString();

        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer hexBuffer = new StringBuffer();
        
        while (matcher.find()) {
            String hex = matcher.group(1);
            StringBuilder magic = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                magic.append('§').append(c);
            }
            matcher.appendReplacement(hexBuffer, magic.toString());
        }
        
        matcher.appendTail(hexBuffer);
        return ChatColor.translateAlternateColorCodes('&', hexBuffer.toString());
    }

    private static String generateGradient(String text, String hexStart, String hexEnd) {
        StringBuilder formats = new StringBuilder();
        String cleanText = text;
        String[] formatCodes = {"&l", "&o", "&n", "&m", "&k", "§l", "§o", "§n", "§m", "§k"};
        for (String code : formatCodes) {
            if (cleanText.contains(code)) {
                formats.append("§").append(code.charAt(1));
                cleanText = cleanText.replace(code, "");
            }
        }

        java.awt.Color c1 = new java.awt.Color(Integer.parseInt(hexStart, 16));
        java.awt.Color c2 = new java.awt.Color(Integer.parseInt(hexEnd, 16));
        StringBuilder result = new StringBuilder();
        int len = cleanText.length();
        for (int i = 0; i < len; i++) {
            float ratio = len > 1 ? (float) i / (len - 1) : 0;
            int red = (int) (c1.getRed() * (1 - ratio) + c2.getRed() * ratio);
            int green = (int) (c1.getGreen() * (1 - ratio) + c2.getGreen() * ratio);
            int blue = (int) (c1.getBlue() * (1 - ratio) + c2.getBlue() * ratio);
            String hex = String.format("%02x%02x%02x", red, green, blue);
            result.append("§x");
            for (char c : hex.toCharArray()) {
                result.append('§').append(c);
            }
            result.append(formats.toString());
            result.append(cleanText.charAt(i));
        }
        return result.toString();
    }
}
