package depression;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


public class FragmentMerger {

    private final Map<String, PersonFragment> personsById;
    private final Map<String, List<PersonFragment>> namesakes;
    private final Map<String, List<String>> fullNameIds;

    public FragmentMerger() {
        this.personsById = new HashMap<>();
        this.namesakes = new HashMap<>();
        this.fullNameIds = new HashMap<>();
    }

    public void structurePersonFragments() {

    }

    public void appendPersonFragment(PersonFragment person) {
        String fullname = null;
        if (person.firstName != null && person.lastName != null) {
            fullname = person.firstName + " " + person.lastName;
        }
        if (person.id == null) {
            namesakes.putIfAbsent(fullname, new ArrayList<>());
            namesakes.get(fullname).add(person);
        } else {
            traverseRelatives(person);
            recordIdentifiedPerson(person);
            if (fullname != null) {
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
        for (PersonFragment child : person.children) {
            child.numberOfSiblings = person.numberOfChildren != null ? person.numberOfChildren - 1 : null;
            recordIdentifiedPerson(child);
        }
        for (PersonFragment sibling : person.siblings) {
            sibling.numberOfSiblings = person.numberOfSiblings;
            recordIdentifiedPerson(sibling);
        }
        for (PersonFragment parent : person.parents) {
            parent.numberOfChildren = person.numberOfChildren != null ? person.numberOfSiblings + 1 : null;
            recordIdentifiedPerson(parent);
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

    private boolean hasRelatives(PersonFragment person) {
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
            for (String personId : personsById.keySet()) {
                writer.println(personId);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void complexUnidentifiedMerge() {
        for (String fullname : namesakes.keySet()) {
            List<PersonFragment> personsByName = namesakes.get(fullname);
            while (!personsByName.isEmpty()) {
                PersonFragment currPerson = personsByName.getFirst();
                if (!hasRelatives(currPerson)) {
                    personsByName.remove(currPerson);
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
                        personsByName.remove(currPerson);
                        continue;
                    }
                }
                if (currPerson.spouce != null && currPerson.spouce.id != null) {
                    PersonFragment spoucePerson = personsById.get(currPerson.spouce.id);
                    if (spoucePerson.numberOfChildren != null) {
                        currPerson.numberOfChildren = spoucePerson.numberOfChildren;
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
                        personsByName.remove(currPerson);
                        continue;
                    }
                }
                if (!currPerson.siblings.isEmpty()) {
                    currPerson.numberOfSiblings = currPerson.siblings.size();
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
                        personsByName.remove(currPerson);
                        continue;
                    }
                }
                System.out.println("Unidentified person: " + currPerson);
                throw new IllegalStateException("Can't merge unidentified person with it ID instance");
            }
        }
        namesakes.clear();
    }
}
