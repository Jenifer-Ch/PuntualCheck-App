package ec.edu.puntualcheck.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.Collections;
import java.util.List;
import ec.edu.puntualcheck.R;
import ec.edu.puntualcheck.adapters.NotificacionAdapter;
import ec.edu.puntualcheck.api.RetrofitClient;
import ec.edu.puntualcheck.models.Notificacion;
import ec.edu.puntualcheck.services.FCMService;
import ec.edu.puntualcheck.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificacionesFragment extends Fragment {

    private RecyclerView rv;
    private SwipeRefreshLayout swipe;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_historial, container, false);

        session = new SessionManager(getContext());
        rv = v.findViewById(R.id.rvAsistencias);
        swipe = v.findViewById(R.id.swipeRefresh);

        // UI Setup
        v.findViewById(R.id.containerStats).setVisibility(View.GONE);
        v.findViewById(R.id.btnFiltrarFecha).setVisibility(View.GONE);
        TextView label = v.findViewById(R.id.tvLabelHistorial);
        if (label != null) label.setText("Buzón de Notificaciones");

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        swipe.setOnRefreshListener(this::cargarNotificaciones);

        cargarNotificaciones();
        return v;
    }

    private void cargarNotificaciones() {
        swipe.setRefreshing(true);
        int miIdRep = session.getUserId();

        RetrofitClient.getApiService().getNotificaciones(miIdRep).enqueue(new Callback<List<Notificacion>>() {
            @Override
            public void onResponse(Call<List<Notificacion>> call, Response<List<Notificacion>> response) {
                swipe.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Notificacion> lista = response.body();

                    // ORDENAR POR ID DESCENDENTE
                    Collections.sort(lista, (n1, n2) -> Integer.compare(n2.getId(), n1.getId()));

                    rv.setAdapter(new NotificacionAdapter(lista));
                }
            }

            @Override
            public void onFailure(Call<List<Notificacion>> call, Throwable t) {
                swipe.setRefreshing(false);
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}