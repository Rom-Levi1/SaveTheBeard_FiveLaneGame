package dev.romle.hw1_app.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentContainerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textview.MaterialTextView
import dev.romle.hw1_app.R
import dev.romle.hw1_app.model.ScoreData


class MapFragment : Fragment() {

    private lateinit var googleMap: GoogleMap

    private lateinit var map_fragment : FragmentContainerView

    private  lateinit var map_LBL_title : MaterialTextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_map, container, false)

        findViews(view)

        return view
    }

    private fun findViews(view: View) {

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment

        mapFragment.getMapAsync { gmap ->
            googleMap = gmap

        }
    }

    fun zoom(scoreData: ScoreData){
        val pos = LatLng(scoreData.latitude, scoreData.longitude)

        googleMap?.let {
            it.clear()
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 12f)) //zooming
            it.addMarker(MarkerOptions().position(pos).title("${scoreData.playerName} (${scoreData.score})"))
        }
    }

}
