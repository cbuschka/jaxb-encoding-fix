package marshaller;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import lombok.SneakyThrows;

public class WrongMarshaller {

    private static final String XML_HEADER_TEMPLATE = "<?xml version=\"1.0\" encoding=\"%s\"?>";
    private final JAXBContext jaxbContext;

    @SneakyThrows
    public <T> WrongMarshaller(Class<T> targetClass) {
      this.jaxbContext = JAXBContext.newInstance(targetClass);
    }

    private Marshaller createMarshaller() throws JAXBException {
      return jaxbContext.createMarshaller();
    }

    public <T> String marshal(T model, Charset encoding) throws JAXBException, IOException {
      return marshalInternally(model, createMarshaller(), encoding);
    }

    protected <T> String marshalInternally(T model, Marshaller marshaller, Charset encoding)
        throws JAXBException, IOException {

      try (var outputStream = new ByteArrayOutputStream();
          var writer = new OutputStreamWriter(outputStream, encoding)) {
        marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding.name());
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        // Fragment prevents the marshaller from writing the XML declaration/header
        marshaller.marshal(model, writer);
        writer.flush();
        return XML_HEADER_TEMPLATE.formatted(encoding.name()).concat(outputStream.toString(encoding));
      }
    }
}
