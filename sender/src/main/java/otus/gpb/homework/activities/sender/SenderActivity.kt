package otus.gpb.homework.activities.sender

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import otus.gpb.homework.activities.receiver.R
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class SenderActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sender)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Инициализация FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val toGoogleMaps: Button = this.findViewById(R.id.to_google_maps)
        toGoogleMaps.setOnClickListener {
            Toast.makeText(this, "To Google Maps", Toast.LENGTH_SHORT).show()
            // Запрос разрешений, если они еще не были предоставлены
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
            // Получение текущей геопозиции
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        location?.let {
                            val latitude = it.latitude
                            val longitude = it.longitude
                            val query = URLEncoder.encode("рестораны", StandardCharsets.UTF_8.toString())
                            val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$query")
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                                setPackage("com.google.android.apps.maps")
                            }
                            // Проверяем, что Google Maps установлено на устройстве
                            if (mapIntent.resolveActivity(packageManager) != null) {
                                startActivity(mapIntent)
                            } else {
                                Toast.makeText(this, "Google Maps не установлено", Toast.LENGTH_SHORT).show()
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                startActivity(mapIntent)
                            }
                        }
                    }
            }
        }

        val sendMail: Button = this.findViewById(R.id.send_mail)
        sendMail.setOnClickListener {
            Toast.makeText(this, "Send Mail", Toast.LENGTH_SHORT).show()
            val email = "android@otus.ru"
            val subject = "Тест из зажания activity_02";
            val text = "Добрый день! Это содержание письма, отправленного из приложения activity_02."

            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:") // Только почтовые клиенты смогут обработать этот Intent
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, text)
            }

            // Проверяем, что есть клиент, который может обработать наш Intent
            if (emailIntent.resolveActivity(packageManager) != null) {
                startActivity(emailIntent)
            } else {
                Toast.makeText(this, "Не найдено приложение для отправки email протоколом mailto:", Toast.LENGTH_SHORT).show()

                val emailIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "message/rfc822" // MIME-тип для email-клиентов
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                    putExtra(Intent.EXTRA_SUBJECT, subject)
                    putExtra(Intent.EXTRA_TEXT, text)
                }

                try {
                    startActivity(Intent.createChooser(emailIntent, "Выберите email клиент"))
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(this, "Нет доступных почтовых приложений", Toast.LENGTH_SHORT).show()
                }

            }
        }

        val openReceiver: Button = this.findViewById(R.id.open_receiver)
        openReceiver.setOnClickListener {
            Toast.makeText(this, "Open Receiver", Toast.LENGTH_SHORT).show()
            val payload = Payload("Славные парни", "2016",
                "Что бывает, когда напарником брутального костолома " +
                        "становится субтильный лопух? Наемный охранник Джексон Хили " +
                        "и частный детектив Холланд Марч вынуждены работать в паре, " +
                        "чтобы распутать плевое дело о пропавшей девушке, которое " +
                        "оборачивается преступлением века. Смогут ли парни разгадать " +
                        "сложный ребус, если у каждого из них – свои, весьма " +
                        "индивидуальные методы.")
            // Создание Intent
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                addCategory(Intent.CATEGORY_DEFAULT)
                putExtra("title", payload.title)
                putExtra("year", payload.year)
                putExtra("description", payload.description)
                setPackage("otus.gpb.homework.activities.receiver")
            }
            // Проверяем, что Google Maps установлено на устройстве
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "Receiver не установлен", Toast.LENGTH_SHORT).show()
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    addCategory(Intent.CATEGORY_DEFAULT)
                    putExtra("title", payload.title)
                    putExtra("year", payload.year)
                    putExtra("description", payload.description)
                }
                startActivity(intent)
            }
        }
    }
}