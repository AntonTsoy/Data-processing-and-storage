package depression;

import javax.xml.stream.XMLStreamException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


public class FragmentMerger {

    private final XMLParser parser;

    public FragmentMerger(XMLParser parser) {
        this.parser = parser;
    }

    public void mainMerge() throws XMLStreamException, IOException, IllegalAccessException {
        Map<String, PersonFragment> peopleById = new HashMap<>();
        Map<String, List<PersonFragment>> fullNamesakes = new HashMap<>();
        Map<String, Set<String>> fullNameIds = new HashMap<>();
        PersonFragment person = parser.parsePersonFragment();
        int counterId = 0, counterName = 0;
        while (person != null) {
            if (person.id != null) {
                PersonFragment targetPerson = peopleById.get(person.id);
                if (targetPerson != null) {
                    mergeWith(targetPerson, person);
                    person = targetPerson;
                } else {
                    counterId++;
                    peopleById.put(person.id, person);
                }
                if (person.firstName != null && person.lastName != null) {
                    String fullname = person.firstName + " " + person.lastName;
                    fullNameIds.putIfAbsent(fullname, new HashSet<>());
                    fullNameIds.get(fullname).add(person.id);
                }
            } else {
                String fullname = person.firstName + " " + person.lastName;
                fullNamesakes.get(fullname);
                fullNamesakes.computeIfAbsent(fullname, k -> new ArrayList<>());
                fullNamesakes.get(fullname).add(person);
                counterName++;
            }
            handlePersonRelatives(person);
            person = parser.parsePersonFragment();
        }
        System.out.println(counterId + " " + counterName);
        parser.close();

        for (String fullname : fullNameIds.keySet()) {
            if (fullNameIds.get(fullname).size() == 1 && fullNamesakes.containsKey(fullname)) {
                for (String personId : fullNameIds.get(fullname)) {
                    person = peopleById.get(personId);
                    for (PersonFragment currPerson : fullNamesakes.get(fullname)) {
                        mergeWith(person, currPerson);
                    }
                }
                handlePersonRelatives(person);
                fullNamesakes.remove(fullname);
            }
        }

        String filePath = "people_with_id.txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (String personId : peopleById.keySet()) {
                PersonFragment currPerson = peopleById.get(personId);
                writer.println(currPerson);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void mergeRelatives(List<PersonFragment> persons, Integer expectedSize) {
        Map<String, PersonFragment> peopleById = new HashMap<>();
        Map<String, PersonFragment> peopleByName = new HashMap<>();
        while (!persons.isEmpty()) {
            PersonFragment person = persons.getFirst();
            if (person.id != null) {
                PersonFragment relative = peopleById.get(person.id);
                if (relative != null) {
                    mergeWith(relative, person);
                } else {
                    peopleById.put(person.id, person);
                }
            } else {
                String fullname = person.firstName + " " + person.lastName;
                PersonFragment relative = peopleByName.get(fullname);
                if (relative != null) {
                    mergeWith(relative, person);
                } else {
                    peopleByName.put(fullname, person);
                }
            }
            persons.remove(person);
        }

        if (expectedSize != null) {
            if (peopleById.size() > expectedSize || peopleByName.size() > expectedSize) {
                throw new IllegalStateException("Conflicting field relative_size during merge.");
            }
            if (expectedSize == 1 && peopleById.size() == 1 && peopleByName.size() == 1) {
                PersonFragment personWithName = ((List<PersonFragment>) peopleByName.values()).getLast();
                mergeWith(personWithName, ((List<PersonFragment>) peopleById.values()).getLast());
                persons.add(personWithName);
            } else {
                persons.addAll(peopleById.values());
                persons.addAll(peopleByName.values());
            }
        }
    }

    private void handlePersonRelatives(PersonFragment person) {
        try {
            mergeRelatives(person.parents, null);
            mergeRelatives(person.children, person.numberOfChildren);
            mergeRelatives(person.siblings, person.numberOfSiblings);
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            System.out.println("Person: " + person);
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
}
