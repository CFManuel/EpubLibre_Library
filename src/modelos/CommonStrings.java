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
    String VERSION = "v0.6.3";
    String LAST_UPDATE = "LAST_UPDATE";
    String VISIBLE_COLUMNS = "VISIBLE_COLUMNS";
    String ORDER_COLUMNS = "ORDER_COLUMNS";
    String WIDTH_COLUMNS = "WIDTH_COLUMNS";
    String WIDTH_WINDOW = "WIDTH_WINDOW";
    String HEIGHT_WINDOW = "HEIGHT_WINDOW";
    String CSV_URL = "https://epublibre.org/rssweb/csv";
    String RSS_URL = "http://epublibre.org/rssweb/rss/completo"; //Compacto
    //Main.getLocation() para apuntar a la carpeta epl del jar
    String CSV_NAME = "epublibre.csv";
    String PATTERN_A = "[àáâä]";
    String PATTERN_E = "[èéêë]";
    String PATTERN_I = "[îíìï]";
    String PATTERN_O = "[ôöóò]";
    String PATTERN_U = "[úùûü]";
    String PATTERN_FOR_PARAGRAPH = "([a-z][\\.!?])([A-Z])";
    int DATA_OLD = 4;
}
