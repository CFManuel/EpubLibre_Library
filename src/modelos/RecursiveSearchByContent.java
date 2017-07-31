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

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class RecursiveSearchByContent implements FileVisitor<Path>, CommonStrings {
    private final Pattern pattern1 =
            Pattern.compile("<p class=\"tautor\"><code class=\"sans\">(.+?)<\\/code><\\/p>[\\s\\S]*<h1 class=\"ttitulo\"[\\s\\S]*><strong class=\"sans\">(.+?)<\\/strong><\\/h1>[\\s\\S]*<p class=\"trevision\"><strong class=\"sans\">ePub r(\\d\\.\\d)<\\/strong><\\/p>");
    private final Pattern pattern2 =
            Pattern.compile("<p class=\"tautor\">(.+?)<\\/p>[\\s\\S]*<h1 class=\"ttitulo\">(.+?)<\\/h1>[\\s\\S]*<p class=\"trevision\">ePub r(\\d+?\\.\\d+?)<\\/p>");
    private final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:*.{epub}"); //Busca ficheros con extensión
    private ArrayList<Libro> epubs = new ArrayList<>(); //Se almacenan los libros

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        //Se comprueba que el fichero es .epub y que se ha encontrado el patron.
        if (pathMatcher.matches(file.getFileName())) {
            ZipFile zipFile = null;
            try {
                zipFile = new ZipFile(file.toFile());
                ZipEntry entry = zipFile.getEntry("OEBPS/Text/titulo.xhtml");
                if (entry != null) {
                    StringWriter sw = new StringWriter();
                    IOUtils.copy(zipFile.getInputStream(entry), sw, "UTF-8");
                    String text = sw.toString();
                    Matcher matcher1 = pattern1.matcher(text);
                    Matcher matcher2 = pattern2.matcher(text);
                    if (matcher2.find()) {
                        epubs.add(
                                new Libro().setTitulo(matcher2.group(2).replaceAll("&amp;", "&"))
                                        .setAutor(matcher2.group(1).replaceAll("&amp;", "&"))
                                        .setRevision(Double.parseDouble(matcher2.group(3))));
                    } else if (matcher1.find()) {
                        epubs.add(
                                new Libro().setTitulo(matcher1.group(2).replaceAll("&amp;", "&"))
                                        .setAutor(matcher1.group(1).replaceAll("&amp;", "&"))
                                        .setRevision(Double.parseDouble(matcher1.group(3))));
                    }
                }
            } catch (IOException e) {
                System.err.println(file.getFileName().toString());
            } finally {
                if (zipFile != null) try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    public ArrayList<Libro> getEpubs() {
        return epubs;
    }

}
