
package com.example.voicetask;

import java.util.Calendar;
import java.util.Locale;

public class DateTimeParser {

    // Récupérer l'instant actuel
    public Calendar getNow() {
        return Calendar.getInstance(Locale.getDefault());
    }

    // Récupérer la date actuelle sans l'heure
    public Calendar getToday() {

        Calendar date = getNow();

        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        return date;
    }

    // Calculer une date après un nombre de jours
    public Calendar getDateAfterDays(int days) {

        Calendar date = getToday();
        date.add(Calendar.DAY_OF_MONTH, days);

        return date;
    }

    // Calculer une date avant un nombre de jours
    public Calendar getDateBeforeDays(int days) {

        return getDateAfterDays(-days);
    }

    // Calculer une date après un nombre de semaines
    public Calendar getDateAfterWeeks(int weeks) {

        Calendar date = getToday();
        date.add(Calendar.WEEK_OF_YEAR, weeks);

        return date;
    }

    // Calculer une date après un nombre de mois
    public Calendar getDateAfterMonths(int months) {

        Calendar date = getToday();
        date.add(Calendar.MONTH, months);

        return date;
    }

    // Calculer une date après un nombre d'années
    public Calendar getDateAfterYears(int years) {

        Calendar date = getToday();
        date.add(Calendar.YEAR, years);

        return date;
    }

    // Calculer une date avec plusieurs durées combinées
    public Calendar getDateAfterDuration(
            int days, int weeks, int months, int years) {

        Calendar date = getToday();

        date.add(Calendar.YEAR, years);
        date.add(Calendar.MONTH, months);
        date.add(Calendar.WEEK_OF_YEAR, weeks);
        date.add(Calendar.DAY_OF_MONTH, days);

        return date;
    }

    // Calculer une date avant plusieurs durées combinées
    public Calendar getDateBeforeDuration(
            int days, int weeks, int months, int years) {

        return getDateAfterDuration(
                -days,
                -weeks,
                -months,
                -years
        );
    }

    // Récupérer la date d'hier
    public Calendar getYesterday() {
        return getDateBeforeDays(1);
    }

    // Récupérer la date d'avant-hier
    public Calendar getDayBeforeYesterday() {
        return getDateBeforeDays(2);
    }

    // Récupérer la date de demain
    public Calendar getTomorrow() {
        return getDateAfterDays(1);
    }

    // Récupérer la date d'après-demain
    public Calendar getDayAfterTomorrow() {
        return getDateAfterDays(2);
    }

    // Récupérer la date de la semaine dernière
    public Calendar getLastWeek() {
        return getDateAfterWeeks(-1);
    }

    // Récupérer la date de la semaine prochaine
    public Calendar getNextWeek() {
        return getDateAfterWeeks(1);
    }

    // Récupérer la date du mois dernier
    public Calendar getLastMonth() {
        return getDateAfterMonths(-1);
    }

    // Récupérer la date du mois prochain
    public Calendar getNextMonth() {
        return getDateAfterMonths(1);
    }

    // Récupérer la date de l'année dernière
    public Calendar getLastYear() {
        return getDateAfterYears(-1);
    }

    // Récupérer la date de l'année prochaine
    public Calendar getNextYear() {
        return getDateAfterYears(1);
    }
}
