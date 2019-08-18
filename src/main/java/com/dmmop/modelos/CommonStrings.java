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

package com.dmmop.modelos;

public interface CommonStrings {
    String VERSION = "v1.7";
    String DROPBOX_API = "https://content.dropboxapi.com/2/files/download";
    String TOKEN_API = "XvRZ_44-BGAAAAAAAAAAD5Ydn7d9Dnac0PCVz6qzy69FqqLgO2AaTbNj91_aVCMo";
    String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)";

    String VERSION_CHECK = "Version_check";
    String EPL_FORO = "ePL_foro";
    String NEWS = "Novedades";
    String DONWLOAD_LINK = "Dropbox_Jar";

    String LAST_UPDATE = "LAST_UPDATE";
    String VISIBLE_COLUMNS = "VISIBLE_COLUMNS";
    String ORDER_COLUMNS = "ORDER_COLUMNS";
    String WIDTH_COLUMNS = "WIDTH_COLUMNS";
    String WIDTH_WINDOW = "WIDTH_WINDOW";
    String HEIGHT_WINDOW = "HEIGHT_WINDOW";
    String LAST_SEARCH = "LAST_SEARCH";

    String CSV_NAME = "csv_full_imgs.csv";
    String PATTERN_A = "[àáâä]";
    String PATTERN_E = "[èéêë]";
    String PATTERN_I = "[îíìï]";
    String PATTERN_O = "[ôöóò]";
    String PATTERN_U = "[úùûü]";
    String PATTERN_FOR_PARAGRAPH = "(.[\\.!?])([A-Z«¿¡—])";
    String PATTERN_FOR_IDREV = "\\[(\\d+)].*?\\((r\\d.\\d)(.+)?\\)";
    String EPUB_BASE_1 = "<p class=\"tautor\"><code class=\"sans\">(.+?)<\\/code><\\/p>[\\s\\S]*<h1 class=\"ttitulo\"[\\s\\S]*><strong class=\"sans\">(.+?)<\\/strong><\\/h1>[\\s\\S]*<p class=\"trevision\"><strong class=\"sans\">ePub r(\\d\\.\\d)<\\/strong><\\/p>";
    String EPUB_BASE_2 = "<p class=\"tautor\">(.+?)<\\/p>[\\s\\S]*<h1 class=\"ttitulo\">(.+?)<\\/h1>[\\s\\S]*<p class=\"trevision\">ePub r(\\d+?\\.\\d+?)<\\/p>";
}
