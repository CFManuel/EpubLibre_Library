/*
 * Copyright (c) 2020. Ladaga.
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

import com.dmmop.exceptions.NoValidCSVFile;
import com.dmmop.files.Utils;
import com.dmmop.parser.Csv;
import com.dmmop.vista.Main;
import com.sun.javafx.tk.Toolkit;
import javafx.concurrent.Task;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.dmmop.modelos.CommonStrings.*;


public class UpdateTask extends Task {
    private static final int TOTAL_PROGRESS = 7;
    private int numLibrosImportados = 0;

    @Override
    protected Object call() throws Exception {
        updateProgress(0, TOTAL_PROGRESS);
        updateMessage("Iniciando descarga de csv...");

        updateMessage("Descargando csv...");
        //updateProgress(1, TOTAL_PROGRESS);
        File zip = Utils.downloadCSVfromDropbox(this);
        //Tamaño del fichero en Mb.
        int zipSize = (int) zip.length() / 2048;
        int totalProgress=zipSize;

        updateProgress(3+totalProgress, TOTAL_PROGRESS+totalProgress);
        updateMessage("CSV Descargado.");

        try {
            updateMessage("Comprobando validez...");

            if (zipSize < 10) {
                throw new NoValidCSVFile("Archivo no válido.");
            } else {
                Utils.unZip(zip);
            }
            updateProgress(4+totalProgress, TOTAL_PROGRESS+totalProgress);
            updateMessage("CSV válido.");
            Csv csv = new Csv();
            updateMessage("Importando CSV...");
            File csvFile = new File(Main.getLocation() + CSV_NAME);
            numLibrosImportados = csv.importCSV(csvFile,this);
            updateMessage("CSV importado.");

            updateProgress(5+totalProgress, TOTAL_PROGRESS+totalProgress);
            updateMessage("Actualizando fecha...");

            updateMessage("Fecha actualizada...");
            updateProgress(6+totalProgress, TOTAL_PROGRESS+totalProgress);
            //Utils.deleteZip(zip, csvFile);
            Utils.deleteZip(zip);

        } finally {
            updateProgress(TOTAL_PROGRESS+totalProgress, TOTAL_PROGRESS+totalProgress);
        }
        return null;
    }

    @Override
    public void updateMessage(String s) {
        super.updateMessage(s);
    }

    @Override
    public void updateProgress(long l, long l1) {
        super.updateProgress(l, l1);
    }

    public int getNumLibrosImportados() {
        return numLibrosImportados;
    }
}
