package projekt;


import java.util.ArrayList;

public class TrainRoute {
    public int routeID;
    public int stopsByIDSize;
    private ArrayList<Station> stops;

    TrainRoute(int routeID, ArrayList<Integer> stopsByID, StationDatabase stationDatabase){
        stops = new ArrayList<>();
        this.routeID = routeID;
        stopsByIDSize = stopsByID.size();

        for (Integer integer : stopsByID) {
            stops.add(stationDatabase.getStationsById().get(integer));
        }
    }

    public TrainRoute() {
        routeID = 0;

    }


    public ArrayList<Station> getStops(){ return this.stops;}

    public Station getStop(int stopID){
        return this.stops.get(stopID);
    }
    /*public void setStop(Station station, int id){
        int stopsSize = stops.size();
        if(if==stopsSize){

        }else if(id>stopsSize){
            id = id%stopsSize;
        }

        if(stops.get(id==0? stopsSize:(id-1)).connectedWith.contains(station) ) {
            if(stops.get(id== stopsSize? 0:id+1).connectedWith.contains(station)) {
                this.stops.set(id, station);
            } else throw new IlegalArgumentException("Station not connected with next station in trainroute");
        } else throw new IllegalArgumentException("Station not connected with previous station in trainroute");

    }*/

}
