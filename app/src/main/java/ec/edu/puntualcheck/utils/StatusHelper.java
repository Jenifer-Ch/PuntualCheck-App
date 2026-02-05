package ec.edu.puntualcheck.utils;

import android.graphics.Color;

public class StatusHelper {
    public static int getStatusColor(String estado) {
        if (estado == null) return Color.GRAY;
        switch (estado.toUpperCase()) {
            case "PRESENTE": return Color.parseColor("#000149");
            case "TARDANZA": return Color.parseColor("#f74900");
            case "FALTA": return Color.RED;
            default: return Color.GRAY;
        }
    }
}