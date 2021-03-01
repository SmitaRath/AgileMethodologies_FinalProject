import java.util.ArrayList;

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

    @Override
    public String toString() {
        for(String i : child){
            System.out.print(i);
        }
//        return "Individual{" +
//                "id='" + id + '\'' +
//                ", name='" + husbandId + '\'' +
//                '}';
        return "";
    }
}
