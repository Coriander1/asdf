import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.StringWriter;

//@SpringBootTest(classes = {Main.class})
class OthrTest {

    @Test
    public void test() throws ParserConfigurationException, JAXBException, TransformerException, SAXException, IOException {


        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();


        Book book = new Book();
        book.id = "some id";
        book.name = ("hey");

        JAXBContext context = JAXBContext.newInstance(Book.class);
        Marshaller mar = context.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        SchemaFactory sf = SchemaFactory.newDefaultInstance();
        mar.marshal(book, doc);

        Marshaller mar2 = context.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        SchemaFactory sf2 = SchemaFactory.newDefaultInstance();
        Schema schema2 = sf.newSchema(this.getClass().getResource("blah.xsd"));
        mar.setSchema(schema2);


        DocumentBuilderFactory dbf2 = DocumentBuilderFactory.newInstance();
        dbf2.setNamespaceAware(true);
        DocumentBuilder db2 = dbf2.newDocumentBuilder();
        Document doc2 = db2.newDocument();

        StringWriter writer = new StringWriter();
        mar2.marshal(doc.getOwnerDocument(), writer);
        String s = writer.toString();

        schema2.newValidator().validate(new DOMSource (doc));

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(System.out);
        t.transform(source, result);

        System.out.println("hey");
    }
}
