package vista;

import daoSqLite.ConnectorHelper;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import modelos.Libro;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.commons.cli.*;
import parser.Csv;
import parser.Rss;
import vista.controllers.BookViewer;
import vista.controllers.MainTableViewController;
import vista.controllers.RootLayoutController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main extends Application {
    private Main main;
    private Stage primaryStage;
    private BorderPane rootLayout;
    private ObservableList<Libro> libros = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("EpubLibre Library v0.3.");
        this.primaryStage.getIcons().add(new Image("vista/resources/EPL_Portadas_NEGRO.png"));
        this.main = this;
        new ConnectorHelper().crearTabla();
        initRootLayout();
        initMainTableView();
    }

    private void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("resources/RootLayout.fxml"));
            this.rootLayout = loader.load();
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            RootLayoutController.setMain(this);
            primaryStage.setResizable(true);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initMainTableView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            // Se indica la situación del xml gráfico.
            loader.setLocation(Main.class.getResource("resources/MainTableView.fxml"));
            // Se carga el contenedor, en este caso un AnchorPane.
            AnchorPane pane = loader.load();
            // Se integra en la parte central del RootLayout.
            pane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            rootLayout.setCenter(pane);
            // Se indica el tamaño del root para que se ajuste a la vista de datos.
            rootLayout.setPrefSize(pane.getWidth(), pane.getHeight());
            // Se relaciona el controlador.
            MainTableViewController controller = loader.getController();
            // Se le entrega la instancia del Main al controlador.
            controller.setMain(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void launchBook(Libro libro) {
        try {
            FXMLLoader loader = new FXMLLoader();
            BookViewer controller = new BookViewer(libro);
            loader.setController(controller);
            loader.setLocation(Main.class.getResource("resources/BookViewer.fxml"));
            AnchorPane pane = loader.load();
            Scene scene = new Scene(pane);
            Stage dialogStage = new Stage();
            dialogStage.setTitle(libro.getTitulo());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(this.primaryStage);
            dialogStage.setScene(scene);

            controller.setDialogStage(dialogStage);
            controller.setLibro(libro);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Libro> getLibros() {
        return libros;
    }

    public Main setLibros(ArrayList<Libro> libros) {
        this.libros.clear();
        this.libros.addAll(libros);
        return this;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        if (args.length > 1) {
            Options opciones = new Options();
            String ficheroEntrada = null;
            Option cargarFichero = new Option("i", "input", true, "Fichero de origen de datos(csv, rss)");
            cargarFichero.setRequired(false);
            opciones.addOption(cargarFichero);

            CommandLineParser parser = new DefaultParser();
            HelpFormatter formatter = new HelpFormatter();
            CommandLine cmd;

            try {
                cmd = parser.parse(opciones, args);
                ficheroEntrada = cmd.getOptionValue("input");
                if (ficheroEntrada != null && Files.exists(Paths.get(ficheroEntrada))) {
                    File archivo = new File(ficheroEntrada);

                    if (archivo.getName().endsWith("csv")) {
                        new Csv().importCSV(archivo);
                    } else if (archivo.getName().endsWith("rss")) {
                        Rss.importXML(archivo);
                    } else {
                        throw new ParseException("No es un fichero valido");
                    }
                } else {
                    throw new ParseException("No existe ese fichero");
                }
                System.exit(0);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                formatter.printHelp("EpubLibre_Library", opciones);
                System.exit(1);
                return;
            }
        } else {
            launch(args);
        }
    }
}
