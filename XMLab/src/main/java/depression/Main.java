package depression;

import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Main {
    public static void handlePersonRelatives(
            PersonFragment person,
            Map<String, PersonFragment> peopleById,
            Map<String, List<PersonFragment>> fullNamesakes
    ) {}

    public static void main(String[] args) throws XMLStreamException, FileNotFoundException, IllegalAccessException {
        XMLParser parser = new XMLParser("people.xml");
        Map<String, PersonFragment> peopleById = new HashMap<>();
        Map<String, List<PersonFragment>> fullNamesakes = new HashMap<>();
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
            } else {
                String fullname = person.firstName + " " + person.lastName;
                fullNamesakes.get(fullname);
                fullNamesakes.computeIfAbsent(fullname, k -> new ArrayList<>());
                fullNamesakes.get(fullname).add(person);
                counterName++;
            }
            //handlePersonRelatives(person);
            person = parser.parsePersonFragment();
        }
        System.out.println(counterId + " " + counterName);
    }
}
