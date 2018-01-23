package com.schooltrack.map;

/**
 * Created by Gracie on 11/7/2015.
 */
public class AvailableRouteItemBean {
//old key   AIzaSyA4tqDpWm39EwPTEE5esil_hCethlMgWL0
//AIzaSyDHrRh37ZUAmsKnpyQgFbV-h7YqKnUR-w0

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
