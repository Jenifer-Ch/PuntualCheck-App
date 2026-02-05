package ec.edu.puntualcheck;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.google.android.material.navigation.NavigationView;

import ec.edu.puntualcheck.activities.DocenteScannerActivity;
import ec.edu.puntualcheck.activities.EstudianteQrFragment;
import ec.edu.puntualcheck.activities.HistorialAsistenciaFragment;
import ec.edu.puntualcheck.activities.LoginActivity;
import ec.edu.puntualcheck.activities.RepresentanteFragment;
import ec.edu.puntualcheck.activities.NotificacionesFragment;
import ec.edu.puntualcheck.utils.SessionManager;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Inicialización de componentes
        session = new SessionManager(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // 2. Configuración del menú hamburguesa
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 3. Personalizar menú según el Rol guardado en el Login
        configurarMenuPorRol();

        // 4. Cargar pantalla inicial según el Rol
        if (savedInstanceState == null) {
            cargarPantallaInicial();
        }

        // 5. Manejo moderno del botón "Atrás"
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    MainActivity.super.onBackPressed();
                }
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                androidx.core.app.ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    private void configurarMenuPorRol() {
        Menu menu = navigationView.getMenu();
        String rol = (session.getRol() != null) ? session.getRol().toUpperCase() : "ESTUDIANTE";

        // Ocultar todos los grupos
        menu.setGroupVisible(R.id.group_estudiante, false);
        menu.setGroupVisible(R.id.group_docente, false);
        menu.setGroupVisible(R.id.group_representante, false);

        // Mostrar solo el grupo correspondiente
        switch (rol) {
            case "DOCENTE":
                menu.setGroupVisible(R.id.group_docente, true);
                break;
            case "REPRESENTANTE":
                menu.setGroupVisible(R.id.group_representante, true);
                break;
            default: // ESTUDIANTE
                menu.setGroupVisible(R.id.group_estudiante, true);
                break;
        }
    }

    private void cargarPantallaInicial() {
        String rol = (session.getRol() != null) ? session.getRol().toUpperCase() : "ESTUDIANTE";

        // El estudiante inicia viendo su QR, el representante su hijo y el docente el historial
        if (rol.equals("ESTUDIANTE")) {
            reemplazarFragmento(new EstudianteQrFragment());
        } else if (rol.equals("REPRESENTANTE")) {
            reemplazarFragmento(new RepresentanteFragment());
        } else {
            reemplazarFragmento(new HistorialAsistenciaFragment());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // Lógica de navegación detallada
        if (id == R.id.nav_qr) {
            reemplazarFragmento(new EstudianteQrFragment());
        }
        else if (id == R.id.nav_mis_asistencias || id == R.id.nav_historial_lecturas) {
            reemplazarFragmento(new HistorialAsistenciaFragment());
        }
        else if (id == R.id.nav_asistencia_hijo) {
            reemplazarFragmento(new RepresentanteFragment());
        }
        else if (id == R.id.nav_notificaciones) {
            reemplazarFragmento(new NotificacionesFragment());
        }
        else if (id == R.id.nav_escanear) {
            // Abrimos la Activity del escáner
            startActivity(new Intent(this, DocenteScannerActivity.class));
        }
        else if (id == R.id.nav_logout) {
            cerrarSesion();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void reemplazarFragmento(Fragment fragmento) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragmento)
                .commit();

        if (getSupportActionBar() != null) {
            if (fragmento instanceof EstudianteQrFragment) getSupportActionBar().setTitle("Mi Código QR");
            else if (fragmento instanceof RepresentanteFragment) getSupportActionBar().setTitle("Mi Representado");
            else if (fragmento instanceof NotificacionesFragment) getSupportActionBar().setTitle("Notificaciones");
            else getSupportActionBar().setTitle("Historial Puntual Check");
        }
    }

    private void cerrarSesion() {
        session.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}