package main;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;


public class Sprint4 {
    ArrayList<String> errorAnamolyUS25 = new ArrayList<>();
    ArrayList<String> successDataUS34 = new ArrayList<>();

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
        Individual husband = new Individual();
        Individual wife = new Individual();
        String str;
        for(Family fam:families){
            str="";
            husband=getIndividualData(fam.husbandId,individuals);
             wife=getIndividualData(fam.wifeId,individuals);
            if(husband.age>=wife.age*2){
                    str ="Family Id: " + fam.id +
                        "; Husband ID: " + husband.id +
                        "; Husband's Age: " + husband.age +
                        "; Wife ID: " + wife.id +
                        "; Wife's Age: " + wife.age;
                    successDataUS34.add(str);
            }
        }
    }
    //us34 changes ends @sr

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

        for(String str :errorAnamolyUS25){
            fileOut.println(str);
            System.out.println(str);
        }


    }
}
