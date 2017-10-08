package app.property.management.activity

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import app.property.management.R
import app.property.management.fragment.DetailsFragment
import app.property.management.model.OfferedService
import app.property.management.util.RealmUtil
import io.realm.Realm
import kotlinx.android.synthetic.main.details.*


/**
 * Created by kombo on 07/10/2017.
 */
class Details : AppCompatActivity() {

    lateinit var realm: Realm

    companion object {
        val TAG = Details::class.java.simpleName
        val SELECTED_SERVICE = "title"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.details)

        realm = Realm.getInstance(RealmUtil.getRealmConfig())

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setUpViewPager()
        tabLayout.setupWithViewPager(viewPager)
        viewPager.currentItem = intent.getIntExtra(SELECTED_SERVICE, 0)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.setCurrentItem(tab.position, true)
            }
        })
    }

    private fun setUpViewPager() {
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        val services = realm.where(OfferedService::class.java).findAll()

        if (services.isNotEmpty())
            for (service in services)
                viewPagerAdapter.add(DetailsFragment.newInstance(service.title), service.title)

        viewPager.adapter = viewPagerAdapter
    }

    private class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        private val fragmentList = ArrayList<Fragment>()
        private val titles = ArrayList<String>()

        override fun getItem(position: Int): Fragment = fragmentList[position]

        override fun getCount(): Int = fragmentList.size

        fun add(fragment: Fragment, title: String?) {
            fragmentList.add(fragment)
            titles.add(title!!)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return titles[position]
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        realm.close()
    }
}