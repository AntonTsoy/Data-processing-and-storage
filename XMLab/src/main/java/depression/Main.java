package depression;

import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws XMLStreamException, FileNotFoundException, IllegalAccessException {
        XMLParser parser = new XMLParser("people.xml");
        PersonFragment person = parser.parsePersonFragment();
        List<PersonFragment> persons = new ArrayList<>();
        while (person != null) {
            persons.add(person);
            person = parser.parsePersonFragment();
        }
        System.out.println(persons.size());
        System.out.println(persons.get(persons.size()-2));
        System.out.println(persons.getLast());
    }
}
