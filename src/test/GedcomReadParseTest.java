package test;

import main.*;
import org.junit.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class GedcomReadParseTest {
    Family f = new Family();
    GedcomReadParse g1 = new GedcomReadParse();

    /**
     * us-01 test cases
     * 1. date validation
     * 2. date after current date
     */
    @Test
    public void checkDate() throws Exception{
        Date t1 = new Date("13 MAR 2021");
        assertEquals(t1,g1.validateDate("13 MAR 2021"));
        assertEquals(null,g1.validateDate("45 MAR 2021"));
        assertEquals(null,g1.validateDate("MAR 13 2021"));
        assertEquals(true,g1.validateDate(t1,"2021-03-13"));
        t1=new Date("13 MAR 2022");
        assertEquals(false,g1.validateDate(t1,"2022-03-13"));
        assertEquals(true,g1.validateDate(t1,"NA"));
        assertEquals(true,g1.validateDate(t1,"INVALID DATE"));

    }

    /**
     * US-21
     * validating gender
     */

    @Test
    public void validateGender(){
        Individual i1 = new Individual();
        i1.id="I1";
        i1.gender="F";
        g1.individuals.add(i1);
        assertEquals(false,g1.validateGenderForFamily("I1","M"));
        assertEquals(true,g1.validateGenderForFamily("I1","F"));
    }


    /**
     * US-07
     * Difference between birth and death should be less than 150 years
     */

    @Test
    public void differenceBetweenTwoAge() {
        Date date1 = new Date("13 MAR 1850");
        Date date2 = new Date("13 MAR 2020");
        assertEquals(170, g1.differenceBetweenTwoAge(date1, date2));
        assertNotEquals(150, g1.differenceBetweenTwoAge(date1, date2));
    }

    /**
     * US-07
     * Difference between birth and death should be less than 150 years
     */

    @Test
    public void calculateDays() {
        Date date1 = new Date("13 MAR 2021");
        assertEquals(3, g1.calculateDays(date1));
        assertNotEquals(10, g1.calculateDays(date1));
    }
}