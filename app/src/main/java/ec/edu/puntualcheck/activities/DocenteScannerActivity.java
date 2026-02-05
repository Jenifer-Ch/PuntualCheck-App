package ec.edu.puntualcheck.activities;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import ec.edu.puntualcheck.R;
import ec.edu.puntualcheck.api.RetrofitClient;
import ec.edu.puntualcheck.models.Asistencia;
import ec.edu.puntualcheck.models.Estudiante;
import ec.edu.puntualcheck.models.Notificacion;
import ec.edu.puntualcheck.models.EstudianteRepresentante;
import ec.edu.puntualcheck.services.FCMService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocenteScannerActivity extends AppCompatActivity {

    private DecoratedBarcodeView barcodeView;
    private TextView tvResultado, tvEstudianteNombre;
    private ImageView imgEstado;
    private boolean escaneando = true;

    // El "Mapa" para guardar nombres localmente: ID -> Nombre
    private HashMap<Integer, String> mapaEstudiantes = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_escanear);

        barcodeView = findViewById(R.id.barcode_scanner);
        tvResultado = findViewById(R.id.tvResultado);
        tvEstudianteNombre = findViewById(R.id.tvEstudianteNombre);
        imgEstado = findViewById(R.id.imgEstado);

        // PASO 1: Descargar nombres apenas abre la cámara para tenerlos en memoria
        descargarNombresEstudiantes();

        // PASO 2: Iniciar escaneo continuo
        barcodeView.decodeContinuous(callback);
    }

    private void descargarNombresEstudiantes() {
        RetrofitClient.getApiService().getEstudiantes().enqueue(new Callback<List<Estudiante>>() {
            @Override
            public void onResponse(Call<List<Estudiante>> call, Response<List<Estudiante>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Estudiante e : response.body()) {
                        mapaEstudiantes.put(e.getId(), e.getNombre());
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Estudiante>> call, Throwable t) {
                Toast.makeText(DocenteScannerActivity.this, "Error cargando nombres", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() == null || !escaneando) return;
            escaneando = false;
            registrarAsistenciaReal(result.getText());
        }
        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {}
    };

    private void registrarAsistenciaReal(String token) {
        tvResultado.setText("Sincronizando...");
        tvEstudianteNombre.setText("Validando...");

        final int idEst;
        try {
            idEst = Integer.parseInt(token);
        } catch (Exception e) {
            tvResultado.setText("❌ QR INVÁLIDO");
            new Handler().postDelayed(this::reiniciarEscaner, 2000);
            return;
        }

        final String nombreEst = mapaEstudiantes.containsKey(idEst) ?
                mapaEstudiantes.get(idEst) : "Estudiante ID: " + idEst;

        SimpleDateFormat sdfFecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        TimeZone tz = TimeZone.getTimeZone("America/Guayaquil");
        sdfFecha.setTimeZone(tz);
        sdfHora.setTimeZone(tz);

        String fechaStr = sdfFecha.format(new Date());
        String horaStr = sdfHora.format(new Date());

        Asistencia asis = new Asistencia(idEst, fechaStr, horaStr, "PRESENTE", "MOVIL", "Scanner App");

        RetrofitClient.getApiService().registrarAsistencia(asis).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() || response.code() == 409) {

                    if (response.code() == 409) {
                        tvResultado.setText("⚠️ YA REGISTRADO");
                        tvResultado.setTextColor(getResources().getColor(R.color.modern_orange));
                        imgEstado.setImageResource(android.R.drawable.ic_dialog_alert);
                        imgEstado.setColorFilter(getResources().getColor(R.color.modern_orange));
                    } else {
                        tvResultado.setText("✅ REGISTRO EXITOSO");
                        tvResultado.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                        imgEstado.setImageResource(android.R.drawable.checkbox_on_background);
                        imgEstado.setColorFilter(getResources().getColor(android.R.color.holo_green_dark));

                        // BUSCA AL REPRESENTANTE PARA CREAR LA NOTIFICACIÓN
                        enviarNotificacionAPI(idEst, nombreEst, fechaStr, horaStr);
                    }

                    tvEstudianteNombre.setText(nombreEst);

                } else {
                    tvResultado.setText("❌ ERROR API: " + response.code());
                    imgEstado.setImageResource(android.R.drawable.ic_delete);
                }

                new Handler().postDelayed(() -> reiniciarEscaner(), 3000);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                tvResultado.setText("❌ SIN CONEXIÓN");
                new Handler().postDelayed(() -> reiniciarEscaner(), 3000);
            }
        });
    }

    private void enviarNotificacionAPI(int idEst, String nombre, String fecha, String hora) {
        // 1. Consultamos la tabla de relaciones REALES de Railway
        RetrofitClient.getApiService().getRelaciones().enqueue(new Callback<List<EstudianteRepresentante>>() {
            @Override
            public void onResponse(Call<List<EstudianteRepresentante>> call, Response<List<EstudianteRepresentante>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean representanteEncontrado = false;
                    for (EstudianteRepresentante rel : response.body()) {
                        // 2. SOLO enviamos la notificación si el estudianteId coincide con el que escaneamos
                        if (rel.getEstudianteId() == idEst) {
                            crearRegistroNotificacion(idEst, rel.getRepresentanteId(), nombre, fecha, hora);
                            representanteEncontrado = true;
                        }
                    }
                    if (!representanteEncontrado) {
                        // Si llega aquí, es que te falta crear el vínculo en Postman para este alumno
                        android.util.Log.e("PuntualCheck", "No hay representante vinculado para el alumno ID: " + idEst);
                    }
                }
            }
            @Override public void onFailure(Call<List<EstudianteRepresentante>> call, Throwable t) {}
        });
    }

    private void crearRegistroNotificacion(int idEst, int idRep, String nombre, String fecha, String hora) {
        Notificacion noti = new Notificacion();
        noti.setEstudianteId(idEst);
        noti.setRepresentanteId(idRep);
        noti.setFechaEvento(fecha + "T" + hora);
        noti.setTipo("ASISTENCIA");
        noti.setCanal("APP");
        noti.setDetalle("El estudiante " + nombre + " registró su entrada a las " + hora);

        RetrofitClient.getApiService().crearNotificacion(noti).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // Notificación guardada en la base de datos MySQL con éxito
            }
            @Override public void onFailure(Call<ResponseBody> call, Throwable t) {}
        });
    }

    private void reiniciarEscaner() {
        tvResultado.setText("Listo para escanear");
        tvResultado.setTextColor(getResources().getColor(R.color.modern_blue_dark));
        tvEstudianteNombre.setText("Aproxime el código QR");
        imgEstado.setImageResource(android.R.drawable.ic_menu_camera);
        imgEstado.setColorFilter(null);
        escaneando = true;
    }

    @Override protected void onResume() { super.onResume(); barcodeView.resume(); }
    @Override protected void onPause() { super.onPause(); barcodeView.pause(); }
}