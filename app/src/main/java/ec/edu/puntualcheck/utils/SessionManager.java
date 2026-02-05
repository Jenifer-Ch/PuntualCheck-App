package ec.edu.puntualcheck.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "PuntualCheckPrefs";
    private static final String KEY_USER_ID = "userId"; // Nuevo campo
    private static final String KEY_NOMBRE = "nombre";
    private static final String KEY_ROL = "rol";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveSession(int userId, String rol, String nombre) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_ROL, rol);
        editor.putString(KEY_NOMBRE, nombre);
        editor.apply();
    }

    public boolean isLoggedIn() { return pref.getBoolean(KEY_IS_LOGGED_IN, false); }
    public int getUserId() { return pref.getInt(KEY_USER_ID, 0); }
    public String getRol() { return pref.getString(KEY_ROL, ""); }
    public String getNombre() { return pref.getString(KEY_NOMBRE, ""); }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}