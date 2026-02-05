package ec.edu.puntualcheck.utils;

import android.graphics.Color;

public class StatusHelper {
    public static int getStatusColor(String estado) {
        if (estado == null) return Color.GRAY;
        switch (estado.toUpperCase()) {
            case "PRESENTE": return Color.parseColor("#000149"); // Azul Navy
            case "TARDANZA": return Color.parseColor("#f74900"); // Naranja PuntualCheck
            case "FALTA": return Color.RED;
            default: return Color.GRAY;
        }
    }
}