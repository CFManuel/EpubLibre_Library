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

package com.dmmop.parser;

import com.dmmop.controller.UpdateTask;
import com.dmmop.daosqlite.InsertDatas;
import com.dmmop.modelos.Libro;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class Csv {
    /**
     * Abre el csv, lo lee linea a linea e inserta los datos en la base en forma de transacción.
     *
     * @param csvFile Archivo csv con los datos completos.
     * @throws IOException Archivo no encontrado.
     */
    public int importCSV(File csvFile, UpdateTask importar) throws IOException {
        LineIterator it = FileUtils.lineIterator(csvFile, "utf-8");
        InsertDatas idatas = new InsertDatas();
        String line;
        String[] items;
        int numLibrosImportados =0;
        try {
            idatas.crearTabla();
            it.nextLine();
            idatas.conectar();
            while (it.hasNext()) {
                line = it.nextLine();
                items = line.replaceAll("\",", "").split("\"");

                Libro libro = generarLibro(items);
                try {
                    idatas.insertarLibros(libro);
                    if(importar!=null) {
                        importar.updateMessage("Importando CSV... " + numLibrosImportados + " Libros");
                    }
                    numLibrosImportados++;
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

        return numLibrosImportados--;
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
                lib.setEnlaces("magnet:?xt=urn:btih:" + items[16].split(",")[0]);
                lib.setImgURI(items[17]);
            } catch (Exception ignored) {
            }
        } catch (Exception e) {
            System.err.println(lib.toString());
            e.printStackTrace();
        }
        return lib;
    }
}
