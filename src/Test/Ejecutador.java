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

package Test;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import vista.Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;

public class Ejecutador {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        String file = "diagrama.uml";
        MessageDigest md = MessageDigest.getInstance("MD5");
        String digest = getDigest(new FileInputStream(file), md, 2048);

        System.out.println("MD5 Digest: " + digest);

    }

    public static String getDigest(InputStream is, MessageDigest md, int byteArraySize)
            throws NoSuchAlgorithmException, IOException {

        md.reset();
        byte[] bytes = new byte[byteArraySize];
        int numBytes;
        while ((numBytes = is.read(bytes)) != -1) {
            md.update(bytes, 0, numBytes);
        }
        byte[] digest = md.digest();
        String result = new String(HexBin.encode(digest));
        return result;

        /*       ArrayList<Lib> libros = getBookList();
        libros.forEach(lib -> {
            if (lib.getColeccion().equalsIgnoreCase(""))
                System.out.println(String.format("[url=https://www.epublibre.org/libro/detalle/%d] %d - %s - %s[/url]", lib.getId(), lib.getId(), lib.getTitulo(), lib.getAutor()));
            else {
                System.out.println(String.format("[url=https://www.epublibre.org/libro/detalle/%d] %d - %s - %s -%s, %d[/url]",
                        lib.getId(), lib.getId(), lib.getTitulo(), lib.getAutor(), lib.getColeccion(), lib.getVolumen()));
            }
        });*/

    }


    public static ArrayList<Lib> getBookList() {

        ArrayList<Lib> libros = new ArrayList<>();
        Connection conn = null;
        try {
            String appLocation = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent() + "/epl/";
            Class.forName("org.sqlite.JDBC");

            conn = DriverManager.getConnection("jdbc:sqlite:" + appLocation + "epubLibre.db");
            conn.setAutoCommit(false);

            String sql = "SELECT * FROM libros WHERE lower(imgDir) like '%photobucket%' and lower(imgDir) not LIKE  '%i50%'";
            Statement st = conn.createStatement();
            ResultSet rst = st.executeQuery(sql);
            while (rst.next()) {
                libros.add(new Lib()
                        .setId(rst.getInt("epl_id"))
                        .setTitulo(rst.getString("titulo"))
                        .setAutor(rst.getString("autor"))
                        .setColeccion(rst.getString("coleccion"))
                        .setVolumen(rst.getInt("volumen")));
            }
            st.close();
            rst.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return libros;
    }

}
