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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by david on 02/07/2017.
 */
public class GetDatas extends ConnectorHelper implements CommonStrings {
    public static int TITLE = 1;
    public static int AUTHOR = 2;
    public static int GENDER = 3;
    public static int LANGUAGE = 4;

    /**
     * @param busqueda Palabra que se desea buscar en la db.
     * @param tipo     Campo sobre el que se desea realizar la busqueda.
     * @return
     * @throws SQLException           Error al generar la conexión.
     * @throws ClassNotFoundException Driver no encontrado.
     */
    public ArrayList<Libro> getLibros(String busqueda, int tipo) throws SQLException, ClassNotFoundException {
        String sql = "";
        if (tipo == TITLE) {
            sql = "SELECT * FROM LIBROS WHERE lower(titulo) LIKE lower(?)";
        } else if (tipo == AUTHOR) {
            sql = "SELECT * FROM LIBROS WHERE lower(autor) LIKE lower(?)";
        } else if (tipo == GENDER) {
            sql = "SELECT * FROM LIBROS WHERE lower(generos) LIKE lower(?)";
        } else if (tipo == LANGUAGE) {
            sql = "SELECT * FROM LIBROS WHERE lower(idioma) LIKE lower(?)";
        }
        ArrayList<Libro> libros = new ArrayList<>();
        busqueda = String.format("%%%s%%", busqueda);
        super.conectar();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, busqueda);
        ResultSet rst = ps.executeQuery();
        while (rst.next()) {
            libros.add(crearLibro(rst));
        }
        super.desconectar();
        return libros;
    }

    private Libro crearLibro(ResultSet rst) throws SQLException {
        Libro libro = new Libro()
                .setTitulo(rst.getString("titulo"))
                .setAutor(rst.getString("autor"))
                .setGeneros(rst.getString("generos"))
                .setColeccion(rst.getString("coleccion"))
                .setVolumen(rst.getDouble("volumen"))
                .setFecha_publi(rst.getInt("fecha_publi"))
                .setSinopsis(rst.getString("sinopsis"))
                .setPaginas(rst.getInt("paginas"))
                .setRevision(rst.getDouble("revision"))
                .setIdioma(rst.getString("idioma"))
                .setPublicado(rst.getString("publicado"))
                .setEstado(rst.getString("estado"))
                .setValoracion(rst.getDouble("valoracion"))
                .setN_votos(rst.getInt("n_votos"))
                .setEnlaces(rst.getString("enlaces"))
                .setImgURI(rst.getString("imgdir"));
        return libro;
    }

    public Date getLastUpdate() throws SQLException, ClassNotFoundException {
        String sql = String.format("SELECT TEXT_ID FROM CONFIG WHERE upper(text_id) = upper(%s)", LAST_UPDATE);
        super.conectar();
        Date date = new Date();
        date.getTime();
        super.desconectar();
        return null;
    }

    public int countBooks() throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) many FROM LIBROS";
        int libros = 0;
        super.conectar();
        Statement s = conn.createStatement();
        ResultSet rst = s.executeQuery(sql);
        while (rst.next()) {
            libros = rst.getInt("many");
        }
        super.desconectar();
        return libros;
    }
}
