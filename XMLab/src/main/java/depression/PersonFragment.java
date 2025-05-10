package depression;

import java.util.List;

public class PersonFragment {
    public String id;
    public String firstName;  // fullname: first, firstname, name (both names)
    public String lastName; // fullname: family, surname, family-name, name (both names)
    public Boolean isMale;
    public PersonFragment spouce;
    public List<PersonFragment> parents;
    public List<PersonFragment> children;
    public List<PersonFragment> siblings;
    public Integer numberOfChildren;
    public Integer numberOfSiblings;

    public PersonFragment(String personId) {
        this.id = personId;
    }

    public PersonFragment(String firstName, String lastname) {
        this.firstName = firstName;
        this.lastName = lastname;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PersonFragment{");
        if (id != null) {
            sb.append("id=").append(id).append("; ");
        }
        if (firstName != null) {
            sb.append("firstName=").append(firstName).append("; ");
        }
        if (lastName != null) {
            sb.append("lastName=").append(firstName).append("; ");
        }
        if (isMale != null) {
            sb.append("isMale=").append(isMale).append("; ");
        }
        if (spouce != null) {
            sb.append("spouce=").append(spouce.id).append("; ");
        }
        if (numberOfChildren != null) {
            sb.append("numberOfChildren=").append(numberOfChildren).append("; ");
        }
        if (numberOfSiblings != null) {
            sb.append("numberOfSiblings=").append(numberOfSiblings).append("; ");
        }
        if (hasSpouce != null) {
            sb.append("hasSpouce=").append(hasSpouce).append("; ");
        }
        sb.append("}");
        return sb.toString();
    }
}
