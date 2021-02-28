import java.time.LocalDate;
import java.util.Date;

public class Individual {
    String id=null;
    String name;
    String gender;
    String dateOfBirth;
    Date dobDate;
    int age;
    boolean alive=true;
    String death;
    Date deathDate;
    String child;
    String spouse;

    public Individual() {
        this.id = null;
        this.name = "NA";
        this.gender = "NA";
        this.dateOfBirth = "NA";
        this.alive = true;
        this.death = "NA";
        this.child = "NA";
        this.spouse = "NA";
    }

    @Override
    public String toString() {
        return "Individual{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", dobDate=" + dobDate +
                ", age=" + age +
                ", alive=" + alive +
                ", death='" + death + '\'' +
                ", deathDate=" + deathDate +
                ", child='" + child + '\'' +
                ", spouse='" + spouse + '\'' +
                '}';
    }
}
