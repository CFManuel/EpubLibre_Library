package vista;

import daoSqLite.ConnectorHelper;
import daoSqLite.GetLibros;
import org.apache.commons.cli.*;
import parser.Csv;
import parser.Rss;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;


public class Ejecutador {
    public static void main(String[] args) {
        new ConnectorHelper().crearTabla();
        GetLibros gt = new GetLibros();
        try {
            gt.getLibros("casa");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
/*
        try {
            Rss.importXML(new File("C:\\Users\\david\\Desktop\\rss_total_comp.rss"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }



     Options opciones = new Options();
        Option autor = new Option("a", "autor", true, "Buscar por autor");
        autor.setRequired(false);
        opciones.addOption(autor);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(opciones, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("EpubLibre_Library", opciones);

            System.exit(1);
            return;
        }

        String autor2 = cmd.getOptionValue("autor");
        Csv csv = new Csv();
        try {
            csv.importCSV(new File("C:\\Users\\david\\Desktop\\epublibre.csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
