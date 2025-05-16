package depression;

import java.lang.reflect.Field;
import java.util.ArrayList;
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

    private void initListFields() {
        this.parents = new ArrayList<>();
        this.children = new ArrayList<>();
        this.siblings = new ArrayList<>();
    }

    public PersonFragment() {
        initListFields();
    }

    public PersonFragment(String personId) {
        this.id = personId;
        initListFields();
    }

    public PersonFragment(String firstName, String lastname) {
        this.firstName = firstName;
        this.lastName = lastname;
        initListFields();
    }

    /*
     * Метод, который проверяет - являются ли все публичные поля экземпляра null или пустыми коллекциями.
     */
    public boolean isEmpty() throws IllegalAccessException {
        for (Field field : this.getClass().getFields()) {
            Object value = field.get(this);
            if (value instanceof List) {
                if (!((List<?>) value).isEmpty()) {
                    return false;
                }
            } else if (value != null) {
                return false;
            }
        }
        return true;
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
            sb.append("lastName=").append(lastName).append("; ");
        }
        if (isMale != null) {
            sb.append("isMale=").append(isMale).append("; ");
        }
        if (spouce != null) {
            sb.append("spouce=").append(spouce).append("; ");
        }
        if (!parents.isEmpty()) {
            sb.append("parents=").append(parents).append("; ");
        }
        if (!children.isEmpty()) {
            sb.append("children=").append(children).append("; ");
        }
        if (!siblings.isEmpty()) {
            sb.append("siblings=").append(siblings).append("; ");
        }
        if (numberOfChildren != null) {
            sb.append("numberOfChildren=").append(numberOfChildren).append("; ");
        }
        if (numberOfSiblings != null) {
            sb.append("numberOfSiblings=").append(numberOfSiblings).append("; ");
        }
        sb.append("}");
        return sb.toString();
    }
}
