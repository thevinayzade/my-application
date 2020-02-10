package com.vinay.sqlitekotlindemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        click floatingActionButton to start AddUpdateRecordActivity

        addRecord.setOnClickListener {
            startActivity(Intent(this,AddUpdateRecordActivity::class.java))
        }
    }
}
