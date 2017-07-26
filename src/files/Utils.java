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

import com.google.gson.Gson;
import modelos.CommonStrings;
import modelos.Libro;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import uriSchemeHandler.CouldNotOpenUriSchemeHandler;
import uriSchemeHandler.URISchemeHandler;
import vista.Main;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;


public class Utils implements CommonStrings {
    /**
     * Descarga el .zip de la página oficial de EpubLibre
     *
     * @return File con el .zip.
     */
    @Deprecated
    public static File downloadCSVfromEPL() {
        File destino = null;
        try {
            URL url = new URL(CSV_URL);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:31.0) Gecko/20100101 Firefox/31.0");
            conn.connect();
            destino = new File(Main.getLocation() + "epub.zip");
            FileUtils.copyInputStreamToFile(conn.getInputStream(), destino);
            System.out.println(destino.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return destino;
    }

    /**
     * Descarga el .zip desde la Dropbox.
     *
     * @return File con el .zip
     */
    public static File downloadCSVfromDropbox() {
        File destino = null;
        try {
            String link = DROPBOX_API;
            URL url = new URL(link);
            URLConnection uc = url.openConnection();

            uc.setReadTimeout(5 * 1000);
            uc.setConnectTimeout(5 * 1000);
            uc.setRequestProperty("Authorization", "Bearer " + TOKEN_API);
            uc.setRequestProperty("Dropbox-API-Arg", "{\"path\": \"/csv_full_imgs.zip\"}");
            destino = new File(Main.getLocation() + "epub.zip");
            FileUtils.copyInputStreamToFile(uc.getInputStream(), destino);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return destino;
    }

    public static HashMap<String, String> getConfig() {
        StringWriter writer = new StringWriter();
        Gson gson = new Gson();
        HashMap<String, String> mapa = new HashMap<>();
        String json;
        String link = DROPBOX_API;
        try {
            URL url = new URL(link);
            URLConnection uc = url.openConnection();

            uc.setReadTimeout(3 * 1000);
            uc.setConnectTimeout(3 * 1000);
            uc.setRequestProperty("Authorization", "Bearer " + TOKEN_API);
            uc.setRequestProperty("Dropbox-API-Arg", "{\"path\": \"/config.json\"}");

            IOUtils.copy(uc.getInputStream(), writer, StandardCharsets.UTF_8);
            json = writer.toString();
            mapa = gson.fromJson(json, HashMap.class);
        } catch (Exception e) {
            //Sin conexión.
        }
        return mapa;
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
     * @param forDelete Ficheros a borrar.
     */
    public static void deleteZip(File... forDelete) {
        for (File file : forDelete) {
            file.delete();
        }

    }

    public static void crearEPL() {
        File carpeta = new File(Main.getLocation());
        if (!carpeta.exists()) {
            carpeta.mkdir();
        }
    }

    /**
     * Lanza el URI Scheme correspondiente a un magentLink almacenado en un libro.
     *
     * @param libro Libro a descargar.
     */
    public static void launchTorrent(Libro libro) {
        try {
            URI magnetLink = new URI(String.format("%s&dn=EPL_[%d]_%s", libro.getEnlaces(), libro.getEpl_id(), libro.getTitulo().replaceAll("\\s", "_")));
            URISchemeHandler uriSchemeHandler = new URISchemeHandler();
            uriSchemeHandler.open(magnetLink);

        } catch (URISyntaxException | CouldNotOpenUriSchemeHandler e) {
            e.printStackTrace();
        }
    }
}
