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

package daoSqLite;

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
     * @throws IllegalAccessException Faltan permisos de escritura.
     * @throws InstantiationException Error al instanciar el driver.
     * @throws SQLException           Error al generar la conexión.
     */
    public void conectar() throws ClassNotFoundException, SQLException {
        Connection conn = null;
        String driver = "org.my";
        Class.forName("org.sqlite.JDBC");

        conn = DriverManager.getConnection("jdbc:sqlite:epl/epubLibre.db");
        conn.setAutoCommit(false);
        this.conn = conn;

    }
/*
    *//**
     * Elimina todos los libros de la db para evitar duplicados.
     *
     * @throws SQLException Error al borrar la tabla.
     *//*
    public void limpiarTabla() throws SQLException {
        String sql = "DELETE FROM libros";
        Statement st = conn.createStatement();
        st.execute(sql);
    }*/

    /**
     * Crea las tablas necesarias para trabajar.
     */
    public void crearTabla() {
        try {
            conectar();
            String tablaLibros = "CREATE TABLE IF NOT EXISTS libros (\n" +
                    "  epl_id      NUMERIC,\n" +
                    "  titulo      TEXT,\n" +
                    "  titsense    TEXT,\n" +
                    "  autor       TEXT,\n" +
                    "  autsense    TEXT,\n" +
                    "  generos     TEXT,\n" +
                    "  gensense    TEXT,\n" +
                    "  coleccion   TEXT,\n" +
                    "  colsense    TEXT,\n" +
                    "  volumen     NUMERIC,\n" +
                    "  fecha_publi NUMERIC,\n" +
                    "  sinopsis    TEXT,\n" +
                    "  paginas     NUMERIC,\n" +
                    "  revision    REAL,\n" +
                    "  idioma      TEXT,\n" +
                    "  idisense    TEXT,\n" +
                    "  publicado   TEXT,\n" +
                    "  estado      TEXT,\n" +
                    "  valoracion  REAL,\n" +
                    "  n_votos     NUMERIC,\n" +
                    "  enlaces     TEXT,\n" +
                    "  imgDir      TEXT,\n" +
                    "  CONSTRAINT lib_titAut PRIMARY KEY (epl_id)\n" +
                    ")";
            String tablaConfig = "CREATE TABLE IF NOT EXISTS CONFIG (\n" +
                    "  TEXT_ID TEXT PRIMARY KEY ,\n" +
                    "  DATASTRING TEXT)";
            Statement st = conn.createStatement();
            st.execute(tablaLibros);
            st.execute(tablaConfig);

        } catch (Exception e) {
            //System.err.println(e.getCause());
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
