package main;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Sprint3 {
    ArrayList<String> successAnomalyDataUS38 = new ArrayList<>();
    ArrayList<String> errorAnomalyData = new ArrayList<>();
    String message = "";
    ArrayList<ListSiblings> listSiblings = new ArrayList<>();
    ArrayList<String> successAnomalyDataUS29 = new ArrayList<>();
    ArrayList<String> successAnomalyDataUS30 = new ArrayList<>();
    ArrayList<String> successAnomalyDataUS33 = new ArrayList<>();
    ArrayList<String> successAnomalyDataUS31 = new ArrayList<>();
    ArrayList<String> successAnomalyDataUS32 = new ArrayList<>();
    GedcomReadParse g = new GedcomReadParse();

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
        for (Individual ind : individuals) {
            if (ind.id.equals(id))
                return ind;
        }
        return null;
    }

    // us-39 changes starts @KP
    public long calculateDays(Date dob) {
        Instant instant = dob.toInstant();
        ZonedDateTime zone = instant.atZone(ZoneId.systemDefault());
        LocalDate givenDate = zone.toLocalDate();
        long p2 = ChronoUnit.DAYS.between(LocalDate.now(), givenDate);
        return p2;
    }
    // us-39 changes ends @KP

    public void US09_birthBeforeDeathOfParents(Family family, ArrayList<Individual> individuals) {

        Individual childIndividualData = null, motherIndividualData = null, fatherIndividualData = null;


        if (family.wifeId != null) {
            motherIndividualData = getIndividualData(family.wifeId, individuals);

        }
        if (family.husbandId != null) {
            fatherIndividualData = getIndividualData(family.husbandId, individuals);
        }

        for (String child : family.child) {
            childIndividualData = getIndividualData(child, individuals);
            if (childIndividualData != null && childIndividualData.dobDate != null) {
                if (motherIndividualData != null && motherIndividualData.deathDate != null) {
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

    public void US38_listAllLivingUpcomingBirthday(Individual individual) {
        if (individual.dobDate != null) {
            long noDays = calculateDays(individual.dobDate);
            if (noDays <= 30 && noDays >= 0) {
                String successMessage = "", name = "";
                String[] formatName;
                formatName = individual.name.split("/");
                name = formatName[0] + formatName[1];
                successMessage = "ID: " + individual.id + " NAME: " + name + " Date of Birth: " + individual.dateOfBirth + " Birthday in under 30 days";
                successAnomalyDataUS38.add(successMessage);
            }
        }
    }

    //US30 changes starts @pp
    public void us30_ListLivingMarriedIndividual(ArrayList<Family> families, ArrayList<Individual>individuals){
        HashMap<String,Integer>f = new HashMap<String, Integer>();
        HashMap<String,Integer>live = new HashMap<String, Integer>();
        HashMap<String,String>name = new HashMap<String, String>();

        for(int i=0;i<families.size();i++){
            f.put(families.get(i).husbandId,0);
            f.put(families.get(i).wifeId,0);
        }
        for(int i=0;i<individuals.size();i++){
            name.put(individuals.get(i).id,individuals.get(i).name);
            if(individuals.get(i).alive){
                live.put(individuals.get(i).id,1);
            }
            else{
                live.put(individuals.get(i).id,0);
            }
        }
        for(int i=0;i<families.size();i++){
            if(families.get(i).marrriedDate!=null&&families.get(i).dividedDate==null&&f.get(families.get(i).husbandId)==0&&live.get(families.get(i).husbandId)==1){
                f.put(families.get(i).husbandId,1);
                String[] formatName;
                String str="",message="";
                formatName = name.get(families.get(i).husbandId).split("/");
                str = str + formatName[0] + formatName[1];
                message = "ID: " + families.get(i).husbandId + " NAME: " + name.get(families.get(i).husbandId);
                successAnomalyDataUS30.add(message);
            }
            if(families.get(i).marrriedDate!=null&&families.get(i).dividedDate==null&&f.get(families.get(i).wifeId)==0&&live.get(families.get(i).wifeId)==1){
                f.put(families.get(i).wifeId,1);
                String[] formatName;
                String str="",message="";
                formatName = name.get(families.get(i).wifeId).split("/");
                str = str + formatName[0] + formatName[1];
                message = "ID: " + families.get(i).wifeId + " NAME: " + str;
                successAnomalyDataUS30.add(message);
            }
        }
    }
    //US30 changes ends @pp

    //US33 changes starts @pp
    public void us33_ListAllOrphanedChildrenBelow18(ArrayList<Family>families,ArrayList<Individual>individuals){
        HashMap<String,String>name = new HashMap<String, String>();
        HashMap<String,Integer>alive = new HashMap<String, Integer>();
        HashMap<String,Integer>age = new HashMap<String, Integer>();
        Date today = new Date();
        for(int i=0;i<individuals.size();i++){
            age.put(individuals.get(i).id,-1);
            if(individuals.get(i).deathDate!=null){
                alive.put(individuals.get(i).id,1);
            }
            else if(individuals.get(i).deathDate==null){
                alive.put(individuals.get(i).id,0);
            }
            if(individuals.get(i).dobDate!=null&&(individuals.get(i).dobDate.before(today)||individuals.get(i).dobDate.equals(today))&&individuals.get(i).age<18&&individuals.get(i).age>=0){
                age.put(individuals.get(i).id,1);
            }
            else if(individuals.get(i).dobDate!=null&&(individuals.get(i).dobDate.after(today)||(!individuals.get(i).dobDate.equals(today)))&&(individuals.get(i).age>=18||individuals.get(i).age<0)){
                age.put(individuals.get(i).id,0);
            }
            name.put(individuals.get(i).id,individuals.get(i).name);
        }
        for(int i = 0;i<families.size();i++){
            if(alive.get(families.get(i).husbandId)==1&&alive.get(families.get(i).wifeId)==1){
                if(families.get(i).child.size()>0) {
                    for (int j = 0; j < families.get(i).child.size(); j++) {
                        if (age.get(families.get(i).child.get(j))==1){
                            String[] formatName;
                            String str = "", message = "";
                            formatName = name.get(families.get(i).child.get(j)).split("/");
                            str = str + formatName[0] + formatName[1];
                            message = "ID: " + families.get(i).child.get(j) + " NAME: " + str;
                            successAnomalyDataUS33.add(message);
                        }
                    }
                }
            }
        }
    }
    //US33 changes ends @pp

    //US31 changes starts @AS
    public void us31_ListAllUnmarriedOver30(ArrayList<Family>families,ArrayList<Individual>individuals){
        HashMap<String,String>name = new HashMap<String, String>();
        HashMap<String,Integer>id = new HashMap<String, Integer>();
        for(int i=0; i<individuals.size();i++){
            id.put(individuals.get(i).id,0);
        }
        for(int i=0; i<families.size();i++){
            id.put(families.get(i).husbandId,1);
            id.put(families.get(i).wifeId,1);
        }
        for(int i=0;i<individuals.size();i++){
            if(id.get(individuals.get(i).id)==0&&individuals.get(i).age>30){
                String[] formatName;
                String str = "", message = "";
                formatName = individuals.get(i).name.split("/");
                str = str + formatName[0] + formatName[1];
                message = "ID: " + individuals.get(i).id + " NAME: " + str;
                successAnomalyDataUS31.add(message);
            }
        }
    }
    //US31 changes ends @AS

    //US32 changes starts @AS
    public void us32_ListMultipleBirths(ArrayList<Family>families,ArrayList<Individual>individuals){
        HashMap<String,String>birthdate = new HashMap<String, String>();
        HashMap<String,Integer>idflag = new HashMap<String, Integer>();
        int f=0;
        for(int i=0;i<individuals.size();i++){
            if(individuals.get(i).dobDate!=null)
                birthdate.put(individuals.get(i).id,individuals.get(i).dateOfBirth);
            idflag.put(individuals.get(i).id,0);
        }
        for(int i=0;i<families.size();i++){
            if(families.get(i).child.size()>1){
                for(int j=0;j<families.get(i).child.size();j++){
                    if(idflag.get(families.get(i).child.get(j))==0){
                    String message = "";
                    message = message + "ID: " + families.get(i).child.get(j);
                    for(int k=j+1;k<families.get(i).child.size();k++){
                        if(birthdate.get(families.get(i).child.get(j)).equals(birthdate.get(families.get(i).child.get(k)))){
                            message = message + "; ID: " + families.get(i).child.get(k);
                            f=1;
                            idflag.put(families.get(i).child.get(j),1);
                            idflag.put(families.get(i).child.get(k),1);
                        }
                    }
                    if(f==1){
                        successAnomalyDataUS32.add(message);
                    }
                    f=0;
                    }
                }
            }
        }
    }
    //US32 changes ends @AS


    public void sprint3SuccessOutput(PrintStream fileOut) {
        //us38 changes starts @kp
        if (!successAnomalyDataUS38.isEmpty()) {
            fileOut.println();
            System.out.println();
            fileOut.println("US39: List all Upcoming birthday which is in 30 days");
            System.out.println("US39: List all Upcoming birthday which is in 30 days");
            for (String str : successAnomalyDataUS38) {
                fileOut.println(str);
                System.out.println(str);
            }
        }
        //us38 changes ends @kp

        //us28 changes starts @sr
        fileOut.println();
        System.out.println();
        fileOut.println("US28 Order of siblings by age");
        System.out.println("US28 Order of siblings by age");
        for (ListSiblings lissib : listSiblings) {

            if (lissib.siblings.size() > 1) {
                fileOut.println("For Family ID :" + lissib.familyID);
                System.out.println("For Family ID :" + lissib.familyID);
                for(ChildData data:lissib.siblings){
                    fileOut.println("Child ID: "+ data.childID +
                            " Name: " + data.childName + " " +
                            "Age: " + data.age);
                    System.out.println("Child ID: "+ data.childID +
                            " Name: " + data.childName + " " +
                            "Age: " + data.age);
                }
                fileOut.println();
                System.out.println();

            }
        }
        //us28 changes ends @sr

        //us29 changes starts @sr
        fileOut.println();
        System.out.println();
        fileOut.println("US29 List of all deceased individuals");
        System.out.println("US29 List of all deceased individuals");
        for(String str:successAnomalyDataUS29){
            fileOut.println(str);
            System.out.println(str);
        }
        //us29 changes ends @sr

        //us30 changes starts @pp
        fileOut.println();
        System.out.println();
        fileOut.println("US30 List of all living married individuals");
        System.out.println("US30 List of all living married individuals");
        for(String str:successAnomalyDataUS30){
            fileOut.println(str);
            System.out.println(str);
        }
        //us30 changes ends @pp

        //us33 changes starts @pp
        fileOut.println();
        System.out.println();
        fileOut.println("US33 List of all Orphaned Children under 18 years");
        System.out.println("US33 List of all Orphaned Children under 18 years");
        for(String str:successAnomalyDataUS33){
            fileOut.println(str);
            System.out.println(str);
        }
        //us33 changes ends @pp

        //us31 changes starts @AS
        fileOut.println();
        System.out.println();
        fileOut.println("US31 List of all individuals who are never married and are above 30 years");
        System.out.println("US31 List of all individuals who are never married and are above 30 years");
        for(String str:successAnomalyDataUS31){
            fileOut.println(str);
            System.out.println(str);
        }
        //us31 changes ends @AS

        //us32 changes starts @AS
        fileOut.println();
        System.out.println();
        fileOut.println("US32 List of ID's of all individual having multiple births");
        System.out.println("US32 List of ID's of all individual having multiple births");
        for(String str:successAnomalyDataUS32){
            fileOut.println(str);
            System.out.println(str);
        }
        //us32 changes ends @AS

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

    //us28 changes starts @sr
    public Individual getIndividualChildData(String id, ArrayList<Individual> individuals, String familyID) {
        for (Individual ind : individuals) {
            if (ind.id.equals(id) && (ind.child.equals(familyID)))
                return ind;
        }
        return null;
    }

    public void us28_orderSiblingsByAge(ArrayList<Family> family, ArrayList<Individual> individuals) {
        ListSiblings listSib;
        Individual ind;
        Individual wife;
        Individual husband;
        ChildData childData;
        for (int i = 0; i < family.size(); i++) {
            listSib = new ListSiblings();
            listSib.familyID = family.get(i).id;
            for (String child : family.get(i).child) {
                childData = new ChildData();
                childData.childID=child;
                ind = getIndividualData(child,individuals);
                childData.childName=ind.name.replaceAll("/","");;
                childData.age=ind.age;
                listSib.siblings.add(childData);
            }
            for (int j = i + 1; j < family.size(); j++) {
                if (family.get(i).wifeId.equals(family.get(j).wifeId) ||
                        family.get(i).husbandId.equals(family.get(j).husbandId)) {
                    wife=getIndividualData(family.get(i).wifeId,individuals);
                    husband=getIndividualData(family.get(i).husbandId,individuals);
                    if(!family.get(i).id.equals(wife.spouse))
                        listSib.familyID=listSib.familyID + " " + wife.spouse;
                    if(!family.get(i).id.equals(husband.spouse))
                        listSib.familyID=listSib.familyID + " " + husband.spouse;
                    for (String child : family.get(j).child) {
                        childData = new ChildData();
                        ind =getIndividualChildData(child,individuals,wife.spouse);
                        if(ind==null)
                            ind =getIndividualChildData(child,individuals,husband.spouse);
                            childData.childID=ind.id;
                            childData.childName = ind.name.replaceAll("/", "");
                            childData.age = ind.age;
                            listSib.siblings.add(childData);
                    }
                }
            }
            Collections.sort(listSib.siblings,Collections.reverseOrder());
            listSiblings.add(listSib);
        }

    }

    class ChildData implements Comparable<ChildData>{
        String childID;
        String childName;
        int age;

        @Override
        public int compareTo(ChildData data) {

            return this.age > data.age ? 1 : this.age < data.age ? -1 : 0;
        }
    }
    class ListSiblings{
        String familyID;
        ArrayList<ChildData> siblings=new ArrayList<>();
    }
    //us28 changes ends @sr

    //us29 changes starts @sr
    public void us29_listDeceasedIndividual(ArrayList<Individual> individuals){
        for (Individual ind : individuals){
            message="";
            if(!ind.alive && !(ind.deathDate==null)){
                message =  "ID: " + ind.id +
                            " Name: " + ind.name.replaceAll("/","") +
                         " Date of Death: " + ind.death;

                successAnomalyDataUS29.add(message);
            }

        }
    }
    //us29 changes ends @sr

}