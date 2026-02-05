package ec.edu.puntualcheck.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import ec.edu.puntualcheck.R;
import ec.edu.puntualcheck.adapters.AsistenciaAdapter;
import ec.edu.puntualcheck.api.RetrofitClient;
import ec.edu.puntualcheck.models.Asistencia;
import ec.edu.puntualcheck.models.Estudiante;
import ec.edu.puntualcheck.models.EstudianteRepresentante;
import ec.edu.puntualcheck.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RepresentanteFragment extends Fragment {

    private RecyclerView rv;
    private TextView tvNombreHijo, tvPorcentaje, tvTardanzas;
    private Spinner spinnerHijos;
    private View btnLimpiar;
    private HashMap<Integer, String> mapaEstudiantes = new HashMap<>();
    private List<Estudiante> listaHijos = new ArrayList<>();
    private String fechaFiltro = null;
    private int idHijoSeleccionado = -1;

    // 1. DECLARAMOS LA VARIABLE SESSION (Esto faltaba)
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_representante, container, false);

        // 2. INICIALIZAMOS SESSION (Esto faltaba)
        session = new SessionManager(getContext());

        tvNombreHijo = view.findViewById(R.id.tvNombreHijo);
        tvPorcentaje = view.findViewById(R.id.tvPorcentajeAsistencia);
        tvTardanzas = view.findViewById(R.id.tvConteoTardanzas);
        spinnerHijos = view.findViewById(R.id.spinnerHijos);
        btnLimpiar = view.findViewById(R.id.btnLimpiarFiltroRep);
        rv = view.findViewById(R.id.rvAsistenciasHijo);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        view.findViewById(R.id.btnFiltrarRep).setOnClickListener(v -> mostrarCalendario());

        btnLimpiar.setOnClickListener(v -> {
            fechaFiltro = null;
            btnLimpiar.setVisibility(View.GONE);
            if (idHijoSeleccionado != -1) {
                cargarAsistenciasDelHijo(idHijoSeleccionado);
            }
        });

        cargarListaHijosAPI();
        return view;
    }

    private void cargarListaHijosAPI() {
        // Ahora session ya existe y no dará error
        int miIdRep = session.getUserId();

        RetrofitClient.getApiService().getRelaciones().enqueue(new Callback<List<EstudianteRepresentante>>() {
            @Override
            public void onResponse(Call<List<EstudianteRepresentante>> call, Response<List<EstudianteRepresentante>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> nombresSpinner = new ArrayList<>();
                    listaHijos.clear();

                    for (EstudianteRepresentante rel : response.body()) {
                        // Filtramos solo los hijos que pertenecen a este representante
                        if (rel.getRepresentanteId() == miIdRep) {
                            nombresSpinner.add(rel.getNombreEstudiante());

                            Estudiante e = new Estudiante();
                            e.setId(rel.getEstudianteId());
                            e.setNombre(rel.getNombreEstudiante());
                            listaHijos.add(e);
                            mapaEstudiantes.put(e.getId(), e.getNombre());
                        }
                    }

                    if (listaHijos.isEmpty()) {
                        tvNombreHijo.setText("Sin representados vinculados");
                        return;
                    }

                    // Llenar el Spinner con los nombres
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_item, nombresSpinner);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerHijos.setAdapter(adapter);

                    // LOGICA DEL SELECTOR (Cuando cambias de hijo)
                    spinnerHijos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Estudiante seleccionado = listaHijos.get(position);
                            idHijoSeleccionado = seleccionado.getId();
                            tvNombreHijo.setText(seleccionado.getNombre());
                            cargarAsistenciasDelHijo(idHijoSeleccionado);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                }
            }

            @Override
            public void onFailure(Call<List<EstudianteRepresentante>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al cargar vínculos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarAsistenciasDelHijo(int id) {
        RetrofitClient.getApiService().getAsistenciasFiltradas(id, fechaFiltro, fechaFiltro)
                .enqueue(new Callback<List<Asistencia>>() {
                    @Override
                    public void onResponse(Call<List<Asistencia>> call, Response<List<Asistencia>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Asistencia> lista = response.body();
                            calcularEstadisticas(lista);
                            rv.setAdapter(new AsistenciaAdapter(lista, mapaEstudiantes));
                        }
                    }
                    @Override public void onFailure(Call<List<Asistencia>> call, Throwable t) {}
                });
    }

    private void calcularEstadisticas(List<Asistencia> lista) {
        if (lista.isEmpty()) {
            tvPorcentaje.setText("0%");
            tvTardanzas.setText("0");
            return;
        }
        int presentes = 0, tardanzas = 0;
        for (Asistencia a : lista) {
            if (a.getEstado().equalsIgnoreCase("PRESENTE")) presentes++;
            if (a.getEstado().equalsIgnoreCase("TARDANZA")) tardanzas++;
        }
        int pct = (presentes * 100) / lista.size();
        tvPorcentaje.setText(pct + "%");
        tvTardanzas.setText(String.valueOf(tardanzas));
    }

    private void mostrarCalendario() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(getContext(), (view, y, m, d) -> {
            String mes = (m + 1) < 10 ? "0" + (m + 1) : String.valueOf(m + 1);
            String dia = d < 10 ? "0" + d : String.valueOf(d);
            fechaFiltro = y + "-" + mes + "-" + dia;
            btnLimpiar.setVisibility(View.VISIBLE);
            cargarAsistenciasDelHijo(idHijoSeleccionado);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }
}