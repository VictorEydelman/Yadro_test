package mad.project.aidlservice

import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat

/**
 * Класс MainActivity - это класс, наследуемыq от AppCompatActivity, и является входной точкой приложения
 *
 * В нём идёт подключение к клиенту и запуск приложения
 */
class MainActivity: AppCompatActivity() {
    private val TAG = "MainActivity"

    private lateinit var iAidlInterface: IAidlInterface

    /**
     * Объект ServiceConnection для связывания с AIDL-сервисом.
     * Содержит методы, которые вызываются при подключении и отключении от сервиса.
     */
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        /**
        * Вызывается при успешном подключении к сервису.
        *
        * @param name Имя сервиса.
        * @param service IBinder сервиса, который предоставляет доступ к интерфейсу AIDL.
        */
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.i(TAG, "onServiceConnected: name = $name, service = $service")
            iAidlInterface = IAidlInterface.Stub.asInterface(service)
        }

        /**
         * Вызывается при отключении от сервиса.
         *
         * @param name Имя сервиса.
         */
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i(TAG, "onServiceDisconnected: name = $name")
        }

    }

    /**
     * Метод onCreate вызывается при создании активности.
     *
     * Здесь происходит запрос разрешений и связывание с AIDL-сервисом.
     *
     * Подключение к чтению и записи в контакты.
     *
     * @param savedInstanceState Состояние активности.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_CONTACTS), 100)
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_CONTACTS),
                100
            )
        }
        val serviceIntent = Intent(this, AidlService::class.java)
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE)

    }
}