package entidades;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Artistas {

    @JsonProperty("Artistas")
    private List<Artista> artistas;

    public List<Artista> getArtistas()              { return artistas; }
    public void setArtistas(List<Artista> artistas) { this.artistas = artistas; }
}
