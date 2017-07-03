/*
 * Copyright (c) 2017. Ladaga.
 * This file is part of EpubLibre_Library.
 *
 *     EpubLibre_Library is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     EpubLibre_Library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package parser;

import daoSqLite.InsertDatas;
import modelos.Libro;
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
    static final InsertDatas insertDatas = new InsertDatas();

    /**
     * Importa los libros a la db desde un archivo XML (.rss).
     *
     * @param xmlFile Fichero XML.
     * @throws FileNotFoundException Error al abrir el fichero.
     * @throws XMLStreamException    Error al realizar el stream.
     */
    public static void importXML(File xmlFile) {
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

/**
 * Handler SAX para la lectura del XML (rss total).
 */
class MyHandler extends DefaultHandler {
    private String valor = "";
    private Libro libro;

    public MyHandler(Libro libro) {
        this.libro = libro;
    }

    /**
     * Acciones en la etiqueta de apertura.
     *
     * @param uri
     * @param localName  Nombre de la etiqueta
     * @param qName
     * @param attributes Atributos pertenecientes a la etiqueta.
     * @throws SAXException Error en eventos de SAX.
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        valor = "";
        if (localName.equalsIgnoreCase("item")) {
            libro = new Libro();
        }
    }

    /**
     * Acciones al cierre de la etiqueta, en este caso guardar el valor.
     *
     * @param uri
     * @param localName Nombre de la etiqueta
     * @param qName
     * @throws SAXException Error en eventos de SAX.
     */
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
            Matcher matcher = Pattern.compile("src=\"(.+?)\"", Pattern.CASE_INSENSITIVE).matcher(valor);
            if (matcher.find()) {
                dir = matcher.group(1);
                libro.setImgURI(dir);
            }
        } else if (localName.equalsIgnoreCase("item")) {
            try {
                //Inserta datos al final de la lectura de un libro.
                Rss.insertDatas.insertarLibros(libro);
            } catch (Exception e) {
                //nada
            }
        }
    }

    /**
     * Guarda el valor dentro de las etiquetas xml.
     *
     * @param ch     Caracteres en la etiqueta.
     * @param start  Posición de inicio.
     * @param length Longitud del texto.
     * @throws SAXException Error en eventos de SAX.
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        valor = new String(ch, start, length);
    }
}
