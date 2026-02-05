package ec.edu.puntualcheck.api;

import java.util.List;
import ec.edu.puntualcheck.models.Asistencia;
import ec.edu.puntualcheck.models.Estudiante;
import ec.edu.puntualcheck.models.EstudianteRepresentante;
import ec.edu.puntualcheck.models.Notificacion;
import ec.edu.puntualcheck.models.Representante;
import ec.edu.puntualcheck.models.Usuario;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // --- SECCIÓN USUARIOS Y PERFILES ---
    @GET("api/usuarios")
    Call<List<Usuario>> getUsuarios();

    @GET("api/estudiantes")
    Call<List<Estudiante>> getEstudiantes();

    @GET("api/representantes")
    Call<List<Representante>> getRepresentantes();

    // Obtener la tabla de vínculos para saber quién es hijo de quién
    @GET("api/estudiante-representante")
    Call<List<EstudianteRepresentante>> getRelaciones();


    // --- SECCIÓN ASISTENCIAS ---
    @POST("api/asistencias")
    Call<ResponseBody> registrarAsistencia(@Body Asistencia asistencia);

    // Para el Docente (Ver todo o filtrar por fecha global)
    @GET("api/asistencias")
    Call<List<Asistencia>> getAsistenciasDocente(
            @Query("fechaDesde") String d,
            @Query("fechaHasta") String h
    );

    // Para el Estudiante/Representante (Ver por ID y filtrar por fecha)
    @GET("api/asistencias")
    Call<List<Asistencia>> getAsistenciasFiltradas(
            @Query("estudianteId") Integer id,
            @Query("fechaDesde") String d,
            @Query("fechaHasta") String h
    );

    // Método simple por si solo necesitas el ID
    @GET("api/asistencias")
    Call<List<Asistencia>> getAsistenciasPorEstudiante(@Query("estudianteId") int estudianteId);


    // --- SECCIÓN NOTIFICACIONES ---
    @GET("api/notificaciones")
    Call<List<Notificacion>> getNotificaciones(@Query("representanteId") int representanteId);

    @POST("api/notificaciones")
    Call<ResponseBody> crearNotificacion(@Body Notificacion notificacion);
}