package com.morfitime.application.cluster

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.morfitime.R

class ClusterMarker(context: Context, map: GoogleMap, clusterManager: ClusterManager<ClusterMap>) :
    DefaultClusterRenderer<ClusterMap>(context, map, clusterManager) {

    var drawable = ContextCompat.getDrawable(context, R.drawable.ic_pin)

    override fun onBeforeClusterItemRendered(item: ClusterMap, markerOptions: MarkerOptions) {
        val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth, drawable!!.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable!!.setBounds(0, 0, canvas.width, canvas.height)
        drawable!!.draw(canvas)
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
    }
}
