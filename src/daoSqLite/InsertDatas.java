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
        String sql = "INSERT OR REPLACE INTO libros(epl_id, titulo, titsense, autor, autsense, generos, gensense, coleccion, colsense," +
                " volumen, fecha_publi, sinopsis, paginas, revision, idioma, idisense, publicado, estado, valoracion, " +
                "n_votos, enlaces, imgDir)" +
                " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String titsense = normalize(libro.getTitulo());
        String autsense = normalize(libro.getAutor());
        String gensense = normalize(libro.getGeneros());
        String colsense = normalize(libro.getColeccion());
        String idisense = normalize(libro.getIdioma());
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, libro.getEpl_id());
            ps.setString(2, libro.getTitulo());
            ps.setString(3, titsense);
            ps.setString(4, libro.getAutor());
            ps.setString(5, autsense);
            ps.setString(6, libro.getGeneros());
            ps.setString(7, gensense);
            ps.setString(8, libro.getColeccion());
            ps.setString(9, colsense);
            ps.setDouble(10, libro.getVolumen());
            ps.setInt(11, libro.getFecha_publi());
            ps.setString(12, libro.getSinopsis());
            ps.setInt(13, libro.getPaginas());
            ps.setDouble(14, libro.getRevision());
            ps.setString(15, libro.getIdioma());
            ps.setString(16, idisense);
            ps.setString(17, libro.getPublicado());
            ps.setString(18, libro.getEstado());
            ps.setDouble(19, libro.getValoracion());
            ps.setInt(20, libro.getN_votos());
            ps.setString(21, libro.getEnlaces());
            ps.setString(22, libro.getImgURI());
            ps.execute();
        } catch (SQLException e) {
            if (e.getErrorCode() != 19) e.printStackTrace();
        }
    }

    public void insertarImgLink(Libro libro) {
        String sql = "UPDATE libros SET imgDir = ? WHERE  epl_id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, libro.getImgURI());
            ps.setInt(2, libro.getEpl_id());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
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

    public void insertConfig(String id, String value) throws SQLException, ClassNotFoundException {
        String sql = "REPLACE INTO CONFIG(TEXT_ID, DATASTRING) VALUES(?,?)";

        super.conectar();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, id);
        ps.setString(2, value);
        ps.execute();
        super.desconectar();
    }

    public void deleteConfig(String id) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM config WHERE TEXT_ID = ?";
        super.conectar();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, id);
        ps.execute();
        super.desconectar();
    }

    private String normalize(String texto) {
        return texto.replaceAll(PATTERN_A, "a")
                .replaceAll(PATTERN_E, "e")
                .replaceAll(PATTERN_I, "I")
                .replaceAll(PATTERN_O, "O")
                .replaceAll(PATTERN_U, "u");
    }

}
