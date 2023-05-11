package jacques.raul.uv.photoaday;

public class UsuarioModel {
    String email;
    String contrasenya;
    String nombre;
    String apellidos;

    public UsuarioModel(String email, String contrasenya, String nombre) {
        this.email = email;
        this.contrasenya = contrasenya;
        this.nombre = nombre;
    }

    public UsuarioModel(String email, String contrasenya, String nombre, String apellidos) {
        this.email = email;
        this.contrasenya = contrasenya;
        this.nombre = nombre;
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasenya() {
        return contrasenya;
    }

    public void setContrasenya(String contrasenya) {
        this.contrasenya = contrasenya;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }
}
