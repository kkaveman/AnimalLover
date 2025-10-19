package com.example.animallover

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class AdoptionCenterFragment : Fragment() {

    private var mapView: MapView? = null
    private var locationOverlay: MyLocationNewOverlay? = null
    private lateinit var fabMyLocation: FloatingActionButton
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    private var lastLocation: GeoPoint? = null
    private var hasSearched = false

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val TAG = "AdoptionCenter"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_adoption_center, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize osmdroid configuration
        val ctx = requireContext().applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        // Set user agent to prevent getting blocked
        Configuration.getInstance().userAgentValue = requireContext().packageName

        // Initialize map
        mapView = view.findViewById(R.id.mapView)
        fabMyLocation = view.findViewById(R.id.fabMyLocation)

        setupMap()
        setupLocationButton()

        // Check and request location permission
        if (checkLocationPermission()) {
            enableMyLocation()
        } else {
            requestLocationPermission()
        }
    }

    private fun setupMap() {
        mapView?.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            isTilesScaledToDpi = true
            minZoomLevel = 4.0
            maxZoomLevel = 20.0

            // Restore last location if available, otherwise use default
            val location = lastLocation ?: GeoPoint(-6.2088, 106.8456)
            controller.setZoom(15.0)
            controller.setCenter(location)

            android.util.Log.d(TAG, "Map setup complete")
        }
    }

    private fun setupLocationButton() {
        fabMyLocation.setOnClickListener {
            if (checkLocationPermission()) {
                goToMyLocation()
            } else {
                requestLocationPermission()
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Location permission is required to find nearby pet shops",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun enableMyLocation() {
        if (!checkLocationPermission()) return

        mapView?.let { map ->
            // Remove old overlay if exists
            locationOverlay?.let { overlay ->
                map.overlays.remove(overlay)
                overlay.disableMyLocation()
            }

            // Add new location overlay
            locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), map)
            locationOverlay?.let { overlay ->
                overlay.enableMyLocation()
                overlay.enableFollowLocation()
                map.overlays.add(overlay)

                android.util.Log.d(TAG, "Location overlay enabled")
            }

            // Wait for location and search (only if not searched yet)
            if (!hasSearched) {
                map.postDelayed({
                    locationOverlay?.myLocation?.let { location ->
                        lastLocation = location
                        map.controller.animateTo(location)
                        searchNearbyPetShops(location)
                        hasSearched = true
                    } ?: run {
                        Toast.makeText(
                            requireContext(),
                            "Getting your location...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, 2000)
            } else {
                // Restore search results if we have a location
                lastLocation?.let { location ->
                    searchNearbyPetShops(location)
                }
            }
        }
    }

    private fun goToMyLocation() {
        locationOverlay?.myLocation?.let { location ->
            lastLocation = location
            mapView?.controller?.animateTo(location)
            clearPetShopMarkers()
            searchNearbyPetShops(location)
        } ?: run {
            Toast.makeText(
                requireContext(),
                "Getting your location...",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun searchNearbyPetShops(location: GeoPoint) {
        scope.launch {
            try {
                Toast.makeText(
                    requireContext(),
                    "Searching for pet shops nearby...",
                    Toast.LENGTH_SHORT
                ).show()

                val petShops = withContext(Dispatchers.IO) {
                    searchOverpassAPI(location)
                }

                clearPetShopMarkers()

                petShops.forEach { shop ->
                    addMarker(shop.name, shop.location, shop.address)
                }

                if (petShops.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "No pet shops found nearby. Try moving to a different area.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Found ${petShops.size} pet-related locations nearby",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error searching pet shops", e)
                Toast.makeText(
                    requireContext(),
                    "Error searching: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun searchOverpassAPI(location: GeoPoint): List<PetShop> {
        val radius = 5000
        val lat = location.latitude
        val lon = location.longitude

        val query = """
            [out:json][timeout:25];
            (
              node["shop"="pet"](around:$radius,$lat,$lon);
              node["shop"="pet_grooming"](around:$radius,$lat,$lon);
              node["amenity"="veterinary"](around:$radius,$lat,$lon);
              way["shop"="pet"](around:$radius,$lat,$lon);
              way["shop"="pet_grooming"](around:$radius,$lat,$lon);
              way["amenity"="veterinary"](around:$radius,$lat,$lon);
            );
            out center;
        """.trimIndent()

        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val urlString = "https://overpass-api.de/api/interpreter?data=$encodedQuery"

        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 15000
        connection.readTimeout = 15000

        val response = connection.inputStream.bufferedReader().use { it.readText() }
        connection.disconnect()

        return parseOverpassResponse(response)
    }

    private fun parseOverpassResponse(jsonResponse: String): List<PetShop> {
        val petShops = mutableListOf<PetShop>()

        try {
            val jsonObject = JSONObject(jsonResponse)
            val elements = jsonObject.getJSONArray("elements")

            for (i in 0 until elements.length()) {
                val element = elements.getJSONObject(i)

                val lat: Double
                val lon: Double

                if (element.has("lat") && element.has("lon")) {
                    lat = element.getDouble("lat")
                    lon = element.getDouble("lon")
                } else if (element.has("center")) {
                    val center = element.getJSONObject("center")
                    lat = center.getDouble("lat")
                    lon = center.getDouble("lon")
                } else {
                    continue
                }

                val tags = if (element.has("tags")) element.getJSONObject("tags") else null
                val name = tags?.optString("name", "Pet Shop") ?: "Pet Shop"
                val address = buildAddress(tags)

                petShops.add(
                    PetShop(
                        name = name,
                        location = GeoPoint(lat, lon),
                        address = address
                    )
                )
            }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error parsing response", e)
        }

        return petShops
    }

    private fun buildAddress(tags: JSONObject?): String {
        if (tags == null) return "Address not available"

        val parts = mutableListOf<String>()

        tags.optString("addr:street")?.let { if (it.isNotEmpty()) parts.add(it) }
        tags.optString("addr:housenumber")?.let { if (it.isNotEmpty()) parts.add(it) }
        tags.optString("addr:city")?.let { if (it.isNotEmpty()) parts.add(it) }

        return if (parts.isEmpty()) "Address not available" else parts.joinToString(", ")
    }

    private fun addMarker(title: String, location: GeoPoint, description: String) {
        mapView?.let { map ->
            val marker = Marker(map)
            marker.position = location
            marker.title = title
            marker.snippet = description
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            map.overlays.add(marker)
            map.invalidate()
        }
    }

    private fun clearPetShopMarkers() {
        mapView?.let { map ->
            map.overlays.removeAll { it is Marker }
            map.invalidate()
        }
    }

    override fun onResume() {
        super.onResume()
        android.util.Log.d(TAG, "onResume called")
        mapView?.onResume()

        // Re-enable location if permission is granted
        if (checkLocationPermission() && locationOverlay == null) {
            enableMyLocation()
        }
    }

    override fun onPause() {
        super.onPause()
        android.util.Log.d(TAG, "onPause called")

        // Save current location
        locationOverlay?.myLocation?.let { location ->
            lastLocation = location
        }

        mapView?.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        android.util.Log.d(TAG, "onDestroyView called")

        // Properly cleanup location overlay
        locationOverlay?.let { overlay ->
            overlay.disableMyLocation()
            overlay.disableFollowLocation()
        }
        locationOverlay = null

        // Cleanup map
        mapView?.onDetach()
        mapView = null

        // Cancel coroutines
        scope.cancel()
    }

    data class PetShop(
        val name: String,
        val location: GeoPoint,
        val address: String
    )
}