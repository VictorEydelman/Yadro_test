package mad.project.yadro_test

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import mad.project.yadro_test.ui.theme.Yadro_testTheme
import java.io.BufferedInputStream
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ResolveInfo
import android.os.Build
import android.os.IBinder
import mad.project.aidlservice.IAidlInterface

/**
 * Класс MainActivity - это класс, наследуемыq от AppCompatActivity, и является входной точкой приложения
 *
 * В нём идёт подключение к AIDL-серверу и запуск приложения
 */
class MainActivity : ComponentActivity() {
    val REQUEST_PERMISSON = 1
    private var aildInterface: IAidlInterface? = null
    private var isServiceConnected = false
    private val serviceConnection: ServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            aildInterface = IAidlInterface.Stub.asInterface(service)
            isServiceConnected = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceConnected = false
        }
    }

    /**
     * Вызывается при создании активности.
     *
     * Запрашивает доступ к чтению контактов и устанавливает интерфейс пользователя.
     *
     * @param savedInstanceState - состояние активности.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        var contacts = listOf<Contact>()
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.READ_CONTACTS),REQUEST_PERMISSON)
        } else{
            val getContact = GetContact(contentResolver)
            contacts = getContact.getContacts()
        }
        var intent = Intent("mad.project.aidlservice.AIDL")
        intent.component = ComponentName("mad.project.aidlservice", "mad.project.aidlservice.AidlService")
        intent = implicitToExplicitIntent(intent)!!
        bindService(intent,serviceConnection,BIND_AUTO_CREATE)
        setContent {
            Yadro_testTheme {
                Surface {
                    ContactScreen(initContacts = contacts, isServiceConnected, aildInterface, contentResolver)
                }
            }
        }
    }
    /**
     * Преобразует неявный интент в явный, если найден единственный сервис.
     *
     * @param intent Неявный интент для поиска сервиса.
     * @return Явный интент или оригинальный интент, если явный не найден.
     */
    private fun implicitToExplicitIntent(intent: Intent): Intent? {
        val resolveInfoList: List<ResolveInfo> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentServices(
                intent,
                PackageManager.ResolveInfoFlags.of(
                    PackageManager.MATCH_DEFAULT_ONLY.toLong()
                )
            )
        } else {
            packageManager.queryIntentServices(intent, 0)
        }

        if (resolveInfoList.size != 1) {
            return intent
        }

        val serviceInfo = resolveInfoList[0]
        val component = ComponentName(serviceInfo.serviceInfo.packageName, serviceInfo.serviceInfo.name)
        val explicitIntent = Intent(intent)
        explicitIntent.component = component
        return explicitIntent
    }

    /**
     * Вызывается при уничтожении активности.
     */
    override fun onDestroy() {
        super.onDestroy()
        if (isServiceConnected) {
            unbindService(serviceConnection)
            isServiceConnected = false
        }
    }
}
