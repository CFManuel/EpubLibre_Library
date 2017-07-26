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

package controller;

import daosqlite.GetDatas;
import daosqlite.InsertDatas;
import exceptions.NoValidCSVFile;
import files.Utils;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import modelos.CommonStrings;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import parser.Csv;
import vista.Main;

import java.io.File;
import java.sql.SQLException;

import static vista.controllers.Alertas.*;

public final class UpdateDB implements CommonStrings {
    private static final int TOTAL_PROGRESS = 7; //Pasos de información de la actualización.

    private UpdateDB() {
    }

    /**
     * Comprueba si existe fecha en la base de datos, sino, la inserta.
     * Comprueba cuantos dias han pasado desde la última actualización y realiza las acciones necesarias.
     */
    public static void timeToUpdate() {
        if (checkAppVersion()) alertNewAppUpdate();
        if (checkDBage()) updateDataBase();

    }

    /**
     * Inicia el proceso de actualización, descarga el fichero .csv, comprueba que pesa al menos 30mb, sino descarta el proceso.
     * Importa el .csv y actualiza la fecha en la base de datos.
     */
    public static void updateDataBase() {
        ProgressForm progressForm = new ProgressForm();
        Task importar = new Task() {
            @Override
            protected Object call() throws Exception {
                updateProgress(0, TOTAL_PROGRESS);
                updateMessage("Iniciando descarga de csv...");

                updateMessage("Descargando csv...");
                updateProgress(1, TOTAL_PROGRESS);
                File zip = Utils.downloadCSVfromDropbox();

                updateProgress(3, TOTAL_PROGRESS);
                updateMessage("CSV Descargado.");

                try {
                    updateMessage("Comprobando validez...");
                    int zipSize = (int) zip.length() / 2048; //Tamaño del fichero en Mb.
                    if (zipSize < 10) {
                        throw new NoValidCSVFile("Archivo no válido.");
                    } else {
                        Utils.unZip(zip);
                    }
                    updateProgress(4, TOTAL_PROGRESS);
                    updateMessage("CSV válido.");
                    Csv csv = new Csv();
                    updateMessage("Importando CSV...");
                    csv.importCSV(new File(Main.getLocation() + CSV_NAME));
                    updateMessage("CSV importado.");

                    updateProgress(5, TOTAL_PROGRESS);
                    updateMessage("Actualizando fecha...");
                    updateDate();
                    updateMessage("Fecha actualizada...");
                    updateProgress(6, TOTAL_PROGRESS);
                } finally {
                    Utils.deleteZip(zip);
                    updateProgress(TOTAL_PROGRESS, TOTAL_PROGRESS);
                }
                return null;
            }
        };
        progressForm.activateProgressBar(importar);
        importar.setOnSucceeded(e -> {
            progressForm.getDialogStage().close();
            alertUpdateOK();
        });
        importar.setOnFailed(e -> {
            progressForm.getDialogStage().close();
            alertUpdateFail();
        });
        new Thread(importar).start();
    }

    /**
     * Inserta en la base de datos la fecha actual.
     */
    private static void updateDate() {
        try {
            InsertDatas insertDatas = new InsertDatas();
            insertDatas.updateDate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkAppVersion() {
        boolean bool = Main.configuracion.get(VERSION_CHECK).equalsIgnoreCase(VERSION);
        return !bool;
    }

    private static boolean checkDBage() {
        boolean actualizar = false;
        try {
            GetDatas getDatas = new GetDatas(); //Optiene la fecha de la base
            String lastDate = getDatas.getLastUpdate();
            if (lastDate.equalsIgnoreCase("")) {
                updateDate();
                updateDataBase();
            } else {
                //Marca el dia de actual.
                DateTime now = new DateTime();
                DateTimeFormatter format = DateTimeFormat.forPattern("dd/MM/yyyy");
                DateTime lastUpdate = format.parseDateTime(lastDate); //Transforma la cadena a DateTime

                Period periodo = new Period(lastUpdate, now); //Comprueba cuantos dias han pasado.
                int dias = periodo.getDays();
                if (dias >= DATA_OLD) actualizar = true;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return actualizar;
    }

    /**
     * Clase para lanzar una ventana de ProgressBar a partir de un Task.
     * <p>
     * Usage:
     * progressForm.activateProgressBar(task);
     * inTask: updateProgress(start, end); updateMessage("mesage");
     * task.setOnSucceeded(e -> {progressForm.getDialogStage().close());
     * task.setOnFailed(e -> progressForm.getDialogStage().close());
     */
    public static class ProgressForm {
        private final Stage dialogStage;
        private final ProgressBar pb = new ProgressBar();
        private final Label label = new Label();

        public ProgressForm() {
            dialogStage = new Stage();
            dialogStage.initStyle(StageStyle.UTILITY);
            dialogStage.setResizable(false);
            dialogStage.setTitle("Actualización en curso.");
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            // PROGRESS BAR
            label.setText("alerto");

            pb.setProgress(-1F);
            pb.setPrefWidth(200);

            final VBox vb = new VBox();
            vb.setSpacing(5);
            vb.setAlignment(Pos.CENTER_LEFT);
            vb.getChildren().addAll(label, pb);

            Scene scene = new Scene(vb);
            dialogStage.setScene(scene);
        }

        /**
         * Asigna a la barra de progreso un task sobre el que se va a informar
         *
         * @param task Tarea que se desea vigilar.
         */
        public void activateProgressBar(final Task<?> task) {
            pb.progressProperty().bind(task.progressProperty());
            label.textProperty().bind(task.messageProperty());

            dialogStage.show();
        }

        public Stage getDialogStage() {
            return dialogStage;
        }
    }
}