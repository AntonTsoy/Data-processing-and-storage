package depression;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class XMLParser {

    private final XMLStreamReader reader;

    public XMLParser(String xmlFilePath) throws FileNotFoundException, XMLStreamException {
        this.reader = XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(xmlFilePath));
    }

    public PersonFragment parsePersonFragment() throws XMLStreamException {
        PersonFragment currentPerson = null;
        while (true) {
            if (this.reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
                String currentElement = this.reader.getLocalName();
                if ("person".equals(currentElement)) {
                    currentPerson = new PersonFragment(this.getAttribute("id"));
                    String[] fullname = this.stringParts(this.getAttribute("name"));
                    currentPerson.firstName = fullname[0];
                    currentPerson.lastName = fullname[1];
                } else if ("id".equals(currentElement)) {
                    currentPerson.id = this.getAttribute("value");
                } else if ("children-number".equals(currentElement)) {
                    currentPerson.numberOfChildren = Integer.valueOf(this.getAttribute("value"));
                } else if ("siblings-number".equals(currentElement)) {
                    currentPerson.numberOfSiblings = Integer.valueOf(this.getAttribute("value"));
                } else if ("firstname".equals(currentElement) || "first".equals(currentElement)) {
                    currentPerson.firstName = this.getAttributeAndBody("value");
                } else if ("surname".equals(currentElement)) {
                    currentPerson.lastName = this.getAttribute("value");
                } else if ("family".equals(currentElement) || "family-name".equals(currentElement)) {
                    currentPerson.lastName = this.readCharacters();
                } else if ("gender".equals(currentElement)) {
                    currentPerson.isMale = this.defineGenderIsMale(this.getAttributeAndBody("value"));
                } else if ("spouce".equals(currentElement)) {
                    String spouceName = this.getAttribute("value");
                    if (spouceName != null && !"NONE".equals(spouceName)) {
                        String[] spouceNameParts = this.stringParts(spouceName);
                        currentPerson.spouce = new PersonFragment(spouceNameParts[0], spouceNameParts[1]);
                    }
                } else if ("husband".equals(currentElement) || "wife".equals(currentElement)) {
                    String spouceId = this.getAttribute("value");
                    currentPerson.spouce = new PersonFragment(spouceId);
                } else if () {

                }
            } else {
                if ("person".equals(this.reader.getLocalName())) {
                    return;  // AB
                }
            }

            if (this.reader.hasNext()) {
                this.reader.nextTag();
            } else {
                break;
            }
        }
        return currentPerson;
    }

    public void parseTag() throws XMLStreamException {
        if ("parent".equals(currentElement) || "father".equals(currentElement) || "mother".equals(currentElement)) {
            String value = reader.getAttributeValue(null, "value");
            if (value == null) value = readCharacters(reader);
            if (value != null && !"UNKNOWN".equalsIgnoreCase(value.trim())) {
                PersonFragment parent = new PersonFragment();
                parent.id = value.trim();
                currentPerson.parents.add(parent);
            }
        } else if ("siblings".equals(currentElement)) {
            // Тут сложна, могут быть перечислены в атрибуте value через пробел ID-шники
            String value = reader.getAttributeValue(null, "val");
            if (value == null) value = readCharacters(reader);
            if (value != null) {
                for (String sid : value.trim().split(" ")) {
                    PersonFragment sibling = new PersonFragment();
                    sibling.id = sid;
                    currentPerson.siblings.add(sibling);
                }
            }
        } else if ("children".equals(currentElement)) {
            // дочерние элементы будут — жди <son>, <daughter>, <child>
        } else if ("son".equals(currentElement) || "daughter".equals(currentElement) || "child".equals(currentElement)) {
            String cid = reader.getAttributeValue(null, "id");
            if (cid == null) cid = readCharacters(reader);
            if (cid != null) {
                PersonFragment child = new PersonFragment();
                child.id = cid.trim();
                currentPerson.children.add(child);
            }
        }
    }

    private String[] stringParts(String input) {
        return input.split("\\s+");
    }

    private String getAttribute(String key) {
        return this.reader.getAttributeValue(null, key).trim();
    }

    private String getAttributeAndBody(String key) throws XMLStreamException {
        String value = this.getAttribute("key");
        if (value == null) {
            return this.readCharacters();
        }
        return value;
    }

    private Boolean defineGenderIsMale(String info) {
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

    public static PersonFragment mergeFragments(PersonFragment a, PersonFragment b) {
        if (a.firstName == null) a.firstName = b.firstName;
        if (a.lastName == null) a.lastName = b.lastName;
        if (a.isMale == null) a.isMale = b.isMale;
        if (a.spouce == null) a.spouce = b.spouce;

        a.parents.addAll(b.parents);
        a.children.addAll(b.children);
        a.siblings.addAll(b.siblings);

        if (a.numberOfChildren == null) a.numberOfChildren = b.numberOfChildren;
        if (a.numberOfSiblings == null) a.numberOfSiblings = b.numberOfSiblings;

        return a;
    }
}
