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

import daoSqLite.GetDatas;
import daoSqLite.InsertDatas;
import modelos.Libro;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;


public class Csv {
    /**
     * Abre el csv, lo lee linea a linea e inserta los datos en la base
     * en forma de transacción.
     *
     * @param csvFile Archivo csv con los datos completos.
     * @throws IOException
     */
    public void importCSV(File csvFile) throws IOException {
        LineIterator it = FileUtils.lineIterator(csvFile, "utf-8");
        int count = 0;
        InsertDatas idatas = new InsertDatas();
        GetDatas getDatas = new GetDatas();
        String line;
        String[] items;
        try {
            idatas.crearTabla();
            HashMap<Integer, Double> librosActuales = getDatas.getEPL_ID();
            it.nextLine();
            idatas.conectar();

            while (it.hasNext()) {
                line = it.nextLine();
                items = line.replaceAll("\",", "").split("\"");
                Libro libro = generarLibro(items);
                try {
                    idatas.insertarLibros(libro);
                    count++;
                } catch (Exception e) {
                    //nada
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                idatas.rollBack();
            } catch (SQLException e1) {
                System.err.println("Error al insertar datos");
            }
        } finally {

            LineIterator.closeQuietly(it);
            try {
                idatas.desconectar();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Registros: " + count + " integrados");
    }

    /**
     * Lee el primer caracter de cada linea y devuelve el número de lineas del documento.
     *
     * @param csvFile
     * @return
     * @throws IOException
     */
    private int calcularRegistros(File csvFile) throws IOException {
        int lineCount = 0;
        try (InputStream in = new BufferedInputStream(new FileInputStream(csvFile))) {
            byte[] buf = new byte[4096 * 16];
            int c;
            while ((c = in.read(buf)) > 0) {
                for (int i = 0; i < c; i++) {
                    if (buf[i] == '\n') lineCount++;
                }
            }
        }
        return lineCount;
    }

    /**
     * Obtiene un libro a partir de una linea del csv.
     *
     * @param items Array de parametros del csv.
     * @return Libro con los datos cargados.
     */
    private Libro generarLibro(String[] items) {
        Libro lib = new Libro();
        try {
            if (!items[1].equalsIgnoreCase("")) lib.setEpl_id(Integer.parseInt(items[1]));
            lib.setTitulo(items[2]);
            lib.setAutor(items[3]);
            lib.setGeneros(items[4]);
            lib.setColeccion(items[5]);
            if (!items[6].equalsIgnoreCase("")) lib.setVolumen(Double.parseDouble(items[6]));
            if (!items[7].equalsIgnoreCase("")) lib.setFecha_publi(Integer.parseInt(items[7]));
            if (!items[8].equalsIgnoreCase("")) lib.setSinopsis(items[8]);
            if (!items[9].equalsIgnoreCase("")) lib.setPaginas(Integer.parseInt(items[9]));
            if (!items[10].equalsIgnoreCase("")) lib.setRevision(Double.parseDouble(items[10]));
            lib.setIdioma(items[11]);
            lib.setPublicado(items[12]);
            lib.setEstado(items[13]);
            if (!items[14].equalsIgnoreCase("")) lib.setValoracion(Double.parseDouble(items[14]));
            if (!items[15].equalsIgnoreCase("")) lib.setN_votos(Integer.parseInt(items[15]));
            try {
                lib.setEnlaces("magnet:?xt=urn:btih:" + items[16]);
            } catch (Exception e) {
            }
        } catch (Exception e) {
            System.err.println(lib.toString());
            e.printStackTrace();
        }
        return lib;
    }
}
