package jacques.raul.uv.photoaday;

import android.net.Uri;

import java.util.Date;

public class FotoModel {

    String email;
    String ubicacion;
    String fechaFoto;
    String foto;

    public FotoModel(){}

    public FotoModel(String email, String ubicacion, String fechaFoto, String foto) {
        this.email = email;
        this.ubicacion = ubicacion;
        this.fechaFoto = fechaFoto;
        this.foto = foto;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String direccion) {
        this.ubicacion = direccion;
    }

    public String getFechaFoto() {
        return fechaFoto;
    }

    public void setFechaFoto(String fechaFoto) {
        this.fechaFoto = fechaFoto;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
