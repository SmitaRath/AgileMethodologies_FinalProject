import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class GedcomReadParse {
    //defining zero level tags
    static final String[] zeroLevelTags = {"HEAD", "TRLR", "NOTE"};

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
            "DIV","DATE"};


    //method to check the tag is valid or not
    static boolean findTag(String str,String[] tags){
        for(int i=0;i<tags.length;i++){
            if(str.equals(tags[i]))
                return true;
        }
        return false;
    }

    // method to read GEDCOM FILE
    public void readGEDCOMFILE() {
        BufferedReader reader;
        String[] splitString;
        GedcomEntry e1;
        try {
            //OPENING A FILE
            reader = new BufferedReader(new FileReader(
                    "Group01-AgileMethods.GED"));
            //READING FIRST LINE
            String line = reader.readLine();
            //VERIFYING IF LINE IS NOT NULL AND DOESNOT CONTAIN only SPACEs
            while (line != null) {
                //spliting the line into array
                splitString = line.split(" ");

                //looping through the array
                e1 = new GedcomEntry();
                for (int i = 0; i < splitString.length; i++) {
                    //switch statement for different levels of i
                    switch (i) {
                        //for level attribute
                        case 0: {
                            e1.level = splitString[i];
                            break;
                        }
                        //for tag attribute
                        case 1: {
                            //checking level based on that calling required function
                            switch (e1.level) {
                                case "0": {
                                    //checking for zero level tags
                                    if (findTag(splitString[i], zeroLevelTags)) {
                                        e1.validInvalidFlag = "Y";
                                        e1.tag = splitString[i];
                                    }
                                    //if not then checking for exception tags
                                    else if (splitString.length > 2 && findTag(splitString[splitString.length - 1], exceptionTags)) {
                                        e1.validInvalidFlag = "Y";
                                        e1.tag = splitString[splitString.length - 1];
                                        e1.arguments = splitString[1];
                                    }
                                }
                                break;

                                case "1": {
                                    //checking for one level tags
                                    if (findTag(splitString[i], oneLevelTags)) {
                                        e1.validInvalidFlag = "Y";
                                        e1.tag = splitString[i];
                                    }
                                }
                                break;

                                case "2": {
                                    //checking for two level tags
                                    if (findTag(splitString[i], twoLevelTags)) {
                                        e1.validInvalidFlag = "Y";
                                        e1.tag = splitString[i];
                                    }
                                }
                                break;
                            }
                            //if none of the condition is satisfied
                            if(e1.validInvalidFlag.equals("")) {
                                //checking if the tag is the last element and setting other fields
                                if (splitString.length > 2 && findTag(splitString[splitString.length - 1], allTags)) {
                                    e1.validInvalidFlag = "N";
                                    e1.tag = splitString[splitString.length - 1];
                                    e1.arguments = line.substring(line.indexOf(" ") + 1, line.indexOf(e1.tag));
                                } else {
                                    e1.validInvalidFlag = "N";
                                    e1.tag = splitString[i];
                                }
                            }
                            break;
                        }
                        //for other scenarios setting the argument attribute
                        default: {
                            if (e1.arguments.equals(""))
                                e1.arguments = line.substring(line.indexOf(" ", line.indexOf(" ") + 1) + 1, line.length());
                            break;
                        }

                    }

                }
                //printing the output to console.
                if(line.trim().length() > 0) {
                    System.out.println("-->" + line);
                    if (!e1.arguments.equals(""))
                        System.out.println("<--" + e1.level + "|" + e1.tag + "|" + e1.validInvalidFlag + "|" + e1.arguments);
                    else
                        System.out.println("<--" + e1.level + "|" + e1.tag + "|" + e1.validInvalidFlag);
                }
                //going to next line in the file
                line = reader.readLine();
            }
            //file closed
            reader.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }

    }
}










