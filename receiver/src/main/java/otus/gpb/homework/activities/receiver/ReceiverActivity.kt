package otus.gpb.homework.activities.receiver

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ReceiverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receiver)

        // Получаем TextView и ImageView из layout
        val titleTextView = findViewById<TextView>(R.id.titleTextView)
        val yearTextView = findViewById<TextView>(R.id.yearTextView)
        val descriptionTextView = findViewById<TextView>(R.id.descriptionTextView)
        val posterImageView = findViewById<ImageView>(R.id.posterImageView)

        // Получаем данные из Intent
        val title = intent.getStringExtra("title") ?: "пустой title"
        val year = intent.getStringExtra("year") ?: "пустой year"
        val description = intent.getStringExtra("description") ?: "пустой description"

        // Отображаем данные в TextView
        titleTextView.text = title
        yearTextView.text = year
        descriptionTextView.text = description

        // В зависимости от названия фильма, выбираем картинку
        val posterImageRes = when (title.lowercase()) {
            "Славные парни".lowercase() -> R.drawable.niceguys
            "Интерстеллар".lowercase() -> R.drawable.interstellar
            else -> android.R.color.transparent // Для неизвестных фильмов
        }

        // Устанавливаем картинку в ImageView
        val drawable = getDrawable(posterImageRes)
        posterImageView.setImageDrawable(drawable)
    }
}
