package depression;

import java.util.List;
import javax.xml.bind.annotation.*;


@XmlRootElement(name = "person")
@XmlAccessorType(XmlAccessType.FIELD)
public class Person {
    @XmlAttribute(name = "id", required = true)
    @XmlID
    public String id;

    @XmlElement(name = "firstName", required = true)
    public String firstName;

    @XmlElement(name = "lastName", required = true)
    public String lastName;

    @XmlElement(name = "isMale", required = true)
    public Boolean isMale;

    @XmlElement(name = "fatherId")
    @XmlIDREF
    public Person father;

    @XmlElement(name = "motherId")
    @XmlIDREF
    public Person mother;

    @XmlElement(name = "spouceId")
    @XmlIDREF
    public Person spouce;

    @XmlElementWrapper(name = "sons")
    @XmlElement(name = "sonId")
    @XmlIDREF
    public List<Person> sons;

    @XmlElementWrapper(name = "daughters")
    @XmlElement(name = "daughterId")
    @XmlIDREF
    public List<Person> daughters;

    @XmlElementWrapper(name = "brothers")
    @XmlElement(name = "brotherId")
    @XmlIDREF
    public List<Person> brothers;

    @XmlElementWrapper(name = "sisters")
    @XmlElement(name = "sisterId")
    @XmlIDREF
    public List<Person> sisters;

    public Person(PersonFragment basePerson) {
        this.id = basePerson.id;
        this.firstName = basePerson.firstName;
        this.lastName = basePerson.lastName;
        this.isMale = basePerson.isMale;
    }
}
