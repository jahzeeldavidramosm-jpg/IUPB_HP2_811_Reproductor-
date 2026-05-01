package entidades;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Artista {

    @JsonProperty("Nombre")
    private String nombre;

    @JsonProperty("Tipo")
    private String tipo;

    @JsonProperty("Pais")
    private String pais;

    @JsonProperty("Canciones")
    private List<Cancion> canciones;

    public String getNombre()          { return nombre; }
    public String getTipo()            { return tipo; }
    public String getPais()            { return pais; }
    public List<Cancion> getCanciones(){ return canciones; }

    public void setNombre(String nombre)             { this.nombre = nombre; }
    public void setTipo(String tipo)                 { this.tipo = tipo; }
    public void setPais(String pais)                 { this.pais = pais; }
    public void setCanciones(List<Cancion> canciones){ this.canciones = canciones; }
}
