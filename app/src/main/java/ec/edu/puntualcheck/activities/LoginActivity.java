package ec.edu.puntualcheck.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import ec.edu.puntualcheck.MainActivity;
import ec.edu.puntualcheck.R;
import ec.edu.puntualcheck.api.RetrofitClient;
import ec.edu.puntualcheck.models.Estudiante;
import ec.edu.puntualcheck.models.Representante;
import ec.edu.puntualcheck.models.Usuario;
import ec.edu.puntualcheck.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText etEmail, etPassword;
    Button btnLogin;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new SessionManager(this);
        if (session.isLoggedIn()) { irAlMain(); }

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> ejecutarLoginReal());
    }

    private void ejecutarLoginReal() {
        String emailStr = etEmail.getText().toString().trim();
        String passStr = etPassword.getText().toString().trim();

        if (emailStr.isEmpty() || passStr.isEmpty()) {
            Toast.makeText(this, "Por favor completa los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Paso 1: Obtener todos los usuarios de Railway
        RetrofitClient.getApiService().getUsuarios().enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Usuario usuarioEncontrado = null;
                    for (Usuario u : response.body()) {
                        if (u.getCorreo().equalsIgnoreCase(emailStr) && u.getPasswordHash().equals(passStr)) {
                            usuarioEncontrado = u;
                            break;
                        }
                    }

                    if (usuarioEncontrado != null) {
                        String rol = usuarioEncontrado.getRol().toUpperCase();

                        // Paso 2: Lógica según el Rol
                        if (rol.equals("ESTUDIANTE")) {
                            vincularEstudianteYEntrar(usuarioEncontrado);
                        }
                        else if (rol.equals("REPRESENTANTE")) {
                            vincularRepresentanteYEntrar(usuarioEncontrado);
                        }
                        else {
                            // Si es Docente o Admin, guardamos su ID de usuario normal
                            session.saveSession(usuarioEncontrado.getId(), usuarioEncontrado.getRol(), usuarioEncontrado.getNombre());
                            irAlMain();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error de conexión con Railway", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // BUSCA EL ID EN LA TABLA ESTUDIANTES
    private void vincularEstudianteYEntrar(Usuario u) {
        RetrofitClient.getApiService().getEstudiantes().enqueue(new Callback<List<Estudiante>>() {
            @Override
            public void onResponse(Call<List<Estudiante>> call, Response<List<Estudiante>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Estudiante e : response.body()) {
                        if (e.getUsuarioId() == u.getId()) {
                            session.saveSession(e.getId(), u.getRol(), u.getNombre());
                            irAlMain();
                            return;
                        }
                    }
                    Toast.makeText(LoginActivity.this, "No se encontró perfil de estudiante", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<List<Estudiante>> call, Throwable t) {}
        });
    }

    // BUSCA EL ID EN LA TABLA REPRESENTANTES
    private void vincularRepresentanteYEntrar(Usuario u) {
        RetrofitClient.getApiService().getRepresentantes().enqueue(new Callback<List<Representante>>() {
            @Override
            public void onResponse(Call<List<Representante>> call, Response<List<Representante>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Representante r : response.body()) {
                        if (r.getUsuarioId() == u.getId()) {
                            session.saveSession(r.getId(), u.getRol(), u.getNombre());
                            irAlMain();
                            return;
                        }
                    }
                    Toast.makeText(LoginActivity.this, "No se encontró perfil de representante", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<List<Representante>> call, Throwable t) {}
        });
    }

    private void irAlMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}