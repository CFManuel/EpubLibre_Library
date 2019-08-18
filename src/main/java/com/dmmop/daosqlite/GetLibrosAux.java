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

package com.dmmop.daosqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

final class GetLibrosAux {
    public static ResultSet getLibros(
            Connection conn, String[] busqueda, Boolean mostrarLeidos, Boolean collection)
            throws SQLException {
//        System.out.println(Arrays.toString(busqueda) + " - " + mostrarLeidos + " + " + collection);
        String sql_NColeccion_NLeidos =
                "SELECT * FROM libros LEFT JOIN readed ON libros.epl_id = readed.epl_id  WHERE "
                        // titulo
                        + "(lower(titulo) LIKE lower(?) OR lower(titsense) LIKE lower(?) OR "
                        // Autor
                        + "lower(autor) LIKE lower(?)  OR lower(autsense) LIKE lower(?) OR "
                        // Género
                        + "lower(generos) LIKE lower(?) OR lower(gensense) LIKE lower(?) OR "
                        // sinopsis
                        + "lower(sinopsis) LIKE lower(?)) "
                        // Idioma
                        + "AND idioma like ? "
                        + "AND readed.epl_id IS NULL "
                        + "ORDER BY revision DESC ";
        String sql_NColeccion_SLeidos =
                "SELECT * FROM libros WHERE "
                        // titulo
                        + "(lower(titulo) LIKE lower(?) OR lower(titsense) LIKE lower(?) OR "
                        // Autor
                        + "lower(autor) LIKE lower(?)  OR lower(autsense) LIKE lower(?) OR "
                        // Género
                        + "lower(generos) LIKE lower(?) OR lower(gensense) LIKE lower(?) OR "
                        // sinopsis
                        + "lower(sinopsis) LIKE lower(?)) "
                        // Idioma
                        + "AND idioma like ? "
                        + "ORDER BY revision DESC ";
        String sql_SColeccion_NLeidos =
                "SELECT * FROM libros LEFT JOIN readed ON libros.epl_id = readed.epl_id WHERE "
                        // titulo
                        + "(lower(titulo) LIKE lower(?) OR lower(titsense) LIKE lower(?) OR "
                        // Autor
                        + "lower(autor) LIKE lower(?)  OR lower(autsense) LIKE lower(?) OR "
                        // Colección
                        + "lower(coleccion) LIKE lower(?) OR lower(colsense) LIKE lower(?) OR "
                        // Género
                        + "lower(generos) LIKE lower(?) OR lower(gensense) LIKE lower(?) OR "
                        // sinopsis
                        + "lower(sinopsis) LIKE lower(?)) "
                        // Idioma
                        + "AND idioma LIKE ? "
                        + "AND readed.epl_id IS NULL "
                        + "ORDER BY revision DESC ";
        String sql_SColeccion_SLeidos =
                "SELECT * FROM libros WHERE "
                        // titulo
                        + "(lower(titulo) LIKE lower(?) OR lower(titsense) LIKE lower(?) OR "
                        // Autor
                        + "lower(autor) LIKE lower(?)  OR lower(autsense) LIKE lower(?) OR "
                        // Colección
                        + "lower(coleccion) LIKE lower(?) OR lower(colsense) LIKE lower(?) OR "
                        // Género
                        + "lower(generos) LIKE lower(?) OR lower(gensense) LIKE lower(?) OR "
                        // sinopsis
                        + "lower(sinopsis) LIKE lower(?)) "
                        // Idioma
                        + "AND idioma LIKE ? "
                        + "ORDER BY revision DESC ";
        ResultSet resultSet = null;
        PreparedStatement ps = null;
        if (collection) {
            resultSet =
                    (mostrarLeidos)
                            ? getLibrosConColeccion(conn, busqueda, sql_SColeccion_SLeidos)
                            : getLibrosConColeccion(conn, busqueda, sql_SColeccion_NLeidos);
        } else {
            resultSet =
                    (mostrarLeidos)
                            ? getLibrosSinColeccion(conn, busqueda, sql_NColeccion_SLeidos)
                            : getLibrosSinColeccion(conn, busqueda, sql_NColeccion_NLeidos);
        }
        return resultSet;
    }

    /**
     * Se realiza la solicitud de libros, pero sin buscar en las colecciones. Esto evita que se
     * muestren resultados con colecciones vacias.
     *
     * @param busqueda String[] con los datos de la consulta.
     * @return ResultSet con los datos de la Query.
     * @throws SQLException Error con el cursor.
     */
    private static ResultSet getLibrosSinColeccion(Connection conn, String[] busqueda, String sql)
            throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, busqueda[0]);
        ps.setString(2, busqueda[0]);
        ps.setString(3, busqueda[1]);
        ps.setString(4, busqueda[1]);
        ps.setString(5, busqueda[3]);
        ps.setString(6, busqueda[3]);
        ps.setString(7, busqueda[4]);
        ps.setString(8, busqueda[5]);
        return ps.executeQuery();
    }

    private static ResultSet getLibrosConColeccion(Connection conn, String[] busqueda, String sql)
            throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, busqueda[0]);
        ps.setString(2, busqueda[0]);
        ps.setString(3, busqueda[1]);
        ps.setString(4, busqueda[1]);
        ps.setString(5, busqueda[2]);
        ps.setString(6, busqueda[2]);
        ps.setString(7, busqueda[3]);
        ps.setString(8, busqueda[3]);
        ps.setString(9, busqueda[4]);
        ps.setString(10, busqueda[5]);
        return ps.executeQuery();
    }
}
