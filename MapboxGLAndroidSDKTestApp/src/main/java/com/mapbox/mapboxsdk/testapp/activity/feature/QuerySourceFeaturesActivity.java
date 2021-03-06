package com.mapbox.mapboxsdk.testapp.activity.feature;

import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.testapp.R;

import java.util.List;

import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.neq;

/**
 * Test activity showcasing using the query source features API to query feature counts
 */
public class QuerySourceFeaturesActivity extends AppCompatActivity {

  public MapView mapView;
  private MapboxMap mapboxMap;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_query_source_features);

    // Initialize map as normal
    mapView = (MapView) findViewById(R.id.mapView);
    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync(map -> {
      this.mapboxMap = map;
      mapboxMap.getStyle(this::initStyle);
      mapboxMap.setStyle(Style.MAPBOX_STREETS);
    });
    mapView.attachLifeCycle(this);
  }

  private void initStyle(Style style) {
    JsonObject properties = new JsonObject();
    properties.addProperty("key1", "value1");
    final GeoJsonSource source = new GeoJsonSource("test-source",
      FeatureCollection.fromFeatures(new Feature[] {
        Feature.fromGeometry(Point.fromLngLat(17.1, 51), properties),
        Feature.fromGeometry(Point.fromLngLat(17.2, 51), properties),
        Feature.fromGeometry(Point.fromLngLat(17.3, 51), properties),
        Feature.fromGeometry(Point.fromLngLat(17.4, 51), properties),
      }));
    style.addSource(source);

    Expression visible = eq(get("key1"), literal("value1"));
    Expression invisible = neq(get("key1"), literal("value1"));

    CircleLayer layer = new CircleLayer("test-layer", source.getId())
      .withFilter(visible);
    style.addLayer(layer);

    // Add a click listener
    mapboxMap.addOnMapClickListener(point -> {
      // Query
      List<Feature> features = source.querySourceFeatures(eq(get("key1"), literal("value1")));
      Toast.makeText(QuerySourceFeaturesActivity.this, String.format("Found %s features",
        features.size()), Toast.LENGTH_SHORT).show();

      return false;
    });

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setColorFilter(ContextCompat.getColor(this, R.color.primary));
    fab.setOnClickListener(view -> {
      Expression visibility = layer.getFilter();
      if (visibility != null && visibility.equals(visible)) {
        layer.setFilter(invisible);
        fab.setImageResource(R.drawable.ic_layers_clear);
      } else {
        layer.setFilter(visible);
        fab.setImageResource(R.drawable.ic_layers);
      }
    });
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
