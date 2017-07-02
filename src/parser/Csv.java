package parser;

import modelos.Libro;
import daoSqLite.InsertDatas;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.*;
import java.sql.SQLException;


public class Csv {
    /**
     * Abre el csv, lo lee linea a linea e inserta los datos en la base.
     *
     * @param csvFile Archivo csv con los datos completos.
     * @throws IOException
     */
    public void importCSV(File csvFile) throws IOException {
        LineIterator it = FileUtils.lineIterator(csvFile,"utf-8");
        int count = 0;
        InsertDatas idatas = new InsertDatas();
        String line;
        String[] items;

        try {
            it.nextLine();
            idatas.conectar();
            idatas.crearTabla();
            idatas.limpiarTabla();
            while (it.hasNext()) {
                line = it.nextLine();
                line = line.replaceAll("\",", "");
                items = line.split("\"");
                Libro lib = generarLibro(items);
                try {
                    idatas.insertarLibros(lib);
                    count++;
                } catch (Exception e) {
                    //nada
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                idatas.rollBack();
            } catch (SQLException e1) {
                System.err.println("Error al insertar datos");
            }
        } finally {

            LineIterator.closeQuietly(it);
            try {
                idatas.desconectar();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Se han integrado " + count + " registros.");
    }

    /**
     * Lee el primer caracter de cada linea y devuelve el número de lineas del documento.
     *
     * @param csvFile
     * @return
     * @throws IOException
     */
    private int calcularRegistros(File csvFile) throws IOException {
        int lineCount = 0;
        try (InputStream in = new BufferedInputStream(new FileInputStream(csvFile))) {
            byte[] buf = new byte[4096 * 16];
            int c;
            while ((c = in.read(buf)) > 0) {
                for (int i = 0; i < c; i++) {
                    if (buf[i] == '\n') lineCount++;
                }
            }
        }
        return lineCount;
    }

    /**
     * Obtiene
     *
     * @param items
     * @return
     */
    private Libro generarLibro(String[] items) {
        Libro lib = new Libro();
        try {
            if (items[1].equalsIgnoreCase("") == false) lib.setEpl_id(Integer.parseInt(items[1]));
            lib.setTitulo(items[2]);
            lib.setAutor(items[3]);
            lib.setGeneros(items[4]);
            lib.setColeccion(items[5]);
            if (items[6].equalsIgnoreCase("") == false) lib.setVolumen(Double.parseDouble(items[6]));
            if (items[7].equalsIgnoreCase("") == false) lib.setFecha_publi(Integer.parseInt(items[7]));
            if (items[8].equalsIgnoreCase("") == false) lib.setSinopsis(items[8]);
            if (items[9].equalsIgnoreCase("") == false) lib.setPaginas(Integer.parseInt(items[9]));
            if (items[10].equalsIgnoreCase("") == false) lib.setRevision(Double.parseDouble(items[10]));
            lib.setIdioma(items[11]);
            lib.setPublicado(items[12]);
            lib.setEstado(items[13]);
            if (items[14].equalsIgnoreCase("") == false) lib.setValoracion(Double.parseDouble(items[14]));
            if (items[15].equalsIgnoreCase("") == false) lib.setN_votos(Integer.parseInt(items[15]));
            try {

                lib.setEnlaces("magnet:?xt=urn:btih:" + items[16]);
            } catch (Exception e) {
            }
        } catch (Exception e) {
            System.err.println(lib.toString());
            e.printStackTrace();
        }
        return lib;
    }
}
