package app.property.management.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import app.property.management.R
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by kombo on 08/08/2017.
 */

/**
 * Use a text box to allow users to select what properties they want to use.
 */
class PropertySelection : AppCompatActivity() {

    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.property_selection)

        setSupportActionBar(toolbar)


    }

}
