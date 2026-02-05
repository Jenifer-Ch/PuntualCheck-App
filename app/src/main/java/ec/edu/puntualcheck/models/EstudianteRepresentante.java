package ec.edu.puntualcheck.models;

public class EstudianteRepresentante {
    private int id;
    private int estudianteId;
    private int representanteId;
    private String nombreEstudiante;

    public int getEstudianteId() { return estudianteId; }
    public int getRepresentanteId() { return representanteId; }
    public String getNombreEstudiante() { return nombreEstudiante; }
}