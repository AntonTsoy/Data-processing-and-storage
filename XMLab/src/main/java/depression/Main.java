package depression;


public class Main {
    public static void main(String[] args) throws Exception {
        XMLParser parser = new XMLParser("people.xml");
        FragmentMerger resolver = new FragmentMerger();
        PersonFragment person = parser.parsePersonFragment();
        while (person != null) {
            resolver.appendPersonFragment(person);
            person = parser.parsePersonFragment();
        }
        parser.close();
        resolver.structurePersonFragments();
        XMLUtils.marshalPeople(resolver.getPersons(), "schema.xsd", "result.xml");
    }
}
