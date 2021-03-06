package com.mapbox.mapboxsdk.testapp.activity.annotation;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.testapp.R;
import com.mapbox.mapboxsdk.testapp.utils.IconUtils;

/**
 * Test activity showcasing updating a Marker position, title, icon and snippet.
 */
public class DynamicMarkerChangeActivity extends AppCompatActivity {

  private static final LatLng LAT_LNG_CHELSEA = new LatLng(51.481670, -0.190849);
  private static final LatLng LAT_LNG_ARSENAL = new LatLng(51.555062, -0.108417);

  private MapView mapView;
  private MapboxMap mapboxMap;
  private Marker marker;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dynamic_marker);

    mapView = findViewById(R.id.mapView);
    mapView.setTag(false);
    mapView.onCreate(savedInstanceState);
    mapView.attachLifeCycle(this);
    mapView.getMapAsync(mapboxMap -> {
      mapboxMap.setStyle(Style.MAPBOX_STREETS);

      DynamicMarkerChangeActivity.this.mapboxMap = mapboxMap;
      // Create marker
      MarkerOptions markerOptions = new MarkerOptions()
        .position(LAT_LNG_CHELSEA)
        .icon(IconUtils.drawableToIcon(DynamicMarkerChangeActivity.this, R.drawable.ic_stars,
          ResourcesCompat.getColor(getResources(), R.color.blueAccent, getTheme())))
        .title(getString(R.string.dynamic_marker_chelsea_title))
        .snippet(getString(R.string.dynamic_marker_chelsea_snippet));
      marker = mapboxMap.addMarker(markerOptions);
    });

    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setColorFilter(ContextCompat.getColor(this, R.color.primary));
    fab.setOnClickListener(view -> {
      if (mapboxMap != null) {
        updateMarker();
      }
    });
  }

  private void updateMarker() {
    // update model
    boolean first = (boolean) mapView.getTag();
    mapView.setTag(!first);

    // update marker
    marker.setPosition(first ? LAT_LNG_CHELSEA : LAT_LNG_ARSENAL);
    marker.setIcon(IconUtils.drawableToIcon(this, R.drawable.ic_stars, first
      ? ResourcesCompat.getColor(getResources(), R.color.blueAccent, getTheme()) :
      ResourcesCompat.getColor(getResources(), R.color.redAccent, getTheme())
    ));

    marker.setTitle(first
      ? getString(R.string.dynamic_marker_chelsea_title) : getString(R.string.dynamic_marker_arsenal_title));
    marker.setSnippet(first
      ? getString(R.string.dynamic_marker_chelsea_snippet) : getString(R.string.dynamic_marker_arsenal_snippet));
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mapView.onSaveInstanceState(outState);
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }
}
