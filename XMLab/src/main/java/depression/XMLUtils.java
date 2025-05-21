package depression;

import javax.xml.bind.*;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.XMLConstants;
import java.io.File;
import java.util.List;


public class XMLUtils {

    public static void marshalPeople(List<Person> people, String xsdFilePath, String outputFilePath) throws Exception {
        File xsd = new File(xsdFilePath);
        File output = new File(outputFilePath);

        JAXBContext context = JAXBContext.newInstance(PeopleModel.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(xsd);
        marshaller.setSchema(schema);

        marshaller.marshal(new PeopleModel(people), output);
    }
}
