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

import daoSqLite.GetDatas;
import daoSqLite.InsertDatas;
import exceptions.NoValidCSVFile;
import files.Utils;
import javafx.concurrent.Task;
import modelos.CommonStrings;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import parser.Csv;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import static controller.Alertas.alertUpdateFail;
import static controller.Alertas.alertUpdateOK;

/**
 * Created by david on 03/07/2017.
 */
public final class UpdateDB implements CommonStrings {


    /**
     * Comprueba si existe fecha en la base de datos, sino, la inserta.
     * Comprueba cuantos dias han pasado desde la última actualización y realiza las acciones necesarias.
     */
    public static void timeToUpdate() throws NoValidCSVFile {
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
                if (periodo.getDays() == DATA_OLD) {
                    updateDataBase();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inicia el proceso de actualización, descarga el fichero .csv, comprueba que pesa al menos 30mb, sino descarta el proceso.
     * Importa el .csv y actualiza la fecha en la base de datos.
     */
    public static void updateDataBase() {
        Task importar = new Task() {
            @Override
            protected Object call() throws Exception {
                System.out.println("Descargando fichero...");
                File zip = Utils.downloadCSV();
                System.out.println("Descargado.");
                try {
                    int zipSize = (int) zip.length() / 2048; //Tamaño del fichero en Mb.
                    if (zipSize < 10) {
                        throw new NoValidCSVFile("Archivo no válido.");
                    } else {
                        System.out.println("Descomprimiendo fichero...");
                        Utils.unZip(zip);
                        System.out.println("Descomprimido.");
                    }
                    System.out.println("Iniciando importacion de csv...");
                    Csv csv = new Csv();
                    csv.importCSV(new File(CSV_DEST + CSV_NAME));
                    updateDate();
                } catch (NoValidCSVFile noValidCSVFile) {
                    System.err.println("error");

                    throw noValidCSVFile;
                } catch (IOException e) {

                    throw e;
                } finally {
                    Utils.deleteZip(zip);
                }
                return null;
            }
        };
        importar.setOnSucceeded(e -> {
            System.err.println("ok");
            alertUpdateOK();
        });
        importar.setOnFailed(e -> {
            System.err.println("error");
            alertUpdateFail();
        });
        new Thread(importar).start();
    }

    /**
     * Inserta en la base de datos la fecha actual.
     */
    public static void updateDate() {
        try {
            InsertDatas insertDatas = new InsertDatas();
            insertDatas.updateDate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
