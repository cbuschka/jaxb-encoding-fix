package marshaller;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import lombok.SneakyThrows;

public class FixedMarshaller {

  private final JAXBContext jaxbContext;

  @SneakyThrows
  public <T> FixedMarshaller(Class<T> targetClass) {
    this.jaxbContext = JAXBContext.newInstance(targetClass);
  }

  private Marshaller createMarshaller() throws JAXBException {
    return jaxbContext.createMarshaller();
  }

  public <T> byte[] marshal(T model, Charset encoding) throws JAXBException, IOException {
    return marshalInternally(model, createMarshaller(), encoding);
  }

  @SneakyThrows({TransformerConfigurationException.class})
  protected <T> byte[] marshalInternally(T model, Marshaller marshaller, Charset encoding)
      throws JAXBException, IOException {

    try (var outputStream = new ByteArrayOutputStream()) {
      SAXTransformerFactory saxTransformerFactory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
      TransformerHandler serializer = saxTransformerFactory.newTransformerHandler();
      Transformer transformer = serializer.getTransformer();
      transformer.setOutputProperty(OutputKeys.ENCODING, encoding.name());
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
      transformer.setOutputProperty(OutputKeys.METHOD, "xml");
      transformer.setOutputProperty(OutputKeys.INDENT, "no");
      // DON'T SET OutputKeys.STANDALONE!
      serializer.setResult(new StreamResult(outputStream));

      marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.FALSE);
      marshaller.marshal(model, serializer);
      return outputStream.toByteArray();
    }
  }
}
