package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLOutput;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Stream;
import org.nocrala.tools.texttablefmt.Table;
import java.io.PrintStream;
public class GedcomReadParse {

    ArrayList<Family> families = new ArrayList<>();
    ArrayList<Individual> individuals = new ArrayList<>();
    DateFormat formatter = new SimpleDateFormat("dd MMM yyyy");

    //method to check the tag is valid or not
    static boolean findTag(String str, String[] tags) {
        for (int i = 0; i < tags.length; i++) {
            if (str.equals(tags[i]))
                return true;
        }
        return false;
    }

    //method to retrieve name from id from individuals
    String getIndividualName(String id){
        for(Individual ind: individuals){
            if(ind.id.equals(id))
                return ind.name;
        }
        return "id not found";
    }

    //us-21 changes starts @sr
    //method to retrieve gender from id from individuals
    String getGender(String id){
        for(Individual ind: individuals){
            if(ind.id.equals(id))
                return ind.gender;
        }
        return "id not found";
    }
    //us-21 changes ends @sr

    //converting date fromat to yyyy-mm-dd
    String changeDateFormat(String dateVar, Date dataField){
        String dateString;
        String month;
        String day;
        Instant instant = dataField.toInstant();
        ZonedDateTime zone = instant.atZone(ZoneId.systemDefault());
        LocalDate givenDate = zone.toLocalDate();
        month = givenDate.getMonthValue()<10?"0"+givenDate.getMonthValue():""+givenDate.getMonthValue();
        day = givenDate.getDayOfMonth()<10?"0"+givenDate.getDayOfMonth():""+givenDate.getDayOfMonth();
        dateString  = givenDate.getYear() + "-" + month + "-" + day;
        return dateString;
    }

    //calculating age of the individual
    int calculateAge(Date dob) {
        Instant instant = dob.toInstant();
        ZonedDateTime zone = instant.atZone(ZoneId.systemDefault());
        LocalDate givenDate = zone.toLocalDate();
        Period period = Period.between(givenDate, LocalDate.now());
        return period.getYears();
    }

    //us-01 changes starts @sr
    //us01 dates before current date
    public boolean validateDate(Date dateField, String dateStr){
        if(!(dateStr.equals("NA"))) {
            Date today = new Date();
            if (today.before(dateField))
                return false;
        }
        return true;
    }

    //us-01 changes ends @sr

    //us-21 changes starts @sr
    public boolean validateGenderForFamily(String id,String expectedGender){
        if(!getGender(id).equals(expectedGender))
            return false;
        return true;
    }
    //us-21 changes ends @sr

    // method to read GEDCOM FILE
    public void readGEDCOMFILE() {
        BufferedReader reader;
        String[] splitString;
        GedcomEntry e1;
        int counter = 0;
        Individual ind = new Individual();
        Family family = new Family();
        String day;
        String month;

        try {
            PrintStream fileOut = new PrintStream("./out.txt");
            PrintStream originalOut = System.out;
            //OPENING A FILE
            reader = new BufferedReader(new FileReader(
                    "Group01-AgileMethods.GED"));
            //READING FIRST LINE
            String line = reader.readLine();
            //VERIFYING IF LINE IS NOT NULL AND DOESNOT CONTAIN only SPACEs
            while (line != null && line.trim().length() > 0) {

                //spliting the line into array
                splitString = line.split(" ");

                //checking for INDI tag and level 0
                if (splitString.length>2 && splitString[2].equals("INDI") && splitString[0].equals("0")) {
                    //if already data is present in the ind object adding to array list
                    if (!(ind.id==null)) {
                        individuals.add(ind);
                        if(individuals.size()>=5000)
                            throw new ArrayIndexOutOfBoundsException("More than 5000 Individuals not allowed");
                    }

                    //initializing object ind.
                    ind = new Individual();
                    //splitting @ from INDI attr
                    ind.id = splitString[1].replaceAll("@","");
                    //fecthing the name of the INDI
                    line = reader.readLine();
                    splitString=line.split(" ");
                    if (splitString.length>2 && splitString[1].equals("NAME") && splitString[0].equals("1"))
                        ind.name = line.substring(line.indexOf(" ", line.indexOf(" ") + 1) + 1, line.length());


                }
                //if tags occured after INDI tag
                if(!(ind.id==null)) {


                    //fetching gender with level 1
                    if (splitString.length>2 && splitString[1].equals("SEX") && splitString[0].equals("1")) {
                        ind.gender = splitString[2];
                    }

                    //fetching BIRT with level 1
                    else if (splitString.length>1 && splitString[1].equals("BIRT") && splitString[0].equals("1")) {
                        line = reader.readLine();
                        splitString = line.split(" ");
                        //if BIRT tag exist checking date of birth with level and tag
                        if (splitString.length>2 && splitString[1].equals("DATE") && splitString[0].equals("2")) {
                            ind.dateOfBirth = line.substring(line.indexOf(" ", line.indexOf(" ") + 1) + 1, line.length());
                            ind.dobDate = formatter.parse(ind.dateOfBirth);
                            ind.dateOfBirth = changeDateFormat(ind.dateOfBirth,ind.dobDate);
                            ind.age = calculateAge(ind.dobDate);
                        }
                    }

                    //checking DEAT tag with level 1
                    else if (splitString.length>2 && splitString[1].equals("DEAT") && splitString[0].equals("1")) {
                        //setting alive variable depend on attr
                        if (splitString[2].equals("Y"))
                            ind.alive = false;
                        else
                            ind.alive = true;
                        line = reader.readLine();
                        splitString = line.split(" ");
                        //checking DATE tag for death date with level and tag
                        if (splitString.length>2 && splitString[1].equals("DATE") && splitString[0].equals("2")) {
                            {
                                ind.death = line.substring(line.indexOf(" ", line.indexOf(" ") + 1) + 1, line.length());
                                ind.deathDate = formatter.parse(ind.death);
                                ind.death=changeDateFormat(ind.death,ind.deathDate);
                            }
                        }
                    }

                    //checking whether the individual is child or spouse in the family
                    else if (splitString.length>2 && splitString[1].equals("FAMC") && splitString[0].equals("1")) {
                        ind.child = "{'" + splitString[2].replaceAll("@","") + "'}";
                    }

                    if (splitString.length>2 && splitString[1].equals("FAMS") && splitString[0].equals("1") && ind.spouse.equals("NA")) {
                        ind.spouse = "{'" + splitString[2].replaceAll("@","") + "'}";
                    }
                }

                // ------ main.Family Code logic ------ //
                // Checking for FAM tag for family at level 0
                if (splitString.length > 2 && splitString[2].equals("FAM") && splitString[0].equals("0")) {
                    //if already data is present in the fam object adding to array list
                    if (!(family.id==null)) {
                        if(families.size() >= 1000) {
                            throw new ArrayIndexOutOfBoundsException("More than 1000 families not allowed");
                        }
                        families.add(family);
                    }
                    family = new Family();
                    family.id = splitString[1].replaceAll("@","");
                }

                if(!(family.id == null)) {
                    // fetching husband with level 1 adding into the list
                    if (splitString.length > 2 && splitString[1].equals("HUSB") && splitString[0].equals("1")) {
                        family.husbandId = splitString[2].replaceAll("@","");
                    }

                    // fetching wife with level 1 adding into the list
                    else if (splitString.length > 2 && splitString[1].equals("WIFE") && splitString[0].equals("1")) {
                        family.wifeId = splitString[2].replaceAll("@","");
                    }

                    // fetching children with level 1 adding into the list
                    else if (splitString.length > 2 && splitString[1].equals("CHIL") && splitString[0].equals("1")) {
                        family.child.add(splitString[2].replaceAll("@",""));
                    }

                    // fetching marriage with level 1 adding into the list
                    else if (splitString.length > 1 && splitString[1].equals("MARR") && splitString[0].equals("1")) {
                        line = reader.readLine();
                        splitString = line.split(" ");
                        //if BIRT tag exist checking date of birth with level and tag
                        if (splitString.length > 2 && splitString[1].equals("DATE") && splitString[0].equals("2")) {
                            family.dateOfMarried = line.substring(line.indexOf(" ", line.indexOf(" ") + 1) + 1, line.length());
                            family.marrriedDate = formatter.parse(family.dateOfMarried);
                            family.dateOfMarried = changeDateFormat(family.dateOfMarried ,family.marrriedDate);
                        }
                    }

                    // fetching divided with level 1 adding into the list
                    else if (splitString.length > 1 && splitString[1].equals("DIV") && splitString[0].equals("1")) {
                        line = reader.readLine();
                        splitString = line.split(" ");
                        //if BIRT tag exist checking date of birth with level and tag
                        if (splitString.length > 2 && splitString[1].equals("DATE") && splitString[0].equals("2")) {
                            family.dateOfDivided = line.substring(line.indexOf(" ", line.indexOf(" ") + 1) + 1, line.length());
                            family.dividedDate = formatter.parse(family.dateOfDivided);
                            family.dateOfDivided = changeDateFormat(family.dateOfDivided ,family.dividedDate);
                        }
                    }
                }

                line = reader.readLine();
            }


            // After while loop ends, which is end of GED file pushing last class list into respective ArrayList
            if (!(ind.id==null)) {
                if(individuals.size() >= 5000) {
                    throw new ArrayIndexOutOfBoundsException("More than 5000 Individuals not allowed");
                }
                individuals.add(ind);
            }

            if (!(family.id==null)) {
                if(families.size() >= 1000) {
                    throw new ArrayIndexOutOfBoundsException("More than 1000 families not allowed");
                }
                families.add(family);
            }

            // Husband and wife name of family
            for(Family i: families) {
                i.husbandName = getIndividualName(i.husbandId);
                i.wifeName = getIndividualName(i.wifeId);
            }

            // Sorting using Unique Identifier for main.Individual and main.Family
            Collections.sort(families, Family.familyIdComparator);
            Collections.sort(individuals, Individual.IDComparator);

            // Table library
            Table table = new Table(9);
            //us-01 changes starts @sr
            Table us01 = new Table (3);
            //us-01 changes ends @sr

            //us-21 changes starts @sr
            Table us21 = new Table (5);
            //us-21 changes ends @sr

            table.addCell("ID");
            table.addCell("Name");
            table.addCell("Gender");
            table.addCell("Birthday");
            table.addCell("Age");
            table.addCell("Alive");
            table.addCell("Death");
            table.addCell("Child");
            table.addCell("Spouse");

            //us-01 changes starts @sr
            us01.addCell("Individual/Family ID");
            us01.addCell("Field Name");
            us01.addCell("Value");
            //us-01 changes ends @sr

            //us-21 changes starts @sr
            us21.addCell("Family ID");
            us21.addCell("Individual ID");
            us21.addCell("Role");
            us21.addCell("Expected Gender");
            us21.addCell("GEDCOM Gender");
            //us-21 changes ends @sr

            for(Individual i : individuals){
                table.addCell(i.id.toString());
                table.addCell(i.name.toString());
                table.addCell(i.gender.toString());
                table.addCell(i.dateOfBirth.toString());
                table.addCell(String.valueOf(i.age));
                if(i.alive == true) {
                    table.addCell("True");
                }
                else {
                    table.addCell("False");
                }
                table.addCell(i.death.toString());
                table.addCell(i.child.toString());
                table.addCell(i.spouse.toString());

                //us-01 changes starts @sr

                if(!validateDate(i.dobDate,i.dateOfBirth)) {
                    us01.addCell(i.id);
                    us01.addCell("BirthDay");
                    us01.addCell(i.dateOfBirth);
                }


                if(!validateDate(i.deathDate,i.death)){
                    us01.addCell(i.id);
                    us01.addCell("Death");
                    us01.addCell(i.death);
                }

                //us-01 changes ends @sr
            }
            fileOut.println("Individuals");
            fileOut.println(table.render());
            System.out.println("Individuals");
            System.out.println(table.render());

            Table table1 = new Table(8);
            table1.addCell("ID");
            table1.addCell("Married");
            table1.addCell("Divorced");
            table1.addCell("Husband ID");
            table1.addCell("Husband Name");
            table1.addCell("Wife ID");
            table1.addCell("Wife Name");
            table1.addCell("Children");



            for(Family i : families){
                table1.addCell(i.id.toString());
                table1.addCell(i.dateOfMarried.toString());
                table1.addCell(i.dateOfDivided.toString());
                table1.addCell(i.husbandId.toString());
                table1.addCell(i.husbandName.toString());
                table1.addCell(i.wifeId);
                table1.addCell(i.wifeName.toString());
                table1.addCell(i.printChildren());

                //us-01 changes starts @sr

                if(!validateDate(i.marrriedDate,i.dateOfMarried)) {
                    us01.addCell(i.id);
                    us01.addCell("Married");
                    us01.addCell(i.dateOfMarried);
                }

                if(!validateDate(i.dividedDate,i.dateOfDivided)){
                    us01.addCell(i.id);
                    us01.addCell("Divorced");
                    us01.addCell(i.dateOfDivided);
                }
                //us-01 changes ends @sr

                //us-21 changes starts @sr
                if(!validateGenderForFamily(i.husbandId,"M")){
                    us21.addCell(i.id);
                    us21.addCell(i.husbandId);
                    us21.addCell("Husband");
                    us21.addCell("M");
                    us21.addCell(getGender(i.husbandId));
                }

                if(!validateGenderForFamily(i.wifeId,"F")){
                    us21.addCell(i.id);
                    us21.addCell(i.wifeId);
                    us21.addCell("Wife");
                    us21.addCell("F");
                    us21.addCell(getGender(i.wifeId));
                }
                //us-21 changes ends @sr
            }

            fileOut.println("Families");
            fileOut.println(table1.render());
            System.out.println("Families");
            System.out.println(table1.render());

            //us-01 changes starts @sr
            System.out.println("US01 - Dates before Current Date");
            System.out.println(us01.render());
            fileOut.println("US01 - Dates before Current Date");
            fileOut.println(us01.render());
            //us-01 changes ends @sr


            //us-21 changes starts @sr
            System.out.println("US21 - Correct gender for role");
            System.out.println(us21.render());
            fileOut.println("US21 - Correct gender for role");
            fileOut.println(us21.render());
            //us-21 changes ends @sr

            //file closed
            reader.close();
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
