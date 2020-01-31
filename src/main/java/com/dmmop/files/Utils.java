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

import com.dmmop.controller.UpdateDB;
import com.dmmop.controller.UpdateTask;
import com.dmmop.exceptions.NoValidCSVFile;
import com.dmmop.modelos.CommonStrings;
import com.dmmop.modelos.Libro;
import com.dmmop.parser.Csv;
import com.dmmop.vista.Main;
import com.dmmop.vista.controllers.Alertas;
import com.google.gson.Gson;
import javafx.concurrent.Task;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.ini4j.Ini;
import org.ini4j.IniPreferences;
import org.ini4j.Wini;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.prefs.Preferences;

public class Utils implements CommonStrings {
    static File destino = null;
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
            URLConnection conn = getConnection(url);
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
    public static File downloadCSVfromDropbox(UpdateTask importar) {
        File destino = null;
        int TOTAL_PROGRESS = 7;
        try {
            String link = DROPBOX_API;
            URL url = new URL(link);
            HttpURLConnection httpConnection = (HttpURLConnection) getConnection(url);

            httpConnection.setReadTimeout(120 * 1000);
            httpConnection.setConnectTimeout(10 * 1000);
            httpConnection.setRequestProperty("Authorization", "Bearer " + TOKEN_API);
            httpConnection.setRequestProperty("Dropbox-API-Arg", "{\"path\": \"/csv_full_imgs.zip\"}");
            long completeFileSize = httpConnection.getContentLength();

            java.io.BufferedInputStream in = new java.io.BufferedInputStream(httpConnection.getInputStream());
            destino = new File(Main.getLocation() + "epub.zip");
            java.io.FileOutputStream fos = new java.io.FileOutputStream(destino);
            java.io.BufferedOutputStream bout = new BufferedOutputStream(
                    fos, 1024);
            byte[] data = new byte[1024];
            long downloadedFileSize = 0;
            int x = 0;
            while ((x = in.read(data, 0, 1024)) >= 0) {
                downloadedFileSize += x;

                // calculate progress
                int currentProgress=0;
                int totalProgress=0;
                double dCurrentProgress = (((double)downloadedFileSize )/1024)/1024;
                currentProgress = (int) dCurrentProgress;
                double dTotalProgress = (((double)completeFileSize)/1024)/1024;
                totalProgress = (int) dTotalProgress;

                importar.updateMessage("Descargando csv... "+String.format("%.2f MB",dCurrentProgress)+"/"+String.format("%.2f MB",dTotalProgress));
                importar.updateProgress(1+currentProgress, TOTAL_PROGRESS+totalProgress);

                bout.write(data, 0, x);
            }
            bout.close();
            in.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
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
            URLConnection uc = getConnection(url);

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

    public static Proxy getProxy(){
        Preferences prefs = getPrefIniEpub();
        Proxy proxy = null;
        if(prefs!=null) {
            boolean useProxy = prefs.node("ProxyConfig").getBoolean("useProxy",true);
            if(useProxy) {
                String host = prefs.node("ProxyConfig").get("Host",null);
                int port = prefs.node("ProxyConfig").getInt("Port",0);
                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
                boolean useProxyAut = prefs.node("ProxyConfig").getBoolean("useProxyAut",true);
                if(useProxyAut){
                    String user = prefs.node("ProxyConfig").get("User",null);
                    String pass = prefs.node("ProxyConfig").get("Pass",null);
                }
            }
        }

        return proxy;
    }

    public static Preferences getPrefIniEpub(){
        Preferences prefs = null;
        File iniFile = new File(Main.getLocation() + "ePLibrary.ini");

        if(!iniFile.exists()) {
            iniFile = createDefaultIni();
        }

        try {
            Ini ini = new Ini(iniFile);
            prefs = new IniPreferences(ini);
        } catch (IOException e) {
            e.printStackTrace();
            prefs=null;
        }

        return prefs;
    }

    public static File createDefaultIni(){

        File iniEpub = saveIniProxyConfig(false,"192.168.13.129",9666,false,"admin","abcde");

        return iniEpub;
    }

    public static File saveIniProxyConfig(boolean useProxy,String host,int port,boolean useProxyAut,String user,String pass) {
        File iniEpub = new File(Main.getLocation() + "ePLibrary.ini");
        try{
            iniEpub.createNewFile();
            Wini ini = new Wini(iniEpub);

            ini.put("ProxyConfig", "useProxy", useProxy);
            ini.put("ProxyConfig", "Host", host);
            ini.put("ProxyConfig", "Port", port);
            ini.put("ProxyConfig", "useProxyAut", useProxyAut);
            ini.put("ProxyConfig", "User", user);
            ini.put("ProxyConfig", "Pass", pass);
            ini.store();
        }catch(Exception e){
            System.err.println(e.getMessage());
            iniEpub = null;
        }

        return iniEpub;
    }

    public static URLConnection getConnection(URL url) throws IOException {
        URLConnection connection = null;

        Proxy proxy = getProxy();
        if(proxy==null) {
            connection = (HttpURLConnection) (url.openConnection());
        }else{
            connection = (HttpURLConnection) (url.openConnection(proxy));
        }

        return connection;
    }
}
