package main;

import java.util.Comparator;
import java.util.Date;

public class Individual {
    public String id = null;
    int idLineNo;
    public String name;
    int nameLineNo;
    public String gender;
    int genderLineNo;
    public String dateOfBirth;
    int dobLineNo;
    public Date dobDate;
    public int age;
    public boolean alive = true;
    public String death;
    public int deathLineNo;
    public Date deathDate;
    public String child;
    int childLineNo;
    public String spouse;
    int spouseLineNo;

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
        return "main.Individual{" +
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

    static int  extractInt(String s) {
        String num = s.replaceAll("\\D", "");
        // return 0 if no digits found
        return num.isEmpty() ? 0 : Integer.parseInt(num);
    }


    /*Comparator for sorting the list by ID*/
    public static Comparator<Individual> IDComparator = new Comparator<Individual>() {

        /*  public int compare(Individual s1, Individual s2) {
              String Id1 = s1.id.toUpperCase();
              String Id2 = s2.id.toUpperCase();
              //ascending order
              return Id1.compareTo(Id2);
          }};*/
        public int compare(Individual s1, Individual s2) {
            String Id1 = s1.id.toUpperCase();
            String Id2 = s2.id.toUpperCase();

            return extractInt(Id1) - extractInt(Id2);
        }
    };
}