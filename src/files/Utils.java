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

package files;

import modelos.CommonStrings;
import modelos.Libro;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import uriSchemeHandler.CouldNotOpenUriSchemeHandler;
import uriSchemeHandler.URISchemeHandler;
import vista.Main;

import java.io.File;
import java.io.IOException;
import java.net.*;

/**
 * Created by david on 03/07/2017.
 */
public class Utils implements CommonStrings {
    /**
     * Descarga el .zip de la página oficial de EpubLibre
     *
     * @return File con el .zip.
     */
    public static File downloadCSV() {
        File destino = null;
        try {
            URL url = new URL(CSV_URL);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:31.0) Gecko/20100101 Firefox/31.0");
            conn.connect();
            destino = new File(Main.getLocation() + "epub.zip");
            FileUtils.copyInputStreamToFile(conn.getInputStream(), destino);
            //FileUtils.copyURLToFile(url, destino);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return destino;
    }

    /**
     * Recibe un archivo zip y lo descomprime en el directorio junto al jar.
     *
     * @param zip Fichero a descomprimir
     */
    public static void unZip(File zip) {
        try {
            ZipFile zipFile = new ZipFile(zip);
            zipFile.extractAll(Main.getLocation());
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    /**
     * Borra el fichero .zip.
     *
     * @param zip Fichero a borrar.
     */
    public static void deleteZip(File zip) {
        zip.delete();
    }

    public static void crearEPL() {
        File carpeta = new File(Main.getLocation());
        if (carpeta.exists() == false) {
            carpeta.mkdir();
        }
    }

    public static void launchTorrent(Libro libro) {
        try {
            URI magnetLink = new URI(libro.getEnlaces());
            URISchemeHandler uriSchemeHandler = new URISchemeHandler();
            uriSchemeHandler.open(magnetLink);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (CouldNotOpenUriSchemeHandler couldNotOpenUriSchemeHandler) {
            couldNotOpenUriSchemeHandler.printStackTrace();
        }
    }
}
