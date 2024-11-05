package otus.gpb.homework.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ActivityC : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_c)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val openActivityA: Button = this.findViewById(R.id.open_activity_a)
        openActivityA.setOnClickListener {
            Toast.makeText(this, "Open ActivityA!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ActivityA::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
        val openActivityD: Button = this.findViewById(R.id.open_activity_d)
        openActivityD.setOnClickListener {
            Toast.makeText(this, "Open ActivityD!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ActivityD::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        val closeActivityC: Button = this.findViewById(R.id.close_activity_c)
        closeActivityC.setOnClickListener {
            Toast.makeText(this, "Close ActivityC!", Toast.LENGTH_SHORT).show()
            finish()
        }
        val closeStack: Button = this.findViewById(R.id.close_stack)
        closeStack.setOnClickListener {
            Toast.makeText(this, "Close Stack!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ActivityA::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
        title = "ActivityC"
    }
}