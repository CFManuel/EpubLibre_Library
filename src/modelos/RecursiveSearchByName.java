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

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Se usa junto con Files.walkFileTree para buscar en un directorio.
 */
public class RecursiveSearchByName implements FileVisitor<Path>, CommonStrings {
    private final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:*.{epub}"); //Busca ficheros con extensión
    private final Pattern pattern = Pattern.compile(PATTERN_FOR_IDREV); //Patron para identificar el epl_ID y la revisión del libro.
    private Matcher matcher;
    private HashMap<Integer, String> epubs = new HashMap<>(); //Se almacenan los libros

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        matcher = pattern.matcher(file.getFileName().toString()); //Se hace el matcher.
        //Se comprueba que el fichero es .epub y que se ha encontrado el patron.
        if (pathMatcher.matches(file.getFileName()) && matcher.find()) {
            epubs.put(Integer.parseInt(matcher.group(1)), matcher.group(2));
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

    public HashMap<Integer, String> getEpubs() {
        return epubs;
    }
}

