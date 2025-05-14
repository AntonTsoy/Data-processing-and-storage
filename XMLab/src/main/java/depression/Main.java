package depression;

import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


public class Main {

    private static void mergeRelatives(List<PersonFragment> persons, Integer expectedSize) {
        Map<String, PersonFragment> peopleById = new HashMap<>();
        Map<String, PersonFragment> peopleByName = new HashMap<>();
        for (int i = 0; i < persons.size(); i++) {
            PersonFragment person = persons.get(i);
            if (person.id != null) {
                peopleById.putIfAbsent(person.id, person);
                persons.remove(person);
                i--;
            } else {
                String fullname = person.firstName + " " + person.lastName;
                peopleByName.putIfAbsent(fullname, person);
            }
        }

        if (expectedSize != null) {
            if (peopleById.size() > expectedSize || peopleByName.size() > expectedSize) {
                throw new IllegalStateException("Conflicting field relative_size during merge.");
            }
            if (expectedSize == 1 && peopleById.size() == 1 && peopleByName.size() == 1) {
                PersonFragment personWithName = ((List<PersonFragment>) peopleByName.values()).getLast();
                personWithName.mergeWith(((List<PersonFragment>) peopleById.values()).getLast());
                persons.clear();
                persons.add(personWithName);
            } else {
                persons.addAll(peopleById.values());
            }
        }
    }

    private static void handlePersonRelatives(PersonFragment person) {
        try {
            mergeRelatives(person.parents, null);
            mergeRelatives(person.children, person.numberOfChildren);
            mergeRelatives(person.siblings, person.numberOfSiblings);
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            System.out.println("Person: " + person);
        }
    }

    public static void main(String[] args) throws XMLStreamException, FileNotFoundException, IllegalAccessException {
        XMLParser parser = new XMLParser("people.xml");
        Map<String, PersonFragment> peopleById = new HashMap<>();
        Map<String, List<PersonFragment>> fullNamesakes = new HashMap<>();
        Map<String, Set<String>> fullNameIds = new HashMap<>();
        PersonFragment person = parser.parsePersonFragment();
        int counterId = 0, counterName = 0;
        while (person != null) {
            if (person.id != null) {
                PersonFragment targetPerson = peopleById.get(person.id);
                if (targetPerson != null) {
                    targetPerson.mergeWith(person);
                    person = targetPerson;
                } else {
                    peopleById.put(person.id, person);
                    counterId++;
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
        for (String fullname : fullNameIds.keySet()) {
            if (fullNameIds.get(fullname).size() == 1 && fullNamesakes.containsKey(fullname)) {
                for (String personId : fullNameIds.get(fullname)) {
                    person = peopleById.get(personId);
                    for (PersonFragment currPerson : fullNamesakes.get(fullname)) {
                        person.mergeWith(currPerson);
                    }
                }
                handlePersonRelatives(person);
                fullNamesakes.remove(fullname);
            }
        }
        String filePath = "people_with_name.txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (String fullname : fullNamesakes.keySet()) {
                for (PersonFragment currPerson : fullNamesakes.get(fullname)) {
                    writer.println(currPerson);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(counterId + " " + counterName);
    }
}
