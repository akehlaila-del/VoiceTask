
package com.example.voicetask;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TextProcessorTest {

    private final TextProcessor textProcessor = new TextProcessor();

    private String formatDate(Calendar date) {

        SimpleDateFormat formatter =
                new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT);

        return formatter.format(date.getTime());
    }

    private void verifyDate(String phrase, Calendar expectedDate) {

        Calendar actualDate = textProcessor.extractDate(phrase);

        assertNotNull(
                "Aucune date reconnue dans : " + phrase,
                actualDate
        );

        assertEquals(
                "Date incorrecte pour : " + phrase,
                formatDate(expectedDate),
                formatDate(actualDate)
        );
    }

    // Vérifier l'heure extraite
    private void verifyTime(String phrase, String expectedTime) {

        assertEquals(
                "Heure incorrecte pour : " + phrase,
                expectedTime,
                textProcessor.extractTime(phrase)
        );
    }

    private Calendar getDateOffset(int field, int amount) {

        Calendar date = Calendar.getInstance();
        date.add(field, amount);

        return date;
    }

    private Calendar getStartOfPeriod(int field, int offset) {

        Calendar date = Calendar.getInstance();

        if (field == Calendar.WEEK_OF_YEAR) {
            int day = date.get(Calendar.DAY_OF_WEEK);
            date.add(Calendar.DAY_OF_MONTH, -((day + 5) % 7));

        } else if (field == Calendar.MONTH) {
            date.set(Calendar.DAY_OF_MONTH, 1);

        } else if (field == Calendar.YEAR) {
            date.set(Calendar.DAY_OF_YEAR, 1);
        }

        date.add(field, offset);

        return date;
    }

    private Calendar getWeekdayDate(int targetDay, int direction) {

        Calendar date = Calendar.getInstance();

        int currentDay = date.get(Calendar.DAY_OF_WEEK);
        int difference;

        if (direction > 0) {
            difference = (targetDay - currentDay + 7) % 7;
            if (difference == 0) difference = 7;

        } else if (direction < 0) {
            difference = -((currentDay - targetDay + 7) % 7);
            if (difference == 0) difference = -7;

        } else {
            difference = (targetDay - currentDay + 7) % 7;
        }

        date.add(Calendar.DAY_OF_MONTH, difference);

        return date;
    }

    private Calendar getNextOccurrence(int day, int month) {

        Calendar today = Calendar.getInstance();

        Calendar date = Calendar.getInstance();
        date.clear();
        date.set(today.get(Calendar.YEAR), month, day);

        if (date.before(getToday())) {
            date.add(Calendar.YEAR, 1);
        }

        return date;
    }

    private Calendar getToday() {

        Calendar today = Calendar.getInstance();

        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        return today;
    }

    // PRESENT

    @Test
    public void testNow() {
        verifyDate("Faire ceci maintenant", Calendar.getInstance());
    }

    @Test
    public void testCurrently() {
        verifyDate("Je travaille actuellement", Calendar.getInstance());
    }

    @Test
    public void testAtPresent() {
        verifyDate("Je veux le faire à présent", Calendar.getInstance());
    }

    @Test
    public void testToday() {
        verifyDate("Travailler aujourd'hui", Calendar.getInstance());
    }

    // DATES PASSEES ET FUTURES

    @Test
    public void testYesterday() {
        verifyDate("Réviser hier",
                getDateOffset(Calendar.DAY_OF_MONTH, -1));
    }

    @Test
    public void testDayBeforeYesterday() {
        verifyDate("Travailler avant-hier",
                getDateOffset(Calendar.DAY_OF_MONTH, -2));
    }

    @Test
    public void testTomorrow() {
        verifyDate("Réviser demain",
                getDateOffset(Calendar.DAY_OF_MONTH, 1));
    }

    @Test
    public void testDayAfterTomorrow() {
        verifyDate("Rendez-vous après-demain",
                getDateOffset(Calendar.DAY_OF_MONTH, 2));
    }

    // DUREES

    @Test
    public void testThreeDaysInFuture() {
        verifyDate("Travailler dans 3 jours",
                getDateOffset(Calendar.DAY_OF_MONTH, 3));
    }

    @Test
    public void testThreeDaysInPast() {
        verifyDate("Travailler il y a 3 jours",
                getDateOffset(Calendar.DAY_OF_MONTH, -3));
    }

    @Test
    public void testTwoWeeksInFuture() {
        verifyDate("Réunion dans deux semaines",
                getDateOffset(Calendar.WEEK_OF_YEAR, 2));
    }

    @Test
    public void testTwoWeeksInPast() {
        verifyDate("Réunion il y a deux semaines",
                getDateOffset(Calendar.WEEK_OF_YEAR, -2));
    }

    @Test
    public void testFiveMonthsInFuture() {
        verifyDate("Rapport dans 5 mois",
                getDateOffset(Calendar.MONTH, 5));
    }

    @Test
    public void testFiveMonthsInPast() {
        verifyDate("Rapport il y a 5 mois",
                getDateOffset(Calendar.MONTH, -5));
    }

    @Test
    public void testOneYearInFuture() {
        verifyDate("Voyager dans un an",
                getDateOffset(Calendar.YEAR, 1));
    }

    @Test
    public void testOneYearInPast() {
        verifyDate("Voyager il y a un an",
                getDateOffset(Calendar.YEAR, -1));
    }

    @Test
    public void testCombinedDuration() {

        Calendar expected = Calendar.getInstance();
        expected.add(Calendar.YEAR, 1);
        expected.add(Calendar.MONTH, 2);

        verifyDate("Voyager dans un an et deux mois", expected);
    }

    // SEMAINES, MOIS ET ANNEES

    @Test
    public void testLastWeek() {
        verifyDate("La semaine dernière",
                getStartOfPeriod(Calendar.WEEK_OF_YEAR, -1));
    }

    @Test
    public void testNextWeek() {
        verifyDate("La semaine prochaine",
                getStartOfPeriod(Calendar.WEEK_OF_YEAR, 1));
    }

    @Test
    public void testThisWeek() {
        verifyDate("Cette semaine",
                getStartOfPeriod(Calendar.WEEK_OF_YEAR, 0));
    }

    @Test
    public void testLastMonth() {
        verifyDate("Le mois dernier",
                getStartOfPeriod(Calendar.MONTH, -1));
    }

    @Test
    public void testNextMonth() {
        verifyDate("Le mois prochain",
                getStartOfPeriod(Calendar.MONTH, 1));
    }

    @Test
    public void testThisMonth() {
        verifyDate("Ce mois-ci",
                getStartOfPeriod(Calendar.MONTH, 0));
    }

    @Test
    public void testLastYear() {
        verifyDate("L'année dernière",
                getStartOfPeriod(Calendar.YEAR, -1));
    }

    @Test
    public void testNextYear() {
        verifyDate("L'année prochaine",
                getStartOfPeriod(Calendar.YEAR, 1));
    }

    @Test
    public void testThisYear() {
        verifyDate("Cette année",
                getStartOfPeriod(Calendar.YEAR, 0));
    }

    // JOURS DE LA SEMAINE

    @Test
    public void testNextMonday() {
        verifyDate("Lundi prochain",
                getWeekdayDate(Calendar.MONDAY, 1));
    }

    @Test
    public void testNextWednesday() {
        verifyDate("Ce mercredi",
                getWeekdayDate(Calendar.WEDNESDAY, 0));
    }

    @Test
    public void testLastFriday() {
        verifyDate("Vendredi dernier",
                getWeekdayDate(Calendar.FRIDAY, -1));
    }

    @Test
    public void testLastTuesday() {
        verifyDate("Mardi passé",
                getWeekdayDate(Calendar.TUESDAY, -1));
    }

    @Test
    public void testNextSunday() {
        verifyDate("Dimanche prochain",
                getWeekdayDate(Calendar.SUNDAY, 1));
    }

    @Test
    public void testSimpleMonday() {
        verifyDate("Lundi",
                getWeekdayDate(Calendar.MONDAY, 0));
    }

    // MOMENTS DE LA JOURNEE

    @Test
    public void testThisMorning() {
        verifyDate("Travailler ce matin", Calendar.getInstance());
    }

    @Test
    public void testThisAfternoon() {
        verifyDate("Réviser cet après-midi", Calendar.getInstance());
    }

    @Test
    public void testThisEvening() {
        verifyDate("Étudier ce soir", Calendar.getInstance());
    }

    @Test
    public void testTonight() {
        verifyDate("Travailler cette nuit", Calendar.getInstance());
    }

    @Test
    public void testTomorrowMorning() {
        verifyDate("Réviser demain matin",
                getDateOffset(Calendar.DAY_OF_MONTH, 1));
    }

    @Test
    public void testTomorrowAfternoon() {
        verifyDate("Rendez-vous demain après-midi",
                getDateOffset(Calendar.DAY_OF_MONTH, 1));
    }

    @Test
    public void testTomorrowEvening() {
        verifyDate("Travailler demain soir",
                getDateOffset(Calendar.DAY_OF_MONTH, 1));
    }

    @Test
    public void testYesterdayEvening() {
        verifyDate("J'ai étudié hier soir",
                getDateOffset(Calendar.DAY_OF_MONTH, -1));
    }

    @Test
    public void testLastNight() {
        verifyDate("La nuit dernière",
                getDateOffset(Calendar.DAY_OF_MONTH, -1));
    }

    // DATES PRECISES AVEC LE NOM DU MOIS

    @Test
    public void testDateWithMonthName() {
        verifyDate("Rendez-vous le 15 novembre",
                getNextOccurrence(15, Calendar.NOVEMBER));
    }

    @Test
    public void testDateWithExplicitYear() {

        Calendar expected = Calendar.getInstance();
        expected.clear();
        expected.set(2026, Calendar.DECEMBER, 20);

        verifyDate("Rendez-vous le 20 décembre 2026", expected);
    }

    @Test
    public void testFirstJanuary() {
        verifyDate("Voyage le premier janvier",
                getNextOccurrence(1, Calendar.JANUARY));
    }

    @Test
    public void testFirstJanuaryWithOrdinal() {
        verifyDate("Voyage le 1er janvier",
                getNextOccurrence(1, Calendar.JANUARY));
    }

    @Test
    public void testNextYearInPhrase() {

        Calendar expected = Calendar.getInstance();
        expected.clear();
        expected.set(
                Calendar.getInstance().get(Calendar.YEAR) + 1,
                Calendar.FEBRUARY,
                12
        );

        verifyDate(
                "Rendez-vous le 12 février de l'année prochaine",
                expected
        );
    }

    @Test
    public void testLastMarch() {

        Calendar today = getToday();

        int year = today.get(Calendar.YEAR);

        Calendar expected = Calendar.getInstance();
        expected.clear();
        expected.set(year, Calendar.MARCH, 5);

        if (!expected.before(today)) {
            expected.add(Calendar.YEAR, -1);
        }

        verifyDate("Le 5 mars dernier", expected);
    }

    // DATES NUMERIQUES

    @Test
    public void testNumericDateWithYear() {

        Calendar expected = Calendar.getInstance();
        expected.clear();
        expected.set(2026, Calendar.DECEMBER, 20);

        verifyDate("Rendez-vous le 20/12/2026", expected);
    }

    @Test
    public void testNumericDateWithDashes() {

        Calendar expected = Calendar.getInstance();
        expected.clear();
        expected.set(2026, Calendar.DECEMBER, 20);

        verifyDate("Rendez-vous le 20-12-2026", expected);
    }

    @Test
    public void testThirdOfNextMonth() {

        Calendar date = Calendar.getInstance();
        date.set(Calendar.DAY_OF_MONTH, 1);
        date.add(Calendar.MONTH, 1);

        Calendar expected = Calendar.getInstance();
        expected.clear();
        expected.set(
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                3
        );

        verifyDate("Rendez-vous le 3 du mois prochain", expected);
    }

    // DATE INVALIDE

    @Test
    public void testInvalidDate() {

        Calendar result =
                textProcessor.extractDate("Rendez-vous le 31 février 2026");

        assertNull(result);
    }

    // AUCUNE DATE

    @Test
    public void testNoDate() {

        Calendar result = textProcessor.extractDate(
                "Faire les exercices de programmation"
        );

        assertNull(result);
    }

    // EXTRACTION DE L'HEURE VIA TEXTPROCESSOR

    @Test
    public void testExtractTimeFromNumericPhrase() {
        verifyTime("Réunion demain à 14h30", "14:30");
    }

    @Test
    public void testExtractTimeFromWrittenPhrase() {
        verifyTime("Rendez-vous lundi à neuf heures", "09:00");
    }

    @Test
    public void testExtractTimeFromAfternoonPhrase() {
        verifyTime(
                "Travailler demain à 4 heures de l'après-midi",
                "16:00"
        );
    }

    @Test
    public void testExtractTimeFromPhraseWithoutTime() {
        assertNull(textProcessor.extractTime(
                "Rendez-vous avec le directeur demain"
        ));
    }

    // EXTRACTION SIMULTANEE DE LA DATE ET DE L'HEURE

    @Test
    public void testExtractDateAndTimeTogether() {

        String phrase = "Réunion demain à 14h30";

        Calendar expectedDate =
                getDateOffset(Calendar.DAY_OF_MONTH, 1);

        Calendar actualDate = textProcessor.extractDate(phrase);
        String actualTime = textProcessor.extractTime(phrase);

        assertNotNull(
                "La date n'a pas été reconnue",
                actualDate
        );

        assertEquals(
                formatDate(expectedDate),
                formatDate(actualDate)
        );

        assertEquals("14:30", actualTime);
    }
    @Test
    public void testExtractTitleWithNumericTime() {
        assertEquals(
                "Réunion avec le directeur",
                textProcessor.extractTitle(
                        "Réunion avec le directeur demain à 14h30"
                )
        );
    }

    @Test
    public void testExtractTitleWithWrittenTime() {
        assertEquals(
                "Appeler le professeur",
                textProcessor.extractTitle(
                        "Appeler le professeur lundi à neuf heures"
                )
        );
    }

    @Test
    public void testExtractTitleWithDateAndTime() {
        assertEquals(
                "Rendez-vous médical",
                textProcessor.extractTitle(
                        "Rendez-vous médical demain à 10h00"
                )
        );
    }

    @Test
    public void testExtractTitleWithoutDateAndTime() {
        assertEquals(
                "Acheter du matériel informatique",
                textProcessor.extractTitle(
                        "Acheter du matériel informatique"
                )
        );
    }

    @Test
    public void testExtractTitleWithTomorrowMorning() {
        assertEquals(
                "Je dois rencontrer le directeur",
                textProcessor.extractTitle(
                        "Je dois rencontrer le directeur demain matin"
                )
        );
    }

    @Test
    public void testExtractTitleWithNamedDate() {
        assertEquals(
                "Prévoir une réunion",
                textProcessor.extractTitle(
                        "Prévoir une réunion le 15 décembre 2026 à 9 heures"
                )
        );
    }

    @Test
    public void testExtractTitleWithDuration() {
        assertEquals(
                "Appeler maman",
                textProcessor.extractTitle(
                        "Appeler maman dans deux jours"
                )
        );
    }

    @Test
    public void testExtractTitleWithWeekdayAndTime() {
        assertEquals(
                "Rendez-vous avec le médecin",
                textProcessor.extractTitle(
                        "Rendez-vous avec le médecin vendredi prochain à 14h"
                )
        );
    }

    @Test
    public void testExtractTitleWithTodayAndNumericTime() {
        assertEquals(
                "Travailler",
                textProcessor.extractTitle(
                        "Travailler aujourd'hui à 16h30"
                )
        );
    }

    @Test
    public void testExtractAllInformationFromSameSentence() {

        String phrase =
                "Je dois rencontrer le directeur demain matin à 9 heures.";

        // Vérifier le titre
        assertEquals(
                "Je dois rencontrer le directeur",
                textProcessor.extractTitle(phrase)
        );

        // Vérifier la date
        assertNotNull(textProcessor.extractDate(phrase));

        // Vérifier l'heure
        assertEquals(
                "09:00",
                textProcessor.extractTime(phrase)
        );
    }

    // TESTS DES ENTREES VIDES ET INVALIDES

    @Test
    public void testExtractDateWithEmptyText() {
        assertNull(textProcessor.extractDate(""));
    }

    @Test
    public void testExtractDateWithNullText() {
        assertNull(textProcessor.extractDate(null));
    }

    @Test
    public void testExtractTimeWithEmptyText() {
        assertNull(textProcessor.extractTime(""));
    }

    @Test
    public void testExtractTimeWithInvalidHour() {
        assertNull(textProcessor.extractTime("Réunion à 25h"));
    }

    @Test
    public void testExtractTitleWithEmptyText() {
        assertNull(textProcessor.extractTitle(""));
    }

    @Test
    public void testExtractTitleWithNullText() {
        assertNull(textProcessor.extractTitle(null));
    }
    // TESTS DE SUPPRESSION DES EXPRESSIONS HORAIRES DU TITRE

    @Test
    public void testExtractTitleWithMidiAndHalf() {
        assertEquals(
                "Rendez-vous",
                textProcessor.extractTitle("Rendez-vous à midi et demi")
        );
    }

    @Test
    public void testExtractTitleWithMinuitAndHalf() {
        assertEquals(
                "Rendez-vous",
                textProcessor.extractTitle("Rendez-vous à minuit et demi")
        );
    }

    @Test
    public void testExtractTitleWithMinusQuarter() {
        assertEquals(
                "Rendez-vous",
                textProcessor.extractTitle(
                        "Rendez-vous à 7 heures du matin moins le quart"
                )
        );
    }
}
