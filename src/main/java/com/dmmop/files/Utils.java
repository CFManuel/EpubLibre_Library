/*
 * Copyright (c) 2019. Ladaga.
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

package com.dmmop.files;

import com.dmmop.modelos.CommonStrings;
import com.dmmop.modelos.Libro;
import com.dmmop.vista.Main;
import com.dmmop.vista.controllers.Alertas;
import com.google.gson.Gson;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
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
            URL url = new URL("https://epublibre.org/rssweb/csv");
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.connect();
            destino = new File(Main.getLocation() + "epub.zip");
            FileUtils.copyInputStreamToFile(conn.getInputStream(), destino);
//            System.out.println(destino.getAbsolutePath());
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

            uc.setReadTimeout(120 * 1000);
            uc.setConnectTimeout(10 * 1000);
            uc.setRequestProperty("Authorization", "Bearer " + TOKEN_API);
            uc.setRequestProperty("Dropbox-API-Arg", "{\"path\": \"/csv_full_imgs.zip\"}");
            destino = new File(Main.getLocation() + "epub.zip");
            FileUtils.copyInputStreamToFile(uc.getInputStream(), destino);
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
        try {
            URL url = new URL(DROPBOX_API);
            URLConnection uc = url.openConnection();

            uc.setReadTimeout(60 * 1000);
            uc.setConnectTimeout(10 * 1000);
            uc.setRequestProperty("Authorization", "Bearer " + TOKEN_API);
            uc.setRequestProperty("Dropbox-API-Arg", "{\"path\": \"/config.json\"}");

            IOUtils.copy(uc.getInputStream(), writer, StandardCharsets.UTF_8);
            json = writer.toString();
            mapa = gson.fromJson(json, HashMap.class);
        } catch (Exception e) {
            // Sin conexión
            //            System.err.println(e.getMessage());
        }
        return mapa;
    }

    /**
     * Recibe un archivo zip y lo descomprime en el directorio junto al jar.
     *
     * @param zip Fichero a descomprimir
     */
    public static void unZip(File zip) throws ZipException {
        ZipFile zipFile = new ZipFile(zip);
        zipFile.extractAll(Main.getLocation());
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
            String magnet = String.format(
                    "%s&dn=ePL_[%d]_%s",
                    libro.getEnlaces(),
                    libro.getEpl_id(),
                    libro.getTitulo()
                            .replaceAll("\\s", "_")
                            .replaceAll("[´`\"\']", ""));
            URI magnetLink = new URI(magnet);
            Desktop.getDesktop().browse(magnetLink);
        } catch (IOException | URISyntaxException e) {
            Alertas.stackTraceAlert(e);
        }
    }
}
