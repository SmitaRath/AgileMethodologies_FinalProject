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
        Date date2 = new Date("5 DEC 2022");
        assertEquals(0, sprint3.calculateDays(date1));
        assertEquals(100, sprint3.calculateDays(date2));
        assertNotEquals(10, sprint3.calculateDays(date1));
    }

  /*  @Test
    public void US09_birthBeforeDeathOfParents() throws Exception {
        Date date1 = new Date("5 FEB 2021");
        Date date2 = new Date("5 DEC 2021");
        assertEquals(100, sprint3.monthDiffBetweenTwoDate(date1, date2));   // father calculation
        assertEquals(-1, date1.compareTo(date2));                          // mother calculation
        assertEquals(10, sprint3.monthDiffBetweenTwoDate(date2, date1));
    }*/

    @Test
    public void US28_orderSiblingsByAge() throws ParseException {
        Individual Husb = new Individual();
        Individual Wife = new Individual();
        Individual Child1 = new Individual();
        Individual Child2 = new Individual();
        Individual Child3 = new Individual();
        Family Fam1 = new Family();
        DateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        Husb.id = "I1";
        Husb.dateOfBirth = "23 FEB 1981";
        Husb.dobDate = formatter.parse(Husb.dateOfBirth);
        Husb.gender="M";
        Husb.spouse="F1";
        Wife.id = "I2";
        Wife.dateOfBirth = "24 FEB 1980";
        Wife.dobDate = formatter.parse(Wife.dateOfBirth);
        Wife.gender="F";
        Wife.spouse="F1";
        Child1.id = "I3";
        Child1.dateOfBirth = "24 FEB 2006";
        Child1.dobDate = formatter.parse(Wife.dateOfBirth);
        Child1.gender="F";
        Child1.child="";
        Child2.id = "I4";
        Child2.dateOfBirth = "23 FEB 2005";
        Child2.dobDate = formatter.parse(Husb.dateOfBirth);
        Child2.gender="M";
        Child2.child="";
        Child3.id = "I5";
        Child3.dateOfBirth = "24 FEB 2006";
        Child3.dobDate = formatter.parse(Husb.dateOfBirth);
        Child3.gender="M";
        Child3.child="F3";
        Fam1.dateOfMarried="24 FEB 2003";
        Fam1.marrriedDate=formatter.parse(Fam1.dateOfMarried);
        Fam1.husbandId=Husb.id;
        Fam1.wifeId=Wife.id;
        Fam1.child.add(Child1.id);
        Fam1.child.add(Child2.id);

        assertEquals(true,((Child1.dobDate.compareTo(Child2.dobDate))==-1) &&
                Child1.child.equals(Child2.child));
        assertEquals(false,((Child3.dobDate.compareTo(Child2.dobDate))==1) &&
                (Child3.child.equals(Child2.child)));
    }

    @Test
    public void us29_listDeceasedIndividual() throws ParseException {
        Individual I1 = new Individual();
        Individual I2 = new Individual();
        Individual I3 = new Individual();
        DateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        I1.dateOfBirth = "23 FEB 1980";
        I1.dobDate = formatter.parse(I1.dateOfBirth);
        I1.alive=false;
        I1.death = "23 FEB 2000";
        I1.deathDate = formatter.parse(I1.death);
        I2.dateOfBirth = "24 FEB 1980";
        I2.dobDate = formatter.parse(I2.dateOfBirth);
        I3.dateOfBirth = "23 FEB 1980";
        I3.dobDate = formatter.parse(I1.dateOfBirth);
        I3.alive=false;
        I1.death = "45 FEB 2000";
        I3.deathDate = null;
        assertEquals(true,(!I1.alive && I1.deathDate!=null));
        assertEquals(false,(!I2.alive && I2.deathDate!=null));
        assertEquals(false,(!I3.alive && I3.deathDate!=null));
    }
}