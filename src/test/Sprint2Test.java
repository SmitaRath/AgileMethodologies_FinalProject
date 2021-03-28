package test;

import main.*;
import org.junit.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class Sprint2Test {
    Family f = new Family();
    GedcomReadParse g1 = new GedcomReadParse();
    Sprint2 sprint2 = new Sprint2();

    @Test
    public void US08_birthBeforeMarriageOfParents() throws Exception {
        Date date1 = new Date("13 FEB 2021");
        Date date2 = new Date("13 DEC 2021");
        assertEquals(10, sprint2.monthDiffBetweenTwoDate(date1, date2));
        assertNotEquals(150, g1.differenceBetweenTwoAge(date1, date2));
    }

    @Test
    public void US16_maleLastName() throws Exception {
        Individual I1 = new Individual();
        I1.name = "Kandasamy Parthasarathy";
        I1.dateOfBirth = "2020-05-13";
//
//        assertEquals(10, sprint2.monthDiffBetweenTwoDate(date1, date2));
//        assertNotEquals(150, g1.differenceBetweenTwoAge(date1, date2));
    }

}
