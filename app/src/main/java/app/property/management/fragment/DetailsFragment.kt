package app.property.management.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.property.management.R
import app.property.management.activity.PropertySelection
import app.property.management.util.RealmUtil
import io.realm.Realm
import kotlinx.android.synthetic.main.description_layout.*
import kotlinx.android.synthetic.main.schedule_layout.*
import org.jetbrains.anko.support.v4.toast

/**
 * Created by kombo on 08/10/2017.
 */
class DetailsFragment : Fragment() {

    lateinit var realm: Realm

    companion object {
        val TAG = DetailsFragment::class.java.simpleName
        var SERVICE: String? = null

        fun newInstance(title: String?): Fragment {
            val fragment = DetailsFragment()

            val bundle = Bundle()
            bundle.putString(SERVICE, title)
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getInstance(RealmUtil.getRealmConfig())
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.details_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        proceed.setOnClickListener {
            descriptionLayout.visibility = View.GONE
            dateLayout.visibility = View.VISIBLE
        }

        done.setOnClickListener {
            toast("Your request is being processed. We will contact you soon.)")
            activity.startActivity(Intent(activity, PropertySelection::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        realm.close()
    }
}