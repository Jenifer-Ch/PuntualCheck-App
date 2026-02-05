package ec.edu.puntualcheck.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import ec.edu.puntualcheck.R;
import ec.edu.puntualcheck.models.Notificacion;

public class NotificacionAdapter extends RecyclerView.Adapter<NotificacionAdapter.NotiViewHolder> {

    private List<Notificacion> lista;

    public NotificacionAdapter(List<Notificacion> lista) { this.lista = lista; }

    @NonNull
    @Override
    public NotiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notificacion, parent, false);
        return new NotiViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotiViewHolder holder, int position) {
        Notificacion n = lista.get(position);
        holder.tvTitulo.setText(n.getTipo());
        holder.tvDetalle.setText(n.getDetalle());

        String fecha = n.getFechaEvento();
        if (fecha != null && fecha.contains("T")) fecha = fecha.split("T")[0];
        holder.tvFecha.setText(fecha);
    }

    @Override
    public int getItemCount() { return lista.size(); }

    public static class NotiViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDetalle, tvFecha;
        public NotiViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTituloNoti);
            tvDetalle = itemView.findViewById(R.id.tvDetalleNoti);
            tvFecha = itemView.findViewById(R.id.tvFechaNoti);
        }
    }
}