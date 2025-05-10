package depression;

import java.util.List;

class PersonFragment {
    protected String id;
    protected String firstName;  // fullname: first, firstname, name (both names)
    protected String lastName; // fullname: family, surname, name (both names)
    protected Boolean isMale;
    protected PersonFragment spouse;
    protected List<PersonFragment> parents;
    protected List<PersonFragment> children;
    protected List<PersonFragment> siblings;
    protected Integer numberOfChildren;
    protected Integer numberOfSiblings;
}
