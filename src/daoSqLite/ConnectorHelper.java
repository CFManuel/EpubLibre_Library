package daoSqLite;

import org.sqlite.SQLiteConfig;

import java.sql.*;

/**
 * Created by david on 30/06/2017.
 */
public class ConnectorHelper {
    Connection conn = null;

    /**
     * Realiza la conexión con la base de datos, autoCommit off.
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws SQLException
     */
    public void conectar() throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        Connection conn = null;
        String driver = "org.my";
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:epubLibre.db");
        conn.setAutoCommit(false);
        this.conn = conn;

    }
    public void limpiarTabla() throws SQLException {
        String sql = "delete from libros";
        Statement st = conn.createStatement();
        st.execute(sql);
    }
    /**
     * Crea la tabla que poseera los libros
     */
    public void crearTabla() {
        try {
            conectar();
            String tabla = "CREATE TABLE IF NOT EXISTS libros (\n" +
                    "  epl_id      NUMERIC,\n" +
                    "  titulo      TEXT,\n" +
                    "  autor       TEXT,\n" +
                    "  generos     TEXT,\n" +
                    "  coleccion   TEXT,\n" +
                    "  volumen     NUMERIC,\n" +
                    "  fecha_publi NUMERIC,\n" +
                    "  sinopsis    TEXT,\n" +
                    "  paginas     NUMERIC,\n" +
                    "  revision    REAL,\n" +
                    "  idioma      TEXT,\n" +
                    "  publicado   TEXT,\n" +
                    "  estado      TEXT,\n" +
                    "  valoracion  REAL,\n" +
                    "n_votos NUMERIC,\n" +
                    "  enlaces     TEXT," +
                    "CONSTRAINT lib_titAut PRIMARY KEY (titulo, autor))";
            Statement st = conn.createStatement();
            st.execute(tabla);
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
     * @throws SQLException
     */
    public void desconectar() throws SQLException {
        if (conn != null) {
            conn.commit();
            conn.close();
        }
    }
    public void rollBack() throws SQLException {
        conn.rollback();
    }
}
