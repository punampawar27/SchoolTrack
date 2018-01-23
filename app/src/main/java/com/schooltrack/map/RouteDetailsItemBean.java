package com.schooltrack.map;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Gracie on 11/20/2015.
 */
public class RouteDetailsItemBean {

    private List<LatLng> mDirectionPoint=null;
    private String routeId=null;
    LatLng currentLatLng =null;
    String lastUpdateDate=null;
    private LatLng startLatLng;
    private LatLng endLatLng;
    private List<String> stopAddressList=null;
    private List<String> visitedList=null;
    private List<String> ETAList=null;

    public List<LatLng> getmDirectionPoint() {
        return mDirectionPoint;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public LatLng getCurrentLatLng() {
        return currentLatLng;
    }

    public void setCurrentLatLng(LatLng currentLatLng) {
        this.currentLatLng = currentLatLng;
    }

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public LatLng getStartLatLng() {
        return startLatLng;
    }

    public void setStartLatLng(LatLng startLatLng) {
        this.startLatLng = startLatLng;
    }

    public LatLng getEndLatLng() {
        return endLatLng;
    }

    public void setEndLatLng(LatLng endLatLng) {
        this.endLatLng = endLatLng;
    }

    public void setmDirectionPoint(List<LatLng> mDirectionPoint) {
        this.mDirectionPoint = mDirectionPoint;
    }

    public List<String> getStopAddressList() {
        return stopAddressList;
    }

    public void setStopAddressList(List<String> stopAddressList) {
        this.stopAddressList = stopAddressList;
    }

    public List<String> getVisitedList() {
        return visitedList;
    }

    public void setVisitedList(List<String> visitedList) {
        this.visitedList = visitedList;
    }

    public List<String> getETAList() {
        return ETAList;
    }

    public void setETAList(List<String> ETAList) {
        this.ETAList = ETAList;
    }
}
