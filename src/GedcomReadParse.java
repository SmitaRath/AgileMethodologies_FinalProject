import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.ArrayList;
import java.util.Date;

public class GedcomReadParse {
    //defining zero level tags
       /* static final String[] zeroLevelTags = {"HEAD", "TRLR", "NOTE"};

        //defining exception tags
        static final String[] exceptionTags = {"INDI","FAM"};
        //defining one level tags
        static final String[] oneLevelTags = {"NAME", "SEX", "BIRT", "DEAT",
                "FAMC", "FAMS", "MARR", "HUSB", "WIFE", "CHIL",
                "DIV"};

        //defining two level tags
        static final String[] twoLevelTags = { "DATE"};

        //defining all the tags
        static final String[] allTags = { "INDI","FAM","HEAD", "TRLR", "NOTE","NAME", "SEX", "BIRT", "DEAT",
                "FAMC", "FAMS", "MARR", "HUSB", "WIFE", "CHIL",
                "DIV","DATE"};*/
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

    // method to read GEDCOM FILE
    public void readGEDCOMFILE() {
        BufferedReader reader;
        String[] splitString;
        GedcomEntry e1;
        int counter = 0;
        Individual ind = new Individual();
        try {
            //OPENING A FILE
            reader = new BufferedReader(new FileReader(
                    "Group01-AgileMethods.GED"));
            //READING FIRST LINE
            String line = reader.readLine();
            //VERIFYING IF LINE IS NOT NULL AND DOESNOT CONTAIN only SPACEs
            while (line != null && line.trim().length() > 0) {

                //spliting the line into array
                splitString = line.split(" ");
                if (splitString.length>2 && splitString[2].equals("INDI") && splitString[0].equals("0")) {
                    if (!(ind.id==null)) {
                        individuals.add(ind);
                        if(individuals.size()>=1000)
                            break;
                    }
                    ind = new Individual();
                    ind.id = splitString[1];
                    line = reader.readLine();
                    splitString=line.split(" ");
                    if (splitString[1].equals("NAME") && splitString[0].equals("1"))
                        ind.name = line.substring(line.indexOf(" ", line.indexOf(" ") + 1) + 1, line.length());


                }
                if(!(ind.id==null)) {
                    if (splitString[1].equals("SEX") && splitString[0].equals("1")) {
                        ind.gender = splitString[2];
                    }

                    else if (splitString[1].equals("BIRT") && splitString[0].equals("1")) {
                        line = reader.readLine();
                        splitString = line.split(" ");
                        if (splitString[1].equals("DATE") && splitString[0].equals("2")) {
                            ind.dateOfBirth = line.substring(line.indexOf(" ", line.indexOf(" ") + 1) + 1, line.length());
                            ind.dobDate = formatter.parse(ind.dateOfBirth);
                            Instant instant = ind.dobDate.toInstant();
                            ZonedDateTime zone = instant.atZone(ZoneId.systemDefault());
                            LocalDate givenDate = zone.toLocalDate();
                            //   month = givenDate.getMonthValue()?
                            // ind.dateOfBirth  = givenDate.getYear() + "-" + givenDate.getMonthValue() + "-" + givenDate.getDayOfMonth();
                            Period period = Period.between(givenDate, LocalDate.now());
                            ind.age = period.getYears();
                        }
                    }

                    else if (splitString[1].equals("DEAT") && splitString[0].equals("1")) {
                        if (splitString[2].equals("Y"))
                            ind.alive = false;
                        else
                            ind.alive = true;
                        line = reader.readLine();
                        splitString = line.split(" ");
                        if (splitString[1].equals("DATE") && splitString[0].equals("2")) {
                            {
                                ind.death = line.substring(line.indexOf(" ", line.indexOf(" ") + 1) + 1, line.length());
                                ind.deathDate = formatter.parse(ind.death);
                            }
                        }
                    }

                    else if (splitString[1].equals("FAMC") && splitString[0].equals("1")) {
                        ind.child = "{" + splitString[2] + "}";
                    }

                    if (splitString[1].equals("FAMS") && splitString[0].equals("1")) {
                        ind.spouse = "{" + splitString[2] + "}";
                    }
                }


                line = reader.readLine();
            }
            for(Individual i : individuals){
                System.out.println(i.toString());
            }
            //file closed
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
