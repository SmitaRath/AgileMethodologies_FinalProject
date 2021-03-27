package main;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Sprint2 {
    private class familyLastName {
        private String familyId = "";
        private String lastName = "";

    }
   // PrintStream fileOut;
    ArrayList<String> successAnomalyDataUS16 = new ArrayList<>();
    ArrayList<String> errorAnomalyDataUS16 = new ArrayList<>();  // Created error seperate because here we need to display success and error list
    HashMap<String, String> familyLastName = new HashMap<String, String>();
    ArrayList<String> errorAnomalyData = new ArrayList<>();

//    public Sprint2() {
//        try {
//   //         fileOut = new PrintStream("./out.txt");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }

    public  void US16(Individual i) {
        if (i.name != null) {
            String message = "", lastName = "NA";
            String[] formatName;
            formatName = i.name.split("/");
            if(formatName[1] != null) {
                lastName = formatName[1];
            }

            if (!lastName.equals("NA")) {
                if (familyLastName.get(i.spouse) == null) {
                    familyLastName.put(i.spouse, lastName);
                    message = "ID: " + i.id + " GENDER: " + i.gender + " Last NAME: " + lastName;
                    successAnomalyDataUS16.add(message);
                } else {
                    if(familyLastName.get(i.spouse).toLowerCase().equals(lastName.toLowerCase())) {
                        message = "ID: " + i.id + " GENDER: " + i.gender + " Last NAME: " + lastName;
                        successAnomalyDataUS16.add(message);
                    } else {
                        message = "Error: In US16 for INDIVIDUAL at Line no: " + i.nameLineNo + "; ID: "
                                + i.id + " Individual Name: " + i.name + " Family ID: " + i.spouse + " Family Name: " + familyLastName.get(i.spouse) +
                                "; Family last name should be same for all males in family.";
                        errorAnomalyDataUS16.add(message);
                    }
                }
            } else {
                message = "Error: In US16 for INDIVIDUAL at Line no: " + i.nameLineNo + "; ID: "
                        + i.id + " Individual Name: " + i.name +
                        "; Family last name should be present for all males in family.";
                errorAnomalyDataUS16.add(message);
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
    public String getDeathDate(ArrayList<Individual>individuals,Family fam){
        for(Individual ind: individuals){
            if(ind.id.equals(fam.husbandId) || ind.id.equals(fam.wifeId)){
                return ind.death;
            }
        }
        return null;
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

    //us-23 changes method to check individual and date of birth is unique or not starts @sr
    public void checkUniqueDateOfBirthAndName(ArrayList<Individual> individuals){
        String errString="";
        Individual ind1;
        Individual ind2;
        boolean outerIndFlagNotUnique=false;
        for(int i=0;i<individuals.size();i++){
            ind1=individuals.get(i);
            for(int k=i+1;k<individuals.size();k++){
                ind2=individuals.get(k);
                if(ind1.name.equals(ind2.name)){
                    outerIndFlagNotUnique=true;
                    errString = "Error: In US23 for INDIVIDUAL at "
                            + " Line no: " + ind2.nameLineNo
                            + "; Name :" + ind2.name +";"
                            + " is not unique ";
                    errorAnomalyData.add(errString);
                }
                if(outerIndFlagNotUnique){
                    outerIndFlagNotUnique=false;
                    errString = "Error: In US23 for INDIVIDUAL at "
                            + " Line no: " + ind1.nameLineNo
                            + "; Name :" + ind1.name +";"
                            + " is not unique ";
                    errorAnomalyData.add(errString);
                }
            }
        }
    }
   //us-23 changes method to check individual and date of birth is unique or not ends @sr


    public void sprint2Output(PrintStream fileOut) {

        fileOut.println();
        System.out.println();
        fileOut.println("US16: List all Male Last Name");
        System.out.println("US16: List all Male Last Name");
        for (String str : successAnomalyDataUS16) {
            fileOut.println(str);
            System.out.println(str);
        }

        if(!errorAnomalyDataUS16.isEmpty()) {
            fileOut.println();
            fileOut.println("ERROR IN US16 WHICH DON'T HAVE SAME FAMILY NAME IS LISTED BELOW ");
            System.out.println();
            System.out.println("ERROR IN US16 WHICH DON'T HAVE SAME FAMILY NAME IS LISTED BELOW ");
            for (String str : errorAnomalyDataUS16) {
                fileOut.println(str);
                System.out.println(str);
            }

            //us23 changes starts @sr
            for (String str : errorAnomalyData) {
                fileOut.println(str);
                System.out.println(str);
            }
            //us23 changes ends @sr
        }
    }
}
