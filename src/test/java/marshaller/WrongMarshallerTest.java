package marshaller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.SneakyThrows;
import model.Child;
import model.Root;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

class WrongMarshallerTest {

  private final WrongMarshaller wrongMarshaller = new WrongMarshaller(Root.class);

  private Root document;
  private byte[] result;

  @Test
  void worksWithoutUmlauts() {
    givenIsADocumentWithoutUmlauts();

    whenMarshalled();

    thenXmlPrologExists();
    thenChildNameIs("Jonas");
  }

  @Test
  void failsWithUmlauts() {
    givenIsADocumentWithUmlauts();

    whenMarshalled();

    thenXmlPrologExists();
    thenChildNameIs("BÃ¤rbel");
  }


  private void givenIsADocumentWithoutUmlauts() {
    this.document = new Root(new Child("Jonas"));
  }

  private void givenIsADocumentWithUmlauts() {
    this.document = new Root(new Child("Bärbel"));
  }

  @SneakyThrows
  private void thenChildNameIs(String name) {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    documentBuilderFactory.setNamespaceAware(false);
    documentBuilderFactory.setCoalescing(true);
    documentBuilderFactory.setValidating(false);
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    Document document = documentBuilder.parse(new ByteArrayInputStream(result));
    Element child = (Element) document.getDocumentElement().getElementsByTagName("child").item(0);
    assertThat(child.getTextContent()).isEqualTo(name);

  }

  private void thenXmlPrologExists() {
    String xml = new String(result, Charset.forName("ISO-8859-1"));
    assertThat(xml).startsWith("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
  }

  @SneakyThrows
  private void whenMarshalled() {
    this.result = wrongMarshaller.marshal(document, Charset.forName("ISO-8859-1")).getBytes(
        StandardCharsets.UTF_8);
  }
}