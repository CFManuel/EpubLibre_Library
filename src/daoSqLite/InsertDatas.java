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

import modelos.CommonStrings;
import modelos.Libro;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertDatas extends ConnectorHelper implements CommonStrings {
    public void insertarLibros(Libro libro) {
        String sql = "INSERT INTO libros(epl_id, titulo, autor, generos, coleccion, volumen, fecha_publi, " +
                " sinopsis, paginas, revision, idioma, publicado, estado, valoracion," +
                " n_votos, enlaces, imgdir)" +
                " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, libro.getEpl_id());
            ps.setString(2, libro.getTitulo());
            ps.setString(3, libro.getAutor());
            ps.setString(4, libro.getGeneros());
            ps.setString(5, libro.getColeccion());
            ps.setDouble(6, libro.getVolumen());
            ps.setInt(7, libro.getFecha_publi());
            ps.setString(8, libro.getSinopsis());
            ps.setInt(9, libro.getPaginas());
            ps.setDouble(10, libro.getRevision());
            ps.setString(11, libro.getIdioma());
            ps.setString(12, libro.getPublicado());
            ps.setString(13, libro.getEstado());
            ps.setDouble(14, libro.getValoracion());
            ps.setInt(15, libro.getN_votos());
            ps.setString(16, libro.getEnlaces());
            ps.setString(17, libro.getImgURI());
            ps.execute();
        } catch (SQLException e) {
            if (e.getErrorCode() != 19) e.printStackTrace();

        }


    }

    /**
     * Actualiza la fecha de la última actualización de la db.
     *
     * @throws ClassNotFoundException Driver no encontrado.
     * @throws IllegalAccessException Faltan permisos de escritura.
     * @throws InstantiationException Error al instanciar el driver.
     * @throws SQLException           Error al generar la conexión.
     */
    public void updateDate() throws ClassNotFoundException, SQLException {
        String sql = "REPLACE INTO CONFIG(TEXT_ID, DATASTRING) VALUES(?,?)";
        DateTime now = new DateTime();
        DateTimeFormatter format = DateTimeFormat.forPattern("dd/MM/yyyy");

        super.conectar();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, LAST_UPDATE);
        ps.setString(2, format.print(now));
        ps.execute();
        super.desconectar();
    }
}
