package depression;

import javax.xml.stream.XMLStreamException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


public class FragmentMerger {

    private final Map<String, PersonFragment> personsById;
    private final Map<String, List<PersonFragment>> namesakes;
    private final Map<String, List<String>> fullNameIds;

    public FragmentMerger() {
        this.personsById = new HashMap<>();
        this.namesakes = new HashMap<>();
        this.fullNameIds = new HashMap<>();
    }

    public void appendPersonFragment(PersonFragment person) {
        if (person.id != null) {
            PersonFragment targetPerson = personsById.get(person.id);
            if (targetPerson != null) {

            } else {

            }

            if (person.firstName != null && person.lastName != null) {
                String fullname = person.firstName + " " + person.lastName;
                fullNameIds.putIfAbsent(fullname, new ArrayList<>());
                fullNameIds.get(fullname).add(person.id);
            }
        } else {
            String fullname = person.firstName + " " + person.lastName;
            namesakes.computeIfAbsent(fullname, k -> new ArrayList<>());
            namesakes.get(fullname).add(person);
        }
    }

    private void mergeUniqueNames() {
        for (String fullname : fullNameIds.keySet()) {
            if (fullNameIds.get(fullname).size() == 1 && fullNamesakes.containsKey(fullname)) {
                for (String personId : fullNameIds.get(fullname)) {
                    person = personsById.get(personId);
                    for (PersonFragment currPerson : fullNamesakes.get(fullname)) {
                        mergeWith(person, currPerson);
                    }
                }
                handlePersonRelatives(person);
                fullNamesakes.remove(fullname);
            }
        }
    }

    private void mergeWith(PersonFragment destination, PersonFragment source) {
        if (source == null) return;

        mergeField("id", destination.id, source.id, val -> destination.id = val);
        mergeField("firstName", destination.firstName, source.firstName, val -> destination.firstName = val);
        mergeField("lastName", destination.lastName, source.lastName, val -> destination.lastName = val);
        mergeField("isMale", destination.isMale, source.isMale, val -> destination.isMale = val);
        mergeField("numberOfChildren", destination.numberOfChildren, source.numberOfChildren, val -> destination.numberOfChildren = val);
        mergeField("numberOfSiblings", destination.numberOfSiblings, source.numberOfSiblings, val -> destination.numberOfSiblings = val);

        if (destination.spouce != null) {
            this.mergeWith(destination.spouce, source.spouce);
        }
        destination.parents.addAll(source.parents);
        destination.children.addAll(source.children);
        destination.siblings.addAll(source.siblings);
    }

    private <T> void mergeField(String fieldName, T current, T other, java.util.function.Consumer<T> setter) {
        if (other == null) return;
        if (current == null) {
            setter.accept(other);
        } else if (!current.equals(other)) {
            System.out.println("Conflict in field '" + fieldName + "':");
            System.out.println("First:  " + current);
            System.out.println("Second: " + other);
            throw new IllegalStateException("Conflicting field '" + fieldName + "' during merge.");
        }
    }

    private void writePersonsInTxt(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (String personId : personsById.keySet()) {
                PersonFragment currPerson = personsById.get(personId);
                writer.println(currPerson);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
