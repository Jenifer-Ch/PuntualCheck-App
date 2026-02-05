package ec.edu.puntualcheck.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import ec.edu.puntualcheck.R;
import ec.edu.puntualcheck.adapters.AsistenciaAdapter;
import ec.edu.puntualcheck.api.RetrofitClient;
import ec.edu.puntualcheck.models.Asistencia;
import ec.edu.puntualcheck.models.Estudiante;
import ec.edu.puntualcheck.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistorialAsistenciaFragment extends Fragment {

    private RecyclerView rvAsistencias;
    private SwipeRefreshLayout swipeRefresh;
    private SessionManager session;
    private TextView tvAsistenciaPct, tvTardanzasCnt, tvLabel;
    private View btnLimpiar, containerStats;
    private HashMap<Integer, String> mapaNombres = new HashMap<>();
    private String fechaSeleccionada = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historial, container, false);

        session = new SessionManager(getContext());
        rvAsistencias = view.findViewById(R.id.rvAsistencias);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        tvAsistenciaPct = view.findViewById(R.id.tvAsistenciaPorcentaje);
        tvTardanzasCnt = view.findViewById(R.id.tvTardanzasConteo);
        tvLabel = view.findViewById(R.id.tvLabelHistorial);
        btnLimpiar = view.findViewById(R.id.btnLimpiarFiltro);
        containerStats = view.findViewById(R.id.containerStats);

        rvAsistencias.setLayoutManager(new LinearLayoutManager(getContext()));

        // Configuración inicial por ROL
        if (session.getRol().equals("DOCENTE")) {
            containerStats.setVisibility(View.GONE);
            tvLabel.setText("Registros Globales");
        }

        view.findViewById(R.id.btnFiltrarFecha).setOnClickListener(v -> mostrarCalendario());
        btnLimpiar.setOnClickListener(v -> {
            fechaSeleccionada = null;
            btnLimpiar.setVisibility(View.GONE);
            cargarDatos();
        });

        swipeRefresh.setOnRefreshListener(this::cargarDatos);
        cargarDatos();

        return view;
    }

    private void cargarDatos() {
        swipeRefresh.setRefreshing(true);
        RetrofitClient.getApiService().getEstudiantes().enqueue(new Callback<List<Estudiante>>() {
            @Override
            public void onResponse(Call<List<Estudiante>> call, Response<List<Estudiante>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Estudiante e : response.body()) {
                        mapaNombres.put(e.getId(), e.getNombre());
                    }
                    obtenerAsistenciasAPI();
                }
            }
            @Override public void onFailure(Call<List<Estudiante>> call, Throwable t) { swipeRefresh.setRefreshing(false); }
        });
    }

    private void obtenerAsistenciasAPI() {
        Call<List<Asistencia>> call;
        if (session.getRol().equals("DOCENTE")) {
            call = RetrofitClient.getApiService().getAsistenciasDocente(fechaSeleccionada, fechaSeleccionada);
        } else {
            call = RetrofitClient.getApiService().getAsistenciasFiltradas(session.getUserId(), fechaSeleccionada, fechaSeleccionada);
        }

        call.enqueue(new Callback<List<Asistencia>>() {
            @Override
            public void onResponse(Call<List<Asistencia>> call, Response<List<Asistencia>> response) {
                swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Asistencia> lista = response.body();

                    // ORDENAR POR ID DESCENDENTE (Lo más nuevo primero)
                    Collections.sort(lista, (a1, a2) -> Integer.compare(a2.getId(), a1.getId()));

                    rvAsistencias.setAdapter(new AsistenciaAdapter(lista, mapaNombres));

                    if (!session.getRol().equals("DOCENTE")) {
                        actualizarDashboard(lista);
                    }
                }
            }
            @Override public void onFailure(Call<List<Asistencia>> call, Throwable t) { swipeRefresh.setRefreshing(false); }
        });
    }

    private void actualizarDashboard(List<Asistencia> lista) {
        if (lista.isEmpty()) {
            tvAsistenciaPct.setText("0%");
            tvTardanzasCnt.setText("0");
            return;
        }
        int presentes = 0, tardanzas = 0;
        for (Asistencia a : lista) {
            if (a.getEstado().equalsIgnoreCase("PRESENTE")) presentes++;
            if (a.getEstado().equalsIgnoreCase("TARDANZA")) tardanzas++;
        }
        int pct = (presentes * 100) / lista.size();
        tvAsistenciaPct.setText(pct + "%");
        tvTardanzasCnt.setText(String.valueOf(tardanzas));
    }

    private void mostrarCalendario() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(getContext(), (view, y, m, d) -> {
            String mes = (m + 1) < 10 ? "0" + (m + 1) : String.valueOf(m + 1);
            String dia = d < 10 ? "0" + d : String.valueOf(d);
            fechaSeleccionada = y + "-" + mes + "-" + dia;
            btnLimpiar.setVisibility(View.VISIBLE); // Mostramos el botón de borrar filtro
            cargarDatos();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }
}