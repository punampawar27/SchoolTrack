package com.schooltrack.map;

/**
 * Created by Gracie on 11/8/2015.
 */
public class TrackingItemBean {


    private  String routeId=null;
    private  String routeName=null;
    private  String status=null;

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
