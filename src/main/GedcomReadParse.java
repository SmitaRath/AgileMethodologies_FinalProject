package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Array;
import java.sql.SQLOutput;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;
import org.nocrala.tools.texttablefmt.Table;
import java.io.PrintStream;

public class GedcomReadParse {

    public ArrayList<Family> families = new ArrayList<>();
    public ArrayList<Individual> individuals = new ArrayList<>();
    ArrayList<String> successAnomalyDataUS35 = new ArrayList<>();
    ArrayList<String> successAnomalyDataUS36 = new ArrayList<>();
    ArrayList<String> errorAnomalyData = new ArrayList<>();
    ArrayList<String> errorAnomalyDataUS22 = new ArrayList<>();
    ArrayList<String> errorAnomalyDataUS02 = new ArrayList<>();
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
    Individual getIndividualData(String id) {
        for(Individual ind: individuals){
            if(ind.id.equals(id))
                return ind;
        }
        return null;
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

    /* us-22 changes starts @pp */
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

    //us-02 changes start @pp
    public boolean compareBirthWithMarriage(String marriagedate, int year, int month, int day){
        String marriageYear="";
        String marriageMonth="";
        String marriageDay="";
        int i;
        for (i = 0; marriagedate.charAt(i) != '-'; i++) {
            marriageYear = marriageYear + marriagedate.charAt(i);
        }
        for (i = i + 1; marriagedate.charAt(i) != '-'; i++) {
            marriageMonth = marriageMonth + marriagedate.charAt(i);
        }
        for (i = i + 1; i < marriagedate.length(); i++) {
            marriageDay = marriageDay + marriagedate.charAt(i);
        }
        int myear = Integer.valueOf(marriageYear);
        int mmonth = Integer.valueOf(marriageMonth);
        int mday = Integer.valueOf(marriageDay);
        if(validateDate(myear,mmonth,mday)){
            if (year > myear) {
                return true;
            }
            if (year == myear) {
                if (month > mmonth) {
                    return true;
                }
                if (month == mmonth) {
                    if (day >= mday) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //us-02 changes starts @pp
    public boolean ValidateBirthBeforeMarriage(String id, String marriagedate){
        String birthYear="";
        String birthMonth="";
        String birthDay="";
        int i;
        for(Individual ind : individuals){
            if(ind.id.equals(id)){
                for(i=0;ind.dateOfBirth.charAt(i)!='-';i++){
                    birthYear = birthYear + ind.dateOfBirth.charAt(i);
                }
                for(i=i+1;ind.dateOfBirth.charAt(i)!='-';i++){
                    birthMonth = birthMonth + ind.dateOfBirth.charAt(i);
                }
                for(i=i+1;i<ind.dateOfBirth.length();i++){
                    birthDay = birthDay + ind.dateOfBirth.charAt(i);
                }
                int year = Integer.valueOf(birthYear);
                int month = Integer.valueOf(birthMonth);
                int day = Integer.valueOf(birthDay);
                if(validateDate(year,month,day)){
                    if(compareBirthWithMarriage(marriagedate, year, month, day)){
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }
    //us-02 changes ends @pp

    //us-02 changes starts @pp
    public String getBirthDate(Family fam){
        for(Individual ind: individuals){
            if(ind.id.equals(fam.husbandId) || ind.id.equals(fam.wifeId)){
                if(ind.dobDate!=null)
                    return ind.dateOfBirth;
            }
        }
        return null;
    }
    public Individual getIndividual(String id){
        for(Individual ind: individuals){
            if(ind.id.equals(id))
                return ind;
        }
        return null;
    }
    //us-02 changes ends @pp

    //Validate Date created by @pp
    public boolean validateDate(int year, int month, int day){
        if(year<=0||month<=0||day<=0||day>31||month>12)
            return false;
        if(month==2){
            if(year%4==0&&day>29)
                return false;
            if(year%4!=0&&day>28)
                return false;
        }
        if(month%2==0&&month!=8){
            if(day>30)
                return false;
        }
        return true;
    }
    //us-02 changes ends @pp

    // US-03 changes starts@AS
    public boolean ValidateBirthbeforeDeath(Individual ind){
        String birthYear="";
        String birthMonth="";
        String birthDay="";
        int i;
        for(i=0;ind.dateOfBirth.charAt(i)!='-';i++){
            birthYear = birthYear + ind.dateOfBirth.charAt(i);
        }
        for(i=i+1;ind.dateOfBirth.charAt(i)!='-';i++){
            birthMonth = birthMonth + ind.dateOfBirth.charAt(i);
        }
        for(i=i+1;i<ind.dateOfBirth.length();i++){
            birthDay = birthDay + ind.dateOfBirth.charAt(i);
        }
        int year = Integer.valueOf(birthYear);
        int month = Integer.valueOf(birthMonth);
        int day = Integer.valueOf(birthDay);
        if(validateDate(year,month,day)){
            if(compareBirthwithdeath(ind, year, month, day)){
                return true;
            }
        }
        return false;
    }
    // US03 changes end

    // us03 start@ AS
    public boolean compareBirthwithdeath(Individual ind, int year, int month, int day){
        String marriageYear="";
        String marriageMonth="";
        String marriageDay="";
        int i;
        if(ind.alive)
            return false;
        for (i = 0; ind.death.charAt(i) != '-'; i++) {
            marriageYear = marriageYear + ind.death.charAt(i);
        }
        for (i = i + 1; ind.death.charAt(i) != '-'; i++) {
            marriageMonth = marriageMonth + ind.death.charAt(i);
        }
        for (i = i + 1; i < ind.death.length(); i++) {
            marriageDay = marriageDay + ind.death.charAt(i);
        }
        int myear = Integer.valueOf(marriageYear);
        int mmonth = Integer.valueOf(marriageMonth);
        int mday = Integer.valueOf(marriageDay);
        if(validateDate(myear,mmonth,mday)){
            if (year > myear) {
                return true;
            }
            if (year == myear) {
                if (month > mmonth) {
                    return true;
                }
                if (month == mmonth) {
                    if (day >= mday) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }
    // us-03 ends @AS

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
    public long calculateDays(Date dob) {
        Instant instant = dob.toInstant();
        ZonedDateTime zone = instant.atZone(ZoneId.systemDefault());
        LocalDate givenDate = zone.toLocalDate();
        long p2 = ChronoUnit.DAYS.between(givenDate, LocalDate.now());
        return p2;
    }
    // us-35 changes ends @KP


    // us-07 changes starts @KP
    // calculates age between date of birth and date of death
    public int differenceBetweenTwoAge(Date dob, Date deathDate) {
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
        if(!(dateStr.equals("NA") || dateField==null)) {
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
        if(!getIndividualData(id).gender.equals(expectedGender))
            return false;
        return true;
    }
    //us-21 changes ends @sr

    // method to read GEDCOM FILE
    public void readGEDCOMFILE() {
        BufferedReader reader;
        String[] splitString;
        GedcomEntry e1;
        int counter = 1;
        Individual ind = new Individual();
        Family family = new Family();
        Sprint2 sprint2 = new Sprint2();
        Sprint3 sprint3 = new Sprint3();
        String errString="";

        try {
            PrintStream fileOut = new PrintStream("./out.txt");
            PrintStream originalOut = System.out;
            //OPENING A FILE
            reader = new BufferedReader(new FileReader("Group01-AgileMethods.GED"));
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
                    ind.idLineNo=counter;
                    //fecthing the name of the INDI
                    line = reader.readLine();
                    splitString=line.split(" ");
                    if (splitString.length>2 && splitString[1].equals("NAME") && splitString[0].equals("1"))
                        ind.name = line.substring(line.indexOf(" ", line.indexOf(" ") + 1) + 1, line.length());
                    ind.name=ind.name.trim(); //fix for us23
                    counter++;
                    ind.nameLineNo=counter;

                }
                //if tags occured after INDI tag
                if(!(ind.id==null)) {

                    //fetching gender with level 1
                    if (splitString.length>2 && splitString[1].equals("SEX") && splitString[0].equals("1")) {
                        ind.gender = splitString[2];
                        ind.genderLineNo=counter;
                    }

                    //fetching BIRT with level 1
                    else if (splitString.length>1 && splitString[1].equals("BIRT") && splitString[0].equals("1")) {
                        line = reader.readLine();
                        splitString = line.split(" ");
                        //if BIRT tag exist checking date of birth with level and tag
                        if (splitString.length>2 && splitString[1].equals("DATE") && splitString[0].equals("2")) {
                            ind.dateOfBirth = line.substring(line.indexOf(" ", line.indexOf(" ") + 1) + 1, line.length());
                            counter++;
                            ind.dobLineNo=counter;
                            //us-01 changes starts @sr
                            ind.dobDate=validateDate(ind.dateOfBirth);
                            if (ind.dobDate!=null)
                            {
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
                            counter++;
                            ind.deathLineNo=counter;
                            //us-01 changes starts @sr
                            ind.deathDate = validateDate(ind.death);
                            if (ind.deathDate == null || ind.dobDate == null)
                                ind.death = line.substring(line.indexOf(" ", line.indexOf(" ") + 1) + 1, line.length());
                            else {   // us-07 changes starts @KP
                                ind.death = changeDateFormat(ind.death, ind.deathDate);
                                ind.age=differenceBetweenTwoAge(ind.dobDate, ind.deathDate);
                                differenceBetweenTwoAge(ind.dobDate, ind.deathDate);
                            }
                            //us-01 changes ends @sr // us-07 changes ends @KP
                        }
                    }

                    //checking whether the individual is child or spouse in the family
                    else if (splitString.length>2 && splitString[1].equals("FAMC") && splitString[0].equals("1")) {
                        ind.child = splitString[2].replaceAll("@","");
                        ind.childLineNo=counter;
                    }

                    if (splitString.length>2 && splitString[1].equals("FAMS") && splitString[0].equals("1") && ind.spouse.equals("NA")) {
                        ind.spouse = splitString[2].replaceAll("@","") ;
                        ind.spouseLineNo=counter;
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
                    family.idLineNo=counter;
                }

                if(!(family.id == null)) {
                    // fetching husband with level 1 adding into the list
                    if (splitString.length > 2 && splitString[1].equals("HUSB") && splitString[0].equals("1")) {
                        family.husbandId = splitString[2].replaceAll("@","");
                        family.husbandidLineNo=counter;
                    }

                    // fetching wife with level 1 adding into the list
                    else if (splitString.length > 2 && splitString[1].equals("WIFE") && splitString[0].equals("1")) {
                        family.wifeId = splitString[2].replaceAll("@","");
                        family.wifeidLineNo=counter;
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
                            counter++;
                            family.dateOfMarriedidLineNo=counter;
                            //us-01 changes starts @sr
                            family.marrriedDate=validateDate(family.dateOfMarried);
                            if(family.marrriedDate!=null)
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
                            counter++;
                            family.dateOfDividedLineNo=counter;
                            //us-01 changes starts @sr
                            family.dividedDate=validateDate(family.dateOfDivided);
                            if(family.dividedDate!=null)
                                family.dateOfDivided = changeDateFormat(family.dateOfDivided ,family.dividedDate);
                            //us-01 changes starts @sr
                        }
                    }
                }
                line = reader.readLine();
                counter++;
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

            //us-22 changes starts @pp
            checkIndividualId();  //Calling to intialize HashMap
            checkFamilyId();   // Calling to intialize HashMap
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
                if(!i.child.equals("NA"))
                    table.addCell("{'"+i.child.toString()+"'}");
                else
                    table.addCell(i.child.toString());
                if(!i.spouse.equals("NA"))
                    table.addCell("{'" +i.spouse.toString()+"'}");
                else
                    table.addCell(i.spouse.toString());

                //us-01 changes starts @sr

                if(!validateDate(i.dobDate,i.dateOfBirth)) {
                    errString = "Error: In US01 for INDIVDUAL at Line no: " + i.dobLineNo +
                            "; ID: "  + i.id + ";"+
                            " BirthDay " + i.dateOfBirth +
                            " occurs in the future";
                    errorAnomalyData.add(errString);
                }


                if(!validateDate(i.deathDate,i.death)){
                    errString = "Error: In US01 for INDIVDUAL at Line no: " + i.deathLineNo +
                            "; Id: " + i.id +";" +
                            " Death " + i.death +
                            " occurs in the future";
                    errorAnomalyData.add(errString);
                }

                //us-01 changes ends @sr

                //us-42 changes starts
                sprint2.checkIllegitimateDate(i,"BIRT",family);
                if(!(i.death.equals("NA")))
                    sprint2.checkIllegitimateDate(i,"DEAT",family);
                //us-42 changes ends

                //us-07 changes starts @kP
                if(i.age > 150) {
                    errString = "Error: In US07 for INDIVIDUAL at Line no: " + i.dobLineNo + "; ID: "
                            + i.id +
                            "; BirthDay: " + i.dateOfBirth +
                            "; Current date should be less than 150 years after birth for all living people";
                    errorAnomalyData.add(errString);
                }

                if(i.deathDate != null) {
                    int deathAge = differenceBetweenTwoAge(i.dobDate, i.deathDate);
                    if (deathAge > 150) {
                        errString = "Error: In US07 for INDIVIDUAL at Line no: " + i.deathLineNo + "; ID: " +
                                i.id +
                                "; Deathday " + i.death +
                                "; Death date should be less than 150 years after birth for dead people";
                        errorAnomalyData.add(errString);
                    }
                }
                //us-07 changes ends @kp


                //us-35 changes starts @kp
                if(i.dobDate != null) {
                    long noDays = calculateDays(i.dobDate);
                    if (noDays <= 30 && noDays >= 0) {
                        String successMessage = "", name = "";
                        String[] formatName;
                        formatName = i.name.split("/");
                        name = formatName[0] + formatName[1];
                        successMessage = "ID: " + i.id + " NAME: " + name + " Date of Birth: " + i.dateOfBirth + " Birth age in no.Of.Days: " + noDays;
                        successAnomalyDataUS35.add(successMessage);
                    }
                }
                //us-35 changes ends @kp

                //us-36 changes starts @kp
                if(i.deathDate != null) {
                    long noOfDays = calculateDays(i.deathDate);
                    if (noOfDays <= 30 && noOfDays >= 0) {
                        String successMessage = "", name = "";
                        String[] formatName;
                        formatName = i.name.split("/");
                        name = formatName[0] + formatName[1];
                        successMessage = "ID: " + i.id + " NAME: " + name + " Date of Death: " + i.death + " Death age in no.Of.Days: " + noOfDays;
                        successAnomalyDataUS36.add(successMessage);
                    }
                }
                //us-36 changes ends @kp

                //us-22 changes starts @pp
                if(validateIdForIndividual(i.id)) {
                    errString = "Error: In US22 for INDIVIDUAL at Line no: " + i.idLineNo +
                            "; ID: "  + i.id + "; "+
                            "The Individual ID is not unique";
                    errorAnomalyData.add(errString);
                }
                //us-22 ends @pp

                //US-03 Changes starts @AS
                if(i.deathDate!=null&&i.dobDate!=null&&ValidateBirthbeforeDeath(i)){
                    errString = "Error: In US03 for INDIVIDUAL at Line no: "+
                            i.dobLineNo +","+i.deathLineNo + "; ID: " + i.id + "; "+
                            "Date of Birth: " + i.dateOfBirth +
                            "; " + "Date of Death: " + i.death +
                            "; " + "Birth Occurs After Death";
                    errorAnomalyData.add(errString);
                }
                // US-03 changes ends @AS
                if(i.deathDate == null) {
                    sprint3.US38_listAllLivingUpcomingBirthday(i);
                }

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
                    errString = "Error: In US01 for FAMILY at " + "Line no :" + i.dateOfMarriedidLineNo
                            + "; ID: " + i.id +";"
                            + " Marriage Date " + i.dateOfMarried +
                            " occurs in the future";
                    errorAnomalyData.add(errString);

                }

                if(!validateDate(i.dividedDate,i.dateOfDivided)){
                    errString = "Error: In US01 For FAMILY at " + "Line no :" + i.dateOfDividedLineNo
                            + "; ID: " + i.id +";"
                            + " Divided Date " + i.dateOfDivided +
                            " occurs in the future";
                    errorAnomalyData.add(errString);
                }
                //us-01 changes ends @sr

                //us-21 changes starts @sr
                if(!validateGenderForFamily(i.husbandId,"M")){
                    ind = getIndividualData(i.husbandId);
                    errString = "Error: In US21 for INDIVIDUAL at "
                            + " Line no: " + ind.genderLineNo
                            + "; Husband's Id :" + i.husbandId +";"
                            + " in family: " + i.id + ""
                            + " Gender is " + ind.gender;
                    errorAnomalyData.add(errString);
                }

                if(!validateGenderForFamily(i.wifeId,"F")) {
                    ind = getIndividualData(i.wifeId);
                    errString = "Error: In US21 for INDIVIDUAL at "
                            + " Line No: " + ind.genderLineNo
                            + "; Wife's Id :" + i.wifeId +";"
                            + " in family: " + i.id + ""
                            + " Gender is " + ind.gender;
                    errorAnomalyData.add(errString);
                }
                //us-21 changes ends @sr

                //us-42 changes starts
                sprint2.checkIllegitimateDate(ind,"MARR",i);
                if(!(i.dateOfDivided.equals("NA")))
                    sprint2.checkIllegitimateDate(ind,"DIV",i);
                //us-42 changes ends

                //us-22 changes starts @pp
                if(validateIdForFamily(i.id)){
                    errString = "Error: In US22 for FAMILY at Line no: " +
                            + i.idLineNo + "; ID: "
                            + i.id +"; "
                            + "This Family ID is not unique";
                    errorAnomalyData.add(errString);
                }
                //us-22 changes ends @pp

                //us-02 changes starts @pp
                if(i.marrriedDate!=null&&getIndividual(i.husbandId).dobDate!=null&&ValidateBirthBeforeMarriage(i.husbandId, i.dateOfMarried)){
                    errString = "Error: In US02 for INDIVIDUAL at Line no: "+
                            getIndividual(i.husbandId).dobLineNo + "," + i.dateOfMarriedidLineNo +
                            "; ID: " + i.husbandId + "; "+ "Date of Birth: " + getBirthDate(i) +
                            "; " + "Date of Marriage: " + i.dateOfMarried +
                            "; " + "Birth Occurs After Marriage";
                    errorAnomalyData.add(errString);
                }
                if(i.marrriedDate!=null&&getIndividual(i.wifeId).dobDate!=null&&ValidateBirthBeforeMarriage(i.wifeId, i.dateOfMarried)){
                    errString = "Error: In US02 for INDIVIDUAL at Line no: "+
                            getIndividual(i.wifeId).dobLineNo + "," + i.dateOfMarriedidLineNo
                            +"; ID: " + i.wifeId + "; "+ "Date of Birth: " + getBirthDate(i) +
                            "; " + "Date of Marriage: " + i.dateOfMarried +
                            "; " + "Birth Occurs After Marriage";
                    errorAnomalyData.add(errString);
                }
                //us-02 changes ends @pp

                //us-05 changes starts @pp
                if(i.marrriedDate!=null&&getIndividual(i.husbandId).deathDate!=null&&sprint2.ValidateMarriageBeforeDeath(individuals,i.husbandId, i.dateOfMarried)){
                    errString = "Error: In US05 for INDIVIDUAL at Line no: "+
                            getIndividual(i.husbandId).deathLineNo + "," + i.dateOfMarriedidLineNo +
                            "; Husband ID: " + i.husbandId + "; "+ "Date of death: " + getIndividual(i.husbandId).death +
                            "; " + "Date of Marriage: " + i.dateOfMarried +
                            "; " + "Death of Husband Occurs Before his Marriage";
                    sprint2.errorAnomalyData.add(errString);
                }
                if(i.marrriedDate!=null&&getIndividual(i.wifeId).deathDate!=null&&sprint2.ValidateMarriageBeforeDeath(individuals,i.wifeId, i.dateOfMarried)){
                    errString = "Error: In US05 for INDIVIDUAL at Line no: "+
                            getIndividual(i.wifeId).deathLineNo + "," + i.dateOfMarriedidLineNo +
                            "; Wife ID: " + i.wifeId + "; "+ "Date of death: " + getIndividual(i.wifeId).death +
                            "; " + "Date of Marriage: " + i.dateOfMarried +
                            "; " + "Death of Wife Occurs Before her Marriage";
                    sprint2.errorAnomalyData.add(errString);
                }
                //us-05 changes ends @pp

                //us-06 changes starts @pp
                if(i.dividedDate!=null&&getIndividual(i.husbandId).deathDate!=null&&sprint2.ValidateDivorceBeforeDeath(individuals,i.husbandId, i.dateOfDivided)){
                    errString = "Error: In US06 for INDIVIDUAL at Line no: "+
                            getIndividual(i.husbandId).deathLineNo + "," + i.dateOfDividedLineNo +
                            "; Husband ID: " + i.husbandId + "; "+ "Date of death: " + getIndividual(i.husbandId).death +
                            "; " + "Date of Divorce: " + i.dateOfDivided +
                            "; " + "Death of Husband Occurs Before his Divorce";
                    sprint2.errorAnomalyData.add(errString);
                }
                if(i.dividedDate!=null&&getIndividual(i.wifeId).deathDate!=null&&sprint2.ValidateDivorceBeforeDeath(individuals,i.wifeId, i.dateOfDivided)){
                    errString = "Error: In US06 for INDIVIDUAL at Line no: "+
                            getIndividual(i.wifeId).deathLineNo + "," + i.dateOfDividedLineNo +
                            "; Wife ID: " + i.wifeId + "; "+ "Date of death: " + getIndividual(i.wifeId).death +
                            "; " + "Date of Divorce: " + i.dateOfDivided +
                            "; " + "Death of Wife Occurs Before her Divorce";
                    sprint2.errorAnomalyData.add(errString);
                }
                //us-06 changes ends @pp

                //US-08, US16 changes starts @KP
                if(i.child != null) {
                    sprint2.US08_birthBeforeMarriageOfParents(i, individuals);
                    sprint2.US16_maleLastName(i, individuals);
                    sprint3.US09_birthBeforeDeathOfParents(i, individuals);
                }

                //US-08,US16 changes ends @KP

                //us-10 changes starts @AS
                if(i.marrriedDate!=null&&getIndividual(i.husbandId).dobDate!=null&sprint2.compareMarrigeandBirth(i.dateOfMarried,getIndividual(i.husbandId).dateOfBirth)){
                    errString = "Error: In US10 for INDIVIDUAL at Line no: "+
                            getIndividual(i.husbandId).dobLineNo + "," + i.dateOfMarriedidLineNo +
                            "; ID: " + i.husbandId + "; "+ "Date of Birth: " + getIndividual(i.husbandId).dateOfBirth +
                            "; " + "Date of Marriage: " + i.dateOfMarried +
                            "; " + "Marrige date is less than 14 years of birth date";
                    sprint2.errorAnomalyData.add(errString);
                }
                if(i.marrriedDate!=null&&getIndividual(i.wifeId).dobDate!=null&&sprint2.compareMarrigeandBirth(i.dateOfMarried,getIndividual(i.wifeId).dateOfBirth)){
                    errString = "Error: In US10 for INDIVIDUAL at Line no: "+
                            getIndividual(i.wifeId).dobLineNo + "," + i.dateOfMarriedidLineNo
                            +"; ID: " + i.wifeId + "; "+ "Date of Birth: " + getIndividual(i.wifeId).dateOfBirth +
                            "; " + "Date of Marriage: " + i.dateOfMarried +
                            "; " + "Marrige date is less than 14 years of birth date";
                    sprint2.errorAnomalyData.add(errString);
                }
                //us-10 changes ends @AS

                // US15 changes starts @AS
                if(sprint2.NoOfSiblings(families, i.wifeId,i.husbandId)) {
                    errString = "Error: In US15 for Family at Line no: " +
                            i.idLineNo
                            + "; ID: " + i.id +
                            "; " + "All children of this Family have siblings greater than or equal to 15";
                    sprint2.errorAnomalyData.add(errString);
                }
                //US15 ends @AS
            }



            fileOut.println("Families");
            fileOut.println(table1.render());
            System.out.println("Families");
            System.out.println(table1.render());

            //US28 CHANGES STARTS @SR
            sprint3.us28_orderSiblingsByAge(families,individuals);
            sprint3.us29_listDeceasedIndividual(individuals);
            //us28 changes ends @sr

            //US30 changes starts @pp
            sprint3.us30_ListLivingMarriedIndividual(families,individuals);
            //US30 changes ends @pp

            //US33 changes starts @pp
            sprint3.us33_ListAllOrphanedChildrenBelow18(families,individuals);
            //US33 changes ends @pp

            fileOut.println();
            System.out.println();
            fileOut.println("============================== Sprint1 Output =======================================");
            fileOut.println();
            System.out.println("============================== Sprint1 Output =======================================");
            System.out.println();
            if(!successAnomalyDataUS35.isEmpty()) {
                fileOut.println();
                System.out.println();
                fileOut.println("US35: List all Recent births");
                System.out.println("US35: List all Recent births");
                for (String str : successAnomalyDataUS35) {
                    fileOut.println(str);
                    System.out.println(str);
                }
            }

            fileOut.println();
            System.out.println();
            fileOut.println("US36: List all Recent deaths");
            System.out.println("US36: List all Recent deaths");
            for(String str:successAnomalyDataUS36){
                fileOut.println(str);
                System.out.println(str);
            }


            fileOut.println();
            System.out.println();

            for(String str:errorAnomalyData){
                fileOut.println(str);
                System.out.println(str);
            }

            //us-23 sprint2 changes starts @sr

            fileOut.println();
            fileOut.println("============================== Sprint2 Output =======================================");
            System.out.println();
            System.out.println("============================== Sprint2 Output =======================================");
            fileOut.println();
            System.out.println();

            // sprint2.sprint2Output(fileOut);

            sprint2.checkUniqueDateOfBirthAndName(individuals);
            sprint2.sprint2ErrorOutput(fileOut);
            //us-23 sprint2 changes ends @sr

            fileOut.println();
            fileOut.println("============================== Sprint3 Output =======================================");
            System.out.println();
            System.out.println("============================== Sprint3 Output =======================================");
            fileOut.println();
            System.out.println();

            sprint3.sprint3SuccessOutput(fileOut);


            fileOut.println();
            System.out.println();

            sprint3.sprint3ErrorOutput(fileOut);
            //us-23 sprint2 changes ends @sr

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