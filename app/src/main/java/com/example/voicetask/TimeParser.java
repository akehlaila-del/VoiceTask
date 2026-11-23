
package com.example.voicetask;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeParser {

    // Normaliser le texte
    private String normalizeText(String text) {

        if (text == null) {
            return "";
        }

        String normalized = text.toLowerCase(Locale.ROOT)
                .replace("’", " ")
                .replace("'", " ")
                .replace("-", " ");

        normalized = Normalizer.normalize(
                normalized,
                Normalizer.Form.NFD
        );

        normalized = normalized.replaceAll("\\p{M}", "");
        normalized = normalized.replaceAll("\\s+", " ");

        return normalized.trim();
    }

    // Convertir un nombre écrit en lettres
    private int parseNumberWord(String word) {

        switch (word.trim()) {
            case "un":
            case "une": return 1;
            case "deux": return 2;
            case "trois": return 3;
            case "quatre": return 4;
            case "cinq": return 5;
            case "six": return 6;
            case "sept": return 7;
            case "huit": return 8;
            case "neuf": return 9;
            case "dix": return 10;
            case "onze": return 11;
            case "douze": return 12;
            case "treize": return 13;
            case "quatorze": return 14;
            case "quinze": return 15;
            case "seize": return 16;
            case "dix sept": return 17;
            case "dix huit": return 18;
            case "dix neuf": return 19;
            case "vingt": return 20;
            case "vingt et un": return 21;
            case "vingt deux": return 22;
            case "vingt trois": return 23;
            case "vingt quatre": return 24;
            case "vingt cinq": return 25;
            case "vingt six": return 26;
            case "vingt sept": return 27;
            case "vingt huit": return 28;
            case "vingt neuf": return 29;
            case "trente": return 30;
            case "trente cinq": return 35;
            case "quarante": return 40;
            case "quarante cinq": return 45;
            case "cinquante": return 50;
            case "cinquante cinq": return 55;
            default: return -1;
        }
    }

    private int parseNumber(String value) {

        if (value == null) {
            return 0;
        }

        value = value.trim();

        if (value.matches("\\d+")) {
            return Integer.parseInt(value);
        }

        return parseNumberWord(value);
    }

    // Formater l'heure au format HH:mm
    private String formatTime(int hour, int minute) {

        if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
            return null;
        }

        return String.format(Locale.ROOT, "%02d:%02d", hour, minute);
    }

    // Détecter midi et minuit avec leurs minutes éventuelles
    private String extractSpecialTime(String text) {

        Pattern pattern = Pattern.compile(
                "\\b(midi|minuit)"
                        + "(?:\\s+(?:et\\s+)?"
                        + "(quart|demi|demie|quinze|trente|"
                        + "quarante cinq|\\d{1,2}))?\\b"
        );

        Matcher matcher = pattern.matcher(text);

        if (!matcher.find()) {
            return null;
        }

        int hour = matcher.group(1).equals("midi") ? 12 : 0;
        int minute = extractMinutes(matcher.group(2));

        return formatTime(hour, minute);
    }

    // Adapter l'heure selon la période de la journée
    private int adjustHour(int hour, String period) {

        if (period == null) {
            return hour;
        }

        if (period.contains("apres midi")
                || period.contains("du soir")
                || period.contains("de la soiree")
                || period.contains("en soiree")) {

            if (hour >= 1 && hour <= 11) {
                return hour + 12;
            }
        }

        return hour;
    }

    // Extraire les minutes
    private int extractMinutes(String expression) {

        if (expression == null || expression.trim().isEmpty()) {
            return 0;
        }

        expression = expression.trim();

        switch (expression) {
            case "quart":
            case "quinze":
                return 15;

            case "demi":
            case "demie":
            case "trente":
                return 30;

            case "quarante cinq":
                return 45;

            default:
                return parseNumber(expression);
        }
    }

    // Extraire les heures au format numérique
    private String extractNumericTime(String text) {

        // Format 20:30
        Pattern colonPattern = Pattern.compile(
                "\\b([01]?\\d|2[0-3]):([0-5]\\d)\\b"
        );

        Matcher colonMatcher = colonPattern.matcher(text);

        if (colonMatcher.find()) {

            int hour = Integer.parseInt(colonMatcher.group(1));
            int minute = Integer.parseInt(colonMatcher.group(2));

            return formatTime(hour, minute);
        }

        // Format 20h30 ou 20 h 30
        Pattern compactPattern = Pattern.compile(
                "\\b([01]?\\d|2[0-3])\\s*h\\s*([0-5]\\d)\\b"
        );

        Matcher compactMatcher = compactPattern.matcher(text);

        if (compactMatcher.find()) {

            int hour = Integer.parseInt(compactMatcher.group(1));
            int minute = Integer.parseInt(compactMatcher.group(2));

            return formatTime(hour, minute);
        }

        // Format 7 heures moins le quart
        Pattern minusPattern = Pattern.compile(
                "\\b([01]?\\d|2[0-3])\\s*(?:h|heures?)"
                        + "(?:\\s+(du matin|du soir|de l apres midi))?"
                        + "\\s+moins\\s+(?:le\\s+)?quart\\b"
        );

        Matcher minusMatcher = minusPattern.matcher(text);

        if (minusMatcher.find()) {

            int hour = Integer.parseInt(minusMatcher.group(1));
            String period = minusMatcher.group(2);

            hour = adjustHour(hour, period);

            return formatTime(hour - 1, 45);
        }

        // Format 7 heures ou 7 heures 30
        Pattern hourPattern = Pattern.compile(
                "\\b([01]?\\d|2[0-3])\\s*(?:h|heures?)"
                        + "(?:\\s*(?:et\\s+)?"
                        + "(quart|demie|demi|quinze|trente|"
                        + "quarante cinq|\\d{1,2}))?"
                        + "(?:\\s+(du matin|du soir|de l apres midi))?\\b"
        );

        Matcher hourMatcher = hourPattern.matcher(text);

        if (hourMatcher.find()) {

            int hour = Integer.parseInt(hourMatcher.group(1));
            int minute = extractMinutes(hourMatcher.group(2));
            String period = hourMatcher.group(3);

            hour = adjustHour(hour, period);

            return formatTime(hour, minute);
        }

        return null;
    }

    // Expressions des heures écrites en lettres
    private String getHourExpression() {

        return "vingt et un|vingt deux|vingt trois|"
                + "dix sept|dix huit|dix neuf|"
                + "quatorze|treize|quinze|seize|"
                + "douze|onze|dix|neuf|huit|"
                + "sept|six|cinq|quatre|trois|"
                + "deux|vingt|une|un";
    }

    // Extraire les heures écrites en lettres
    private String extractWrittenTime(String text) {

        // Format sept heures moins le quart
        Pattern minusPattern = Pattern.compile(
                "\\b(" + getHourExpression() + ")"
                        + "\\s+heures?"
                        + "(?:\\s+(du matin|du soir|de l apres midi))?"
                        + "\\s+moins\\s+(?:le\\s+)?quart\\b"
        );

        Matcher minusMatcher = minusPattern.matcher(text);

        if (minusMatcher.find()) {

            int hour = parseNumberWord(minusMatcher.group(1));
            String period = minusMatcher.group(2);

            hour = adjustHour(hour, period);

            return formatTime(hour - 1, 45);
        }

        // Format vingt heures, sept heures et demie, etc.
        Pattern pattern = Pattern.compile(
                "\\b(" + getHourExpression() + ")"
                        + "\\s+heures?"
                        + "(?:\\s+(?:et\\s+)?"
                        + "(quart|demie|demi|quinze|trente|"
                        + "quarante cinq|\\d{1,2}))?"
                        + "(?:\\s+(du matin|du soir|de l apres midi))?\\b"
        );

        Matcher matcher = pattern.matcher(text);

        if (!matcher.find()) {
            return null;
        }

        int hour = parseNumberWord(matcher.group(1));
        int minute = extractMinutes(matcher.group(2));
        String period = matcher.group(3);

        hour = adjustHour(hour, period);

        return formatTime(hour, minute);
    }

    // Méthode principale d'extraction de l'heure
    public String extractTime(String text) {

        String normalizedText = normalizeText(text);

        if (normalizedText.isEmpty()) {
            return null;
        }

        String specialTime = extractSpecialTime(normalizedText);

        if (specialTime != null) {
            return specialTime;
        }

        String numericTime = extractNumericTime(normalizedText);

        if (numericTime != null) {
            return numericTime;
        }

        return extractWrittenTime(normalizedText);
    }
}
