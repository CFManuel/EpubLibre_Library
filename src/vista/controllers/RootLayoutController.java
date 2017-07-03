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

package vista.controllers;

import daoSqLite.GetDatas;
import daoSqLite.InsertDatas;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import modelos.CommonStrings;
import parser.Csv;
import parser.Rss;
import vista.Main;

import java.io.File;
import java.sql.SQLException;

/**
 * Controlador de la vista principal y el MenuBar asignado
 */
public class RootLayoutController implements CommonStrings {
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
        Task importar = null;
        fileChooser.setTitle("Open source file.");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("EpubLibrary", "*.csv", "*.rss"));
        final File datos = fileChooser.showOpenDialog(main.getPrimaryStage());
        if (datos != null) {
            if (datos.getName().endsWith("csv")) {
                importar = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        main.getPrimaryStage().getScene().setCursor(Cursor.WAIT);
                        new Csv().importCSV(datos);
                        main.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
                        return null;
                    }
                };
            } else if (datos.getName().endsWith("rss")) {
                importar = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        main.getPrimaryStage().getScene().setCursor(Cursor.WAIT);
                        Rss.importXML(datos);
                        main.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
                        return null;
                    }
                };
            }
            importar.setOnSucceeded(e -> {
                InsertDatas insertDatas = new InsertDatas();
                try {
                    insertDatas.updateDate();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                alertOK();
            });
            new Thread(importar).start();
        }

    }

    @FXML
    private void getLastUpdate() {
        try {
            GetDatas getDatas = new GetDatas();
            String fecha = getDatas.getLastUpdate();
            int libros = getDatas.countBooks();
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setHeaderText("Información de actualización de la base de datos:");
            info.setContentText(String.format("La base de datos se actualizo por ultima vez en %s. %n" +
                    "Actualmente contiene %d libros.", fecha, libros));
            info.show();
        } catch (Exception e) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText("Ha habido un error al realizar consultar los datos");
            error.setContentText("Me avergüenza decir, que algo no ha salido bien...");
            error.show();
        }
    }

    @FXML
    private void help() {
        Alert informacion = new Alert(Alert.AlertType.INFORMATION);
        informacion.setTitle("EpubLibrary " + VERSION);
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
