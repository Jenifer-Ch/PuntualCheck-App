package ec.edu.puntualcheck.models;

public class Estudiante {
    private int id;
    private int usuarioId;
    private String nombre;
    private String codigo;

    // Constructor vacío (necesario para crear el objeto manualmente)
    public Estudiante() {}

    // --- SETTERS (Esto es lo que te faltaba para quitar el error rojo) ---
    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    // --- GETTERS ---
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getUsuarioId() { return usuarioId; }
    public String getCodigo() { return codigo; }
}