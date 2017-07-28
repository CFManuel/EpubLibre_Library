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

import modelos.CommonStrings;
import modelos.Libro;
import vista.controllers.MainTableViewController;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class GetDatas extends ConnectorHelper implements CommonStrings {

    public static void getIdiomas() {
        String sql = "select distinct idioma from libros ORDER BY idioma ASC ";
        GetDatas getDatas = new GetDatas();
        try {
            getDatas.conectar();
            Statement st = getDatas.conn.createStatement();
            ResultSet rst = st.executeQuery(sql);
            while (rst.next()) MainTableViewController.idiomas.add(rst.getString("idioma"));
        } catch (Exception e) {
            //No hay datos.
        } finally {
            try {
                getDatas.desconectar();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * @param busqueda Palabra que se desea buscar en la db.
     * @return Devuelve los libros encontrados.
     * @throws SQLException           Error al generar la conexión.
     * @throws ClassNotFoundException Driver no encontrado.
     */
    public ArrayList<Libro> getLibros(String[] busqueda)
            throws SQLException, ClassNotFoundException {
        String sql;
        ResultSet rst = null;
        super.conectar();
        PreparedStatement ps = null;
        if (busqueda[2].equalsIgnoreCase("")) {
            //Se realiza una busqueda a parte cuando el usuario no ha seleccionado una colección.
            rst = getLibrosSinColeccion(ps, busqueda);
        } else {
            sql = "SELECT * FROM libros WHERE "
                    //titulo
                    + "(lower(titulo) LIKE lower(?) OR lower(titsense) LIKE lower(?) OR "
                    //Autor
                    + "lower(autor) LIKE lower(?)  OR lower(autsense) LIKE lower(?) OR "
                    //Colección
                    + "lower(coleccion) LIKE lower(?) OR lower(colsense) LIKE lower(?) OR "
                    //Género
                    + "lower(generos) LIKE lower(?) OR lower(gensense) LIKE lower(?) OR "
                    //sinopsis
                    + "lower(sinopsis) LIKE lower(?)) "
                    //Idioma
                    + "AND idioma like ? "
                    + "ORDER BY revision DESC ";
            ps = conn.prepareStatement(sql);
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
            rst = ps.executeQuery();
        }

        HashMap<Integer, Libro> libros = procesarConsultaLibros(rst);
        super.desconectar();
        return new ArrayList<>(libros.values());
    }

    /**
     * Se realiza la solicitud de libros, pero sin buscar en las colecciones. Esto evita que se muestren resultados con colecciones vacias.
     *
     * @param ps       PreparedStatement sobre el que se genera la consulta.
     * @param busqueda String[] con los datos de la consulta.
     * @return ResultSet con los datos de la Query.
     * @throws SQLException Error con el cursor.
     */
    private ResultSet getLibrosSinColeccion(PreparedStatement ps, String[] busqueda) throws SQLException {
        String sql = "SELECT * FROM libros WHERE "
                //titulo
                + "(lower(titulo) LIKE lower(?) OR lower(titsense) LIKE lower(?) OR "
                //Autor
                + "lower(autor) LIKE lower(?)  OR lower(autsense) LIKE lower(?) OR "
                //Género
                + "lower(generos) LIKE lower(?) OR lower(gensense) LIKE lower(?) OR "
                //sinopsis
                + "lower(sinopsis) LIKE lower(?)) "
                //Idioma
                + "AND idioma like ? "
                + "ORDER BY revision DESC ";
        ps = conn.prepareStatement(sql);
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

    public static void getLibrosMultiRev() {
        String sql = "select * from libros where epl_id in (select epl_id from libros GROUP BY epl_id HAVING count(*) > 1) ORDER BY revision DESC";
        GetDatas getDatas = new GetDatas();
        try {
            getDatas.conectar();
            Statement st = getDatas.conn.createStatement();
            ResultSet rst = st.executeQuery(sql);
            HashMap<Integer, Libro> libros = procesarConsultaLibros(rst);
            MainTableViewController.libros.clear();
            MainTableViewController.libros.addAll(new ArrayList<>(libros.values()));
            rst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                getDatas.desconectar();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Obtiene los datos de un libro en concreto a partir de su id y su versión.
     *
     * @param epl_id   ID del libro que se desea mostrar.
     * @param revision Revisión del libro.
     * @return Libro con los valores de la búsqueda.
     * @throws SQLException           Error en la db.
     * @throws ClassNotFoundException Error en driver.
     */
    public Libro getLibro(int epl_id, double revision) throws SQLException, ClassNotFoundException {
        String sql =
                "SELECT * FROM LIBROS WHERE epl_id = ? AND  revision = ? ORDER BY revision DESC ";
        super.conectar();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, epl_id);
        ps.setDouble(2, revision);
        ResultSet rst = ps.executeQuery();
        HashMap<Integer, Libro> libros = procesarConsultaLibros(rst);
        super.desconectar();
        return libros.get(epl_id);
    }

    /**
     * Genera libros con varias revisiones.
     *
     * @param rst ResultSet a procesar.
     * @return HashMap con los resultados de la busqueda
     * @throws SQLException Error en la db.
     */
    private static HashMap<Integer, Libro> procesarConsultaLibros(ResultSet rst) throws SQLException {
        HashMap<Integer, Libro> libros = new HashMap<>();
        while (rst.next()) {
            Libro libro = crearLibro(rst);
            if (libros.containsKey(libro.getEpl_id())) {
                libros.get(libro.getEpl_id()).addRevArray(libro.getRevision());
            } else {
                libros.put(libro.getEpl_id(), libro);
            }
        }
        return libros;
    }

    /**
     * Crea un libro a partir de un resultSet.
     *
     * @param rst ResultSet con los datos del libro.
     * @return Libro con los campos rellenos.
     * @throws SQLException Error en la db.
     */
    private static Libro crearLibro(ResultSet rst) throws SQLException {
        return new Libro()
                .setEpl_id(rst.getInt("epl_id"))
                .setTitulo(rst.getString("titulo"))
                .setAutor(rst.getString("autor"))
                .setGeneros(rst.getString("generos"))
                .setColeccion(rst.getString("coleccion"))
                .setVolumen(rst.getDouble("volumen"))
                .setFecha_publi(rst.getInt("fecha_publi"))
                .setSinopsis(rst.getString("sinopsis"))
                .setPaginas(rst.getInt("paginas"))
                .setRevision(rst.getDouble("revision"))
                .addRevArray(rst.getDouble("revision"))
                .setIdioma(rst.getString("idioma"))
                .setPublicado(rst.getString("publicado"))
                .setEstado(rst.getString("estado"))
                .setValoracion(rst.getDouble("valoracion"))
                .setN_votos(rst.getInt("n_votos"))
                .setEnlaces(rst.getString("enlaces"))
                .setImgURI(rst.getString("imgdir"));
    }

    /**
     * Obtiene la fecha de la última actualización.
     *
     * @return String con la fecha almacenada.
     * @throws SQLException           Error en la db.
     * @throws ClassNotFoundException Error en el driver.
     */
    public String getLastUpdate() throws SQLException, ClassNotFoundException {
        String sql =
                String.format(
                        "SELECT datastring FROM CONFIG WHERE upper(text_id) = upper('%s')",
                        LAST_UPDATE);
        String fecha = "";
        super.conectar();
        Statement st = conn.createStatement();
        ResultSet rst = st.executeQuery(sql);
        while (rst.next()) {
            fecha = rst.getString("datastring");
        }
        super.desconectar();
        return fecha;
    }

    /**
     * Obtiene los datos de configuración de un campo específico.
     *
     * @param id Campo sobre el que se desea obtener su valor.
     * @return String con el valor del campo.
     * @throws SQLException           Error en la db.
     * @throws ClassNotFoundException Error en el driver.
     */
    public String getConfig(String id) throws SQLException, ClassNotFoundException {
        String sql = "SELECT DATASTRING FROM config WHERE upper(text_id) = upper(?)";
        String valor = "";
        super.conectar();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, id);
        ResultSet rst = ps.executeQuery();
        while (rst.next()) {
            valor = rst.getString("datastring");
        }
        super.desconectar();
        return valor;
    }

    /**
     * Obtener un count de la tabla libros de la db.
     *
     * @return Devuelve el número de libros existentes en la db.
     * @throws SQLException           Error en la db.
     * @throws ClassNotFoundException Error en el driver.
     */
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

    /**
     * Devuelve la lista de todos los libros existentes y sus revisiones.
     *
     * @return Devuelve la lista de libras con las versiones existentes de cada uno.
     * @throws SQLException           Error en la db.
     * @throws ClassNotFoundException Error en driver.
     */
    public HashMap<Integer, Double> getEPL_ID() throws SQLException, ClassNotFoundException {
        HashMap<Integer, Double> ePL_IDs = new HashMap<>();
        String sql = "SELECT epl_id, revision FROM LIBROS";
        super.conectar();
        Statement st = conn.createStatement();
        ResultSet rst = st.executeQuery(sql);
        while (rst.next()) {
            ePL_IDs.put(rst.getInt("epl_id"), rst.getDouble("revision"));
        }
        super.desconectar();
        return ePL_IDs;
    }
}
