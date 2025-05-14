package depression;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class XMLParser {

    private final XMLStreamReader reader;

    public XMLParser(String xmlFilePath) throws FileNotFoundException, XMLStreamException {
        this.reader = XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(xmlFilePath));
    }

    public PersonFragment parsePersonFragment() throws XMLStreamException, IllegalAccessException {
        PersonFragment currentPerson = new PersonFragment();
        while (true) {
            if (this.reader.getEventType() == XMLStreamConstants.START_ELEMENT
                    || this.reader.getEventType() == XMLStreamConstants.END_ELEMENT) {
                if (!this.handleTag(this.reader.getEventType(), this.reader.getLocalName(), currentPerson)) {
                    if (this.reader.hasNext()) {
                        this.reader.next();
                    } else {
                        break;
                    }
                    break;
                }
            }
            if (this.reader.hasNext()) {
                this.reader.next();
            } else {
                break;
            }
        }
        if (currentPerson.isEmpty()) {
            return null;
        }
        return currentPerson;
    }
    
    private boolean handleTag(int event, String tag, PersonFragment person) throws XMLStreamException {
        if (event == XMLStreamConstants.START_ELEMENT) {
            if ("person".equals(tag)) {
                person.id = this.getAttribute("id");
                String fullname = this.getAttribute("name");
                if (fullname != null) {
                    String[] nameParts = this.stringParts(fullname);
                    person.firstName = nameParts[0];
                    person.lastName = nameParts[1];
                }
            } else if ("id".equals(tag)) {
                person.id = this.getAttribute("value");
            } else if ("children-number".equals(tag)) {
                String childrenSize = this.getAttribute("value");
                if (childrenSize != null) person.numberOfChildren = Integer.valueOf(childrenSize);
            } else if ("siblings-number".equals(tag)) {
                String siblingsSize = this.getAttribute("value");
                if (siblingsSize != null) person.numberOfSiblings = Integer.valueOf(siblingsSize);
            } else if ("firstname".equals(tag) || "first".equals(tag)) {
                person.firstName = this.getAttributeAndBody("value");
            } else if ("surname".equals(tag)) {
                person.lastName = this.getAttribute("value");
            } else if ("family".equals(tag) || "family-name".equals(tag)) {
                person.lastName = this.readCharacters();
            } else if ("gender".equals(tag)) {
                person.isMale = this.defineGenderIsMale(this.getAttributeAndBody("value"));
            } else if ("spouce".equals(tag)) {
                String spouceName = this.getAttribute("value");
                if (spouceName != null && !spouceName.equals("NONE")) {
                    String[] spouceNameParts = this.stringParts(spouceName);
                    person.spouce = new PersonFragment(spouceNameParts[0], spouceNameParts[1]);
                }
            } else if ("husband".equals(tag) || "wife".equals(tag)) {
                person.spouce = new PersonFragment(this.getAttribute("value"));
                if ("husband".equals(tag)) {
                    person.isMale = false;
                    person.spouce.isMale = true;
                } else {
                    person.isMale = true;
                    person.spouce.isMale = false;
                }
            } else if ("son".equals(tag) || "daughter".equals(tag)) {
                PersonFragment child = new PersonFragment(this.getAttribute("id"));
                child.isMale = "son".equals(tag);
                person.children.add(child);
            } else if ("child".equals(tag)) {
                String[] childNameParts = this.stringParts(this.readCharacters());
                person.children.add(new PersonFragment(childNameParts[0], childNameParts[1]));
            } else if ("siblings".equals(tag)) {
                String siblingsIds = this.getAttribute("val");
                if (siblingsIds != null) {
                    String[] idsParts = this.stringParts(siblingsIds);
                    for (String siblingId : idsParts) person.siblings.add(new PersonFragment(siblingId));
                }
            } else if ("brother".equals(tag) || "sister".equals(tag)) {
                String[] siblingNameParts = this.stringParts(this.readCharacters());
                PersonFragment sibling = new PersonFragment(siblingNameParts[0], siblingNameParts[1]);
                sibling.isMale = "brother".equals(tag);
                person.siblings.add(sibling);
            } else if ("father".equals(tag) || "mother".equals(tag)) {
                String[] parentNameParts = this.stringParts(this.readCharacters());
                PersonFragment parent = new PersonFragment(parentNameParts[0], parentNameParts[1]);
                parent.isMale = "father".equals(tag);
                person.parents.add(parent);
            } else if ("parent".equals(tag)) {
                String parentId = this.getAttribute("value");
                if (parentId != null && !parentId.equals("UNKNOWN")) {
                    person.parents.add(new PersonFragment(parentId));
                }
            }
        } else if (event == XMLStreamConstants.END_ELEMENT) {
            return !"person".equals(tag);
        }
        return true;
    }

    private String[] stringParts(String input) {
        return input.split("\\s+");
    }

    private String getAttribute(String key) {
        String result = this.reader.getAttributeValue(null, key);
        return (result != null) ? result.trim() : null;
    }

    private String getAttributeAndBody(String key) throws XMLStreamException {
        String value = this.getAttribute(key);
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
}
