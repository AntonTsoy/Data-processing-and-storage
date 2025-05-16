package depression;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;


public class Main {
    public static void main(String[] args) throws XMLStreamException, IOException, IllegalAccessException {
        XMLParser parser = new XMLParser("people.xml");
        FragmentMerger resolver = new FragmentMerger(parser);
        resolver.mainMerge();
    }
}
