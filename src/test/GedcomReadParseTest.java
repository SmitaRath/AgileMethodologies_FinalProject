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

    @Test
    /*Validating function ValidateBirthBeforeMarriage for us-02*/
    public void ValidateUS02(){
        Individual I1 = new Individual();
        I1.id = "I10";
        Individual I2 = new Individual();
        I2.id="I11";
        I1.dateOfBirth = "2020-05-13";
        I2.dateOfBirth = "2003-08-20";
        g1.individuals.add(I1);
        g1.individuals.add(I2);
        f.husbandId = "I10";
        f.wifeId = "I11";
        f.dateOfMarried = "2003-08-22";
        g1.families.add(f);
        assertEquals(true, g1.ValidateBirthBeforeMarriage(f.husbandId, f.dateOfMarried));
        assertEquals(false, g1.ValidateBirthBeforeMarriage(f.wifeId, f.dateOfMarried));
    }

    @Test
    /*Validate Date function used for us-02*/
    public void correctDate(){
        int year = 2001;
        int month = 2;
        int day = 29;
        assertEquals(false,g1.validateDate(year,month,day));
        assertEquals(false, g1.validateDate(2016,4,31));
    }

    @Test
    //Validate Whether Id's are unique
    public void ValidateUS22(){
        Individual I1 = new Individual();
        I1.id = "I10";
        Individual I2 = new Individual();
        I2.id = "I10";
        Family f1 = new Family();
        Family f2 = new Family();
        f1.id = "F10";
        f2.id = "F10";
        g1.individuals.add(I1);
        g1.individuals.add(I2);
        g1.families.add(f1);
        g1.families.add(f2);
        g1.checkIndividualId();
        g1.checkFamilyId();
        assertEquals(true, g1.validateIdForIndividual(I1.id));
        assertEquals(true, g1.validateIdForFamily(f1.id));
    }
}