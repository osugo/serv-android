package app.property.management.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import app.property.management.R
import app.property.management.fragment.ServiceSelection
import app.property.management.fragment.Summary
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by kombo on 23/08/2017.
 */
class Requests : AppCompatActivity(), ServiceSelection.OnServicesSelectionComplete {

    override fun onComplete() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.contentFrame, Summary())
                .commitAllowingStateLoss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.requests)

        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.contentFrame, ServiceSelection())
                    .commitAllowingStateLoss()
        }
    }

}