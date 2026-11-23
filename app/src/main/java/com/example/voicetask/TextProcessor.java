
package com.example.voicetask;

import java.text.Normalizer;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextProcessor {

    private final DateTimeParser dateTimeParser;
    private final TimeParser timeParser;

    public TextProcessor() {
        dateTimeParser = new DateTimeParser();
        timeParser = new TimeParser();
    }

    // Nettoyer le texte reçu
    public String cleanText(String text) {
        if (text == null) {
            return "";
        }
        return text.trim().replaceAll("\\s+", " ");
    }

    // Normaliser le texte : minuscules, sans accents
    private String normalizeText(String text) {

        String normalized = cleanText(text).toLowerCase(Locale.ROOT);

        normalized = normalized.replace("’", " ");
        normalized = normalized.replace("'", " ");
        normalized = normalized.replace("-", " ");

        normalized = Normalizer.normalize(
                normalized,
                Normalizer.Form.NFD
        );

        normalized = normalized.replaceAll("\\p{M}", "");
        normalized = normalized.replaceAll("\\s+", " ");

        return normalized.trim();
    }

    // Convertir un nombre écrit en chiffres ou en lettres
    private int parseNumber(String numberText) {

        if (numberText == null) {
            return -1;
        }

        String number = numberText.trim();

        if (number.matches("\\d+")) {
            try {
                return Integer.parseInt(number);
            } catch (NumberFormatException e) {
                return -1;
            }
        }

        switch (number) {
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
            default: return -1;
        }
    }

    // Expression des nombres reconnus
    private String getNumberExpression() {

        return "\\d+|"
                + "vingt et un|vingt deux|vingt trois|"
                + "vingt quatre|vingt cinq|vingt six|"
                + "vingt sept|vingt huit|vingt neuf|"
                + "dix sept|dix huit|dix neuf|"
                + "quatre|quinze|quatorze|treize|"
                + "douze|trente|vingt|seize|"
                + "onze|deux|trois|cinq|"
                + "sept|huit|neuf|dix|"
                + "six|un|une";
    }

    // Expression régulière des durées
    private String getDurationExpression() {

        return "\\b(" + getNumberExpression() + ")"
                + "\\s+(jours?|semaines?|mois|ans?|annees?)\\b";
    }

    // Premier jour de la semaine : lundi
    private Calendar getStartOfWeek() {

        Calendar date = dateTimeParser.getToday();

        int day = date.get(Calendar.DAY_OF_WEEK);
        int daysSinceMonday = (day + 5) % 7;

        date.add(Calendar.DAY_OF_MONTH, -daysSinceMonday);

        return date;
    }

    // Premier jour du mois
    private Calendar getStartOfMonth(int offset) {

        Calendar date = dateTimeParser.getToday();

        date.set(Calendar.DAY_OF_MONTH, 1);
        date.add(Calendar.MONTH, offset);

        return date;
    }

    // Premier jour de l'année
    private Calendar getStartOfYear(int offset) {

        Calendar date = dateTimeParser.getToday();

        date.set(Calendar.DAY_OF_YEAR, 1);
        date.add(Calendar.YEAR, offset);

        return date;
    }

    // Calculer une date correspondant à un jour de la semaine
    private Calendar getWeekdayDate(int targetDay, int direction) {

        Calendar date = dateTimeParser.getToday();

        int currentDay = date.get(Calendar.DAY_OF_WEEK);
        int difference;

        if (direction > 0) {

            difference = (targetDay - currentDay + 7) % 7;

            if (difference == 0) {
                difference = 7;
            }

        } else if (direction < 0) {

            difference = -((currentDay - targetDay + 7) % 7);

            if (difference == 0) {
                difference = -7;
            }

        } else {

            difference = (targetDay - currentDay + 7) % 7;
        }

        date.add(Calendar.DAY_OF_MONTH, difference);

        return date;
    }

    // Extraire un jour de la semaine
    private Calendar extractWeekday(String text) {

        String[] weekdays = {
                "dimanche", "lundi", "mardi",
                "mercredi", "jeudi", "vendredi", "samedi"
        };

        int[] calendarDays = {
                Calendar.SUNDAY,
                Calendar.MONDAY,
                Calendar.TUESDAY,
                Calendar.WEDNESDAY,
                Calendar.THURSDAY,
                Calendar.FRIDAY,
                Calendar.SATURDAY
        };

        int direction = 0;

        if (text.contains("prochain") || text.contains("suivant")) {
            direction = 1;
        } else if (text.contains("dernier")
                || text.contains("derniere")
                || text.contains("passe")
                || text.contains("precedent")
                || text.contains("precedente")) {
            direction = -1;
        }

        for (int i = 0; i < weekdays.length; i++) {

            Pattern pattern = Pattern.compile(
                    "\\b" + weekdays[i] + "\\b"
            );

            Matcher matcher = pattern.matcher(text);

            if (matcher.find()) {
                return getWeekdayDate(calendarDays[i], direction);
            }
        }

        return null;
    }

    // Extraire une date à partir d'une durée passée ou future
    private Calendar extractDuration(String text) {

        boolean isPast = text.contains("il y a ");
        boolean isFuture = text.contains("dans ");

        if (!isPast && !isFuture) {
            return null;
        }

        Pattern pattern = Pattern.compile(getDurationExpression());
        Matcher matcher = pattern.matcher(text);

        int days = 0;
        int weeks = 0;
        int months = 0;
        int years = 0;

        boolean found = false;

        while (matcher.find()) {

            String numberText = matcher.group(1);
            String unit = matcher.group(2);

            if (numberText == null || unit == null) {
                continue;
            }

            int number = parseNumber(numberText);

            if (number <= 0) {
                continue;
            }

            found = true;

            if (unit.startsWith("jour")) {
                days += number;
            } else if (unit.startsWith("semaine")) {
                weeks += number;
            } else if (unit.equals("mois")) {
                months += number;
            } else if (unit.startsWith("an")
                    || unit.startsWith("annee")) {
                years += number;
            }
        }

        if (!found) {
            return null;
        }

        if (isPast) {
            days = -days;
            weeks = -weeks;
            months = -months;
            years = -years;
        }

        return dateTimeParser.getDateAfterDuration(
                days, weeks, months, years
        );
    }

    // Convertir le nom d'un mois en numéro Calendar
    private int getMonthNumber(String month) {

        if (month == null) {
            return -1;
        }

        switch (month) {
            case "janvier": return Calendar.JANUARY;
            case "fevrier": return Calendar.FEBRUARY;
            case "mars": return Calendar.MARCH;
            case "avril": return Calendar.APRIL;
            case "mai": return Calendar.MAY;
            case "juin": return Calendar.JUNE;
            case "juillet": return Calendar.JULY;
            case "aout": return Calendar.AUGUST;
            case "septembre": return Calendar.SEPTEMBER;
            case "octobre": return Calendar.OCTOBER;
            case "novembre": return Calendar.NOVEMBER;
            case "decembre": return Calendar.DECEMBER;
            default: return -1;
        }
    }

    // Construire une date après avoir vérifié sa validité
    private Calendar createExactDate(int day, int month, int year) {

        Calendar date = Calendar.getInstance();

        date.clear();
        date.setLenient(false);

        try {
            date.set(year, month, day, 0, 0, 0);
            date.getTime();
            return date;

        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // Extraire une date précise écrite avec un jour et un mois
    private Calendar extractNamedDate(String text) {

        String months =
                "janvier|fevrier|mars|avril|mai|juin|"
                        + "juillet|aout|septembre|octobre|"
                        + "novembre|decembre";

        Pattern pattern = Pattern.compile(
                "\\b(?:le\\s+)?(1er|premier|\\d{1,2})\\s+("
                        + months
                        + ")(?:\\s+(\\d{4}))?"
                        + "(?:\\s+de\\s+l\\s+annee\\s+"
                        + "(prochaine|derniere))?"
                        + "(?:\\s+(prochain|prochaine|suivant|suivante|"
                        + "dernier|derniere|passe|precedent|precedente))?\\b"
        );

        Matcher matcher = pattern.matcher(text);

        if (!matcher.find()) {
            return null;
        }

        String dayText = matcher.group(1);
        String monthText = matcher.group(2);
        String yearText = matcher.group(3);
        String yearExpression = matcher.group(4);
        String qualifier = matcher.group(5);

        int day = "premier".equals(dayText) || "1er".equals(dayText)
                ? 1 : parseNumber(dayText);

        int month = getMonthNumber(monthText);

        if (day < 1 || month < 0) {
            return null;
        }

        Calendar today = dateTimeParser.getToday();

        int year;

        if (yearText != null) {

            year = Integer.parseInt(yearText);

        } else {

            year = today.get(Calendar.YEAR);

            if ("prochaine".equals(yearExpression)) {
                year++;
            } else if ("derniere".equals(yearExpression)) {
                year--;
            }
        }

        Calendar candidate = createExactDate(day, month, year);

        if (candidate == null) {
            return null;
        }

        if (yearText == null && yearExpression == null) {

            boolean isPast = "dernier".equals(qualifier)
                    || "derniere".equals(qualifier)
                    || "passe".equals(qualifier)
                    || "precedent".equals(qualifier)
                    || "precedente".equals(qualifier);

            boolean isFuture = "prochain".equals(qualifier)
                    || "prochaine".equals(qualifier)
                    || "suivant".equals(qualifier)
                    || "suivante".equals(qualifier);

            if (isPast && !candidate.before(today)) {
                year--;
            } else if (isFuture && !candidate.after(today)) {
                year++;
            } else if (qualifier == null && candidate.before(today)) {
                year++;
            }

            candidate = createExactDate(day, month, year);

            if (candidate == null) {
                return null;
            }
        }

        return candidate;
    }

    // Extraire une date du type : le 3 du mois prochain
    private Calendar extractDayOfNextMonth(String text) {

        Pattern pattern = Pattern.compile(
                "\\b(?:le\\s+)?(\\d{1,2})\\s+du\\s+mois\\s+prochain\\b"
        );

        Matcher matcher = pattern.matcher(text);

        if (!matcher.find()) {
            return null;
        }

        int day = Integer.parseInt(matcher.group(1));

        Calendar date = Calendar.getInstance();

        date.set(Calendar.DAY_OF_MONTH, 1);
        date.add(Calendar.MONTH, 1);

        return createExactDate(
                day,
                date.get(Calendar.MONTH),
                date.get(Calendar.YEAR)
        );
    }

    // Extraire une date numérique : 15/11/2026 ou 15-11-2026
    private Calendar extractNumericDate(String text) {

        Pattern pattern = Pattern.compile(
                "\\b(\\d{1,2})[/-](\\d{1,2})"
                        + "(?:[/-](\\d{2,4}))?\\b"
        );

        Matcher matcher = pattern.matcher(text);

        if (!matcher.find()) {
            return null;
        }

        int day = Integer.parseInt(matcher.group(1));
        int month = Integer.parseInt(matcher.group(2)) - 1;

        String yearText = matcher.group(3);

        Calendar today = dateTimeParser.getToday();

        int year;

        if (yearText != null) {
            year = Integer.parseInt(yearText);

            if (yearText.length() == 2) {
                year += year < 50 ? 2000 : 1900;
            }

        } else {
            year = today.get(Calendar.YEAR);
        }

        Calendar candidate = createExactDate(day, month, year);

        if (candidate == null) {
            return null;
        }

        if (yearText == null && candidate.before(today)) {
            candidate = createExactDate(day, month, year + 1);
        }

        return candidate;
    }

    // Reconnaître les moments de la journée
    private Calendar extractDayPeriodDate(String text) {

        if (text.matches(".*\\b(hier|la veille)\\b.*")
                || text.contains("nuit derniere")
                || text.contains("soiree derniere")) {
            return dateTimeParser.getYesterday();
        }

        if (text.matches(".*\\bdemain\\b.*")
                || text.contains("lendemain")) {
            return dateTimeParser.getTomorrow();
        }

        if (text.contains("ce matin")
                || text.contains("cette matinee")
                || text.contains("dans la matinee")
                || text.contains("en matinee")
                || text.contains("cet apres midi")
                || text.contains("cette apres midi")
                || text.contains("au cours de l apres midi")
                || text.contains("dans l apres midi")
                || text.contains("ce soir")
                || text.contains("en soiree")
                || text.contains("cette nuit")
                || text.contains("aujourd hui matin")
                || text.contains("aujourd hui soir")) {
            return dateTimeParser.getToday();
        }

        return null;
    }

    // Extraire une date à partir du texte reçu
    public Calendar extractDate(String text) {

        String normalizedText = normalizeText(text);

        if (normalizedText.isEmpty()) {
            return null;
        }

        Calendar numericDate = extractNumericDate(normalizedText);

        if (numericDate != null) {
            return numericDate;
        }

        Calendar nextMonthDate = extractDayOfNextMonth(normalizedText);

        if (nextMonthDate != null) {
            return nextMonthDate;
        }

        Calendar namedDate = extractNamedDate(normalizedText);

        if (namedDate != null) {
            return namedDate;
        }

        if (normalizedText.matches(
                ".*\\b(maintenant|actuellement|presentement|"
                        + "tout de suite|en ce moment|a present|"
                        + "a l instant|des maintenant)\\b.*"
        )) {
            return dateTimeParser.getNow();
        }

        Calendar dayPeriodDate = extractDayPeriodDate(normalizedText);

        if (dayPeriodDate != null) {
            return dayPeriodDate;
        }

        if (normalizedText.matches(
                ".*\\b(aujourd hui|ce jour|en cette journee)\\b.*"
        )) {
            return dateTimeParser.getToday();
        }

        if (normalizedText.contains("avant hier")) {
            return dateTimeParser.getDayBeforeYesterday();
        }

        if (normalizedText.matches(
                ".*\\b(hier|la veille|jour precedent)\\b.*"
        )) {
            return dateTimeParser.getYesterday();
        }

        if (normalizedText.contains("apres demain")) {
            return dateTimeParser.getDayAfterTomorrow();
        }

        if (normalizedText.matches(
                ".*\\b(demain|lendemain|jour suivant)\\b.*"
        )) {
            return dateTimeParser.getTomorrow();
        }

        if (normalizedText.contains("cette semaine")
                || normalizedText.contains("la semaine en cours")) {
            return getStartOfWeek();
        }

        if (normalizedText.contains("semaine prochaine")
                || normalizedText.contains("prochaine semaine")
                || normalizedText.contains("semaine suivante")) {

            Calendar date = getStartOfWeek();
            date.add(Calendar.WEEK_OF_YEAR, 1);
            return date;
        }

        if (normalizedText.contains("semaine derniere")
                || normalizedText.contains("semaine passee")
                || normalizedText.contains("derniere semaine")
                || normalizedText.contains("semaine precedente")) {

            Calendar date = getStartOfWeek();
            date.add(Calendar.WEEK_OF_YEAR, -1);
            return date;
        }

        if (normalizedText.contains("ce mois ci")
                || normalizedText.contains("ce mois")) {
            return getStartOfMonth(0);
        }

        if (normalizedText.contains("mois prochain")
                || normalizedText.contains("prochain mois")
                || normalizedText.contains("mois suivant")) {
            return getStartOfMonth(1);
        }

        if (normalizedText.contains("mois dernier")
                || normalizedText.contains("mois passe")
                || normalizedText.contains("dernier mois")
                || normalizedText.contains("mois precedent")) {
            return getStartOfMonth(-1);
        }

        if (normalizedText.contains("cette annee")
                || normalizedText.contains("annee en cours")) {
            return getStartOfYear(0);
        }

        if (normalizedText.contains("annee prochaine")
                || normalizedText.contains("prochaine annee")
                || normalizedText.contains("annee suivante")) {
            return getStartOfYear(1);
        }

        if (normalizedText.contains("annee derniere")
                || normalizedText.contains("annee passee")
                || normalizedText.contains("derniere annee")
                || normalizedText.contains("annee precedente")) {
            return getStartOfYear(-1);
        }

        Calendar durationDate = extractDuration(normalizedText);

        if (durationDate != null) {
            return durationDate;
        }

        Calendar weekdayDate = extractWeekday(normalizedText);

        if (weekdayDate != null) {
            return weekdayDate;
        }

        return null;
    }

    // Extraire l'heure à partir du texte reçu
    public String extractTime(String text) {
        return timeParser.extractTime(text);
    }

    // Extraire le titre de la tâche
    public String extractTitle(String text) {

        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        String title = cleanText(text);
        String normalized = normalizeText(text);

        // Supprimer les heures reconnues par TimeParser
        String extractedTime = extractTime(text);

        if (extractedTime != null) {

            title = title.replaceAll(
                    "(?i)\\b(?:[01]?\\d|2[0-3])\\s*(?:h|heures?)"
                            + "(?:\\s*(?:et\\s+)?"
                            + "(?:quart|demi|demie|quinze|trente|"
                            + "quarante cinq|\\d{1,2}))?"
                            + "(?:\\s+(?:du matin|du soir|"
                            + "de l'après-midi|de la soirée))?\\b",
                    ""
            );

            title = title.replaceAll(
                    "\\b(?:[01]?\\d|2[0-3]):[0-5]\\d\\b",
                    ""
            );

            title = title.replaceAll(
                    "(?iu)\\b(?:vingt et un|vingt deux|vingt trois|"
                            + "dix sept|dix huit|dix neuf|"
                            + "quatorze|treize|quinze|seize|"
                            + "douze|onze|dix|neuf|huit|sept|"
                            + "six|cinq|quatre|trois|deux|un|une)"
                            + "\\s+heures?"
                            + "(?:\\s+(?:et\\s+)?"
                            + "(?:quart|demi|demie|quinze|trente|"
                            + "quarante cinq|\\d{1,2}))?"
                            + "(?:\\s+(?:du matin|du soir|"
                            + "de l'après-midi|de la soirée))?\\b",
                    ""
            );
        }

        // Supprimer les dates avec un nom de mois
        title = title.replaceAll(
                "(?iu)\\b(?:le\\s+)?(?:1er|premier|\\d{1,2})"
                        + "\\s+(?:janvier|février|fevrier|mars|"
                        + "avril|mai|juin|juillet|août|aout|"
                        + "septembre|octobre|novembre|décembre|decembre)"
                        + "(?:\\s+\\d{4})?"
                        + "(?:\\s+de\\s+l'année\\s+(?:prochaine|dernière))?"
                        + "\\b",
                ""
        );

        // Supprimer les dates numériques
        title = title.replaceAll(
                "\\b\\d{1,2}[/-]\\d{1,2}(?:[/-]\\d{2,4})?\\b",
                ""
        );

        // Supprimer les durées futures et passées
        String numberWords =
                "(?:\\d+|un|une|deux|trois|quatre|cinq|six|sept|"
                        + "huit|neuf|dix|onze|douze|treize|"
                        + "quatorze|quinze|seize|dix sept|"
                        + "dix huit|dix neuf|vingt|vingt et un|"
                        + "vingt deux|vingt trois|vingt quatre|"
                        + "vingt cinq|vingt six|vingt sept|"
                        + "vingt huit|vingt neuf|trente)";

        title = title.replaceAll(
                "(?iu)\\b(?:dans|il y a)\\s+" + numberWords
                        + "\\s+(?:jours?|semaines?|mois|ans?|années|annees)\\b",
                ""
        );

        // Supprimer les dates relatives et leurs moments de journée
        title = title.replaceAll(
                "(?iu)\\b(?:après-demain|apres-demain|"
                        + "après demain|apres demain|"
                        + "avant-hier|avant hier|"
                        + "aujourd'hui|aujourd hui|"
                        + "demain|hier|lendemain|la veille)"
                        + "(?:\\s+(?:matin|matinée|matinee|"
                        + "après-midi|apres midi|"
                        + "soir|soirée|soiree|nuit))?\\b",
                ""
        );

        // Supprimer les jours de la semaine et leurs qualificatifs
        title = title.replaceAll(
                "(?iu)\\b(?:lundi|mardi|mercredi|jeudi|"
                        + "vendredi|samedi|dimanche)"
                        + "(?:\\s+(?:prochain|prochaine|"
                        + "dernier|dernière|derniere|"
                        + "passé|passe|suivant|suivante|"
                        + "précédent|precedent|précédente|precedente))?\\b",
                ""
        );

        // Supprimer les moments de la journée autonomes
        title = title.replaceAll(
                "(?iu)\\b(?:ce matin|cette matinée|cette matinee|"
                        + "cet après-midi|cet apres midi|"
                        + "ce soir|cette nuit|en soirée|en soiree)"
                        + "\\b",
                ""
        );

        // Supprimer les expressions de liaison devenues inutiles
        title = title.replaceAll(
                "(?iu)\\b(?:à|a|vers|pour|le|du|dans)\\s*$",
                ""
        );

        // Nettoyer les espaces et la ponctuation
        title = title.replaceAll("[,;:.!?]+", " ");
        title = title.replaceAll("\\s+", " ").trim();

        if (title.isEmpty()) {
            return null;
        }

        return title;
    }
}
