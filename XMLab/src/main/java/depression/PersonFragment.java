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

    public void mergeWith(PersonFragment other) {
        if (other == null) return;

        mergeField("id", this.id, other.id, val -> this.id = val);
        mergeField("firstName", this.firstName, other.firstName, val -> this.firstName = val);
        mergeField("lastName", this.lastName, other.lastName, val -> this.lastName = val);
        mergeField("isMale", this.isMale, other.isMale, val -> this.isMale = val);
        mergeField("spouce", this.spouce, other.spouce, val -> this.spouce = val);
        mergeField("numberOfChildren", this.numberOfChildren, other.numberOfChildren, val -> this.numberOfChildren = val);
        mergeField("numberOfSiblings", this.numberOfSiblings, other.numberOfSiblings, val -> this.numberOfSiblings = val);

        this.parents.addAll(other.parents);
        this.children.addAll(other.children);
        this.siblings.addAll(other.siblings);
    }

    private <T> void mergeField(String fieldName, T current, T other, java.util.function.Consumer<T> setter) {
        if (other == null) return;
        if (current == null) {
            setter.accept(other);
        } else if (!current.equals(other)) {
            System.out.println("Conflict in field '" + fieldName + "':");
            System.out.println("First:  " + this);
            System.out.println("Second: " + other);
            throw new IllegalStateException("Conflicting field '" + fieldName + "' during merge.");
        }
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
