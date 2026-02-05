package ec.edu.puntualcheck.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashMap;
import java.util.List;
import ec.edu.puntualcheck.R;
import ec.edu.puntualcheck.models.Asistencia;
import ec.edu.puntualcheck.utils.StatusHelper;

public class AsistenciaAdapter extends RecyclerView.Adapter<AsistenciaAdapter.AsistenciaViewHolder> {

    private List<Asistencia> listaAsistencias;
    private HashMap<Integer, String> mapaEstudiantes;

    // El constructor ahora pide el mapa de nombres
    public AsistenciaAdapter(List<Asistencia> listaAsistencias, HashMap<Integer, String> mapaEstudiantes) {
        this.listaAsistencias = listaAsistencias;
        this.mapaEstudiantes = mapaEstudiantes;
    }

    @NonNull
    @Override
    public AsistenciaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_asistencia, parent, false);
        return new AsistenciaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AsistenciaViewHolder holder, int position) {
        Asistencia asistencia = listaAsistencias.get(position);

        // BUSCAMOS EL NOMBRE EN EL MAPA USANDO EL ID
        String nombre = "ID: " + asistencia.getEstudianteId();
        if (mapaEstudiantes != null && mapaEstudiantes.containsKey(asistencia.getEstudianteId())) {
            nombre = mapaEstudiantes.get(asistencia.getEstudianteId());
        }

        // Formatear Fecha (Quitar la T00:00:00)
        String fecha = asistencia.getFecha();
        if (fecha != null && fecha.contains("T")) {
            fecha = fecha.split("T")[0];
        }

        holder.tvFecha.setText(nombre);
        holder.tvEstado.setText(asistencia.getEstado() + " | " + fecha + " " + asistencia.getHora());

        int color = StatusHelper.getStatusColor(asistencia.getEstado());
        holder.indicadorColor.setBackgroundColor(color);
        holder.tvEstado.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        return listaAsistencias.size();
    }

    public static class AsistenciaViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvEstado;
        View indicadorColor;

        public AsistenciaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            indicadorColor = itemView.findViewById(R.id.indicadorColor);
        }
    }
}