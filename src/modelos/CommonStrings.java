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

package modelos;

public interface CommonStrings {
    String VERSION = "v1.";
    String DONWLOAD_LINK = "Dropbox_Jar";
    String DROPBOX_API = "https://content.dropboxapi.com/2/files/download";
    String TOKEN_API = "XvRZ_44-BGAAAAAAAAAAD5Ydn7d9Dnac0PCVz6qzy69FqqLgO2AaTbNj91_aVCMo";

    String VERSION_CHECK = "Version_check";
    String EPL_FORO = "ePL_foro";
    String LAST_UPDATE = "LAST_UPDATE";
    String VISIBLE_COLUMNS = "VISIBLE_COLUMNS";
    String ORDER_COLUMNS = "ORDER_COLUMNS";
    String WIDTH_COLUMNS = "WIDTH_COLUMNS";
    String WIDTH_WINDOW = "WIDTH_WINDOW";
    String HEIGHT_WINDOW = "HEIGHT_WINDOW";
    String CSV_URL = "https://epublibre.org/rssweb/csv";
    //Main.getLocation() para apuntar a la carpeta epl del jar
    String CSV_NAME = "csv_full_imgs.csv";
    String PATTERN_A = "[àáâä]";
    String PATTERN_E = "[èéêë]";
    String PATTERN_I = "[îíìï]";
    String PATTERN_O = "[ôöóò]";
    String PATTERN_U = "[úùûü]";
    String PATTERN_FOR_PARAGRAPH = "([a-zA-Z][\\.!?])([A-Z])";
    int DATA_OLD = 4;
}
