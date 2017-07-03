package parser;

import modelos.Libro;
import daoSqLite.InsertDatas;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by david on 02/07/2017.
 */
public class Rss {
    static InsertDatas insertDatas = new InsertDatas();

    public static void importXML(File xmlFile) throws FileNotFoundException, XMLStreamException {
        try {
            insertDatas.crearTabla();
            insertDatas.conectar();
            insertDatas.limpiarTabla();
            Libro libro = new Libro();
            XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.setContentHandler(new MyHandler(libro));
            reader.parse(new InputSource(new FileInputStream(xmlFile)));
        } catch (Exception e) {
            try {
                insertDatas.rollBack();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                insertDatas.desconectar();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

class MyHandler extends DefaultHandler {
    private String valor = "";
    private Libro libro;

    public MyHandler(Libro libro) {
        this.libro = libro;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        valor = "";
        if (localName.equalsIgnoreCase("item")) {
            libro = new Libro();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equalsIgnoreCase("title")) {
            libro.setTitulo(valor);
        } else if (localName.equalsIgnoreCase("autor")) {
            libro.setAutor(valor);
        } else if (localName.equalsIgnoreCase("rev")) {
            libro.setRevision(Double.parseDouble(valor));
        } else if (localName.equalsIgnoreCase("link")) {
            libro.setEnlaces(valor);
        } else if (localName.equalsIgnoreCase("pubdate")) {
            libro.setPublicado(valor);
        } else if (localName.equalsIgnoreCase("description")) {
            String dir = "";
            Matcher matcher =    Pattern.compile("src=\\\"(.+?)\\\"", Pattern.CASE_INSENSITIVE).matcher(valor);
            if (matcher.find()) {
                dir = matcher.group(1);
                libro.setImgURI(dir);
            }
        } else if (localName.equalsIgnoreCase("item")) {
            try {
                Rss.insertDatas.insertarLibros(libro);
            } catch (Exception e) {
                //nada
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        valor = new String(ch, start, length);
    }
}
