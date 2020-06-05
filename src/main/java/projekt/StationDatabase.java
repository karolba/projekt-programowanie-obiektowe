package projekt;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;


public class StationDatabase {
    private HashMap<Integer, Station> stationsById = new HashMap<>();
    private HashMap<String, Station> stationsByName = new HashMap<>();
    private ArrayList<StationLink> stationLinks = new ArrayList<>();

    StationDatabase(){ //makes stations and connections database based on data files (stations.json, trains.json)
        JSONObject stationsFile = new JSONObject(new JSONTokener(getClass().getResourceAsStream("/stations.json")));

        for(Object stationJsonObject : stationsFile.getJSONArray("stations")) {
            JSONObject s = (JSONObject) stationJsonObject;
            int id = s.getInt("id");
            String name = s.getString("name");
            Station station = new Station(
                    id,
                    name,
                    s.getDouble("profitability"),
                    s.getDouble("coordX"),
                    s.getDouble("coordY"));
            stationsById.put(id, station);
            stationsByName.put(name, station);
        }

        for(Object linkJsonObject : stationsFile.getJSONArray("links")) {
            JSONArray link = (JSONArray) linkJsonObject;
            for(int i = 0; i < link.length() - 1; i++) {
                linkTwoStations(link.getInt(i), link.getInt(i + 1));
            }

        }
/*
        try {
            System.out.println("Reading trains datafile ...");
            FileReader scan = new FileReader(trainData);
            BufferedReader buffer = new BufferedReader(scan);
            int i = 0; //licznik czytanych linii
            int j = 0; //licznik obiektów projekt.Train w liście trains
            String line;
            trains = new ArrayList<Train>();
            trains.add(new Train());

            while((line = buffer.readLine()) != null){
                switch (i){
                    case 0:
                        this.trains.get(j).trainID = Integer.parseInt(line);
                        break;
                    case 1:
                        this.trains.get(j).name = line;
                        break;
                    case 2:
                        this.trains.get(j).costPerKM = Integer.parseInt(line);
                        break;
                    case 3:
                        this.trains.get(j).profitPerPassenger = Double.parseDouble(line);
                        break;
                    case 4:
                        this.trains.get(j).seats = Integer.parseInt(line);
                        break;
                    case 5:
                        this.trains.get(j).currentLink = stationLinks.get(Integer.parseInt(line)-1);
                        break;
                }
                i++;
                if (i == 6) {
                    j++;
                    i = 0;
                    this.trains.add(new Train());
                }

            }
            scan.close();
            buffer.close();

        } catch (IOException e){
            e.printStackTrace();
        }*/

    }

    private void linkTwoStations(int id1, int id2) {
        Station s1 = getStationsById().get(id1);
        Station s2 = getStationsById().get(id2);
        s1.connectTo(s2);
        s2.connectTo(s1);

        stationLinks.add(new StationLink(s1, s2));
    }

    public ArrayList<StationLink> getStationLinks() {
        return stationLinks;
    }

    public HashMap<Integer, Station> getStationsById() {
        return stationsById;
    }

    public HashMap<String, Station> getStationsByName() {
        return stationsByName;
    }

    public Station findStation(String stationName) {
        return stationsByName.get(stationName);
    }

    public Station findStation(int stationId) {
        return stationsById.get(stationId);
    }
}

