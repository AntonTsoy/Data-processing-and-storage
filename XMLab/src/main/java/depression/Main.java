package depression;

import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Main {
    public static void handlePersonRelatives(PersonFragment person) {
    }

    public static void main(String[] args) throws XMLStreamException, FileNotFoundException, IllegalAccessException {
        XMLParser parser = new XMLParser("people.xml");
        Map<String, PersonFragment> peopleById = new HashMap<>();
        Map<String, List<PersonFragment>> fullNamesakes = new HashMap<>();
        PersonFragment person = parser.parsePersonFragment();
        while (person != null) {
            if (person.id != null) {
                PersonFragment targetPerson = peopleById.get(person.id);
                if (targetPerson != null) {
                    targetPerson.mergeWith(person);
                    person = targetPerson;
                } else {
                    peopleById.put(person.id, person);
                }
            } else {
                String fullname = person.firstName + " " + person.lastName;
                fullNamesakes.get(fullname);
                fullNamesakes.computeIfAbsent(fullname, k -> new ArrayList<>());
                fullNamesakes.get(fullname).add(person);
            }
            handlePersonRelatives(person);
            person = parser.parsePersonFragment();
        }
    }
}
