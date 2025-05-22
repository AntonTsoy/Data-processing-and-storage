package depression;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;


public class FragmentMerger {

    private final Map<String, PersonFragment> personsById;
    private final Map<String, List<PersonFragment>> namesakes;
    private final Map<String, List<String>> fullNameIds;

    public FragmentMerger() {
        this.personsById = new HashMap<>();
        this.namesakes = new HashMap<>();
        this.fullNameIds = new HashMap<>();
    }

    public List<Person> getPersons() {
        Map<String, Person> resultPersonsById = new HashMap<>();
        for (PersonFragment person : personsById.values()) {
            resultPersonsById.put(person.id, new Person(person));

        }
        List<Person> resultPersons = new ArrayList<>();
        for (;;) {}
        return resultPersons;
    }

    public void structurePersonFragments() {
        mergeUniqueNamedPersons();
        complexUnidentifiedMerge();
        writePersonsInTxt("people_final_id.txt");
    }

    private void mergeUniqueNamedPersons() {
        for (String fullname : fullNameIds.keySet()) {
            if (fullNameIds.get(fullname).size() == 1 && namesakes.containsKey(fullname)) {
                for (PersonFragment namesake : namesakes.get(fullname)) {
                    namesake.id = fullNameIds.get(fullname).getFirst();
                    traverseRelatives(namesake);
                    recordIdentifiedPerson(namesake);
                }
                namesakes.remove(fullname);
            }
        }
    }

    public void appendPersonFragment(PersonFragment person) {
        if (person.id == null) {
            String fullname = person.firstName + " " + person.lastName;
            namesakes.putIfAbsent(fullname, new ArrayList<>());
            namesakes.get(fullname).add(person);
        } else {
            traverseRelatives(person);
            recordIdentifiedPerson(person);
            mergeWith(person, personsById.get(person.id));
            if (person.firstName != null && person.lastName != null) {
                String fullname = person.firstName + " " + person.lastName;
                fullNameIds.putIfAbsent(fullname, new ArrayList<>());
                List<String> currNameIds = fullNameIds.get(fullname);
                if (!currNameIds.contains(person.id)) currNameIds.add(person.id);
            }
        }
    }

    private void traverseRelatives(PersonFragment person) {
        if (person.spouce != null) {
            transferGender(person, person.spouce);
            person.spouce.numberOfChildren = person.numberOfChildren;
            recordIdentifiedPerson(person.spouce);
        }
        for (PersonFragment parent : person.parents) {
            parent.numberOfChildren = person.numberOfSiblings != null ? person.numberOfSiblings + 1 : null;
            recordIdentifiedPerson(parent);
        }
        for (PersonFragment child : person.children) {
            child.numberOfSiblings = person.numberOfChildren != null ? person.numberOfChildren - 1 : null;
            recordIdentifiedPerson(child);
        }
        for (PersonFragment sibling : person.siblings) {
            sibling.numberOfSiblings = person.numberOfSiblings;
            recordIdentifiedPerson(sibling);
        }
    }

    private void recordIdentifiedPerson(PersonFragment person) {
        if (person.id != null) {
            personsById.putIfAbsent(person.id, new PersonFragment());
            mergeWith(personsById.get(person.id), person);
        }
    }

    private void transferGender(PersonFragment first, PersonFragment second) {
        if (first.isMale != null || second.isMale != null) {
            if (first.isMale == null) {
                first.isMale = !second.isMale;
            } else if (second.isMale == null) {
                second.isMale = !first.isMale;
            } else if (first.isMale == second.isMale) {
                System.out.println("Persons must be different gender. Conflict in field 'isMale':");
                System.out.println("First:  " + first);
                System.out.println("Second: " + second);
                throw new IllegalStateException("Persons must be different gender. Conflict in field 'isMale':");
            }
        }
    }

    private boolean hasNoRelatives(PersonFragment person) {
        return person.spouce == null && person.children.isEmpty() && person.parents.isEmpty() && person.siblings.isEmpty();
    }

    private void mergeWith(PersonFragment destination, PersonFragment source) {
        if (source == null) return;

        mergeField("id", destination.id, source.id, val -> destination.id = val);
        mergeField("firstName", destination.firstName, source.firstName, val -> destination.firstName = val);
        mergeField("lastName", destination.lastName, source.lastName, val -> destination.lastName = val);
        mergeField("isMale", destination.isMale, source.isMale, val -> destination.isMale = val);
        mergeField("numberOfChildren", destination.numberOfChildren, source.numberOfChildren, val -> destination.numberOfChildren = val);
        mergeField("numberOfSiblings", destination.numberOfSiblings, source.numberOfSiblings, val -> destination.numberOfSiblings = val);

        if (destination.spouce != null) {
            mergeWith(destination.spouce, source.spouce);
        } else {
            destination.spouce = source.spouce;
        }
        destination.parents.addAll(source.parents);
        destination.children.addAll(source.children);
        destination.siblings.addAll(source.siblings);
    }

    private <T> void mergeField(String fieldName, T current, T other, java.util.function.Consumer<T> setter) {
        if (other == null) return;
        if (current == null) {
            setter.accept(other);
        } else if (!current.equals(other)) {
            System.out.println("Conflict in field '" + fieldName + "':");
            System.out.println("First:  " + current);
            System.out.println("Second: " + other);
            throw new IllegalStateException("Conflicting field '" + fieldName + "' during merge.");
        }
    }

    private void writePersonsInTxt(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (PersonFragment person : personsById.values()) {
                writer.println(person);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void complexUnidentifiedMerge() {
        for (String fullname : namesakes.keySet()) {
            for (int namesakeNum = 0; namesakeNum < namesakes.get(fullname).size(); namesakeNum++) {
                PersonFragment currPerson = namesakes.get(fullname).get(namesakeNum);
                if (hasNoRelatives(currPerson)) {
                    namesakes.get(fullname).remove(namesakeNum--);
                    continue;
                }
                if (currPerson.isMale != null) {
                    List<String> candidates = new ArrayList<>(fullNameIds.get(fullname));
                    for (int i = 0; i < candidates.size(); i++) {
                        String candidateId = candidates.get(i);
                        PersonFragment candidatePerson = personsById.get(candidateId);
                        if (candidatePerson.isMale != null && candidatePerson.isMale != currPerson.isMale) {
                            candidates.remove(candidateId);
                            i--;
                        }
                    }
                    if (candidates.size() == 1) {
                        PersonFragment realPerson = personsById.get(candidates.getFirst());
                        mergeWith(realPerson, currPerson);
                        namesakes.get(fullname).remove(namesakeNum--);
                        continue;
                    }
                }
                if (currPerson.numberOfSiblings != null) {
                    List<String> candidates = new ArrayList<>(fullNameIds.get(fullname));
                    for (int i = 0; i < candidates.size(); i++) {
                        String candidateId = candidates.get(i);
                        PersonFragment candidatePerson = personsById.get(candidateId);
                        if (candidatePerson.numberOfSiblings != null && !candidatePerson.numberOfSiblings.equals(currPerson.numberOfSiblings)) {
                            candidates.remove(candidateId);
                            i--;
                        }
                    }
                    if (candidates.size() == 1) {
                        PersonFragment realPerson = personsById.get(candidates.getFirst());
                        mergeWith(realPerson, currPerson);
                        namesakes.get(fullname).remove(namesakeNum--);
                        continue;
                    }
                }
                if (currPerson.spouce != null && currPerson.spouce.id != null) {
                    PersonFragment spoucePerson = personsById.get(currPerson.spouce.id);
                    if (spoucePerson.numberOfChildren != null) {
                        currPerson.numberOfChildren = spoucePerson.numberOfChildren;
                    }
                }
                if (!currPerson.children.isEmpty()) {
                    String childId = currPerson.children.getFirst().id;
                    PersonFragment childPerson = personsById.get(childId);
                    if (childPerson.numberOfSiblings != null) {
                        currPerson.numberOfChildren = childPerson.numberOfSiblings + 1;
                    }
                }
                if (currPerson.numberOfChildren != null) {
                    List<String> candidates = new ArrayList<>(fullNameIds.get(fullname));
                    for (int i = 0; i < candidates.size(); i++) {
                        String candidateId = candidates.get(i);
                        PersonFragment candidatePerson = personsById.get(candidateId);
                        if (candidatePerson.numberOfChildren != null && !candidatePerson.numberOfChildren.equals(currPerson.numberOfChildren)) {
                            candidates.remove(candidateId);
                            i--;
                        }
                    }
                    if (candidates.size() == 1) {
                        PersonFragment realPerson = personsById.get(candidates.getFirst());
                        mergeWith(realPerson, currPerson);
                        namesakes.get(fullname).remove(namesakeNum--);
                        continue;
                    }
                }
                for (PersonFragment parent : currPerson.parents) {
                    if (parent.id == null) parent.id = fullNameIds.get(parent.firstName + " " + parent.lastName).getFirst();
                    PersonFragment parentPerson = personsById.get(parent.id);
                    Set<String> parentChildren = parentPerson.children
                            .stream()
                            .map(child -> child.id)
                            .collect(Collectors.toCollection(HashSet::new));
                    List<String> candidates = fullNameIds.get(fullname).stream().filter(parentChildren::contains).toList();
                    if (candidates.size() == 1) {
                        PersonFragment realPerson = personsById.get(candidates.getFirst());
                        mergeWith(realPerson, currPerson);
                        namesakes.get(fullname).remove(namesakeNum--);
                        break;
                    }
                }
                for (PersonFragment sibling : currPerson.siblings) {
                    if (sibling.id == null) sibling.id = fullNameIds.get(sibling.firstName + " " + sibling.lastName).getFirst();
                    PersonFragment siblingPerson = personsById.get(sibling.id);
                    Set<String> siblingSiblings = siblingPerson.siblings
                            .stream()
                            .map(sib -> sib.id)
                            .collect(Collectors.toCollection(HashSet::new));
                    List<String> candidates = fullNameIds.get(fullname).stream().filter(siblingSiblings::contains).toList();
                    if (candidates.size() == 1) {
                        PersonFragment realPerson = personsById.get(candidates.getFirst());
                        mergeWith(realPerson, currPerson);
                        namesakes.get(fullname).remove(namesakeNum--);
                        break;
                    }
                }
            }
        }
        namesakes.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    private void identifyAllRelatives(PersonFragment person) {
        for (PersonFragment child : person.children) {
            if (child.id == null && !differPersonAndRelative(person, child)) {
                String fullname = child.firstName + " " + child.lastName;
                for (String candidateId : fullNameIds.get(fullname)) {
                    for (PersonFragment candidateParent : personsById.get(candidateId).parents) {
                        findPersonIdByUniqueName(candidateParent);
                        if (candidateParent.id.equals(person.id)) {
                            child.id = candidateId;
                        }
                    }
                }
            }
        }
        for (PersonFragment parent : person.parents) {

        }
    }

    private void helper(PersonFragment person, PersonFragment child) {
        String fullname = child.firstName + " " + child.lastName;
        for (String candidateId : fullNameIds.get(fullname)) {
            for (PersonFragment candidateParent : personsById.get(candidateId).parents) {
                findPersonIdByUniqueName(candidateParent);
                if (candidateParent.id.equals(person.id)) {
                    child.id = candidateId;
                }
            }
        }
    }

    private void findPersonIdByUniqueName(PersonFragment person) {
        if (person.id == null) {
            String personName = person.firstName + " " + person.lastName;
            if (fullNameIds.get(personName).size() == 1) {
                person.id = fullNameIds.get(personName).getFirst();
            }
        }
    }

    private boolean differPersonAndRelative(PersonFragment person, PersonFragment relative) {
        if (person.id == null) return false;
        if (relative.id == null) {
            if (relative.firstName.equals(person.firstName) && relative.lastName.equals(person.lastName)) {
                String fullname = person.firstName + " " + person.lastName;
                List<String> candidateIds = new ArrayList<>(fullNameIds.get(fullname));
                candidateIds.remove(person.id);
                relative.id = candidateIds.getFirst();
            } else {
                return false;
            }
        }
        return true;
    }
}
