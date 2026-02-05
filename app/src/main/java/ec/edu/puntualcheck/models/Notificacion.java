package ec.edu.puntualcheck.models;

public class Notificacion {
    private int id;
    private int estudianteId;
    private int representanteId;
    private String fechaEvento;
    private String tipo;
    private String canal;
    private String estadoEnvio;
    private String detalle;

    // --- CONSTRUCTOR VACÍO ---
    public Notificacion() {}

    // --- SETTERS ---
    public void setEstudianteId(int estudianteId) { this.estudianteId = estudianteId; }
    public void setRepresentanteId(int representanteId) { this.representanteId = representanteId; }
    public void setFechaEvento(String fechaEvento) { this.fechaEvento = fechaEvento; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setCanal(String canal) { this.canal = canal; }
    public void setDetalle(String detalle) { this.detalle = detalle; }

    // --- GETTERS ---
    public int getId() { return id; }
    public String getTipo() { return tipo; }
    public String getDetalle() { return detalle; }
    public String getFechaEvento() { return fechaEvento; }
}