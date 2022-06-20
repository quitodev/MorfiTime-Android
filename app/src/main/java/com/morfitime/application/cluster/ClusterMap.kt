package com.morfitime.application.cluster

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class ClusterMap(private val titleItem: String, private val latlngItem: LatLng) : ClusterItem {
    override fun getSnippet(): String {
        return ""
    }
    override fun getTitle(): String {
        return titleItem
    }
    override fun getPosition(): LatLng {
        return latlngItem
    }
}