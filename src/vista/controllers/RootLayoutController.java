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

import controller.UpdateDB;
import daosqlite.GetDatas;
import daosqlite.InsertDatas;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.ButtonType;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import modelos.CommonStrings;
import modelos.Libro;
import parser.Csv;
import vista.Main;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static vista.controllers.Alertas.*;

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

    @FXML
    private void ePLDownload() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Carpeta con ePubs.");
        File fs = directoryChooser.showDialog(main.getPrimaryStage());
        main.getPrimaryStage().getScene().setCursor(Cursor.WAIT);
        File[] fList = fs.listFiles();
        String[] busqueda = new String[6];
        for (int i = 0; i < 6; i++) busqueda[i] = "%%";
        try {
            GetDatas getDatas = new GetDatas();
            final ArrayList<Libro> librosDB = getDatas.getLibros(busqueda);
            System.out.println(librosDB.size());
            HashMap<Integer, String> librosDownload = new HashMap<>();
            Pattern pattern = Pattern.compile("\\[(\\d+)].*?\\((r\\d.\\d)\\)");
            Matcher matcher;
            for (File file : fList) {
                matcher = pattern.matcher(file.getName());
                if (matcher.find()) librosDownload.put(Integer.parseInt(matcher.group(1)), matcher.group(2));
            }
            //Comprueba que hay libros descargados.
            if (librosDownload.size() > 0) {
                for (int i = 0; i < librosDB.size(); i++) {
                    if (librosDownload.containsKey(librosDB.get(i).getEpl_id())) librosDB.remove(i);
                }

                MainTableViewController.libros.clear();
                MainTableViewController.libros.addAll(librosDB);
            }
        } catch (SQLException | ClassNotFoundException e) {
            main.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
            e.printStackTrace();
        }
        main.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
    }

    /**
     * Abre la pestaña de importación de ficheros. Lanza un FileChooser para archivos .csv y .rss.
     * Según el tipo de fichero abre su parser correspondiente.
     */
    @FXML
    @Deprecated
    private void importDataSource() {
        FileChooser fileChooser = new FileChooser();
        Task importar = null;
        fileChooser.setTitle("Open source file.");
        fileChooser
                .getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("EpubLibrary", "*.csv"));
        final File datos = fileChooser.showOpenDialog(main.getPrimaryStage());
        UpdateDB.ProgressForm progressForm = new UpdateDB.ProgressForm();

        if (datos != null) {
            if (datos.getName().endsWith("csv")) {
                importar =
                        new Task() {
                            @Override
                            protected Object call() throws Exception {
                                progressForm.getDialogStage().getScene().setCursor(Cursor.WAIT);
                                updateMessage("Iniciando actualización");
                                updateProgress(1, 4);
                                main.getPrimaryStage().getScene().setCursor(Cursor.WAIT);
                                updateMessage("Importando CSV...");
                                updateProgress(2, 4);
                                new Csv().importCSV(datos);
                                updateMessage("CSV importado.");
                                updateProgress(3, 4);
                                main.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
                                progressForm.getDialogStage().getScene().setCursor(Cursor.DEFAULT);
                                updateMessage("Finalizando actualización..,");
                                updateProgress(4, 4);
                                return null;
                            }
                        };
            }
            assert importar != null;
            importar.setOnSucceeded(
                    e -> {
                        InsertDatas insertDatas = new InsertDatas();
                        try {
                            insertDatas.updateDate();
                        } catch (ClassNotFoundException | SQLException e1) {
                            e1.printStackTrace();
                        }
                        progressForm.getDialogStage().close();
                        alertUpdateOK();
                    });
            importar.setOnFailed(
                    e -> {
                        alertUpdateFail();
                        progressForm.getDialogStage().close();
                    });
            progressForm.activateProgressBar(importar);
            new Thread(importar).start();
        }
    }

    /**
     * Confirma si el usuario desea actualizar.
     */
    @FXML
    private void epubLibreImport() throws InterruptedException {
        Optional<ButtonType> boton = updateConfirmation();
        if (boton.isPresent() && boton.get() == ButtonType.OK) {
            UpdateDB.updateDataBase();
        }
    }

    /**
     * Muestra la información de la base de datos.
     */
    @FXML
    private void getLastUpdate() {
        try {
            GetDatas getDatas = new GetDatas();
            String fecha = getDatas.getLastUpdate();
            int libros = getDatas.countBooks();
            alertDBInformation(fecha, libros);
        } catch (Exception e) {
            alertDBError();
        }
    }

    /**
     * Información sobre la aplicación.
     */
    @FXML
    private void help() {
        aplicationInfo();
    }

    /**
     * Función propia JafaFX que se ejecuta al iniciar el componente
     */
    @FXML
    private void initialize() {

        //do Something.
    }

    @FXML
    private void getMultiRev() {
        GetDatas.getLibrosMultiRev();
    }

    @FXML
    private void resetGUI()
            throws SQLException, ClassNotFoundException, IOException, URISyntaxException {
        Optional<ButtonType> borrar = panicButton();
        if (borrar.isPresent() && borrar.get() == ButtonType.OK) {
            InsertDatas insertDatas = new InsertDatas();
            insertDatas.deleteConfig(CommonStrings.VISIBLE_COLUMNS);
            insertDatas.deleteConfig(CommonStrings.ORDER_COLUMNS);
            insertDatas.deleteConfig(CommonStrings.WIDTH_COLUMNS);
            insertDatas.deleteConfig(CommonStrings.HEIGHT_WINDOW);
            insertDatas.deleteConfig(CommonStrings.WIDTH_WINDOW);
        }
    }
}
