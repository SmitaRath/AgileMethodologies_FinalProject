package main;

import java.io.PrintStream;
import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;


public class Sprint4 {
    ArrayList<String> errorAnamolyUS25 = new ArrayList<>();
    ArrayList<String> successDataUS34 = new ArrayList<>();
    ArrayList<String> successAnomalyDataUS39 = new ArrayList<>();
    ArrayList<String> sprint4ErrorAnamolyData = new ArrayList<>();
    String message = "";

    // us-39 changes starts @KP
    public long calculateDays(Date dob) {
        Instant instant = dob.toInstant();
        ZonedDateTime zone = instant.atZone(ZoneId.systemDefault());
        LocalDate givenDate = zone.toLocalDate();
        Period period = Period.between(LocalDate.now(), givenDate);
        Date present = new Date();
        if (period.getYears() > 0)
            return 100;
        else {
            int monthDiff = dob.getMonth() - present.getMonth();
            if (monthDiff == 0 || monthDiff == 1) {
                System.out.println("dob.getDAy" + dob.getDay() + " present.getDAy" + present.getDay());
                return dob.getDay() - present.getDay();
            }
        }
        return 100;
    }
    // us-39 changes ends @KP

    // us-12 changes starts @KP
    // calculates months between two dates
    public int yearDiffBetweenTwoDate(Date dob, Date parents) {
        if(dob != null && parents != null) {
            Instant instantDob = dob.toInstant();
            Instant instantParentDate = parents.toInstant();
            ZonedDateTime zoneDob = instantDob.atZone(ZoneId.systemDefault());
            ZonedDateTime zoneParentDate = instantParentDate.atZone(ZoneId.systemDefault());
            LocalDate givenDobDate = zoneDob.toLocalDate();
            LocalDate givenParentDate= zoneParentDate.toLocalDate();
            Period period = Period.between(givenParentDate, givenDobDate);
            if (period.getYears() > 0)
                return period.getYears();

            return -1;
        }
        return -1;
    }
    // us-12 changes ends @KP


    //us25 changes starts @sr
    public Individual getIndividualData(String id, ArrayList<Individual> individuals) {
        for (Individual ind : individuals) {
            if (ind.id.equals(id))
                return ind;
        }
        return null;
    }

    public void us25_uniqueFirstNamesInFamily(ArrayList<Family> families, ArrayList<Individual> individuals){
        String errString="";
        int duplicateNameCount=0;
        int duplicateDOBCount=0;
        Individual outerLoopChild = new Individual();
        Individual innerLoopChild = new Individual();
        Individual ind = new Individual();
        String fullnameOuter="";
        String fullnameInner="";
        String[] name;
        HashSet<String> duplicateNamesList = new HashSet<>();
        HashSet<String> duplicateDOBList = new HashSet<>();
        for(Family fam:families) {
            errString="";
            for (int i = 0; i < fam.child.size(); i++) {
                outerLoopChild = getIndividualData(fam.child.get(i), individuals);
                fullnameOuter = "";
                name = outerLoopChild.name.split(" ");
                for (int index = 0; index < name.length; index++) {
                    name[index] = name[index].toUpperCase().trim();
                    name[index] = name[index].replaceAll("/", "");
                    fullnameOuter = fullnameOuter + name[index];
                }
                for (int j = i + 1; j < fam.child.size(); j++) {
                    innerLoopChild = getIndividualData(fam.child.get(j), individuals);
                    fullnameInner = "";
                    name = innerLoopChild.name.split(" ");
                    for (int innerindex = 0; innerindex < name.length; innerindex++) {
                        name[innerindex] = name[innerindex].toUpperCase().trim();
                        name[innerindex] = name[innerindex].replaceAll("/", "");
                        fullnameInner = fullnameInner + name[innerindex];
                    }
                    if (fullnameOuter.equals(fullnameInner) && !fullnameOuter.equals("NA")) {
                        if (duplicateNamesList.add(innerLoopChild.id)) {
                            duplicateNameCount = duplicateNameCount + 1;
                            duplicateNamesList.add(outerLoopChild.id);
                        }
                    }
                    if (outerLoopChild.dateOfBirth.equals(innerLoopChild.dateOfBirth)) {
                        if (duplicateDOBList.add(innerLoopChild.id)) {
                            duplicateDOBCount = duplicateDOBCount + 1;
                            duplicateDOBList.add(outerLoopChild.id);
                        }

                    }
                }
            }
            if (duplicateNameCount >= 2) {
                errString = "Error: In US25 for Family "
                        + "; Id :" + fam.id
                        + "; Name is not unique ";
                errorAnamolyUS25.add(errString);
                for (String str : duplicateNamesList) {
                    ind = getIndividualData(str, individuals);
                    errString = "Child ID :" + str
                            + "; Line no: " + ind.nameLineNo
                            + "; Name :" + ind.name
                            + "; Name is not unique ";
                    errorAnamolyUS25.add(errString);
                }
            }
            if (duplicateDOBCount >= 2) {
                errString = "Error: In US25 for Family "
                        + "; Id :" + fam.id
                        + "; Date Of Birth is not unique ";
                errorAnamolyUS25.add(errString);
                for (String str : duplicateDOBList) {
                    ind = getIndividualData(str, individuals);
                    errString = "Child ID :" + str
                            + "; Line no: " + ind.dobLineNo
                            + "; Date of Birth :" + ind.dateOfBirth
                            + "; Date Of Birth is not unique ";
                    errorAnamolyUS25.add(errString);
                }
            }

            duplicateDOBCount=0;
            duplicateNameCount=0;
            duplicateDOBList.clear();
            duplicateNamesList.clear();
        }
    }
    //us25 changes ends @sr

    //us34 changes starts @sr
    public void us34_largeAgeDifference(ArrayList<Family> families, ArrayList<Individual> individuals){
        GedcomReadParse g1 = new GedcomReadParse();
        Individual husband = new Individual();
        Individual wife = new Individual();
        int husbandAge=0;
        int wifeAge=0;
        String str;
        for(Family fam:families){
            str="";
            husband=getIndividualData(fam.husbandId,individuals);
            if(husband.dobDate!=null)
            {
                husbandAge=g1.calculateAge(husband.dobDate);
                if(husbandAge<0)
                    husbandAge=0;
            }
            wife=getIndividualData(fam.wifeId,individuals);
            if(wife.dobDate!=null)
            {
                wifeAge=g1.calculateAge(wife.dobDate);
                if(wifeAge<0)
                    wifeAge=0;
            }
            if(husband.age>=wife.age*2){
                    str ="Family Id: " + fam.id +
                        "; Husband ID: " + husband.id +
                        "; Husband's Age: " + husbandAge +
                        "; Wife ID: " + wife.id +
                        "; Wife's Age: " + wifeAge;
                    successDataUS34.add(str);
            }
        }
    }
    //us34 changes ends @sr

    //us39 changes starts @kp
    public void US39_listAllLivingUpcomingAnniversary(Family family, String husbandName, String wifeName) {
        long noDays = calculateDays(family.marrriedDate);
        if (noDays < 29 && noDays >= 0) {
            husbandName = husbandName.replaceAll("/", "");
            wifeName = wifeName.replaceAll("/", "");
            message = "ID: " + family.id + " Husband Name: " + husbandName + " Wife Name: " + wifeName +" Married Date: " + family.dateOfMarried +" Upcoming Anniversay in under 30 days";
            successAnomalyDataUS39.add(message);
        }
    }
    //us39 changes ends @kp

    //us12 changes starts @kp
    public void US12_parentsNotTooOld(Family family, ArrayList<Individual> individuals) {
        Individual motherData, fatherData;
        motherData = getIndividualData(family.wifeId, individuals);
        fatherData = getIndividualData(family.husbandId, individuals);
        if(motherData != null && fatherData != null) {
            for (String child : family.child) {
                Individual individualData = null;
                individualData = getIndividualData(child, individuals);
                if (individualData != null) {
                    String motherName = motherData.name.replaceAll("/", "");
                    String fatherName = fatherData.name.replaceAll("/", "");
                    if (yearDiffBetweenTwoDate(individualData.dobDate, motherData.dobDate) >= 60) {
                        message = "Error: In US09 for INDIVIDUAL at Line no: " + motherData.dobLineNo + "; ID: " + motherData.id + "; Name: " + motherName + "; Birth date: " + motherData.dateOfBirth + "; Mother should be less than 60 years older than children for" + "; Child ID: " + individualData.id + "; Child birthdate: " + individualData.dateOfBirth;
                        sprint4ErrorAnamolyData.add(message);
                    }
                    if (yearDiffBetweenTwoDate(individualData.dobDate, fatherData.dobDate) >= 80) {
                        message = "Error: In US09 for INDIVIDUAL at Line no: " + fatherData.dobLineNo + "; ID: " + fatherData.id + "; Name: " + fatherName + "; Birth date: " + fatherData.dateOfBirth + "; Father should be less than 80 years older than children for" + "; Child ID: " + individualData.id + "; Child birthdate: " + individualData.dateOfBirth;
                        sprint4ErrorAnamolyData.add(message);
                    }
                }
            }
        }
    }
    //us12 changes ends @kp

    public void printErrorSuccess(PrintStream fileOut){

        if(!successDataUS34.isEmpty()){
            fileOut.println("US34 List large Age Difference");
            System.out.println("US34 List large Age Difference");
            for(String str :successDataUS34){
                fileOut.println(str);
                System.out.println(str);
            }
        }

        fileOut.println();
        System.out.println();

        if(!successAnomalyDataUS39.isEmpty()){
            fileOut.println("US39 List upcoming Anniversary in the next 30 days");
            System.out.println("US39 List upcoming Anniversary in the next 30 days");
            for(String str :successAnomalyDataUS39){
                fileOut.println(str);
                System.out.println(str);
            }
        }

        fileOut.println();
        System.out.println();

        for(String str :errorAnamolyUS25){
            fileOut.println(str);
            System.out.println(str);
        }

        for(String str :sprint4ErrorAnamolyData){
            fileOut.println(str);
            System.out.println(str);
        }



    }
}