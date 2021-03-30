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
        I1.name = "Kandasamy /Parthasarathy";
        I1.dateOfBirth = "2020-05-13";
        String[] formatName;
        formatName = I1.name.split("/");
        assertEquals(formatName[1], "Parthasarathy");
    }
    
    @Test
    public void US05_BirthBeforeDeath(){
        Individual I1 = new Individual();
        I1.id = "I10";
        Individual I2 = new Individual();
        I2.id="I11";
        I1.alive=false;
        I2.alive=false;
        I1.death = "2020-05-13";
        I2.death = "2003-08-20";
        g1.individuals.add(I1);
        g1.individuals.add(I2);
        f.husbandId = "I10";
        f.wifeId = "I11";
        f.dateOfMarried = "2003-08-22";
        g1.families.add(f);
        assertEquals(false, sprint2.ValidateMarriageBeforeDeath(g1.individuals,f.husbandId, f.dateOfMarried));
        assertEquals(true, sprint2.ValidateMarriageBeforeDeath(g1.individuals,f.wifeId, f.dateOfMarried));
    }

    @Test
    public void US06_DivorceBeforeDeath(){
        Individual I1 = new Individual();
        I1.id = "I12";
        Individual I2 = new Individual();
        I2.id="I13";
        I1.alive=false;
        I2.alive=false;
        I1.death = "2020-05-13";
        I2.death = "2003-08-20";
        g1.individuals.add(I1);
        g1.individuals.add(I2);
        f.husbandId = "I12";
        f.wifeId = "I13";
        f.dateOfDivided = "2003-10-14";
        g1.families.add(f);
        assertEquals(false, sprint2.ValidateDivorceBeforeDeath(g1.individuals,f.husbandId, f.dateOfDivided));
        assertEquals(true, sprint2.ValidateDivorceBeforeDeath(g1.individuals,f.wifeId, f.dateOfDivided));
    }

}
