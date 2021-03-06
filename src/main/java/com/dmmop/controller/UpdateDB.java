/*
 * Copyright (c) 2019. Ladaga.
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

package com.dmmop.controller;

import com.dmmop.daosqlite.GetDatas;
import com.dmmop.daosqlite.InsertDatas;
import com.dmmop.exceptions.NoValidCSVFile;
import com.dmmop.files.Utils;
import com.dmmop.modelos.CommonStrings;
import com.dmmop.parser.Csv;
import com.dmmop.vista.Main;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;

import static com.dmmop.vista.controllers.Alertas.*;

public final class UpdateDB implements CommonStrings {
    private static final int TOTAL_PROGRESS = 7; //Pasos de información de la actualización.

    private UpdateDB() {
    }

    /**
     * Comprueba si existe fecha en la base de datos, sino, la inserta.
     * Comprueba cuantos dias han pasado desde la última actualización y realiza las acciones necesarias.
     */
    public static void timeToUpdate() {
        if (checkDBage()) updateDataBase();
        if (checkAppVersion()) alertNewAppUpdate();
    }

    /**
     * Inicia el proceso de actualización, descarga el fichero .csv, comprueba que pesa al menos 30mb, sino descarta el proceso.
     * Importa el .csv y actualiza la fecha en la base de datos.
     */

    public static void updateDataBase() {
        ProgressForm progressForm = new ProgressForm();
        UpdateTask importar = new UpdateTask();
        progressForm.activateProgressBar(importar);
        importar.setOnSucceeded(e -> {
            updateDate();
            progressForm.getDialogStage().close();
            GetDatas.getIdiomas();
            Main.getMainTableViewController().restoreLastSearch();
            alertUpdateOK(importar.getNumLibrosImportados());
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

    /**
     * Comprueba si hay una nueva versión de la aplicación.
     *
     * @return True si hay que actualizar.
     */
    private static boolean checkAppVersion() {
        boolean bool = true;
        try {
            String version = Main.getConfiguracion().get(VERSION_CHECK);
            if (version != null) bool = (version.compareToIgnoreCase(VERSION) != 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return !bool;
    }

    /**
     * Comprueba si la DB tiene 4 o más dias de antiguedad.
     *
     * @return True si hay que actualizar.
     */
    public static boolean checkDBage() {
        boolean actualizar = true;
        try {
            GetDatas getDatas = new GetDatas(); //Optiene la fecha de la base
            String lastDate = getDatas.getLastUpdate();
            if (!lastDate.equalsIgnoreCase("")) {
                //Marca el dia de actual.
                DateTime now = new DateTime();
                DateTimeFormatter format = DateTimeFormat.forPattern("dd/MM/yyyy");
                DateTime lastUpdate = format.parseDateTime(lastDate); //Transforma la cadena a DateTime

                Days antiguedad = Days.daysBetween(lastUpdate, now); //Comprueba cuantos dias han pasado.

                int dias = antiguedad.getDays();
                //Actualiza si ha pasado un día de diferencia y son más de las 4.
                actualizar = (dias >= 1 && now.hourOfDay().get() >= 4) || (dias >= 2);
            }
        } catch (SQLException | ClassNotFoundException e) {
            //no update
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