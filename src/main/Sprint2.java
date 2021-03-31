package main;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Sprint2 {

    ArrayList<String> successAnomalyDataUS16 = new ArrayList<>();
    ArrayList<String> errorAnomalyDataUS16 = new ArrayList<>();  // Created error seperate because here we need to display success and error list
    HashMap<String, String> familyLastName = new HashMap<String, String>();
    ArrayList<String> errorAnomalyData = new ArrayList<>();
    String message = "";

    // us-08 changes starts @KP
    // calculates months between two dates
    public int monthDiffBetweenTwoDate(Date dob, Date divDate) {
        Instant instantDob = dob.toInstant();
        Instant instantDivDate = divDate.toInstant();
        ZonedDateTime zoneDob = instantDob.atZone(ZoneId.systemDefault());
        ZonedDateTime zoneDeathDate = instantDivDate.atZone(ZoneId.systemDefault());
        LocalDate givenDobDate = zoneDob.toLocalDate();
        LocalDate givenDeathDate = zoneDeathDate.toLocalDate();
        Period period = Period.between(givenDeathDate, givenDobDate);
        if (period.getYears() > 0)   // If more than year, then it's more than 9 months; sending random no greater than 9
            return 100;

        if (period.getYears() == 0) {
            if (period.getMonths() >= 0) {
                return period.getMonths();
            }
        }

        return 0;
    }
    // us-08 changes ends @KP

    public  void US16_maleLastName(Family family, ArrayList<Individual> individuals) {
        if (family.husbandName != null) {
            String lastName = null;
            String[] formatName;
            formatName =  family.husbandName.split("/");
            if(formatName[1] != null) {
                lastName = formatName[1].trim();
            }

            if (lastName != null) {
                Individual individualData = null;
                for(String child: family.child) {
                    for (Individual ind : individuals) {
                        if (ind.id.equals(child) && ind.gender.toLowerCase().equals("m")) {
                            individualData = ind;
                            break;
                        }
                    }
                    if(individualData != null ) {
                        String[] individualName;
                        String individualLastName = null;
                        individualName =  individualData.name.split("/");
                        if(individualName[1] != null) {
                            individualLastName = individualName[1].trim();
                        }

                        if(lastName.toLowerCase().equals(individualLastName.toLowerCase())) {
                            message = "ID: " + individualData.id + " GENDER: " + individualData.gender + " Last NAME: " + individualLastName;
                            successAnomalyDataUS16.add(message);
                        } else {
                            message = "Error: In US16 for INDIVIDUAL at Line no: " + individualData.nameLineNo + "; ID: "
                                    + individualData.id + "; Individual Name: " + individualData.name + " ; Family ID: " + individualData.spouse + " ; Family Name: " + lastName +
                                    "; Family last name should be same for all males in family.";
                            errorAnomalyDataUS16.add(message);

                        }
                    }
                }
            } else {
                message = "Error: In US16 for INDIVIDUAL at Line no: " + family.husbandidLineNo + "; ID: "
                        + family.husbandId + " Individual Name: " + family.husbandName +
                        "; Family last name should be present for all males in family.";
                errorAnomalyDataUS16.add(message);
            }
        }
    }

    public  void US08_birthBeforeMarriageOfParents(Family family, ArrayList<Individual> individuals) {
        Individual individualData = null;
        for(String child: family.child) {
            for(Individual ind: individuals) {
                if(ind.id.equals(child)) {
                    individualData = ind;
                    break;
                }
            }

            if (individualData != null && individualData.dobDate != null && family.marrriedDate != null) {
                if(individualData.dobDate.compareTo(family.marrriedDate) <= 0) {
                    message = "Error: In US08 for INDIVIDUAL at Line no: " + individualData.dobLineNo + "; ID: "
                            + individualData.id + "; Individual Name: " + individualData.name + "; Birth date: " + individualData.dateOfBirth + " ; Family ID: " + family.id + " ; Parents Married Date: " + family.dateOfMarried +
                            "; Line no:" + family.dateOfMarriedidLineNo + "; Children should be born after marriage of parents.";
                    errorAnomalyData.add(message);
                }
            }

            if (family.dividedDate !=  null) {
                if (monthDiffBetweenTwoDate(individualData.dobDate ,family.dividedDate) >= 9) {
                    message = "Error: In US08 for INDIVIDUAL at Line no: " + individualData.dobLineNo + "; ID: "
                            + individualData.id + "; Individual Name: " + individualData.name + "; Birth date: " + individualData.dateOfBirth + " ; Family ID: " + family.id + " ; Parents Divorced Date: " + family.dateOfDivided +
                            "; Line no:" + family.dateOfDividedLineNo + "; Children should be born, not more than 9 months of their divorce";
                    errorAnomalyData.add(message);
                }
            }
        }
    }

    //US05 changes starts @pp
    public boolean compareDeathWithMarriage(String marriagedate, int year, int month, int day){
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
            if (myear > year) {
                return true;
            }
            if (myear == year) {
                if (mmonth > month) {
                    return true;
                }
                if (mmonth == month) {
                    if (mday >= day) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

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
    
    public boolean ValidateMarriageBeforeDeath(ArrayList<Individual> individuals, String id, String marriagedate){
        String deathYear="";
        String deathMonth="";
        String deathDay="";
        int i;
        for(Individual ind : individuals){
            if(ind.id.equals(id)&&(!ind.alive)){
                for(i=0;ind.death.charAt(i)!='-';i++){
                    deathYear = deathYear + ind.death.charAt(i);
                }
                for(i=i+1;ind.death.charAt(i)!='-';i++){
                    deathMonth = deathMonth + ind.death.charAt(i);
                }
                for(i=i+1;i<ind.death.length();i++){
                    deathDay = deathDay + ind.death.charAt(i);
                }
                int year = Integer.valueOf(deathYear);
                int month = Integer.valueOf(deathMonth);
                int day = Integer.valueOf(deathDay);
                if(validateDate(year,month,day)){
                    if(compareDeathWithMarriage(marriagedate, year, month, day)){
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }
    //US05 changes ends @pp
    
    //US06 changes starts @pp
    public boolean compareDeathWithDivorce(String divorcedate, int year, int month, int day){
        String divorceYear="";
        String divorceMonth="";
        String divorceDay="";
        int i;
        for (i = 0; divorcedate.charAt(i) != '-'; i++) {
            divorceYear = divorceYear + divorcedate.charAt(i);
        }
        for (i = i + 1; divorcedate.charAt(i) != '-'; i++) {
            divorceMonth = divorceMonth + divorcedate.charAt(i);
        }
        for (i = i + 1; i < divorcedate.length(); i++) {
            divorceDay = divorceDay + divorcedate.charAt(i);
        }
        int diyear = Integer.valueOf(divorceYear);
        int dimonth = Integer.valueOf(divorceMonth);
        int diday = Integer.valueOf(divorceDay);
        if(validateDate(diyear,dimonth,diday)){
            if (diyear > year) {
                return true;
            }
            if (diyear == year) {
                if (dimonth > month) {
                    return true;
                }
                if (dimonth == month) {
                    if (diday >= day) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean ValidateDivorceBeforeDeath(ArrayList<Individual> individuals, String id, String divorcedate){
        String deathYear="";
        String deathMonth="";
        String deathDay="";
        int i;
        for(Individual ind : individuals){
            if(ind.id.equals(id)&&(!ind.alive)){
                for(i=0;ind.death.charAt(i)!='-';i++){
                    deathYear = deathYear + ind.death.charAt(i);
                }
                for(i=i+1;ind.death.charAt(i)!='-';i++){
                    deathMonth = deathMonth + ind.death.charAt(i);
                }
                for(i=i+1;i<ind.death.length();i++){
                    deathDay = deathDay + ind.death.charAt(i);
                }
                int year = Integer.valueOf(deathYear);
                int month = Integer.valueOf(deathMonth);
                int day = Integer.valueOf(deathDay);
                if(validateDate(year,month,day)){
                    if(compareDeathWithDivorce(divorcedate, year, month, day)){
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }
    //US06 changes ends @pp

    //us-23 changes method to check individual and date of birth is unique or not starts @sr
    public boolean checkUniqueDateOfBirthAndName(ArrayList<Individual> individuals){
        String errString="";
        Individual ind1;
        Individual ind2;
        boolean outerNameFlagNotUnique=false;
        boolean outerDOBFlagNotUnique=false;
        int prevSize=errorAnomalyData.size();
        String[] name;
        String fullnameOuter="";
        String fullnameInner="";
        for(int i=0;i<individuals.size();i++){
            ind1=individuals.get(i);
            name = ind1.name.split("/");
            fullnameOuter=name[0].toUpperCase()+name[1].toUpperCase();
            for(int k=i+1;k<individuals.size();k++){
                ind2=individuals.get(k);
                name = ind2.name.split("/");
                fullnameInner=name[0].toUpperCase()+name[1].toUpperCase();
                if(fullnameOuter.equals(fullnameInner)){
                    outerNameFlagNotUnique=true;
                    errString = "Error: In US23 for INDIVIDUAL at "
                            + " Line no: " + ind2.nameLineNo
                            + "; Id :" + ind2.id
                            + "; Name :" + ind2.name +";"
                            + " is not unique ";
                    errorAnomalyData.add(errString);
                }

                if(ind1.dateOfBirth.equals(ind2.dateOfBirth)) {
                    outerDOBFlagNotUnique = true;
                    errString = "Error: In US23 for INDIVIDUAL at "
                            + " Line no: " + ind2.dobLineNo
                            + "; Id :" + ind2.id
                            + "; Date Of Birth :" + ind2.dateOfBirth + ";"
                            + " is not unique ";
                    errorAnomalyData.add(errString);
                }
            }

            if(outerNameFlagNotUnique){
                outerNameFlagNotUnique=false;
                errString = "Error: In US23 for INDIVIDUAL at "
                        + " Line no: " + ind1.nameLineNo
                        + "; Id :" + ind1.id
                        + "; Name :" + ind1.name +";"
                        + " is not unique ";
                errorAnomalyData.add(errString);
            }

            if(outerDOBFlagNotUnique){
                outerDOBFlagNotUnique=false;
                errString = "Error: In US23 for INDIVIDUAL at "
                        + " Line no: " + ind1.dobLineNo
                        + "; Id :" + ind1.id
                        + "; Date Of Birth :" + ind1.dateOfBirth +";"
                        + " is not unique ";
                errorAnomalyData.add(errString);
            }
        }
        if(prevSize!=errorAnomalyData.size())
            return false;
        else
            return true;
    }
    //us-23 changes method to check individual and date of birth is unique or not ends @sr

   //us-42 changes method to Reject illegitimate dates starts @sr
    public boolean checkIllegitimateDate(Individual ind,String flag,Family family){
        String errString="";
        int prevSize=errorAnomalyData.size();
        switch(flag) {
            case "BIRT": {
                if (ind.dobDate == null) {
                    errString = "Error: In US42 for INDIVIDUAL at "
                            + " Line no: " + ind.dobLineNo
                            + "; Id :" + ind.id
                            + "; Date Of Birth :" + ind.dateOfBirth + ";"
                            + " is not legitimate ";
                    errorAnomalyData.add(errString);
                }
            }
            break;

            case "DEAT": {
                if (ind.deathDate == null) {
                    errString = "Error: In US42 for INDIVIDUAL at "
                            + " Line no: " + ind.deathLineNo
                            + "; Id :" + ind.id
                            + "; Death Date :" + ind.death + ";"
                            + " is not legitimate ";
                    errorAnomalyData.add(errString);
                }
            }
            break;

            case "MARR": {
                if (family.marrriedDate == null) {
                    errString = "Error: In US42 for Family at "
                            + " Line no: " + family.dateOfMarriedidLineNo
                            + "; Family Id :" + family.id
                            + "; Marriage Date :" + family.dateOfMarried + ";"
                            + " is not legitimate ";
                    errorAnomalyData.add(errString);
                }
            }
            break;

            case "DIV": {
                if (family.dividedDate == null) {
                    errString = "Error: In US42 for Family at "
                            + " Line no: " + family.dateOfDividedLineNo
                            + "; Family Id :" + family.id
                            + "; Divorced Date :" + family.dateOfDivided + ";"
                            + " is not legitimate ";
                    errorAnomalyData.add(errString);
                }
            }
            break;
        }

        if(prevSize!=errorAnomalyData.size())
            return false;
        else
            return true;
    }
    //us-42 changes method to Reject illegitimate dates ends @sr




    //us09 changes starts @AS
    public boolean compareMarrigeandBirth(String marriagedate, String birthDate ){
    String marriageYear="";
    String marriageMonth="";
    String marriageDay="";
    String birthYear="";
    String birthMonth="";
    String birthDay="";

    int i;
    for (i = 0; marriagedate.charAt(i)!= '-'; i++) {
        marriageYear = marriageYear + marriagedate.charAt(i);
    }
    for (i = i + 1; marriagedate.charAt(i) != '-'; i++) {
        marriageMonth = marriageMonth + marriagedate.charAt(i);
    }
    for (i = i + 1; i < marriagedate.length(); i++) {
        marriageDay = marriageDay + marriagedate.charAt(i);
    }
    for(i=0; birthDate.charAt(i)!='-';i++)
    {
        birthYear = birthYear + birthDate.charAt(i);
    }
    for (i = i + 1; birthDate.charAt(i) != '-'; i++) {
        birthMonth = birthMonth + marriagedate.charAt(i);
    }
    for (i = i + 1; i < birthDate.length(); i++) {
        birthDay = birthDay + birthDate.charAt(i);
    }
    int myear = Integer.valueOf(marriageYear);
    int mmonth = Integer.valueOf(marriageMonth);
    int mday = Integer.valueOf(marriageDay);
    int byear = Integer.valueOf(birthYear);
    int bmonth = Integer.valueOf(birthMonth);
    int bday = Integer.valueOf(birthDay);
    if(validateDate(myear,mmonth,mday)&&validateDate(byear,bmonth,bday)) {
        if (myear >= byear&&myear<=byear+14) {
            return true;
        }
        return false;
    }
    return false;
    }
    //us09 changes ends @AS

    public void sprint2Output(PrintStream fileOut) {

        fileOut.println();
        System.out.println();
        fileOut.println("US16: List all Male Last Name");
        System.out.println("US16: List all Male Last Name");
        for (String str : successAnomalyDataUS16) {
            fileOut.println(str);
            System.out.println(str);
        }

    }

    public void sprint2ErrorOutput(PrintStream fileOut){
        //us23 changes starts @sr
        fileOut.println();
        System.out.println();
        for (String str : errorAnomalyData) {
            fileOut.println(str);
            System.out.println(str);
        }
        //us23 changes ends @sr

        if(!errorAnomalyDataUS16.isEmpty()) {
            fileOut.println();
            fileOut.println("ERROR IN US16 WHICH DON'T HAVE SAME FAMILY NAME IS LISTED BELOW ");
            System.out.println();
            System.out.println("ERROR IN US16 WHICH DON'T HAVE SAME FAMILY NAME IS LISTED BELOW ");
            for (String str : errorAnomalyDataUS16) {
                fileOut.println(str);
                System.out.println(str);
            }
        }
    }

}
