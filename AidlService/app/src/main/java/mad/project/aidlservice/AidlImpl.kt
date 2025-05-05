package mad.project.aidlservice

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Log
import androidx.core.app.ActivityCompat
import java.io.IOException

/**
 * Contact - дата-класс представляющий контакт.
 *
 * @property id Уникальный идентификатор контакта.
 * @property name Имя контакта.
 * @property phoneNumber Номер телефона контакта.
 * @property email Электронная почта контакта.
 * @property photo Фото контакта в формате Bitmap.
 */
data class Contact(val id:Int,val name: String, val phoneNumber: String, val email: String, val photo: Bitmap?)

/**
 * AidlImpl - реализация интерфейса AIDL, предоставляющая методы для работы с контактами.
 *
 * @property context - контекст приложения.
 */
class AidlImpl(private val context: Context) : IAidlInterface.Stub(){
    /**
     * Метод для удаления дубликатов контактов.
     *
     * @return Строка с результатом выполнения операции удаления дубликатов.
     * @throws IOException Если произошла ошибка во время удаления дубликатов.
     */
    @Throws(IOException::class)
    override fun deleteDublicate(): String {
        val deleteDuplicateContact = DeleteDuplicateContact(context)
        return deleteDuplicateContact.deleteDuplicateContacts()
    }
}