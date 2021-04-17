package test;

import main.*;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Sprint4Test {
    GedcomReadParse g1 = new GedcomReadParse();

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
}
