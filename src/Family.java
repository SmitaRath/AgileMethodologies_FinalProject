import java.util.ArrayList;
import java.util.Comparator;
public class Family{
    String id;
    String name;
    String husbandId;
    String husbandName;
    String wifeId;
    String wifeName;
    ArrayList<String> child;
    String divorced;

    public Family() {
        this.id = null;
        this.husbandId = null;
        this.wifeId = null;
        this.name = "NA";
        this.husbandName = "NA";
        this.wifeName = "NA";
        this.child = new ArrayList<>();
        this.divorced = "NA";
    }

//    public void printChildId() {
//        for(int i=0; i< child.length ; ++i) {
//           // System.out.print();
//        }
//    }

    public String toString() {
        for(String i : child){
            System.out.print(i);
        }
        return this.id;
    }

    /* Comparator for sorting the list by Family ID */
    public static Comparator<Family> familyIdComparator = new Comparator<Family>() {

        public int compare(Family s1, Family s2) {
            String Id1 = s1.id.toUpperCase();
            String Id2 = s2.id.toUpperCase();

            //ascending order
            return Id1.compareTo(Id2);
        }};
}
