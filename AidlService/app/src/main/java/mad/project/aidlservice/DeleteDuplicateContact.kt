package mad.project.aidlservice

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import java.io.BufferedInputStream

/**
 * Класс DeleteDuplicateContact предназначен для нахождения всех одинаковых контактов
 * и оставление только 1 из них.
 *
 * @property context - контекст приложения
 */
class DeleteDuplicateContact(context: Context) {
    val contentResolver = context.contentResolver

    /**
     * Метод предназаначеный для удаления дубликатов
     *
     * @return String - информация о результате удаления
     */
    fun deleteDuplicateContacts():String{
        try {
            var status = false
            val contactList = getContacts()
            for (i in 0..contactList.size - 2) {
                for (j in i + 1..contactList.size - 1) {
                    val contact1 = contactList[i]
                    val contact2 = contactList[j]
                    if (contact1.photo == contact2.photo && contact1.email == contact2.email &&
                        contact1.phoneNumber == contact2.phoneNumber && contact1.name == contact2.name
                    ) {
                        status = true
                        val uri: Uri = ContentUris.withAppendedId(
                            ContactsContract.Contacts.CONTENT_URI,
                            contact2.id.toLong()
                        )
                        contentResolver.delete(uri, null, null)
                    }
                }
            }
            if (status) {
                return "Удаление прошло успешно"
            } else {
                return "Нету одинаковых контактов"
            }
        } catch (e: Exception){
            Log.e("DeleteDuplicateContact", e.toString())
            return "Ошибка удаления"
        }
    }

    /**
     * Метод предназаначеный для доставания всех контактов
     *
     * @return ArrayList<Contact> - Список всех контактов
     */
    private fun getContacts(): ArrayList<Contact>{
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
                            Log.w("DeleteDuplicateContact", "Column index for PHONE.NUMBER not found")
                        }
                    } ?: run {
                        Log.w("DeleteDuplicateContact", "phoneNumberCursor is null")
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
                            Log.w("DeleteDuplicateContact", "Column index for Email.DATA not found")
                        }
                    } ?: run {
                        Log.w("DeleteDuplicateContact", "emailCursor is null")
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
            Log.e("DeleteDuplicateContact", "Cursor is null")
        }
        contactsCursor?.close()
        return contactList
    }
}