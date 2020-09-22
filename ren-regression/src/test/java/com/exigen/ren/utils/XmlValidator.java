package com.exigen.ren.utils;

import com.exigen.istf.data.TestData;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.verification.CustomAssertions;
import com.exigen.ren.common.enums.DocGenEnum;
import com.google.common.collect.ImmutableMap;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.CommonMethods.LOGGER;

public class XmlValidator {

    private Document xmlDocument;

    public XmlValidator(Document xmlDocument) {
        this.xmlDocument = xmlDocument;
    }

    public XmlValidator(String xml) {
        this.parseToDocument(xml);
    }

    public XmlValidator(File xmlFile) {
        this.parseToDocument(xmlFile);
    }

    private void parseToDocument(String xml) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            InputSource stream = new InputSource(new StringReader(xml));
            xmlDocument = builder.parse(stream);
        } catch (Exception e) {
            throw new IstfException("Unable to parse DTO xml document", e);
        }
    }

    private void parseToDocument(File xmlFile) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            xmlDocument = builder.parse(xmlFile);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IstfException("Unable to parse DTO xml document", e);
        }
    }

    public Node findNode(String xpath) {
        XPath xPath = XPathFactory.newInstance().newXPath();
        try {
            return (Node) xPath.compile(xpath).evaluate(xmlDocument, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Node findNode(String xpath, Document xmlDocument) {
        XPath xPath = XPathFactory.newInstance().newXPath();
        try {
            return (Node) xPath.compile(xpath).evaluate(xmlDocument, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public NodeList findNodes(String xpath) {
        XPath xPath = XPathFactory.newInstance().newXPath();
        try {
            return (NodeList) xPath.compile(xpath).evaluate(xmlDocument, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void checkDocument(DocGenEnum.AllSections xpath, String value) {
        assertThat(getNodeValue(xpath))
                .as("Node: " + xpath.name() + " xpath: " + xpath.get())
                .isEqualTo(value);
    }

    public void checkDocument(String xpath, String value) {
        assertThat(getNodeValue(xpath))
                .as("Node: " + xpath)
                .isEqualTo(value);
    }

    public void checkDocumentContains(String xpath, String value) {
        assertThat(getNodeValue(xpath))
                .as("Node: " + xpath)
                .contains(value);
    }

    public void checkDocument(Map<DocGenEnum.AllSections, String> pathAndValue) {
        assertSoftly(softly -> {
            for (Map.Entry<DocGenEnum.AllSections, String> param : pathAndValue.entrySet()) {
                Node node = findNode(param.getKey().get());
                if (node == null) {
                    softly.fail("Node not found: " + param.getKey().get());
                } else {
                    softly.assertThat(node.getTextContent())
                            .as("Node: " + param.getKey().name() + " xpath: " + param.getKey().get())
                            .isEqualTo(param.getValue());
                }
            }
        });
    }

    public void checkDocument(ImmutableMap<String, String> pathAndValue) {
        assertSoftly(softly -> {
            for (Map.Entry<String, String> param : pathAndValue.entrySet()) {
                Node node = findNode(param.getKey());
                if (node == null) {
                    softly.fail("Node not found: " + param.getKey());
                } else {
                    softly.assertThat(node.getTextContent())
                            .as("Node: " + param.getKey() + " xpath: " + param.getKey())
                            .isEqualTo(param.getValue());
                }
            }
        });
    }

    public void checkDocument(TestData tdDataSource) {
        assertSoftly(softly -> {
            tdDataSource.getKeys().forEach(key -> {
                Node node = findNode(DocGenEnum.AllSections.valueOf(key).get());
                if (node == null) {
                    softly.fail("Node not found: " + DocGenEnum.AllSections.valueOf(key).get());
                } else {
                    softly.assertThat(node.getTextContent())
                            .as("Node: " + DocGenEnum.AllSections.valueOf(key).name() + " xpath: " + DocGenEnum.AllSections.valueOf(key).get())
                            .isEqualTo(tdDataSource.getValue(key));
                }
            });
        });
    }

    public void checkNodeIsPresent(DocGenEnum.AllSections... xpath) {
        assertSoftly(softly -> {
            for (DocGenEnum.AllSections xPath : xpath) {
                Node node = findNode(xPath.get());
                softly.assertThat(node).withFailMessage("Node is absent: " + xPath.get()).isNotNull();
            }
        });
    }

    public void checkNodeNotPresent(DocGenEnum.AllSections... xpath) {
        assertSoftly(softly -> {
            for (DocGenEnum.AllSections xPath : xpath) {
                Node node = findNode(xPath.get());
                softly.assertThat(node).withFailMessage("Node is present: " + xPath.get()).isNull();
            }
        });
    }

    public String convertNodeToString(Node node) {
        Transformer transformer;
        StringWriter stringWriter = new StringWriter();
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(new DOMSource(node), new StreamResult(stringWriter));
        } catch (TransformerException e) {
            LOGGER.error("Can't transform node to string", e);
        }
        return stringWriter.toString();
    }

    public List<String> convertNodeListToList(NodeList nodeList) {
        return IntStream.range(0, nodeList.getLength())
                .mapToObj(nodeList::item)
                .map(Node::getTextContent)
                .collect(Collectors.toList());
    }

    public String getNodeValue(DocGenEnum.AllSections xpath) {
        Node node = findNode(xpath.get());
        if (node == null) {
            CustomAssertions.fail("Node not found: " + xpath.get());
            return null;
        } else {
            return node.getTextContent();
        }
    }

    public String getNodeValue(String xpath) {
        Node node = findNode(xpath);
        if (node == null) {
            CustomAssertions.fail("Node not found: " + xpath);
            return null;
        } else {
            return node.getTextContent();
        }
    }

}