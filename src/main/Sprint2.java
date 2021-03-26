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
        }
    }
}
