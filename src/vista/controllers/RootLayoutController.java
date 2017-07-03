package vista.controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import parser.Csv;
import parser.Rss;
import vista.Main;

import java.io.File;

/**
 * Controlador de la vista principal y el MenuBar asignado
 */
public class RootLayoutController {
    private static Main main;

    /**
     * Recibe la instancia del archivo main para poder consultar datos de la misma.
     *
     * @param main Instancia del objeto Main.
     */
    public static void setMain(Main main) {
        RootLayoutController.main = main;
    }

    /**
     * Abre la pestaña de importación de ficheros.
     * Lanza un FileChooser para archivos .csv y .rss.
     * Según el tipo de fichero abre su parser correspondiente.
     */
    @FXML
    private void importDataSource() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open source file.");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("EpubLibrary", "*.csv", "*.rss"));
        final File datos = fileChooser.showOpenDialog(main.getPrimaryStage());
        if (datos != null) {
            if (datos.getName().endsWith("csv")) {
                Task importar = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        main.getPrimaryStage().getScene().setCursor(Cursor.WAIT);
                        new Csv().importCSV(datos);
                        main.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
                        return null;
                    }
                };
                importar.setOnSucceeded(e -> alertOK());
                new Thread(importar).start();

            } else if (datos.getName().endsWith("rss")) {
                Task importar = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        main.getPrimaryStage().getScene().setCursor(Cursor.WAIT);
                        Rss.importXML(datos);
                        main.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
                        return null;
                    }
                };
                importar.setOnSucceeded(e -> alertOK());
                new Thread(importar).start();

            }

        }

    }

    @FXML
    private void help() {
        Alert informacion = new Alert(Alert.AlertType.INFORMATION);
        informacion.setTitle("EpubLibrary v0.4.");
        informacion.setHeaderText("Created by and for EpubLibre.");
        informacion.setContentText("Created by ladaga on 02/07/17.");
        informacion.show();
    }

    /**
     * Función propia JafaFX que se ejecuta al iniciar el componente
     */
    @FXML
    private void initialize() {
        //do Something.
    }

    /**
     * Alerta de fin de carga de datos.
     */
    private void alertOK() {
        Alert fin = new Alert(Alert.AlertType.INFORMATION);
        fin.setHeaderText("Se han cargado los libros con exito.");
        fin.setContentText("Actualizacion finalizada.");
        fin.showAndWait();
    }
}
