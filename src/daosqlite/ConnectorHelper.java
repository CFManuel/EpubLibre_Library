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

package daosqlite;

import vista.Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase con las utilidades de conexión a base de datos
 */
public class ConnectorHelper {
    Connection conn = null;

    /**
     * Realiza la conexión con la base de datos, autoCommit off.
     *
     * @throws ClassNotFoundException Driver no encontrado.
     * @throws SQLException Error al generar la conexión.
     */
    public void conectar() throws ClassNotFoundException, SQLException {
        Connection conn;
        Class.forName("org.sqlite.JDBC");

        conn = DriverManager.getConnection("jdbc:sqlite:" + Main.getLocation() + "epubLibre.db");
        conn.setAutoCommit(false);
        this.conn = conn;
    }

    /**
     * Crea las tablas necesarias para trabajar.
     */
    public void crearTabla() {
        try {
            conectar();
            String tablaLibros =
                    "CREATE TABLE IF NOT EXISTS libros (\n"
                            + "  epl_id      NUMERIC COLLATE BINARY,\n"
                            + "  titulo      TEXT COLLATE BINARY,\n"
                            + "  titsense    TEXT COLLATE BINARY,\n"
                            + "  autor       TEXT COLLATE BINARY,\n"
                            + "  autsense    TEXT COLLATE BINARY,\n"
                            + "  generos     TEXT COLLATE BINARY,\n"
                            + "  gensense    TEXT COLLATE BINARY,\n"
                            + "  coleccion   TEXT COLLATE BINARY,\n"
                            + "  colsense    TEXT COLLATE BINARY,\n"
                            + "  volumen     NUMERIC COLLATE BINARY,\n"
                            + "  fecha_publi NUMERIC COLLATE BINARY,\n"
                            + "  sinopsis    TEXT COLLATE BINARY,\n"
                            + "  paginas     NUMERIC COLLATE BINARY,\n"
                            + "  revision    REAL COLLATE BINARY,\n"
                            + "  idioma      TEXT COLLATE BINARY,\n"
                            + "  idisense    TEXT COLLATE BINARY,\n"
                            + "  publicado   TEXT COLLATE BINARY,\n"
                            + "  estado      TEXT COLLATE BINARY,\n"
                            + "  valoracion  REAL COLLATE BINARY,\n"
                            + "  n_votos     NUMERIC COLLATE BINARY,\n"
                            + "  enlaces     TEXT COLLATE BINARY,\n"
                            + "  imgDir      TEXT COLLATE BINARY,\n"
                            + "  CONSTRAINT lib_titAut PRIMARY KEY (epl_id, revision))";

            String tablaConfig =
                    "CREATE TABLE IF NOT EXISTS CONFIG (\n"
                            + "  TEXT_ID TEXT PRIMARY KEY ,\n"
                            + "  DATASTRING TEXT)";
            String tablaLeidos =
                    "CREATE TABLE IF NOT EXISTS readed(epl_id NUMERIC COLLATE BINARY REFERENCES LIBROS(EPL_ID))";
            Statement st = conn.createStatement();
            st.execute(tablaLeidos);
            st.execute(tablaLibros);
            st.execute(tablaConfig);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {

                desconectar();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Cierra la base de datos y realiza el commit de la transacción.
     *
     * @throws SQLException Error al realizar el commit.
     */
    public void desconectar() throws SQLException {
        if (conn != null) {
            conn.commit();
            conn.close();
        }
    }

    /**
     * Devuelve la db al estado del último commit.
     *
     * @throws SQLException Error al realizar el rollback.
     */
    public void rollBack() throws SQLException {
        conn.rollback();
    }
}
