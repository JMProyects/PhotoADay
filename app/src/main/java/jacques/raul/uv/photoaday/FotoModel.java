package jacques.raul.uv.photoaday;

import android.net.Uri;

import java.util.Date;

public class FotoModel {

    UsuarioModel usuario;
    String ubicacion;
    Date fechaFoto;
    Uri foto;

    public FotoModel(String direccion, Date fechaFoto, Uri foto) {
        this.ubicacion = direccion;
        this.fechaFoto = fechaFoto;
        this.foto = foto;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String direccion) {
        this.ubicacion = direccion;
    }

    public Date getFechaFoto() {
        return fechaFoto;
    }

    public void setFechaFoto(Date fechaFoto) {
        this.fechaFoto = fechaFoto;
    }

    public Uri getFoto() {
        return foto;
    }

    public void setFoto(Uri foto) {
        this.foto = foto;
    }

    public UsuarioModel getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioModel usuario) {
        this.usuario = usuario;
    }
}
