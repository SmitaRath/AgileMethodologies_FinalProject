package main;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class Family{
    String id;
    int idLineNo;
    String husbandId;
    int husbandidLineNo;
    String husbandName;
    String wifeId;
    int wifeidLineNo;
    String wifeName;
    String dateOfMarried;
    int dateOfMarriedidLineNo;
    Date marrriedDate;
    String dateOfDivided;
    int dateOfDividedLineNo;
    Date dividedDate;
    ArrayList<String> child;
    String divorced;

    public Family() {
        this.id = null;
        this.husbandId = null;
        this.wifeId = null;
        this.husbandName = "NA";
        this.wifeName = "NA";
        this.child = new ArrayList<>();
        this.divorced = "NA";
        this.dateOfMarried = "NA";
        this.dateOfDivided = "NA";
    }
    public String childData(){
        String str="{";
        for(int i=0;i<this.child.size()-1;i++){

            str=str+"'"+child.get(i)+"',";
        }
        str=str+ "'"+ child.get(this.child.size()-1)+"'}";
        return str;
    }

    /* Comparator for sorting the list by main.Family ID */
    public static Comparator<Family> familyIdComparator = new Comparator<Family>() {
        public int compare(Family s1, Family s2) {
            String Id1 = s1.id.toUpperCase();
            String Id2 = s2.id.toUpperCase();

            //ascending order
            return Id1.compareTo(Id2);
        }
    };

    public String printChildren() {
        String str = "NA";
        for(int i=0; i < this.child.size(); ++i) {
            if(i == 0)
                str = "{'" + this.child.get(i) + "'";
            else
                str += ", '" + this.child.get(i) + "'";

            if(i == this.child.size()-1)
                str += "}";
        }
        return str;
    }

}
