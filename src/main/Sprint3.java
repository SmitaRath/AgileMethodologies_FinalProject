package main;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Sprint3 {
    ArrayList<String> errorAnomalyData = new ArrayList<>();
    String message = "";
    GedcomReadParse gedcomReadParse = new GedcomReadParse();

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
        if (period.getYears() < 0)   // If more than year, then it's more than 9 months; sending random no greater than 9
            return 100;

        if (period.getYears() == 0) {
            if (period.getMonths() >= 0) {
                return period.getMonths();
            }
        }

        return 0;
    }
    // us-08 changes ends @KP

     public Individual getIndividualData(String id, ArrayList<Individual> individuals) {
        for(Individual ind: individuals){
            if(ind.id.equals(id))
                return ind;
        }
        return null;
    }

    public void US09_birthBeforeDeathOfParents(Family family, ArrayList<Individual> individuals) {

        Individual childIndividualData = null, motherIndividualData = null, fatherIndividualData = null;


        if (family.wifeId != null) {
            motherIndividualData = getIndividualData(family.wifeId, individuals);

        }
        if (family.husbandId != null) {
            fatherIndividualData = getIndividualData(family.husbandId, individuals);
        }

        for(String child: family.child) {
            childIndividualData = getIndividualData(child, individuals);
            if (childIndividualData != null && childIndividualData.dobDate != null ) {
                if (motherIndividualData !=null && motherIndividualData.deathDate != null) {
                    if (motherIndividualData.deathDate.compareTo(childIndividualData.dobDate) <= 0) {
                        message = "Error: In US09 for INDIVIDUAL at Line no: " + childIndividualData.dobLineNo + "; ID: "
                                + childIndividualData.id + "; Individual Name: " + childIndividualData.name + "; Birth date: " + childIndividualData.dateOfBirth + " ; Mother ID: " + motherIndividualData.id + " ; Mother's death date: " + motherIndividualData.death +
                                "; Line no:" + motherIndividualData.deathLineNo + "; Children should be born before death of mother.";
                        errorAnomalyData.add(message);
                    }
                }
                if (fatherIndividualData != null && fatherIndividualData.deathDate != null) {
                     if (monthDiffBetweenTwoDate(fatherIndividualData.deathDate, childIndividualData.dobDate) >= 9) {
                        message = "Error: In US09 for INDIVIDUAL at Line no: " + childIndividualData.dobLineNo + "; ID: "
                                + childIndividualData.id + "; Individual Name: " + childIndividualData.name + "; Birth date: " + childIndividualData.dateOfBirth + " ; Father ID: " + fatherIndividualData.id + " ; Father's death date: " + fatherIndividualData.death +
                                "; Line no:" + fatherIndividualData.deathLineNo + "; Children should be born, not more than 9 months after father's death";
                        errorAnomalyData.add(message);
                    }
                }
            }
        }
    }

    public void sprint3ErrorOutput(PrintStream fileOut) {
        //us09 changes starts @kp
        fileOut.println();
        System.out.println();
        for (String str : errorAnomalyData) {
            fileOut.println(str);
            System.out.println(str);
        }
        //us09 changes ends @kp
    }
}
