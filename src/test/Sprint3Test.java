package test;

import main.*;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class Sprint3Test {
    Family f = new Family();
    GedcomReadParse g1 = new GedcomReadParse();
    Sprint3 sprint3 = new Sprint3();

    @Test
    public void US38_listAllLivingUpcomingBirthday() throws Exception {
        Date date1 = new Date();
        assertEquals(0, sprint3.calculateDays(date1));
        assertNotEquals(10, sprint3.calculateDays(date1));
    }

    @Test
    public void US09_birthBeforeDeathOfParents() throws Exception {
        Date date1 = new Date("5 FEB 2021");
        Date date2 = new Date("5 DEC 2021");
        assertEquals(0, sprint3.monthDiffBetweenTwoDate(date1, date2));
        assertEquals(10, sprint3.monthDiffBetweenTwoDate(date2, date1));
    }
}