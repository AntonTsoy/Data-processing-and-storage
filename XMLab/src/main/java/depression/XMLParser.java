package depression;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class XMLParser {

    private final FileInputStream fis;
    private final XMLStreamReader reader;

    public XMLParser(String xmlFilePath) throws FileNotFoundException, XMLStreamException {
        this.fis = new FileInputStream(xmlFilePath);
        this.reader = XMLInputFactory.newInstance().createXMLStreamReader(fis);
    }

    public PersonFragment parsePersonFragment() throws XMLStreamException, IllegalAccessException {
        PersonFragment person = new PersonFragment();
        while (reader.hasNext()) {
            int event = reader.getEventType();
            if (event == XMLStreamConstants.START_ELEMENT) {
                handleStartElement(reader.getLocalName(), person);
            } else if (event == XMLStreamConstants.END_ELEMENT && "person".equals(reader.getLocalName())) {
                reader.next();
                break;
            }
            reader.next();
        }
        return person.isEmpty() ? null : person;
    }

    private void handleStartElement(String tag, PersonFragment person) throws XMLStreamException {
        switch (tag) {
            case "person":
                person.id = getAttribute("id");
                parseFullName(getAttribute("name"), person);
                break;
            case "id":
                person.id = getAttribute("value");
                break;
            case "firstname":
            case "first":
                person.firstName = getAttributeOrBody("value");
                break;
            case "surname":
                person.lastName = getAttribute("value");
                break;
            case "family":
            case "family-name":
                person.lastName = readCharacters();
                break;
            case "children-number":
                person.numberOfChildren = parseOptionalInt(getAttribute("value"));
                break;
            case "siblings-number":
                person.numberOfSiblings = parseOptionalInt(getAttribute("value"));
                break;
            case "gender":
                person.isMale = defineGender(getAttributeOrBody("value"));
                break;
            case "spouce":
                parseSpouseByName(person);
                break;
            case "husband":
            case "wife":
                parseSpouseById(tag, person);
                break;
            case "son":
            case "daughter":
                parseChildById(tag, person);
                break;
            case "child":
                parseChildByName(person);
                break;
            case "siblings":
                parseSiblingsIds(person);
                break;
            case "brother":
            case "sister":
                parseSiblingByName(tag, person);
                break;
            case "father":
            case "mother":
                parseParentByName(tag, person);
                break;
            case "parent":
                parseParentById(person);
                break;
        }
    }

    private void parseFullName(String fullname, PersonFragment person) {
        if (fullname != null) {
            String[] parts = stringParts(fullname);
            person.firstName = parts[0];
            person.lastName = parts[1];
        }
    }

    private void parseSpouseByName(PersonFragment person) {
        String name = getAttribute("value");
        if (isKnown(name)) {
            String[] parts = stringParts(name);
            person.spouce = new PersonFragment(parts[0], parts[1]);
        }
    }

    private void parseSpouseById(String tag, PersonFragment person) {
        person.spouce = new PersonFragment(getAttribute("value"));
        if ("husband".equals(tag)) {
            person.isMale = false;
            person.spouce.isMale = true;
        } else {
            person.isMale = true;
            person.spouce.isMale = false;
        }
    }

    private void parseChildById(String tag, PersonFragment person) {
        PersonFragment child = new PersonFragment(getAttribute("id"));
        child.isMale = "son".equals(tag);
        person.children.add(child);
    }

    private void parseChildByName(PersonFragment person) throws XMLStreamException {
        String[] parts = stringParts(readCharacters());
        person.children.add(new PersonFragment(parts[0], parts[1]));
    }

    private void parseSiblingsIds(PersonFragment person) {
        String siblingIds = getAttribute("val");
        if (siblingIds != null) {
            for (String id : stringParts(siblingIds)) person.siblings.add(new PersonFragment(id));
        }
    }

    private void parseSiblingByName(String tag, PersonFragment person) throws XMLStreamException {
        String[] siblingNameParts = stringParts(readCharacters());
        PersonFragment sibling = new PersonFragment(siblingNameParts[0], siblingNameParts[1]);
        sibling.isMale = "brother".equals(tag);
        person.siblings.add(sibling);
    }

    private void parseParentByName(String tag, PersonFragment person) throws XMLStreamException {
        String[] parentNameParts = stringParts(readCharacters());
        PersonFragment parent = new PersonFragment(parentNameParts[0], parentNameParts[1]);
        parent.isMale = "father".equals(tag);
        person.parents.add(parent);
    }

    private void parseParentById(PersonFragment person) {
        String id = getAttribute("value");
        if (isKnown(id)) {
            person.parents.add(new PersonFragment(id));
        }
    }

    private boolean isKnown(String value) {
        return value != null && !"UNKNOWN".equalsIgnoreCase(value) && !"NONE".equalsIgnoreCase(value);
    }

    private Integer parseOptionalInt(String value) {
        try {
            return (value != null) ? Integer.valueOf(value.trim()) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String[] stringParts(String input) {
        return input.split("\\s+");
    }

    private String getAttribute(String key) {
        String result = this.reader.getAttributeValue(null, key);
        return (result != null) ? result.trim() : null;
    }

    private String getAttributeOrBody(String key) throws XMLStreamException {
        String value = this.getAttribute(key);
        return (value != null) ? value : this.readCharacters();
    }

    private Boolean defineGender(String info) {
        if ("male".equals(info) || "M".equals(info)) {
            return true;
        } else if ("female".equals(info) || "F".equals(info)) {
            return false;
        }
        return null;
    }

    private String readCharacters() throws XMLStreamException {
        if (this.reader.hasNext()) {
            this.reader.next();
            if (this.reader.getEventType() == XMLStreamConstants.CHARACTERS) {
                return reader.getText().trim();
            }
        }
        return null;
    }

    public void close() throws XMLStreamException, IOException {
        reader.close();
        fis.close();
    }
}
