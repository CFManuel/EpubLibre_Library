package daoSqLite;

import modelos.Libro;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by david on 02/07/2017.
 */
public class GetLibros extends ConnectorHelper {
    /**
     * Devuelve un listado de libros, según los parametros de busqueda.
     *
     * @param busqueda Nombre parcial o total del libro o autor deseado.
     * @return ArrayList<Libro> con los libros coincidentes.
     * @throws ClassNotFoundException Driver no encontrado.
     * @throws IllegalAccessException Faltan permisos de escritura.
     * @throws InstantiationException Error al instanciar el driver.
     * @throws SQLException           Error al generar la conexión.
     */
    public ArrayList<Libro> getLibros(String busqueda) throws ClassNotFoundException, SQLException {
        String sql = "SELECT * FROM LIBROS WHERE lower(titulo) LIKE lower(?) OR lower(autor) like lower(?)";
        ArrayList<Libro> libros = new ArrayList<>();
        busqueda = String.format("%%%s%%", busqueda);
        super.conectar();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, busqueda);
        ps.setString(2, busqueda);
        ResultSet rst = ps.executeQuery();
        while (rst.next()) {
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
            libros.add(libro);
        }
        super.desconectar();

        return libros;
    }
}
