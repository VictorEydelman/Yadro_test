package mad.project.aidlservice

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

/**
 * Класс AidlService наследуется от Service и реализует интерфейс AIDL для предоставления функциональности клиентам.
 */
class AidlService : Service() {

    private val aidlImpl = AidlImpl(this)
    /**
     * Метод onCreate вызывается при создании сервиса.
     */
    override fun onCreate() {
        Log.i("AidlService", "onCreate:")
        super.onCreate()
    }

    /**
     * Метод onBind вызывается при связывании клиента с сервисом.
     * Возвращает объект IBinder, который клиент будет использовать для взаимодействия с сервисом.
     *
     * @param intent Интент, который запустил связывание.
     * @return IBinder объект, который позволяет клиенту взаимодействовать с сервисом.
     */
    override fun onBind(intent: Intent?): IBinder {
        Log.i("AidlService", "onBind:")
        return aidlImpl
    }

    /**
     * Метод onDestroy вызывается при уничтожении сервиса.
     * Здесь можно выполнять очистку ресурсов.
     */
    override fun onDestroy() {
        Log.i("AidlService", "onDestroy:")
        super.onDestroy()
    }
}
