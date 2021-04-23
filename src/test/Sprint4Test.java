package test;

import main.*;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class Sprint4Test {
    GedcomReadParse g1 = new GedcomReadParse();
    Sprint4 sprint4 = new Sprint4();

    @Test
    public void us25_uniqueFirstNamesInFamily() throws ParseException {
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
        Child1.name="Aishley /Hill/";
        Child1.child="F1";
        Child2.name="Aishley /Hill/";
        Child2.id = "I4";
        Child2.dateOfBirth = "24 FEB 2006";
        Child2.dobDate = formatter.parse(Husb.dateOfBirth);
        Child2.gender="M";
        Child2.child="F1";
        Child3.id = "I5";
        Child3.name="Aishley /Hill/";
        Child3.dateOfBirth = "25 FEB 2006";
        Child3.dobDate = formatter.parse(Husb.dateOfBirth);
        Child3.gender="M";
        Child3.child="F1";
        Fam1.dateOfMarried="25 FEB 2003";
        Fam1.id="F1";
        Fam1.marrriedDate=formatter.parse(Fam1.dateOfMarried);
        Fam1.husbandId=Husb.id;
        Fam1.wifeId=Wife.id;
        Fam1.child.add(Child1.id);
        Fam1.child.add(Child2.id);
        Fam1.child.add(Child3.id);
        assertEquals(true,(Child1.name.equals(Child2.name) && Child1.name.equals(Child3.name)));
        assertEquals(false,(Child1.dateOfBirth.equals(Child2.dateOfBirth) && Child1.dateOfBirth.equals(Child3.dateOfBirth)));
    }

    @Test
    public void us34_largeAgeDifference() throws ParseException {
        Individual Husb1 = new Individual();
        Individual Wife1 = new Individual();
        Individual Husb2 = new Individual();
        Individual Wife2 = new Individual();
        Family Fam1 = new Family();
        Family Fam2 = new Family();
        DateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        Husb1.id = "I1";
        Husb1.dateOfBirth = "23 FEB 1981";
        Husb1.dobDate = formatter.parse(Husb1.dateOfBirth);
        Husb1.age=g1.calculateAge(Husb1.dobDate);
        Husb1.gender="M";
        Husb1.spouse="F1";
        Wife1.id = "I2";
        Wife1.dateOfBirth = "24 FEB 1980";
        Wife1.dobDate = formatter.parse(Wife1.dateOfBirth);
        Wife1.age=g1.calculateAge(Wife1.dobDate);
        Wife1.gender="F";
        Wife1.spouse="F1";
        Fam1.id="F1";
        Fam1.husbandId=Husb1.id;
        Fam1.wifeId=Wife1.id;
        Fam1.dateOfMarried="06 MAR 2005";
        Fam1.marrriedDate=formatter.parse(Fam1.dateOfMarried);
        Husb2.id = "I3";
        Husb2.dateOfBirth = "17 APR 1981";
        Husb2.dobDate = formatter.parse(Husb2.dateOfBirth);
        Husb2.age=g1.calculateAge(Husb2.dobDate);
        Husb2.gender="M";
        Husb2.spouse="F2";
        Wife2.id = "I4";
        Wife2.dateOfBirth = "16 APR 2001";
        Wife2.dobDate = formatter.parse(Wife2.dateOfBirth);
        Wife2.age=g1.calculateAge(Wife2.dobDate);
        System.out.println(Wife2.age);
        System.out.println(Husb2.age);
        Wife2.gender="F";
        Wife2.spouse="F2";
        Fam2.id="F2";
        Fam2.husbandId=Husb2.id;
        Fam2.wifeId=Wife2.id;
        Fam2.dateOfMarried="06 MAR 2020";
        Fam2.marrriedDate=formatter.parse(Fam2.dateOfMarried);
        assertEquals(false,(Husb1.age>=Wife1.age*2));
        assertEquals(false,(Husb2.age>=Wife2.age*2));
        Husb2.dateOfBirth = "16 APR 1981";
        Husb2.dobDate = formatter.parse(Husb2.dateOfBirth);
        Husb2.age=g1.calculateAge(Husb2.dobDate);
        Wife2.dateOfBirth = "16 APR 2001";
        Wife2.dobDate = formatter.parse(Wife2.dateOfBirth);
        Wife2.age=g1.calculateAge(Wife2.dobDate);
        assertEquals(true,(Husb2.age>=Wife2.age*2));
    }

    @Test
    public void yearDiffBetweenTwoDate() throws Exception {
        Date date1 = new Date("5 FEB 2011");
        Date date2 = new Date("5 DEC 2021");
        assertEquals(-1, sprint4.yearDiffBetweenTwoDate(date1, date2));
        assertEquals(10, sprint4.yearDiffBetweenTwoDate(date2, date1));
    }

    @Test
    public void US12_birthBeforeDeathOfParents() throws Exception {
        Date date1 = new Date();
        Date date2 = new Date("5 DEC 2022");
        assertEquals(0, sprint4.calculateDays(date1));
        assertEquals(100, sprint4.calculateDays(date2));
        assertNotEquals(10, sprint4.calculateDays(date1));
    }

    @Test
    public void US18_SiblingsShouldNotMarry(){
        Individual I1 = new Individual();
        Individual I2 = new Individual();
        Individual I3 = new Individual();
        Individual I4 = new Individual();
        Individual I5 = new Individual();
        I1.id = "I1";
        I2.id = "I2";
        I3.id = "I3";
        I4.id = "I4";
        I5.id = "I5";
        Family f1 = new Family();
        f1.husbandId = "I1";
        f1.wifeId = "I2";
        f1.child.add("I3");
        Family f2 = new Family();
        f2.wifeId = "I5";
        f2.husbandId = "I1";
        f2.child.add("I4");
        Family f3 = new Family();
        f3.husbandId = "I3";
        f3.wifeId = "I4";
        g1.families.add(f1);
        g1.families.add(f2);
        g1.families.add(f3);
        assertEquals(true,sprint4.isSibling(g1.families,f3.husbandId,f3.wifeId));
        assertEquals(false,sprint4.isSibling(g1.families,f1.husbandId,f1.wifeId));
    }

    @Test
    public void us17_ParentsShouldNotMarryDescendants(){
        Individual I1 = new Individual();
        Individual I2 = new Individual();
        Individual I3 = new Individual();
        Individual I4 = new Individual();
        Individual I5 = new Individual();
        I1.id = "I1";
        I2.id = "I2";
        I3.id = "I3";
        I4.id = "I4";
        I5.id = "I5";
        Family f1 = new Family();
        f1.husbandId = "I1";
        f1.wifeId = "I2";
        f1.child.add("I3");
        Family f2 = new Family();
        f2.husbandId = "I4";
        f2.wifeId = "I3";
        f2.child.add("I5");
        Family f3 = new Family();
        f3.husbandId = "I1";
        f3.wifeId = "I5";
        g1.families.add(f1);
        g1.families.add(f2);
        g1.families.add(f3);
        assertEquals(true,sprint4.us17_ParentsShouldNotMarryDescendants(g1.families));
    }
}
