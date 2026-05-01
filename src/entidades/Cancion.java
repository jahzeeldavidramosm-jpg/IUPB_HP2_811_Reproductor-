package entidades;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Cancion {

    @JsonProperty("Titulo")
    private String titulo;

    @JsonProperty("Duracion")
    private String duracion;

    @JsonProperty("Año")
    private String año;

    @JsonProperty("Genero")
    private String genero;

    public String getTitulo()   { return titulo; }
    public String getDuracion() { return duracion; }
    public String getAño()      { return año; }
    public String getGenero()   { return genero; }

    public void setTitulo(String titulo)     { this.titulo = titulo; }
    public void setDuracion(String duracion) { this.duracion = duracion; }
    public void setAño(String año)           { this.año = año; }
    public void setGenero(String genero)     { this.genero = genero; }
}
