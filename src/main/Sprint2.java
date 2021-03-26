package main;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

public class Sprint2 {
   // PrintStream fileOut;
    ArrayList<String> successAnomalyDataUS16 = new ArrayList<>();

//    public Sprint2() {
//        try {
//   //         fileOut = new PrintStream("./out.txt");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }

    public  void US16(Individual i) {
        if (i.name != null) {
            String successMessage = "", lastName = "NA";
            String[] formatName;
            formatName = i.name.split("/");
            if(formatName[1] != null)
                lastName = formatName[1];

            successMessage = "ID: " + i.id + " GENDER: " + i.gender + " Last NAME: " + lastName;
            successAnomalyDataUS16.add(successMessage);

        }
    }

    public void sprint2Output(PrintStream fileOut) {

        fileOut.println();
        System.out.println();
        fileOut.println("US35: List all Male Last Name");
        System.out.println("US35: List all Male Last Name");
        for (String str : successAnomalyDataUS16) {
            fileOut.println(str);
            System.out.println(str);
        }
    }
}
