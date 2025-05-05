package mad.project.yadro_test

import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.BufferedInputStream

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
 * Класс для доставания всех контактов
 *
 * @property contentResolver - объект ContentResolver, используемый для доступа к данным контактов.
 *
 * @return ArrayList<Contact> - Список всех контактов
 */
class GetContact(private val contentResolver: ContentResolver) {
    /**
     * Метод предназаначеный для доставания всех контактов
     *
     * @return ArrayList<Contact> - Список всех контактов
     */
    fun getContacts(): ArrayList<Contact>{
        val contactList = ArrayList<Contact>()
        val contactsCursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null)
        contactsCursor?.use {

            while (it.moveToNext()) {
                val idColumnIndex = it.getColumnIndex(ContactsContract.Contacts._ID)
                val displayNameColumnIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                var phoneNumber = ""
                var email = ""
                var photo: Bitmap? = null

                val phoneIndex = it.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                Log.i("n", it.getInt(phoneIndex).toString())
                if (phoneIndex >= 0 && it.getInt(phoneIndex) > 0) {
                    val phoneNumberCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(it.getString(idColumnIndex)),
                        null
                    )
                    phoneNumberCursor?.use { cursor ->
                        val numberIndex =
                            cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        if (numberIndex >= 0) {
                            while (cursor.moveToNext()) {
                                phoneNumber += cursor.getString(numberIndex)
                            }
                        } else {
                            Log.w("GetContact", "Column index for PHONE.NUMBER not found")
                        }
                    } ?: run {
                        Log.w("GetContact", "phoneNumberCursor is null")
                    }

                    val emailCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        arrayOf(it.getString(idColumnIndex)),
                        null
                    )
                    emailCursor?.use { cursor ->
                        val numberIndex =
                            cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)
                        if (numberIndex >= 0) {
                            while (cursor.moveToNext()) {
                                email += cursor.getString(numberIndex) + "\n"
                            }
                        } else {
                            Log.w("GetContact", "Column index for Email.DATA not found")
                        }
                    } ?: run {
                        Log.w("GetContact", "emailCursor is null")
                    }
                    val contactPhotoUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI,it.getString(idColumnIndex))
                    val photoStream = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver,contactPhotoUri)
                    photo = BitmapFactory.decodeStream(BufferedInputStream(photoStream))
                }
                if (idColumnIndex >= 0 && displayNameColumnIndex >= 0) {
                    val rowID = it.getString(idColumnIndex)
                    val displayName = it.getString(displayNameColumnIndex)
                    contactList.add(Contact(rowID.toInt(),displayName, phoneNumber, email, photo))
                }
            }
        } ?: run {
            Log.e("GetContact", "Cursor is null")
        }
        contactsCursor?.close()
        return contactList
    }
}