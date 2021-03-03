import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class Family{
    String id;
    String husbandId;
    String husbandName;
    String wifeId;
    String wifeName;
    String dateOfMarried;
    Date marrriedDate;
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
