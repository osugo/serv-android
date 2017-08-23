package app.property.management.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.RadioGroup
import app.property.management.R
import app.property.management.model.Property
import app.property.management.model.User
import app.property.management.util.RealmUtil
import com.bumptech.glide.Glide
import io.realm.Realm
import io.realm.exceptions.RealmException
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.property_selection.*
import org.jetbrains.anko.toast
import java.util.*

/**
 * Created by kombo on 08/08/2017.
 */

/**
 * Let's users input their property details.
 */
class PropertySelection : AppCompatActivity(), View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private lateinit var typeOfProperty: String
    private lateinit var nameOfProperty: String
    private lateinit var houseNumber: String
    private lateinit var locationOfProperty: String

    private lateinit var realm: Realm

    companion object {
        val TAG: String = PropertySelection::class.java.simpleName
    }

    override fun onCheckedChanged(radioGroup: RadioGroup, checkedId: Int) {
        typeOfProperty = getPropertyTypeFromId(checkedId)
    }

    override fun onClick(view: View?) {
        nameOfProperty = name.text.toString()
        houseNumber = number.text.toString()
        locationOfProperty = location.text.toString()

        if (typeOfProperty.isEmpty())
            typeOfProperty = getPropertyTypeFromId(propertyType.checkedRadioButtonId)

        val numberType =
                if (typeOfProperty == getString(R.string.commercial))
                    getString(R.string.office_number).toLowerCase(Locale.getDefault())
                else
                    getString(R.string.house_number).toLowerCase(Locale.getDefault())

        if (nameOfProperty.isBlank()) {
            toast("Please enter the name of the property")
            return
        }

        if (houseNumber.isBlank()) {
            toast("Please enter your $numberType")
            return
        }

        if (locationOfProperty.isBlank()) {
            toast("Please enter the location")
            return
        }

        if (nameOfProperty.isNotBlank() && houseNumber.isNotBlank() && locationOfProperty.isNotBlank() && typeOfProperty.isNotBlank()) {
            try {
                val user: User? = realm.where(User::class.java).findFirst()

                realm.executeTransaction {
                    val property = Property(user!!.id, nameOfProperty, locationOfProperty, typeOfProperty)
                    realm.copyToRealmOrUpdate(property)
                }
            } catch (e: RealmException) {
                error(e)
            } finally {
                startActivity(Intent(this, Home::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            }
        }
    }

    private fun getPropertyTypeFromId(id: Int): String {
        var type = ""

        when (id) {
            R.id.commercial -> {
                numberText.text = getString(R.string.office_number)
                type = getString(R.string.commercial)
            }
            R.id.apartment -> {
                numberText.text = getString(R.string.house_number)
                type = getString(R.string.apartment)
            }
            R.id.estate -> {
                numberText.text = getString(R.string.house_number)
                type = getString(R.string.estate)
            }
        }

        return type
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.property_selection)

        realm = Realm.getInstance(RealmUtil.getRealmConfig())

        Glide.with(this).load(R.drawable.apart_two).bitmapTransform(BlurTransformation(this)).into(background)

        proceed.setOnClickListener(this)
        propertyType.setOnCheckedChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        realm.close()
    }

}
