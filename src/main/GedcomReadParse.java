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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Stream;
import org.nocrala.tools.texttablefmt.Table;
import java.io.PrintStream;
import java.util.HashMap;
public class GedcomReadParse {

    ArrayList<Family> families = new ArrayList<>();
    public ArrayList<Individual> individuals = new ArrayList<>();
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
    String getIndividualName(String id) {
        for(Individual ind: individuals){
            if(ind.id.equals(id))
                return ind.name;
        }
        return "id not found";
    }

    //us-21 changes starts @sr
    //method to retrieve gender from id from individuals
    String getGender(String id) {
        for(Individual ind: individuals){
            if(ind.id.equals(id))
                return ind.gender;
        }
        return "id not found";
    }
    //us-21 changes ends @sr
    
    //us-22 changes starts @pp
    //This method to check whether Id's are unique or not of Individual's
    HashMap<String, Integer> IndividualId = new HashMap<String, Integer>();
    public void checkIndividualId() {
        for(Individual ind: individuals){
            if(IndividualId.get(ind.id)!=null){
                IndividualId.put(ind.id,2);
            }
            else if(IndividualId.get(ind.id)==null)
                IndividualId.put(ind.id,1);
        }
    }
    //This method to check whether Id's are unique or not of Families
    HashMap<String, Integer> FamilyId = new HashMap<String, Integer>();
    public void checkFamilyId() {
        for(Family fam: families){
            if(FamilyId.get(fam.id)!=null){
                FamilyId.put(fam.id,2);
            }
            if(FamilyId.get(fam.id)==null)
                FamilyId.put(fam.id,1);
        }
    }
    //us-22 changes ends @pp
    
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

    // us-35 changes starts @KP
    long calculateDays(Date dob) {
        System.out.println(dob);
        Instant instant = dob.toInstant();
        ZonedDateTime zone = instant.atZone(ZoneId.systemDefault());
        LocalDate givenDate = zone.toLocalDate();
      //  Period period = Period.between(givenDate, LocalDate.now());
        long p2 = ChronoUnit.DAYS.between(givenDate, LocalDate.now());
       // Period.ofDays(30);
        return p2;
    }
    // us-35 changes ends @KP


    // us-07 changes starts @KP
    // calculates age between date of birth and date of death
    int differenceBetweenTwoAge(Date dob, Date deathDate) {
        Instant instantDob = dob.toInstant();
        Instant instantDeathDate = deathDate.toInstant();
        ZonedDateTime zoneDob = instantDob.atZone(ZoneId.systemDefault());
        ZonedDateTime zoneDeathDate = instantDeathDate.atZone(ZoneId.systemDefault());
        LocalDate givenDobDate = zoneDob.toLocalDate();
        LocalDate givenDeathDate = zoneDeathDate.toLocalDate();
        Period period = Period.between(givenDobDate, givenDeathDate);
        return period.getYears();
    }
    // us-07 changes ends @KP

    //us-01 changes starts @sr
    //us01 dates before current date
    public boolean validateDate(Date dateField, String dateStr){
        if(!(dateStr.equals("NA") || dateStr.equals("INVALID DATE"))) {
            Date today = new Date();
            if (today.before(dateField))
                return false;
        }
        return true;
    }

    //Method to validate date
    public Date validateDate(String dateField){
        formatter.setLenient(false);
        Date returnDateField;
        try{
            returnDateField=formatter.parse(dateField);
        }
        catch(ParseException e){
            returnDateField=null;
        }
        return returnDateField;
    }

    //us-01 changes ends @sr

    //us-21 changes starts @sr
    public boolean validateGenderForFamily(String id,String expectedGender){
        if(!getGender(id).equals(expectedGender))
            return false;
        return true;
    }
    //us-21 changes ends @sr

    /*us-22 changes starts @pp*/
    //Validating whether the given IndividualId is unique or not
    public boolean validateIdForIndividual(String id){
        if(IndividualId.get(id) == 2)
            return true;
        return false;
    }

    //Validating whether the given FamilyId is unique or not
    public boolean validateIdForFamily(String id) {
        if (FamilyId.get(id)==2)
            return true;
        return false;
    }
    /*us-22 changes end @pp*/

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

                            //us-01 changes starts @sr
                            ind.dobDate=validateDate(ind.dateOfBirth);
                            if (ind.dobDate==null)
                                ind.dateOfBirth="INVALID DATE";
                            else {
                                ind.dateOfBirth = changeDateFormat(ind.dateOfBirth, ind.dobDate);
                                ind.age = calculateAge(ind.dobDate);
                            }
                            //us-01 changes ends @sr
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
                                ind.death = line.substring(line.indexOf(" ", line.indexOf(" ") + 1) + 1, line.length());
                                //us-01 changes starts @sr
                                ind.deathDate = validateDate(ind.death);
                                if (ind.deathDate == null || ind.dobDate == null)
                                    ind.death = "INVALID DATE";
                                else {   // us-07 changes starts @KP
                                    ind.death = changeDateFormat(ind.death, ind.deathDate);
                                    differenceBetweenTwoAge(ind.dobDate, ind.deathDate);
                                }
                                //us-01 changes ends @sr // us-07 changes ends @KP
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
                        if (splitString.length > 2 && splitString[1].equals("DATE") && splitString[0].equals("2"))
                        {
                            family.dateOfMarried = line.substring(line.indexOf(" ", line.indexOf(" ") + 1) + 1, line.length());
                            //us-01 changes starts @sr
                            family.marrriedDate=validateDate(family.dateOfMarried);
                            if(family.marrriedDate==null)
                                family.dateOfMarried="INVALID DATE";
                            else
                            family.dateOfMarried = changeDateFormat(family.dateOfMarried ,family.marrriedDate);
                            //us-01 changes ends @sr
                        }
                    }

                    // fetching divided with level 1 adding into the list
                    else if (splitString.length > 1 && splitString[1].equals("DIV") && splitString[0].equals("1")) {
                        line = reader.readLine();
                        splitString = line.split(" ");
                        //if BIRT tag exist checking date of birth with level and tag
                        if (splitString.length > 2 && splitString[1].equals("DATE") && splitString[0].equals("2")) {
                            family.dateOfDivided = line.substring(line.indexOf(" ", line.indexOf(" ") + 1) + 1, line.length());
                            //us-01 changes starts @sr
                            family.dividedDate=validateDate(family.dateOfDivided);
                            if(family.dividedDate==null)
                                family.dateOfDivided="INVALID DATE";
                            else
                            family.dateOfDivided = changeDateFormat(family.dateOfDivided ,family.dividedDate);
                            //us-01 changes starts @sr
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

            //us-07 changes starts @kp
            Table us07 = new Table (4);
            //us-07 changes ends @kp

            //us-21 changes starts @sr
            Table us21 = new Table (5);
            //us-21 changes ends @sr

            //us-35 changes starts @kp
            Table us35 = new Table (3);
            //us-35 changes ends @kp

            //us-22 changes starts @pp
            Table us22 = new Table(3);
            checkIndividualId();  //Calling to intialize HashMap
            //us-22 changes end @pp

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

            //us-07 changes starts @kp
            us07.addCell("Individual ID");
            us07.addCell("Individual Name");
            us07.addCell("Birth/Death");
            us07.addCell("Date shouln't be greater than 150 year or less than 0");
            //us-07 changes ends @kp

            //us-22 changes starts @pp
            us22.addCell("Individual ID");
            us22.addCell("Individual Name");
            us22.addCell("Unique ID or not");
            //us-22 changes ends @pp

            //us-35 changes starts @kp
            us35.addCell("Individual ID");
            us35.addCell("Individual Name");
            us35.addCell("Date of recent birth");
            //us-35 changes ends @kp

            for(Individual i : individuals) {
                table.addCell(i.id.toString());
                table.addCell(i.name.toString());
                table.addCell(i.gender.toString());
                table.addCell(i.dateOfBirth.toString());
                table.addCell(String.valueOf(i.age));
                if(i.alive) {
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

                //us-07 changes starts @kp
                int birthAge = calculateAge(i.dobDate);
                System.out.println("Birth age" + birthAge);
                if( birthAge > 150 || birthAge < 0) {
                    us07.addCell(i.id);
                    us07.addCell(i.name);
                    us07.addCell("Birth");
                    us07.addCell(i.dateOfBirth);
                }
                if(i.deathDate != null) {
                    int deathAge = differenceBetweenTwoAge(i.dobDate, i.deathDate);
                    if (deathAge > 150 || deathAge < 0) {
                        us07.addCell(i.id);
                        us07.addCell(i.name);
                        us07.addCell("Death");
                        us07.addCell(i.death);
                    }
                }
                //us-35 changes ends @kp


                //us-35 changes starts @kp
                if(calculateDays(i.dobDate) <= 30) {
                    us35.addCell(i.id);
                    us35.addCell(i.name);
                    us35.addCell(i.dateOfBirth);
                }
                //us-35 changes ends @kp

                //us-22 changes starts @pp
                if(validateIdForIndividual(i.id)){
                    us22.addCell(i.id);
                    us22.addCell(i.name);
                    us22.addCell("Not Unique");
                }
                else
                {
                    us22.addCell(i.id);
                    us22.addCell(i.name);
                    us22.addCell("Unique");
                }
                //us-22 ends @pp
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

            //us-07 changes starts @sr
            System.out.println("US07 - Less than 150 years old");
            System.out.println(us07.render());
            fileOut.println("US07 - Less than 150 years old");
            fileOut.println(us07.render());
            //us-01 changes ends @sr


            //us-21 changes starts @sr
            System.out.println("US21 - Correct gender for role");
            System.out.println(us21.render());
            fileOut.println("US21 - Correct gender for role");
            fileOut.println(us21.render());
            //us-21 changes ends @sr
            

            //us-22 changes start @pp
            System.out.println("US22 - Unique Id's");
            System.out.println(us22.render());
            fileOut.println("US22 - Unique Id's");
            fileOut.println(us21.render());
            //us-22 changes end @pp


            //us-35 changes starts @kp
            if(us35.render() != null) {
                System.out.println("US35 - List recent births");
                System.out.println(us35.render());
                fileOut.println("US35 - List recent births");
                fileOut.println(us35.render());
            }
            //us-35 changes ends @kp

            //file closed
            reader.close();
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}