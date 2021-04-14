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

   @Test
    public void US09_birthBeforeDeathOfParents() throws Exception {
        Date date1 = new Date("5 FEB 2021");
        Date date2 = new Date("5 DEC 2021");
        assertEquals(100, sprint3.monthDiffBetweenTwoDate(date1, date2));   // father calculation
        assertEquals(-1, date1.compareTo(date2));                          // mother calculation
        assertEquals(10, sprint3.monthDiffBetweenTwoDate(date2, date1));
    }

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

    @Test
    public void US30_ListLivingMarriedIndividual() throws ParseException {
        Individual I1 = new Individual();
        Individual I2 = new Individual();
        DateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        I1.id = "I1";
        I1.name="Samuel /Lever/";
        I2.id = "I2";
        I2.name = "Stephanie /Silva/";
        I1.alive=true;
        g1.individuals.add(I1);
        I2.alive=false;
        g1.individuals.add(I2);
        f.husbandId = "I1";
        f.wifeId = "I2";
        f.dateOfMarried = "03 APR 2004";
        f.marrriedDate = formatter.parse(f.dateOfMarried);
        f.dividedDate = null;
        g1.families.add(f);
        assertEquals(true,(f.marrriedDate!=null&&f.dividedDate==null&&I1.alive==true));
        assertEquals(false,(f.marrriedDate!=null&&f.dividedDate==null&&I2.alive==true));
    }

    @Test
    public void US33_ListAllOrphanedChildrenBelow18() throws ParseException {
        Individual I1 = new Individual();
        Individual I2 = new Individual();
        Individual I3 = new Individual();
        Individual I4 = new Individual();
        DateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        I1.id = "I1";
        I1.name="Samuel /Lever/";
        I2.id = "I2";
        I2.name = "Stephanie /Silva/";
        I1.alive=false;
        I2.alive=false;
        f.husbandId = "I1";
        f.wifeId = "I2";
        I3.id = "I3";
        I3.dateOfBirth = "14 JUN 2014";
        I3.dobDate = formatter.parse(I3.dateOfBirth);
        I3.age = g1.calculateAge(I3.dobDate);
        Date today = new Date();
        f.child.add("I3");
        I4.id = "I4";
        I4.dateOfBirth = "14 JUN 2000";
        I4.dobDate = formatter.parse(I4.dateOfBirth);
        I4.age = g1.calculateAge(I4.dobDate);
        f.child.add("I4");
        assertEquals(true,(I1.alive==false&&I2.alive==false&&(I3.dobDate!=null&&(I3.dobDate.before(today)||I3.dobDate.equals(today))&&I3.age<18&&I3.age>=0)));
        assertEquals(false,(I1.alive==false&&I2.alive==false&&(I4.dobDate!=null&&(I4.dobDate.before(today)||I4.dobDate.equals(today))&&I4.age<18&&I4.age>=0)));
    }

    @Test
    public void US31_ListAllUnmarriedOver30(){
        Individual I1 = new Individual();
        Individual I2 = new Individual();
        I1.id = "I1";
        I1.name = "Rola /Pever/";
        I2.id = "I2";
        I2.name = "Kandy /Sumo/";
        f.husbandId = "I2";
        f.wifeId = "I1";
        I1.age = 29;
        I2.age = 33;
        assertEquals(true,((f.husbandId=="I2"||f.husbandId=="I2")&&I2.age>30));
        assertEquals(false,((f.wifeId == "I1"||f.husbandId=="I1")&&I1.age>30));
    }

    @Test
    public void US32_ListMultipleBirths(){
        Individual I1 = new Individual();
        Individual I2 = new Individual();
        Individual I3 = new Individual();
        Individual I4 = new Individual();
        I1.id = "I1";
        I1.name = "Rola /Pever/";
        I2.id = "I2";
        I2.name = "Kandy /Sumo/";
        f.husbandId = "I2";
        f.wifeId = "I1";
        I3.id = "I3";
        I3.name = "Silva /Sumo/";
        I4.id = "I4";
        I4.name = "Golu /Sumo/";
        I3.dateOfBirth = "2014-05-16";
        I4.dateOfBirth = "2014-05-16";
        f.child.add("I3");
        f.child.add("I4");
        assertEquals(true,(I3.dateOfBirth.equals(I4.dateOfBirth)));
    }
}