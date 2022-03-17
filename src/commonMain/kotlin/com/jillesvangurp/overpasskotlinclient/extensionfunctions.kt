package com.jillesvangurp.overpasskotlinclient

import com.jillesvangurp.geo.GeoGeometry
import com.jillesvangurp.geojson.*

fun LineStringCoordinates.centroid(): DoubleArray {
    val lon = this.sumOf { it.longitude } / size
    val lat = this.sumOf { it.latitude } / size
    return doubleArrayOf(lon, lat)
}

fun PolygonCoordinates.centroid() = this[0].centroid()
fun MultiPolygonCoordinates.centroid() = this.map { it.centroid() }.toTypedArray().centroid()
fun Geometry.centroid(): PointCoordinates = centroidOrNull() ?: error("missing centroid implementation for ${this::class.simpleName}")

fun Geometry.centroidOrNull(): PointCoordinates? = when (this) {
    is Geometry.Point -> this.coordinates ?: GeoGeometry.polygonCenter(bbox!!)
    is Geometry.LineString -> this.coordinates?.centroid() ?: GeoGeometry.polygonCenter(bbox!!)
    is Geometry.MultiLineString -> this.coordinates?.centroid() ?: GeoGeometry.polygonCenter(bbox!!)
    is Geometry.Polygon -> this.coordinates?.asArray?.centroid() ?: GeoGeometry.polygonCenter(bbox!!)
    is Geometry.MultiPolygon -> this.coordinates?.asArray?.centroid() ?: GeoGeometry.polygonCenter(bbox!!)
    else -> null
}