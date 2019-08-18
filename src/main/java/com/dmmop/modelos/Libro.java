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

import java.util.ArrayList;

/**
 * JavaBean con los datos de cada libro.
 */
public class Libro {

    private int epl_id = 0;
    private String titulo = "";
    private String autor = "";
    private String generos = "";
    private String coleccion = "";
    private Double volumen = 0.0;
    private int fecha_publi = 0;
    private String sinopsis = "";
    private int paginas = 0;
    private Double revision = 0.0;
    private ArrayList<Double> revArray = new ArrayList<>();
    private String idioma = "";
    private String publicado = "";
    private String estado = "";
    private Double valoracion = 0.0;
    private int n_votos = 0;
    private String enlaces = "";
    private String imgURI = "";

    public ArrayList<Double> getRevArray() {
        return revArray;
    }

    public Libro setRevArray(ArrayList<Double> revArray) {
        this.revArray = revArray;
        return this;
    }

    public Libro addRevArray(Double revArray) {
        this.revArray.add(revArray);
        return this;
    }

    public String getImgURI() {
        return imgURI;
    }

    public Libro setImgURI(String imgURI) {
        this.imgURI = imgURI;
        return this;
    }

    public int getEpl_id() {
        return epl_id;
    }

    public Libro setEpl_id(int epl_id) {
        this.epl_id = epl_id;
        return this;
    }

    public String getTitulo() {
        return titulo;
    }

    public Libro setTitulo(String titulo) {
        this.titulo = titulo;
        return this;
    }

    public String getAutor() {
        return autor;
    }

    public Libro setAutor(String autor) {
        this.autor = autor;
        return this;
    }

    public String getGeneros() {
        return generos;
    }

    public Libro setGeneros(String generos) {
        this.generos = generos;
        return this;
    }

    public String getColeccion() {
        return coleccion;
    }

    public Libro setColeccion(String coleccion) {
        this.coleccion = coleccion;
        return this;
    }

    public Double getVolumen() {
        return volumen;
    }

    public Libro setVolumen(Double volumen) {
        this.volumen = volumen;
        return this;
    }

    public int getFecha_publi() {
        return fecha_publi;
    }

    public Libro setFecha_publi(int fecha_publi) {
        this.fecha_publi = fecha_publi;
        return this;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public Libro setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
        return this;
    }

    public int getPaginas() {
        return paginas;
    }

    public Libro setPaginas(int paginas) {
        this.paginas = paginas;
        return this;
    }

    public Double getRevision() {
        return revision;
    }

    public Libro setRevision(Double revision) {
        this.revision = revision;
        return this;
    }

    public String getIdioma() {
        return idioma;
    }

    public Libro setIdioma(String idioma) {
        this.idioma = idioma;
        return this;
    }

    public String getPublicado() {
        return publicado;
    }

    public Libro setPublicado(String publicado) {
        this.publicado = publicado;
        return this;
    }

    public String getEstado() {
        return estado;
    }

    public Libro setEstado(String estado) {
        this.estado = estado;
        return this;
    }

    public Double getValoracion() {
        return valoracion;
    }

    public Libro setValoracion(Double valoracion) {
        this.valoracion = valoracion;
        return this;
    }

    public int getN_votos() {
        return n_votos;
    }

    public Libro setN_votos(int n_votos) {
        this.n_votos = n_votos;
        return this;
    }

    public String getEnlaces() {
        return enlaces;
    }

    public Libro setEnlaces(String enlaces) {
        this.enlaces = enlaces;
        return this;
    }

    @Override
    public String toString() {
        return "Libro{" +
                "epl_id=" + epl_id +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", revision=" + revision +
                ", revArray=" + revArray +
                '}';
    }
}
