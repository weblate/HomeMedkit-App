package ru.application.homemedkit.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import ru.application.homemedkit.data.MedicineDatabase
import ru.application.homemedkit.utils.*
import ru.application.homemedkit.utils.extensions.goAsync

class ActionGroupReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) = goAsync {
        val database = MedicineDatabase.getInstance(context)
        val manager = NotificationManagerCompat.from(context)

        manager.cancel(Int.MAX_VALUE)
        for (item in manager.activeNotifications) {
            if (item.packageName == context.packageName && item.notification.extras.containsKey(IS_ENOUGH_IN_STOCK)) {
                val medicineId = item.notification.extras.getLong(ID)
                val takenId = item.notification.extras.getLong(TAKEN_ID)
                val amount = item.notification.extras.getDouble(BLANK)

                manager.cancel(takenId.toInt())
                database.takenDAO().setNotified(takenId)
                if (intent.action == TYPE) {
                    database.takenDAO().setTaken(takenId, true, System.currentTimeMillis())
                    database.medicineDAO().intakeMedicine(medicineId, amount)
                }
            }
        }

        context.sendBroadcast(Intent(ACTION_CLOSE_ALL_FULL_SCREEN_INTENTS))
    }
}