package ec.edu.puntualcheck.models;

public class Usuario {
    private int id;
    private String nombre;
    private String correo;
    private String passwordHash;
    private String rol;

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getPasswordHash() { return passwordHash; }
    public String getRol() { return rol; }
}