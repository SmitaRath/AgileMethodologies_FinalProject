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

    @Test
    public void US28_orderSiblingsByAge() throws ParseException {
        Individual I1 = new Individual();
        Individual I2 = new Individual();
        DateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        I1.dateOfBirth = "23 FEB 1980";
        I1.dobDate = formatter.parse(I1.dateOfBirth);
        I2.dateOfBirth = "24 FEB 1980";
        I2.dobDate = formatter.parse(I2.dateOfBirth);
        assertEquals(-1,I1.dobDate.compareTo(I2.dobDate));
    }

    @Test
    public void us29_listDeceasedIndividual() throws ParseException {
        Individual I1 = new Individual();
        Individual I2 = new Individual();
        DateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        I1.dateOfBirth = "23 FEB 1980";
        I1.dobDate = formatter.parse(I1.dateOfBirth);
        I1.alive=false;
        I1.death = "23 FEB 2000";
        I1.deathDate = formatter.parse(I1.death);
        I2.dateOfBirth = "24 FEB 1980";
        I2.dobDate = formatter.parse(I2.dateOfBirth);
        assertEquals(true,(!I1.alive && I1.deathDate!=null));
        assertEquals(false,(!I2.alive && I2.deathDate!=null));
    }
}