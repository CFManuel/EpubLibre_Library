package daoSqLite;

import modelos.Libro;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertDatas extends ConnectorHelper {
    public void insertarLibros(Libro libro) throws SQLException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        String sql = "INSERT INTO libros values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, libro.getEpl_id());
            ps.setString(2, libro.getTitulo());
            ps.setString(3,libro.getAutor());
            ps.setString(4, libro.getGeneros());
            ps.setString(5, libro.getColeccion());
            ps.setDouble(6, libro.getVolumen());
            ps.setInt(7, libro.getFecha_publi());
            ps.setString(8,libro.getSinopsis());
            ps.setInt(9, libro.getPaginas());
            ps.setDouble(10, libro.getRevision());
            ps.setString(11, libro.getIdioma());
            ps.setString(12, libro.getPublicado());
            ps.setString(13, libro.getEstado());
            ps.setDouble(14, libro.getValoracion());
            ps.setInt(15, libro.getN_votos());
            ps.setString(16, libro.getEnlaces());
            ps.execute();
        } catch (SQLException e) {
            if(e.getErrorCode() != 19) e.printStackTrace();

        }


    }
}
