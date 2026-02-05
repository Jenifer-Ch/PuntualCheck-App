package ec.edu.puntualcheck.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import ec.edu.puntualcheck.MainActivity;
import ec.edu.puntualcheck.R;

public class FCMService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Esto se ejecuta cuando llega una notificación real de Firebase
        if (remoteMessage.getNotification() != null) {
            mostrarNotificacion(
                    getApplicationContext(),
                    remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody()
            );
        }
    }

    // METODO PARA LANZAR NOTIFICACIONES
    public static void mostrarNotificacion(Context context, String titulo, String mensaje) {
        String channelId = "puntual_check_channel";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Configuración para Android 8.0 o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Asistencias", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.logo_institucion)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(1, builder.build());
    }
}