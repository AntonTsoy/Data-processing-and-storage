package depression;


public class Main {
    public static void main(String[] args) throws Exception {
        long startTime = System.nanoTime();

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

        long endTime = System.nanoTime();
        long executionTime = (endTime - startTime) / 1_000_000;
        System.out.println("Execution time: " + executionTime + " ms");
    }
}
