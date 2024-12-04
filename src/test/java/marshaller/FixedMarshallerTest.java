package marshaller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.SneakyThrows;
import model.Child;
import model.Root;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

class FixedMarshallerTest {

  private final FixedMarshaller fixedMarshaller = new FixedMarshaller(Root.class);

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
  void worksWithUmlauts() {
    givenIsADocumentWithUmlauts();

    whenMarshalled();

    thenXmlPrologExists();
    thenChildNameIs("Bärbel");
  }

  private void thenXmlPrologExists() {
    String xml = new String(result, Charset.forName("iso-8859-1"));
    assertThat(xml).startsWith("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
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

  @SneakyThrows
  private void whenMarshalled() {
    this.result = fixedMarshaller.marshal(document, Charset.forName("iso-8859-1"));
  }
}