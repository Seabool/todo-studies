package seabool;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;


public class XMLHandler {

    private final String filename = "classes.xml";
    private final FilesHandler filesHandler = new FilesHandler();
    private final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    private final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    private Document document;
    private Element root;

    public XMLHandler() throws ParserConfigurationException, TransformerException, IOException, SAXException {
        createXMLFile();
    }

    public void addToXML(String className) throws TransformerException {
        Element aClass = getDocument().createElement("class");
        Element name = getDocument().createElement("name");
        name.appendChild(getDocument().createTextNode(className));
        aClass.appendChild(name);

        getRoot().appendChild(aClass);

        updateXMLFile();
    }

    private void updateXMLFile() throws TransformerException {
        DOMSource source = new DOMSource(getDocument());
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StreamResult result = new StreamResult(getFilename());
        transformer.transform(source, result);
    }

    public boolean checkIfClassExists(String login) {
        return findByName(login) != null;
    }

    private Element findByName(String name) {
        NodeList elements = getDocument().getElementsByTagName("class");
        for (int i = 0; i < elements.getLength(); i++) {
            Node node = elements.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if (element.getElementsByTagName("name").item(0).getTextContent().equals(name)) {
                    return element;
                }
            }
        }
        return null;
    }

    private void createXMLFile() throws TransformerException, IOException, SAXException {
        if (!checkIfXMLFileExists()) {
            document = documentBuilder.newDocument();
            root = document.createElement("classes");
            document.appendChild(root);
            updateXMLFile();
        } else {
            document = documentBuilder.parse(getFilename());
            root = getDocument().getDocumentElement();
        }

    }

    private boolean checkIfXMLFileExists() {
        File xmlFile = new File(filename);
        return xmlFile.exists();
    }

    public Set<StudentClass> initializeClasses() {
        Set<StudentClass> studentClasses = new TreeSet<>();

        NodeList nList = getDocument().getElementsByTagName("class");
        for (int i = 0; i < nList.getLength(); i++) {

            Node nNode = nList.item(i);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element user = (Element) nNode;

                String name = user.getElementsByTagName("name").item(0).getTextContent();
                File file = filesHandler.createClassFolder(name);
                StudentClass studentClass = new StudentClass(name, file);
                for (int j = 0; j < user.getElementsByTagName("note").getLength(); j++) {
                    studentClass.addNote(user.getElementsByTagName("note").item(j).getTextContent());
                }
                studentClasses.add(studentClass);
            }
        }

        return studentClasses;
    }

    public void addNoteToClass(String className, String note) throws TransformerException {
        Element classElement = findByName(className);
        Element noteElement = getDocument().createElement("note");
        noteElement.appendChild(getDocument().createTextNode(note));
        if (classElement != null) {
            classElement.appendChild(noteElement);
            updateXMLFile();
        }
    }

    public void removeFromXML(String className) throws TransformerException {
        Element classElement = findByName(className);
        if (classElement != null) {
            classElement.getParentNode().removeChild(classElement);
            updateXMLFile();
        }
    }

    public String getFilename() {
        return filename;
    }

    public Document getDocument() {
        return document;
    }

    public Element getRoot() {
        return root;
    }
}
