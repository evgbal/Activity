package otus.gpb.homework.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream


class EditProfileActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    // Код запроса разрешения
    private val REQUEST_CAMERA_PERMISSION = 101

    private lateinit var textViewFirstName: TextView
    private lateinit var textViewLastName: TextView
    private lateinit var textViewAge: TextView
    private lateinit var editProfileButton: Button

    // Регистрация результата для запуска FillFormActivity
    private val fillFormResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data ->
                val firstName = data.getStringExtra("firstName")
                val lastName = data.getStringExtra("lastName")
                val age = data.getStringExtra("age")

                // Отображение данных в TextView
                textViewFirstName.text = firstName
                textViewLastName.text = lastName
                textViewAge.text = age
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        imageView = findViewById(R.id.imageview_photo)

        textViewFirstName = findViewById(R.id.textview_name)
        textViewLastName = findViewById(R.id.textview_surname)
        textViewAge = findViewById(R.id.textview_age)
        editProfileButton = findViewById(R.id.button4)

        findViewById<Toolbar>(R.id.toolbar).apply {
            inflateMenu(R.menu.menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.send_item -> {
                        openSenderApp()
                        true
                    }
                    else -> false
                }
            }
        }

        // Установка слушателя для ImageView
        imageView.setOnClickListener {
            showActionDialog()
        }

        // Обработчик нажатия кнопки "Редактировать профиль"
        editProfileButton.setOnClickListener {
            val intent = Intent(this, FillFormActivity::class.java).apply {
                putExtra("firstName", textViewFirstName.text)    // Предустановленное имя
                putExtra("lastName", textViewLastName.text)   // Предустановленная фамилия
                putExtra("age", textViewAge.text)            // Предустановленный возраст
            }
            fillFormResultLauncher.launch(intent)
        }
    }

    /**
     * Используйте этот метод чтобы отобразить картинку полученную из медиатеки в ImageView
     */
    private fun populateImage(uri: Uri) {
        val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
        imageView.setImageBitmap(bitmap)
    }

    // Регистрация контракта для получения изображения из галереи
    private val galleryPickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            populateImage(it)
        }
    }

    // Метод для выбора фото из галереи
    private fun selectPhotoFromGallery() {
        galleryPickerLauncher.launch("image/*") // Открываем галерею для выбора изображения
    }

    private fun openSenderApp() {

        // Добавляем URI изображения, если доступно
        // use the dedicated external directory so the App doesn't need to ask for permission in manifest
        val dirSaveFile = applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        // create needed dirs for file path
        val imagePath = File(dirSaveFile, "external_files")
        imagePath.mkdir()
        // create empty file
        val imageFile = File(imagePath.path, "test.jpg")
        // get the Bitmap of the drawable to show


        // write in the file the drawable image
        try {
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            val fos = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }


        // create the uri
        val imageUri = FileProvider.getUriForFile(this, "otus.gpb.homework.activities.fileprovider", imageFile)


        val telegramIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*" // указываем, что отправляем изображение
            setPackage("org.telegram.messenger") // явно указываем Telegram
            putExtra(Intent.EXTRA_TEXT, "Имя: ${textViewFirstName.text}, Фамилия: ${textViewLastName.text}, Возраст: ${textViewAge.text}")
            // Предполагаем, что картинка добавлена через URI из галереи
            putExtra(Intent.EXTRA_STREAM, imageUri)
            // Разрешаем передавать URI
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        if (telegramIntent.resolveActivity(packageManager) != null) {
            startActivity(telegramIntent)
        } else {
            // Создаем неявный интент с действием SEND
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"  // MIME-тип указывает на отправку
                // Добавляем данные профиля в качестве параметров
                putExtra(Intent.EXTRA_TEXT, "Вот информация моего профиля!")  // Текст для отправки
                putExtra(Intent.EXTRA_SUBJECT, "Детали профиля")  // Тема сообщения
                putExtra(Intent.EXTRA_STREAM, imageUri) // Передаем изображение// Разрешаем передавать URI
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            // Запускаем выбор приложения для отправки
            val chooser = Intent.createChooser(sendIntent, "Поделиться профилем")
            startActivity(chooser)
        }
    }

    // Метод для показа AlertDialog с действиями
    private fun showActionDialog() {
        val options = arrayOf("Сделать фото", "Выбрать фото")
        AlertDialog.Builder(this)
            .setTitle("Выберите действие")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermission() // Проверка разрешения на камеру
                    1 -> selectPhotoFromGallery() // Выбор фото из галереи
                }
            }
            .show()
    }

    // В случае отключенного разрешения, показываем диалог и перенаправляем пользователя в настройки
    private fun showSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Необходимо разрешение")
            .setMessage("Чтобы использовать камеру, откройте настройки и включите разрешение для камеры.")
            .setPositiveButton("Открыть настройки") { _, _ ->
                // Открываем настройки приложения
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    // Проверка состояния разрешения и показ диалога
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Проверяем, можно ли снова запросить разрешение, или нужно отправить в настройки
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // Логика для объясняющего диалога и повторного запроса разрешения
                requestCameraPermissionWithRationale()
            } else {
                // Разрешение запрещено навсегда, показываем диалог для открытия настроек
                showSettingsDialog()
            }
        } else {
            // Разрешение предоставлено
            onCameraPermissionGranted()
        }
    }

    private fun onCameraPermissionGranted() {
        // Разрешение предоставлено, показать изображение
        imageView.setImageResource(R.drawable.cat)
    }

    private fun requestCameraPermissionWithRationale() {
        AlertDialog.Builder(this)
            .setTitle("Требуется разрешение")
            .setMessage("Для работы приложения необходимо разрешение на использование камеры.")
            .setPositiveButton("Дать доступ") { _, _ ->
                // Запрашиваем разрешение
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }



}