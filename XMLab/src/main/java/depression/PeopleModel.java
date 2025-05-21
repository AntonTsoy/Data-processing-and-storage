package depression;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;


@XmlRootElement(name = "people")
@XmlAccessorType(XmlAccessType.FIELD)
public class PeopleModel {

    @XmlElement(name = "person")
    public List<Person> people = new ArrayList<>();

    public PeopleModel() {}

    public PeopleModel(List<Person> people) {
        this.people = people;
    }
}
