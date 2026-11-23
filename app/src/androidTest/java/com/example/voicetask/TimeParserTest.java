
package com.example.voicetask;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TimeParserTest {

    private final TimeParser timeParser = new TimeParser();

    private void verifyTime(String phrase, String expectedTime) {

        assertEquals(
                "Erreur pour la phrase : " + phrase,
                expectedTime,
                timeParser.extractTime(phrase)
        );
    }

    // Heures numériques

    @Test
    public void testEightHours() {
        verifyTime("Rendez-vous à 8 heures", "08:00");
    }

    @Test
    public void testFourteenHours() {
        verifyTime("Réunion à 14 heures", "14:00");
    }

    @Test
    public void testFourteenThirty() {
        verifyTime("Réunion à 14 h 30", "14:30");
    }

    @Test
    public void testSixteenHours() {
        verifyTime("Travailler à 16h", "16:00");
    }

    @Test
    public void testColonFormat() {
        verifyTime("Rendez-vous à 09:15", "09:15");
    }

    // Heures spéciales

    @Test
    public void testNoon() {
        verifyTime("Rendez-vous vers midi", "12:00");
    }

    @Test
    public void testMidnight() {
        verifyTime("Travailler à minuit", "00:00");
    }

    // Heures écrites en lettres

    @Test
    public void testEightWritten() {
        verifyTime("Rendez-vous à huit heures", "08:00");
    }

    @Test
    public void testFourteenWritten() {
        verifyTime("Réunion à quatorze heures", "14:00");
    }

    @Test
    public void testQuarterPastNine() {
        verifyTime("Rendez-vous à neuf heures et quart", "09:15");
    }

    @Test
    public void testHalfPastTen() {
        verifyTime("Réunion à dix heures et demie", "10:30");
    }

    @Test
    public void testFortyFiveMinutes() {
        verifyTime("Rendez-vous à huit heures quarante cinq", "08:45");
    }

    // Heures invalides

    @Test
    public void testInvalidHour() {
        verifyTime("Rendez-vous à 25 heures", null);
    }

    @Test
    public void testInvalidMinutes() {
        verifyTime("Rendez-vous à 14 h 75", null);
    }

    // Aucune heure

    @Test
    public void testNoTime() {

        assertNull(
                timeParser.extractTime(
                        "Rendez-vous avec le directeur demain"
                )
        );
    }

    // Minutes exprimées en lettres

    @Test
    public void testNineHoursFifteen() {
        verifyTime("Rendez-vous à neuf heures quinze", "09:15");
    }

    @Test
    public void testEightHoursAndQuarter() {
        verifyTime("Rendez-vous à huit heures et quart", "08:15");
    }

    @Test
    public void testTenHoursAndHalf() {
        verifyTime("Réunion à dix heures et demie", "10:30");
    }

    @Test
    public void testSevenHoursMinusQuarter() {
        verifyTime("Rendez-vous à sept heures moins le quart", "06:45");
    }

    @Test
    public void testNumericHourMinusQuarter() {
        verifyTime("Rendez-vous à 7 heures moins le quart", "06:45");
    }

    // Indications de période

    @Test
    public void testMorning() {
        verifyTime("Rendez-vous à 7 heures du matin", "07:00");
    }

    @Test
    public void testAfternoon() {
        verifyTime("Rendez-vous à 4 heures de l'après-midi", "16:00");
    }

    @Test
    public void testEvening() {
        verifyTime("Rendez-vous à 8 heures du soir", "20:00");
    }

    @Test
    public void testNumericAfternoon() {
        verifyTime("Réunion à 4h de l'après-midi", "16:00");
    }

    // Expressions approximatives

    @Test
    public void testAroundEighteenHours() {
        verifyTime("Rendez-vous vers 18 heures", "18:00");
    }

    @Test
    public void testAroundNineHours() {
        verifyTime("Rendez-vous aux alentours de 9 heures", "09:00");
    }
    @Test
    public void testWrittenTwentyHours() {
        assertEquals("20:00", timeParser.extractTime("Rendez-vous à vingt heures"));
    }

    @Test
    public void testCompactNumericTime() {
        assertEquals("20:30", timeParser.extractTime("Rendez-vous à 20h30"));
    }

    @Test
    public void testWrittenTimeWithMorningPeriodBeforeMinusQuarter() {
        assertEquals(
                "06:45",
                timeParser.extractTime("Rendez-vous à 7 heures du matin moins le quart")
        );
    }

    @Test
    public void testMidiAndHalf() {
        assertEquals("12:30", timeParser.extractTime("Rendez-vous à midi et demi"));
    }

    @Test
    public void testMinuitAndHalf() {
        assertEquals("00:30", timeParser.extractTime("Rendez-vous à minuit et demi"));
    }
}
