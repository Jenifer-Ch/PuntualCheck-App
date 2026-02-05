package ec.edu.puntualcheck.models;

public class Asistencia {
    private int id;
    private int estudianteId;
    private String fecha;
    private String hora;
    private String estado;
    private String origen;
    private String observacion;

    // Constructor vacío (necesario para Retrofit)
    public Asistencia() {}

    // Constructor para registrar asistencia
    public Asistencia(int estudianteId, String fecha, String hora, String estado, String origen, String observacion) {
        this.estudianteId = estudianteId;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.origen = origen;
        this.observacion = observacion;
    }

    // --- GETTERS ---

    public int getId() { return id; }

    public int getEstudianteId() { return estudianteId; }

    public String getFecha() { return fecha; }

    public String getHora() { return hora; }

    public String getEstado() { return estado; }

    public String getOrigen() { return origen; }

    public String getObservacion() { return observacion; }
}