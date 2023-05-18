package jacques.raul.uv.photoaday;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
            // Crear una notificación
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "canal_notificaciones")
                    .setSmallIcon(R.drawable.icon_cam)
                    .setContentTitle("¡Es hora de tu foto diaria!")
                    .setContentText("Sube tu foto diaria para cumplir el reto PhotoADay!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            // Crear un PendingIntent para abrir la aplicación al hacer clic en la notificación
            Intent mainIntent = new Intent(context, LoginActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);

            // Mostrar la notificación
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(1, builder.build());
        }
    }
}