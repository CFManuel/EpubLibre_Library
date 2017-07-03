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

package files;

import modelos.CommonStrings;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by david on 03/07/2017.
 */
public class Utils implements CommonStrings {
    public static File downloadCSV() {
        File destino = null;
        try {
            URL url = new URL(CSV_URL);
            destino = new File("epub.zip");
            FileUtils.copyURLToFile(url, destino);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return destino;
    }

    public static void unZip(File zip) {
        try {
            ZipFile zipFile = new ZipFile(zip);
            zipFile.extractAll(zip.getPath());
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }
}
