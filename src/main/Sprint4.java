package main;

import java.io.PrintStream;
import java.time.*;
import java.util.*;


public class Sprint4 {
    ArrayList<String> errorAnamolyUS25 = new ArrayList<>();
    ArrayList<String> successDataUS34 = new ArrayList<>();
    ArrayList<String> successAnomalyDataUS39 = new ArrayList<>();
    ArrayList<String> sprint4ErrorAnomalyData = new ArrayList<>();
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
                        message = "Error: In US12 for INDIVIDUAL at Line no: " + motherData.dobLineNo + "; ID: " + motherData.id + "; Name: " + motherName + "; Birth date: " + motherData.dateOfBirth + "; Mother should be less than 60 years older than children for" + "; Child ID: " + individualData.id + "; Child birthdate: " + individualData.dateOfBirth;
                        sprint4ErrorAnomalyData.add(message);
                    }
                    if (yearDiffBetweenTwoDate(individualData.dobDate, fatherData.dobDate) >= 80) {
                        message = "Error: In US12 for INDIVIDUAL at Line no: " + fatherData.dobLineNo + "; ID: " + fatherData.id + "; Name: " + fatherName + "; Birth date: " + fatherData.dateOfBirth + "; Father should be less than 80 years older than children for" + "; Child ID: " + individualData.id + "; Child birthdate: " + individualData.dateOfBirth;
                        sprint4ErrorAnomalyData.add(message);
                    }
                }
            }
        }
    }
    //us12 changes ends @kp

    //us18 changes starts @pp
    public boolean isSibling(ArrayList<Family>families,String s1, String s2){
        String husb="";
        String wife="";
        int f=0;
        for(int i=0;i<families.size();i++){
            for(int j=0;j<families.get(i).child.size();j++){
                if(s1.equals(families.get(i).child.get(j))){
                    husb = families.get(i).husbandId;
                    wife = families.get(i).wifeId;
                    f=1;
                    break;
                }
            }
            if(f==1)
                break;
        }

        for(int i=0;i<families.size();i++){
            if(families.get(i).husbandId.equals(husb)||families.get(i).wifeId.equals(wife)){
                for(int j=0;j<families.get(i).child.size();j++){
                    if(s2.equals(families.get(i).child.get(j))){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    //us18 changes ends @pp

    //us18 changes starts @pp
    public void us18_siblingsShouldNotMarry(ArrayList<Family>families,ArrayList<Individual>individuals){
        HashMap<String,Integer>child = new HashMap<String,Integer>();
        for(int i=0;i<individuals.size();i++){
            child.put(individuals.get(i).id,0);
        }
        for(int i=0;i<families.size();i++){
            for(int j=0;j<families.get(i).child.size();j++){
                child.put(families.get(i).child.get(j),1);
            }
        }
        for(int i=0;i<families.size();i++){
            if(child.get(families.get(i).husbandId)==1&&child.get(families.get(i).wifeId)==1&&isSibling(families,families.get(i).husbandId,families.get(i).wifeId)){
                message = "Error: In US18 for Family - "+ families.get(i).id+" at LineNo: " + families.get(i).husbandidLineNo+" and "+families.get(i).wifeidLineNo
                +" Husband - "+families.get(i).husbandId + " and Wife - "+families.get(i).wifeId
                +" should not be siblings";
                sprint4ErrorAnomalyData.add(message);
            }
        }
    }
    //us18 changes ends @pp

    //us17 changes starts @pp
    public boolean us17_ParentsShouldNotMarryDescendants(ArrayList<Family>families){
        HashMap<String,Integer>flag = new HashMap<String,Integer>();
        int f1=0,f2=0;
        for(int i=0;i<families.size();i++){
            flag.put(families.get(i).husbandId,1);
            flag.put(families.get(i).wifeId,1);
        }
        for(int i=0;i<families.size();i++){
            if(flag.get(families.get(i).husbandId)==1){
                ArrayList<String>spouse = new ArrayList<>();
                for(int j=0;j<families.size();j++){
                    if(families.get(j).husbandId.equals(families.get(i).husbandId))
                        spouse.add(families.get(j).wifeId);
                }
                Queue<String> queue = new LinkedList<>();
                for(int j=0;j<families.size();j++){
                    if(families.get(j).husbandId.equals(families.get(i).husbandId)){
                        if(families.get(j).child.size()>0){
                            for(int k=0;k<families.get(j).child.size();k++){
                                if(flag.containsKey(families.get(j).child.get(k))){
                                    queue.add(families.get(j).child.get(k));
                                }
                            }
                        }
                    }
                }
                while(!queue.isEmpty()){
                    String s1 = queue.remove();
                    for(int j=0;j<spouse.size();j++){
                        if(spouse.get(j).equals(s1)){
                            f1=1;
                            message = "Error: In US17 for Family - "+ families.get(i).id+" at LineNo: " + families.get(i).husbandidLineNo+" and "+families.get(i).wifeidLineNo
                                    +" Wife - "+s1 + " is descendant of Husband - "+families.get(i).husbandId
                                    +" but parents should not marry their descendants";
                            sprint4ErrorAnomalyData.add(message);
                        }
                    }
                    for(int j=0;j<families.size();j++){
                        if(families.get(j).wifeId.equals(s1)){
                            if(families.get(j).child.size()>0){
                                for(int k=0;k<families.get(j).child.size();k++){
                                    if(flag.containsKey(families.get(j).child.get(k))){
                                        queue.add(families.get(j).child.get(k));
                                    }
                                }
                            }
                        }
                    }
                }
                flag.put(families.get(i).husbandId,2);
            }
        }
        for(int i=0;i<families.size();i++){
            flag.put(families.get(i).husbandId,1);
            flag.put(families.get(i).wifeId,1);
        }
        for(int i=0;i<families.size();i++){
            if(flag.get(families.get(i).wifeId)==1){
                ArrayList<String>spouse = new ArrayList<>();
                for(int j=0;j<families.size();j++){
                    if(families.get(j).wifeId.equals(families.get(i).wifeId))
                        spouse.add(families.get(j).husbandId);
                }
                Queue<String> queue = new LinkedList<>();
                for(int j=0;j<families.size();j++){
                    if(families.get(j).wifeId.equals(families.get(i).wifeId)){
                        if(families.get(j).child.size()>0){
                            for(int k=0;k<families.get(j).child.size();k++){
                                if(flag.containsKey(families.get(j).child.get(k))){
                                    queue.add(families.get(j).child.get(k));
                                }
                            }
                        }
                    }
                }
                while(!queue.isEmpty()){
                    String s1 = queue.remove();
                    for(int j=0;j<spouse.size();j++){
                        if(spouse.get(j).equals(s1)){
                            f2=1;
                            message = "Error: In US17 for Family - "+ families.get(i).id+" at LineNo: " + families.get(i).husbandidLineNo+" and "+families.get(i).wifeidLineNo
                                    +" Husband - "+s1 + " is descendant of Wife - "+families.get(i).wifeId
                                    +" but parents should not marry their descendants";
                            sprint4ErrorAnomalyData.add(message);
                        }
                    }
                    for(int j=0;j<families.size();j++){
                        if(families.get(j).husbandId.equals(s1)){
                            if(families.get(j).child.size()>0){
                                for(int k=0;k<families.get(j).child.size();k++){
                                    if(flag.containsKey(families.get(j).child.get(k))){
                                        queue.add(families.get(j).child.get(k));
                                    }
                                }
                            }
                        }
                    }
                }
                flag.put(families.get(i).wifeId,2);
            }
        }
        if(f1==1||f2==1){
            return true;
        }
        return false;
    }
    //us17 changes ends @pp

    //us11 changes starts @as
    public void us11_NoBigamy(ArrayList<Family>families){
        for(int i=0;i<families.size();i++){
            for(int j=0;j<families.size();j++){
                if(j!=i&&families.get(i).husbandId.equals(families.get(j).husbandId)){
                    if(families.get(i).marrriedDate!=null&&families.get(j).marrriedDate!=null&&families.get(j).dividedDate!=null&&families.get(i).dividedDate!=null){
                        if(families.get(i).dividedDate.before(families.get(j).marrriedDate)||families.get(i).marrriedDate.after(families.get(j).dividedDate)){

                        }
                        else{
                            message = "Error: In US11 for Family - "+ families.get(i).id + " at LineNo: " + families.get(i).dateOfMarriedidLineNo+" and "+families.get(j).dateOfMarriedidLineNo
                                    +" Husband - "+families.get(i).husbandId + " is married to more than one individual at a same time but there should not be bigamy.";
                            sprint4ErrorAnomalyData.add(message);
                        }
                    }
                    if(families.get(i).marrriedDate!=null&&families.get(j).marrriedDate!=null&&families.get(j).dividedDate!=null&&families.get(i).dividedDate==null){
                        if(families.get(i).marrriedDate.after(families.get(j).dividedDate)){

                        }
                        else{
                            message = "Error: In US11 for Family - "+ families.get(i).id + " at LineNo: " + families.get(i).dateOfMarriedidLineNo+" and "+families.get(j).dateOfMarriedidLineNo
                                    +" Husband - "+families.get(i).husbandId + " is married to more than one individual at a same time but there should not be bigamy.";
                            sprint4ErrorAnomalyData.add(message);
                        }
                    }
                    if(families.get(i).marrriedDate!=null&&families.get(j).marrriedDate!=null&&families.get(j).dividedDate==null&&families.get(i).dividedDate==null){
                        message = "Error: In US11 for Family - "+ families.get(i).id + " at LineNo: " + families.get(i).dateOfMarriedidLineNo+" and "+families.get(j).dateOfMarriedidLineNo
                                +" Husband - "+families.get(i).husbandId + " is married to more than one individual at a same time but there should not be bigamy.";
                        sprint4ErrorAnomalyData.add(message);
                    }
                    if(families.get(i).marrriedDate!=null&&families.get(j).marrriedDate!=null&&families.get(j).dividedDate==null&&families.get(i).dividedDate!=null){
                        if(families.get(i).dividedDate.before(families.get(j).marrriedDate)){

                        }
                        else{
                            message = "Error: In US11 for Family - "+ families.get(i).id + " at LineNo: " + families.get(i).dateOfMarriedidLineNo+" and "+families.get(j).dateOfMarriedidLineNo
                                    +" Husband - "+families.get(i).husbandId + " is married to more than one individual at a same time but there should not be bigamy.";
                            sprint4ErrorAnomalyData.add(message);
                        }
                    }
                }
                if(j!=i&&families.get(i).wifeId.equals(families.get(j).wifeId)){
                    if(families.get(i).marrriedDate!=null&&families.get(j).marrriedDate!=null&&families.get(j).dividedDate!=null&&families.get(i).dividedDate!=null){
                        if(families.get(i).dividedDate.before(families.get(j).marrriedDate)||families.get(i).marrriedDate.after(families.get(j).dividedDate)){

                        }
                        else{
                            message = "Error: In US11 for Family - "+ families.get(i).id + " at LineNo: " + families.get(i).dateOfMarriedidLineNo+" and "+families.get(j).dateOfMarriedidLineNo
                                    +" Wife - "+families.get(i).wifeId + " is married to more than one individual at a same time but there should not be bigamy.";
                            sprint4ErrorAnomalyData.add(message);
                        }
                    }
                    if(families.get(i).marrriedDate!=null&&families.get(j).marrriedDate!=null&&families.get(j).dividedDate!=null&&families.get(i).dividedDate==null){
                        if(families.get(i).marrriedDate.after(families.get(j).dividedDate)){

                        }
                        else{
                            message = "Error: In US11 for Family - "+ families.get(i).id + " at LineNo: " + families.get(i).dateOfMarriedidLineNo+" and "+families.get(j).dateOfMarriedidLineNo
                                    +" Wife - "+families.get(i).wifeId + " is married to more than one individual at a same time but there should not be bigamy.";
                            sprint4ErrorAnomalyData.add(message);
                        }
                    }
                    if(families.get(i).marrriedDate!=null&&families.get(j).marrriedDate!=null&&families.get(j).dividedDate==null&&families.get(i).dividedDate==null){
                        message = "Error: In US11 for Family - "+ families.get(i).id + " at LineNo: " + families.get(i).dateOfMarriedidLineNo+" and "+families.get(j).dateOfMarriedidLineNo
                                +" Wife - "+families.get(i).wifeId + " is married to more than one individual at a same time but there should not be bigamy.";
                        sprint4ErrorAnomalyData.add(message);
                    }
                    if(families.get(i).marrriedDate!=null&&families.get(j).marrriedDate!=null&&families.get(j).dividedDate==null&&families.get(i).dividedDate!=null){
                        if(families.get(i).dividedDate.before(families.get(j).marrriedDate)){

                        }
                        else{
                            message = "Error: In US11 for Family - "+ families.get(i).id + " at LineNo: " + families.get(i).dateOfMarriedidLineNo+" and "+families.get(j).dateOfMarriedidLineNo
                                    +" Wife - "+families.get(i).wifeId + " is married to more than one individual at a same time but there should not be bigamy.";
                            sprint4ErrorAnomalyData.add(message);
                        }
                    }
                }
            }
        }
    }
    //us11 changes ends @as

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

        for(String str :sprint4ErrorAnomalyData){
            fileOut.println(str);
            System.out.println(str);
        }

    }
}
