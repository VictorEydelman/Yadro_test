package mad.project.yadro_test

import android.content.ContentResolver
import android.os.RemoteException
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import mad.project.aidlservice.IAidlInterface


/**
 * Это экран отображающий контакты с краткой информацией про них (Имя, Фамилия, Номер телефона, email, фото)
 * и кнопка для вызова функции удаляющей все дубликаты через AIDL
 *
 * @param initContacts Начальный список контактов, который будет отображен на экране.
 * @param isServiceConnected Флаг, указывающий, подключен ли сервис AIDL.
 * @param aildInterface Интерфейс AIDL для взаимодействия с удаленными сервисами.
 * @param contentResolver Объект ContentResolver для доступа к данным контактов.
 */
@Composable
fun ContactScreen(
    initContacts: List<Contact>,
    isServiceConnected: Boolean,
    aildInterface: IAidlInterface?,
    contentResolver: ContentResolver
) {
    var contacts by remember { mutableStateOf(initContacts) }
    var result by remember { mutableStateOf("") }
    var showResult by remember { mutableStateOf(false) }


    LaunchedEffect(result) {
        if (result.isNotEmpty()) {
            showResult = true
            delay(10000)
            showResult = false
        }
    }
    Column(modifier = Modifier.fillMaxSize().padding(top = 32.dp) ) {
        LazyColumn {
            items(contacts) { contact: Contact ->
                Row(modifier = Modifier.padding(16.dp)) {
                    contact.photo?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.Gray)
                        )
                    } ?: run {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.Gray)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = "Name: " + contact.name)
                        Text(text = "Phone number: " + contact.phoneNumber)
                        Text(text = "Email: " + contact.email)
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                try {
                    if (isServiceConnected) {
                        val sum = aildInterface!!.deleteDublicate()
                        result = sum.toString()
                        contacts = GetContact(contentResolver).getContacts()
                    } else {
                        result = "Service not bound"
                    }
                } catch (e: NumberFormatException) {
                    Log.e("error", e.toString())
                    result = "Invalid input"
                } catch (e: RemoteException) {
                    Log.e("error", e.toString())
                    result = "RemoteException occurred"
                }
            }) {
                Text("Delete duplicate contacts")
            }

            if (showResult) {
                Text(result)
            }
        }
    }
}


