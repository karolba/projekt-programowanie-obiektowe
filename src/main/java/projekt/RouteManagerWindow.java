package projekt;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The window for managing routes and trains
 */
public class RouteManagerWindow {
    public RouteManager routeManager;
    public StationDatabase stationDatabase;
    private MainWindow mainWindow;
    private VBox trainsBox;
    private VBox routesBox;
    private Tab settingsTab;
    private Tab trainsTab;
    private Tab timetableTab;

    RouteManagerWindow (RouteManager routeManager, StationDatabase stationDatabase, MainWindow mainWindow){
        this.routeManager = routeManager;
        this.stationDatabase = stationDatabase;
        this.mainWindow = mainWindow;
        trainsBox = new VBox(8);
        routesBox = new VBox(8);
    }

    public RouteManager getRouteManager(){ return routeManager;}

    /**
     *
     * @return a JavaFX Scene representing the window of a RouteManager
     */
    public Scene getScene(){

        trainsBox.getChildren().clear();
        routesBox.getChildren().clear();
        TabPane settings = new TabPane();
        settings.setMinHeight(850);
        settings.setMinWidth(900);
        settings.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        settings.setTabDragPolicy(TabPane.TabDragPolicy.FIXED);
        settingsTab  = getSettingsTab();
        trainsTab = getTrainsTab();
        timetableTab = getTimeTableTab();
        settings.getTabs().addAll(timetableTab, trainsTab, settingsTab);
        Scene routeManagerScene = new Scene(settings, 1400,850);
        return routeManagerScene;
    }

    private ArrayList<Region> getSingleTrainGraphicRepresentation(Train train){
        ArrayList<Region> singleTrain = new ArrayList<>();
        //separator
        singleTrain.add(new Separator(Orientation.HORIZONTAL));
        //ID
        HBox idBox = new HBox(10);
        Label id = new Label("ID: " + Integer.toString(train.getTrainID()) + ".");
        idBox.getChildren().add(id);
        singleTrain.add(idBox);
        //name
        HBox nameEditor = new HBox(10);
        Label name = new Label("Nazwa: " + train.getName());
        name.setMinWidth(300);
        TextField editNameField = new TextField();
        editNameField.setPrefColumnCount(18);
        editNameField.setOnAction(e -> {
            train.setName(editNameField.getCharacters().toString());
            name.setText("Nazwa: " + train.getName());
            editNameField.setText("");
        });
        nameEditor.getChildren().addAll(name,editNameField);
        singleTrain.add(nameEditor);
        //current route
        String stops = "";
        Label routeShower = new Label();
        HBox routeEditor = new HBox(10);
        Label currentRouteID = new Label("ID trasy: " + train.getCurrentTrainRoute().routeID);
        currentRouteID.setMinWidth(300);
        TextField editCurrentRouteID = new TextField();
        editCurrentRouteID.setPrefColumnCount(4);
        editCurrentRouteID.setOnAction(e -> {
            int identificator;
            try{
                identificator = Integer.parseInt(editCurrentRouteID.getCharacters().toString());
                if(routeManager.getRoutes().containsKey(identificator)) {
                    train.resetRoute(routeManager.getRouteByID(Integer.parseInt(editCurrentRouteID.getCharacters().toString())));
                    currentRouteID.setText( "ID trasy: " + train.getCurrentTrainRoute().routeID);
                    String s = "";
                    for(Station stop : train.getCurrentTrainRoute().getStops()){s += stop.name + " - ";}
                    s = s.substring( 0, s.lastIndexOf(" - "));
                    routeShower.setText("Przystanki: " + s);
                    editCurrentRouteID.setText("");

                } else {
                    this.showError("Nie istnieje trasa o podanym ID: " + identificator);
                }
            }catch (NumberFormatException ex) {
                this.showError("Wprowadzono nieprawid\u0142ow\u0105 warto\u015b\u0107.: " + editCurrentRouteID.getCharacters().toString());
            }
        });
        routeEditor.getChildren().addAll(currentRouteID,editCurrentRouteID);
        singleTrain.add(routeEditor);

        for(Station stop : train.getCurrentTrainRoute().getStops()){stops+= stop.name + " - ";}
        stops = stops.substring( 0, stops.lastIndexOf(" - "));
        routeShower.setText("Przystanki: " + stops);
        singleTrain.add(routeShower);
        //seats
        HBox seatsEditor = new HBox(10);
        Label seats = new Label("Ilo\u015b\u0107 siedze\u0144: " + train.getSeats());
        seats.setMinWidth(300);
        TextField editSeats = new TextField();
        editSeats.setPrefColumnCount(4);
        editSeats.setOnAction(e -> {
            int s;
            try{
                s = Integer.parseInt(editSeats.getCharacters().toString());
                if(s>0 && s<10001){
                    train.setSeats(s);
                    seats.setText("Ilo\u015b\u0107 siedze\u0144: " + s);
                    editSeats.setText("");
                } else this.showError("Wprowadzono nieprawid\u0142ow\u0105 warto\u015b\u0107 : " + s + "\n Wprowad\u017a warto\u015b\u0107 z przedzia\u0142u (0;10000>");
            }catch (NumberFormatException ex) {
                this.showError("Wprowadzono nieprawid\u0142ow\u0105 warto\u015b\u0107: " + editSeats.getCharacters().toString());
            }
        });
        seatsEditor.getChildren().addAll(seats,editSeats);
        singleTrain.add(seatsEditor);
        //speed
        HBox speedEditor = new HBox(10);
        Label speed = new Label("Pr\u0119dko\u015b\u0107: " + train.getSpeed() + "km/h");
        speed.setMinWidth(300);
        TextField editSpeed = new TextField();
        editSpeed.setPrefColumnCount(4);
        editSpeed.setOnAction(e -> {
            int s;
            try{
                s = Integer.parseInt(editSpeed.getCharacters().toString());
                if(s < 1225) {
                    if (s> 0) {
                        train.setSpeed(s);
                        speed.setText("Pr\u0119dko\u015b\u0107: " + s + "km/h");
                        editSpeed.setText("");
                    } else this.showError("Pr\u0119dko\u015b\u0107 musi by\u0107 wi\u0119ksza od zera.");
                } else this.showError("Poci\u0105gi nie je\u017cd\u017c\u0105 tak szybko ;)\n Wprowad\u017a warto\u015b\u0107 poni\u017cej pr\u0119dko\u015bci d\u017awi\u0119ku w powietrzu o temperaturze 15 stopni Celsjusza.");
            }catch (NumberFormatException ex) {
                this.showError("Wprowadzono nieprawid\u0142ow\u0105 warto\u015b\u0107: " + editSpeed.getCharacters().toString());
            }
        });
        speedEditor.getChildren().addAll(speed,editSpeed);
        singleTrain.add(speedEditor);
        //costPerKM
        HBox costEditor = new HBox(10);
        Label cost = new Label("Koszt przejazdu jednego kilometra: " + train.getCostPerKM() +"z\u0142.");
        cost.setMinWidth(300);
        TextField editCost = new TextField();
        editCost.setPrefColumnCount(4);
        editCost.setOnAction(e -> {
            int c;
            try{
                c = Integer.parseInt(editCost.getCharacters().toString());
                if(c>=0) {
                    train.setCostPerKM(c);
                    cost.setText("Koszt przejazdu jednego kilometra: " + c +"z\u0142.");
                    editCost.setText("");
                } else this.showError("Koszt nie mo\u017ce by\u0107 ujemny.");
            }catch (NumberFormatException ex) {
                this.showError("Wprowadzono nieprawid\u0142ow\u0105 warto\u015b\u0107: " + editCost.getCharacters().toString());
            }
        });
        costEditor.getChildren().addAll(cost,editCost);
        singleTrain.add(costEditor);
        //profitPerPassenger
        HBox profitEditor = new HBox(10);
        Label profit = new Label("Zysk z jednego pasa\u017cera na kilometr trasy: " + train.getProfitPerPassenger() +"z\u0142");
        profit.setMinWidth(300);
        TextField editProfit = new TextField();
        editProfit.setPrefColumnCount(4);
        editProfit.setOnAction(e -> {
            double s;
            try{
                s = Double.parseDouble(editProfit.getCharacters().toString());
                if(s>=0) {
                    train.setProfitPerPassenger(s);
                    profit.setText("Zysk z jednego pasa\u017cera na kilometr trasy: " + s +"z\u0142");
                    editProfit.setText("");
                } else this.showError("Zysk nie mo\u017ce by\u0107 ujemny.");
            }catch (NumberFormatException ex) {
                this.showError("Wprowadzono nieprawid\u0142ow\u0105 warto\u015b\u0107: " + editProfit.getCharacters().toString());
            }
        });
        Button removeTrain = new Button ("Usu\u0144 poci\u0105g");
        removeTrain.setOnAction(e-> {
            routeManager.getTrains().remove(train.getTrainID());
            trainsBox.getChildren().removeAll(singleTrain);
        });
        Region filler = new Region();

        profitEditor.getChildren().addAll(profit,editProfit,filler,removeTrain);
        profitEditor.setHgrow(filler, Priority.ALWAYS);
        singleTrain.add(profitEditor);
        for(Region region :singleTrain){
            region.setPadding(new Insets(0,10,0,10));
        }
        return singleTrain;
    }

    private ArrayList<ArrayList<Region>> getTrainsGraphicRepresentation(ArrayList<Train> trains ){
        ArrayList<ArrayList<Region>> trainsControl = new ArrayList<>();

        for(Train train : trains){
            trainsControl.add(getSingleTrainGraphicRepresentation(train));
        }
        return trainsControl;
    }

    private ArrayList<ArrayList<Region>> getRoutesGraphicRepresentation(ArrayList<TrainRoute> routes){
      ArrayList<ArrayList<Region>> routeGraphicRep = new ArrayList<>();
      for(TrainRoute route:routes){
          routeGraphicRep.add(getSingleRouteGraphicRepresentation(route));
      }

      return routeGraphicRep;
    };

    private ArrayList<Region> getSingleRouteGraphicRepresentation(TrainRoute route){
       // VBox routeBox = new VBox(8);
        HBox stationsBox = new HBox(8);
        HBox box = new HBox(8);
        ArrayList<Region> singleRoute = new ArrayList<>();
        singleRoute.add(new Separator(Orientation.HORIZONTAL));
        singleRoute.add(new Label("ID trasy: " + route.routeID));
        ArrayList<Label> stations = new ArrayList<>();
        for(Station station : route.getStops()){
            stations.add(new Label(station.name + "  ") );
        }
        stationsBox.getChildren().addAll(stations);
        box.getChildren().add(stationsBox);
        Button deleteLastStation = new Button ("Usu\u0144 ostatni\u0105 stacj\u0119");
        if(stations.size() == 0)deleteLastStation.setText("Usu\u0144 tras\u0119");
        AtomicBoolean nextClickDelete = new AtomicBoolean(false);
        deleteLastStation.setOnAction(e -> {
            int i = stations.size();

            if(i == 0){
                nextClickDelete.set(true);
            }if(i == 1) {
                deleteLastStation.setText("Usu\u0144 tras\u0119.");
            }else if(i == 2){
                if(routeManager.getTrainsArrayList().size() != 0){
                    for(Train train : routeManager.getTrains().values()){// checking if some trains have empty route, and if it does, changing it to first not empty one found (to avoid problems with no-route trains)
                        if(train.getCurrentTrainRoute().stopsByIDSize == 2){
                        TrainRoute notEmptyRoute = null; //new TrainRoute();
                        for(TrainRoute trainRoute :routeManager.getTrainRoutesArrayList()){
                            if(trainRoute.stopsByIDSize>1){
                                    if(train.getCurrentTrainRoute() != trainRoute) {
                                          notEmptyRoute = trainRoute;
                                          break;
                                    }
                                }
                            }
                            if(notEmptyRoute == null){
                                routeManager.getTrains().clear();
                                showError("Pociągi zostały usunięte.(Brak Tras)");
                            }else {
                                train.resetRoute(notEmptyRoute);
                            }

                    }
                    }
                }
            }if(nextClickDelete.get()){
                routesBox.getChildren().removeAll(singleRoute);
                routeManager.getRoutes().remove(route.routeID);
            }else {
                box.getChildren().remove(stations.get(i-1));
                stations.remove(i-1);
                route.getStops().remove(i - 1);
                route.stopsByIDSize--;
            }
            if(routeManager.getTrainRoutesArrayList().size()==0){
                routeManager.getTrains().clear();
                showError("Pociągi zostały usunięte.(Brak Tras)");
            }

            mainWindow.refreshScene(getScene());
        });
        box.getChildren().add(deleteLastStation);

        TextField addStation = new TextField();
        addStation.setPrefColumnCount(50);
        String prompt = "";
        for(Station station : route.getStop(route.stopsByIDSize-1).connectedWith){
            prompt += station.stationID + ": " + station.name + "  ";
        }
        addStation.setPromptText(prompt);
        addStation.setOnAction(e -> {
            if(addStation.getCharacters().length() != 0) {
                int id;
                String stationName;
                try {
                    id = Integer.parseInt(addStation.getCharacters().toString());
                    if (stationDatabase.getStationsById().containsKey(id)) {
                        Station addedStation = stationDatabase.findStation(id);
                        if(stations.size()==0){
                            Label stationLabel = new Label(addedStation.name + "  ");
                            stations.add(stationLabel);
                            stationsBox.getChildren().add(stationLabel);
                            route.getStops().add(addedStation);
                            route.stopsByIDSize += 1;
                            addStation.setText("");
                            String promptTwo = "";
                            for(Station station : route.getStop(route.stopsByIDSize-1).connectedWith){
                                promptTwo += station.stationID + ": " + station.name + "  ";
                            }
                            addStation.setPromptText(promptTwo);
                        }else if (route.getStop(route.stopsByIDSize - 1).connectedWith.contains(addedStation)) {
                            Label stationLabel = new Label(addedStation.name + "  ");
                            stations.add(stationLabel);
                            stationsBox.getChildren().add(stationLabel);
                            route.getStops().add(addedStation);
                            route.stopsByIDSize += 1;
                            addStation.setText("");
                            String promptTwo = "";
                            for(Station station : route.getStop(route.stopsByIDSize-1).connectedWith){
                                promptTwo += station.stationID + ": " + station.name + "  ";
                            }
                            addStation.setPromptText(promptTwo);
                        } else {
                            showError("Podana stacja: " + addedStation.name + " nie jest po\u0142\u0105czona z ostatni\u0105 stacj\u0105 tej trasy: " + route.getStop(route.stopsByIDSize - 1).name + ".");
                        }
                    } else {
                        showError("Stacja o podanym ID: " + id + " nie istnieje.");
                    }
                } catch (NumberFormatException g) {
                    stationName = addStation.getCharacters().toString();

                    if (stationDatabase.getStationsByName().containsKey(stationName)) {
                        Station addedStation = stationDatabase.findStation(stationName);
                        if(stations.size()==0){
                            Label stationLabel = new Label(addedStation.name + "  ");
                            stations.add(stationLabel);
                            stationsBox.getChildren().add(stationLabel);
                            route.getStops().add(addedStation);
                            route.stopsByIDSize += 1;
                            addStation.setText("");
                            String promptTwo = "";
                            for(Station station : route.getStop(route.stopsByIDSize-1).connectedWith){
                                promptTwo += station.stationID + ": " + station.name + "  ";
                            }
                            addStation.setPromptText(promptTwo);
                        }else if (route.getStop(route.stopsByIDSize - 1).connectedWith.contains(addedStation)) {
                            Label stationLabel = new Label(addedStation.name + "  ");
                            stations.add(stationLabel);
                            stationsBox.getChildren().add(stationLabel);
                            route.getStops().add(addedStation);
                            route.stopsByIDSize += 1;
                            addStation.setText("");
                            String promptTwo = "";
                            for(Station station : route.getStop(route.stopsByIDSize-1).connectedWith){
                                promptTwo += station.stationID + ": " + station.name + "  ";
                            }
                            addStation.setPromptText(promptTwo);
                        } else {
                            showError("Podana stacja: " + addedStation.name + " nie jest po\u0142\u0105czona z ostatni\u0105 stacj\u0105 tej trasy: " + route.getStop(route.stopsByIDSize - 1).name + ".");
                        }
                    } else {
                        showError("Stacja o podanej nazwie: " + stationName + " nie istnieje.");
                    }
                }
            } else showError("Nie wprowadzono warto\u015bci.");
        });
        box.getChildren().add(addStation);
        singleRoute.add(box);
        return singleRoute;
    }

    private Tab getTimeTableTab(){
        Button startSimulating = new Button("Rozpocznij symulacj\u0119");
        startSimulating.setMinWidth(600);
        startSimulating.setAlignment(Pos.TOP_CENTER);
        startSimulating.setOnAction(e -> {
            this.close();
            mainWindow.changeSettingsButtonText("Zatrzymaj symulacj\u0119 i przejd\u017a do ustawie\u0144.");
            mainWindow.startSimulating();
        });
        routesBox.getChildren().add(startSimulating);
        Button addTrainRoute = new Button("Dodaj now\u0105 tras\u0119");
        addTrainRoute.setOnAction(e->{
            int id = -1;
            for(TrainRoute route : routeManager.getRoutes().values()){
                if(id == -1) id = route.routeID;
                if(route.routeID > id) id = route.routeID;
            }
            id++;
            ArrayList<Integer> list = new ArrayList<Integer>();
            list.add(1);
            TrainRoute trainRoute = new TrainRoute(id, list , stationDatabase );
            routeManager.getRoutes().put(id,trainRoute);
            routesBox.getChildren().addAll(getSingleRouteGraphicRepresentation(trainRoute));
        });
        routesBox.getChildren().add(addTrainRoute);
        Label advice = new Label ("Aby doda\u0107 kolejn\u0105 stacj\u0119, wpisz ID lub nazw\u0119 i zatwierd\u017a(Enter)\n Aby usun\u0105\u0107 tras\u0119, usu\u0144 wszystkie stacje.");
        routesBox.getChildren().add(advice);

        for(ArrayList<Region> control : getRoutesGraphicRepresentation(routeManager.getTrainRoutesArrayList())){
            routesBox.getChildren().addAll(control);
        }

        routesBox.setVisible(true);
        ScrollPane content = new ScrollPane();
        content.setContent(routesBox);
        content.setFitToHeight(true);
        content.setFitToWidth(true);
        Tab timetable = new Tab("Trasy", content);

        return timetable;
    }


    private Tab getSettingsTab(){
        VBox box = new VBox();
        Insets padding = new Insets(8);
        Button startSimulating = new Button("Rozpocznij symulacj\u0119");
        startSimulating.setMinWidth(600);
        startSimulating.setAlignment(Pos.TOP_CENTER);
        startSimulating.setOnAction(e -> {
            this.close();
            mainWindow.changeSettingsButtonText("Zatrzymaj symulacj\u0119 i przejd\u017a do ustawie\u0144.");
            mainWindow.startSimulating();
        });
        box.getChildren().add(startSimulating);

        Label initialMoneyAmount = new Label("Pocz\u0105tkowa ilo\u015b\u0107 pieni\u0119dzy: " + Settings.getInitialMoneyAmount() + "z\u0142");
        TextField setInitialMoneyAmount = new TextField();
        setInitialMoneyAmount.setPrefColumnCount(10);
        setInitialMoneyAmount.setOnAction(e -> {
            try{
                int i = Integer.parseInt(setInitialMoneyAmount.getCharacters().toString());
                Settings.setInitialMoneyAmount(i);
                initialMoneyAmount.setText("Pocz\u0105tkowa ilo\u015b\u0107 pieni\u0119dzy: " + Settings.getInitialMoneyAmount() + "z\u0142");
            }catch (NumberFormatException exce){
                showError("Wprowadzono nieprawid\u0142ow\u0105 warto\u015b\u0107: " + setInitialMoneyAmount.getCharacters().toString());
            }
        });
        HBox moneyEditor = new HBox();
        moneyEditor.setPadding(padding);
        moneyEditor.getChildren().addAll(initialMoneyAmount,setInitialMoneyAmount);

        Label stopCondition = new Label("Czas symulowania: " + Settings.getStopCondition() + "h");
        TextField setStopCondition = new TextField();
        setStopCondition.setPrefColumnCount(10);
        setStopCondition.setOnAction(e -> {
            try{
                int i = Integer.parseInt(setStopCondition.getCharacters().toString());
                Settings.setStopCondition(i);
                stopCondition.setText("Czas symulowania: " + Settings.getStopCondition() + "h");
            }catch (NumberFormatException exce){
                showError("Wprowadzono nieprawid\u0142ow\u0105 warto\u015b\u0107: " + setStopCondition.getCharacters().toString());
            }
        });
        HBox stopConditionEditor = new HBox();
        stopConditionEditor.setPadding(padding);
        stopConditionEditor.getChildren().addAll(stopCondition,setStopCondition);

        Label useRouteManager = new Label("Czy u\u017cywa\u0107 mened\u017cera tras?\n\nZaznacz je\u015bli chcesz aby poci\u0105gi je\u017adzi\u0142y wed\u0142ug rozk\u0142adu." +
                "\nW przeciwnym wypadku trasy b\u0119d\u0105 pseudolosowe.");
        CheckBox ifuseRouteManager = new CheckBox();
        ifuseRouteManager.setSelected(Settings.useRouteManager);
        ifuseRouteManager.setOnAction(e ->{
            boolean b = !Settings.useRouteManager;
            Settings.useRouteManager = b;
            ifuseRouteManager.setSelected(Settings.useRouteManager);
        });
        HBox usingRouteManagerEditor = new HBox();
        usingRouteManagerEditor.setPadding(new Insets(8));
        usingRouteManagerEditor.getChildren().addAll(useRouteManager,ifuseRouteManager);

        box.getChildren().addAll(moneyEditor,stopConditionEditor, usingRouteManagerEditor);
        Tab settings = new Tab("Ustawienia", box);
        return settings;
    }

    private Tab getTrainsTab(){
        Button startSimulating = new Button("Rozpocznij symulacj\u0119");
        startSimulating.setMinWidth(600);
        startSimulating.setAlignment(Pos.TOP_CENTER);
        startSimulating.setOnAction(e -> {
            this.close();
            mainWindow.changeSettingsButtonText("Zatrzymaj symulacj\u0119 i przejd\u017a do ustawie\u0144.");
            mainWindow.startSimulating();
        });
        trainsBox.getChildren().add(startSimulating);

        Button addTrain = new Button("Dodaj poci\u0105g");
        addTrain.setOnAction(e -> {
            try{
                Train newTrain = this.trainMaker();
                routeManager.getTrains().put(newTrain.getTrainID(), newTrain);
                trainsBox.getChildren().addAll(getSingleTrainGraphicRepresentation(newTrain));

            } catch(NullPointerException exc){
                showError("Dodawanie poci\u0105gu anulowane.");
            }
        });
        trainsBox.getChildren().add(addTrain);

        for(ArrayList<Region> singleTrain : getTrainsGraphicRepresentation(routeManager.getTrainsArrayList())){
            trainsBox.getChildren().addAll(singleTrain);
        }

        trainsBox.setSpacing(8);
        trainsBox.setVisible(true);
        //box.setAlignment(Pos.TOP_CENTER);
        ScrollPane content = new ScrollPane();
        content.setContent(trainsBox);
        content.setFitToHeight(true);
        content.setFitToWidth(true);
        Tab trains = new Tab("Poci\u0105gi", content);
        return trains;
    }

    private void close (){ mainWindow.startSimulating();}
    public void showError(String message){
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setTitle("B\u0142\u0105d");

        Button ok = new Button ("Zamknij");
        ok.setOnAction(f -> dialogStage.close());
        VBox vbox = new VBox(new Label (message), ok);
        vbox.setSpacing(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(30));

        dialogStage.setScene(new Scene(vbox));
        dialogStage.show();

    }

    public ArrayList<Tab> getRouteManagerTabs(){
        ArrayList<Tab> list = new ArrayList<>();
        list.add(settingsTab);
        list.add(timetableTab);
        return list;
    }

    private Train trainMaker(){
        Stage trainMaker = new Stage();
        trainMaker.setTitle("Tworzenie poci\u0105gu");
        trainMaker.setMinHeight(400);
        trainMaker.setMinWidth(800);
        trainMaker.initModality(Modality.WINDOW_MODAL);
        final Train[] train = new Train[1];

        //name
        TextField name = new TextField();
        name.setPromptText("Nazwa");
        name.setMinWidth(200);
        //currentRoute
        TextField currentRouteID = new TextField();
        currentRouteID.setPromptText("ID trasy");
        currentRouteID.setMinWidth(200);
        Label routeShower = new Label("Wci\u015bnij enter aby wy\u015bwietli\u0107 przystanki");
        currentRouteID.setOnAction(e -> {
            int routeIDGiven;
            try{
                routeIDGiven = Integer.parseInt(currentRouteID.getCharacters().toString());
                if(routeManager.getRoutes().containsKey(routeIDGiven)) {
                    String s = "";
                    for(Station stop : routeManager.getRouteByID(routeIDGiven).getStops()){s += stop.name + " - ";}
                    s = s.substring( 0, s.lastIndexOf(" - "));
                    routeShower.setText("Przystanki: " + s);

                } else {
                    this.showError("Nie istnieje trasa o podanym ID: " + routeIDGiven);
                }
            }catch (NumberFormatException ex) {
                this.showError("Wprowadzono nieprawid\u0142ow\u0105 warto\u015b\u0107.: " + currentRouteID.getCharacters().toString());
            }
        });
        //seats
        TextField seats = new TextField();
        seats.setPromptText("Ilo\u015b\u0107 siedze\u0144");
        seats.setMinWidth(200);
        //speed
        TextField speed = new TextField();
        speed.setPromptText("Pr\u0119dko\u015b\u0107 (km/h)");
        speed.setMinWidth(200);
        //costPerKM
        TextField costPerKM = new TextField();
        costPerKM.setPromptText("Koszt przejazdu jednego kilometra");
        costPerKM.setMinWidth(200);
        //profitPerPassenger
        TextField profitPerPassenger = new TextField();
        profitPerPassenger.setPromptText("Zysk z przewiezienia jednego klienta na trasie o d\u0142ugo\u015bci jednego kilometra");
        profitPerPassenger.setMinWidth(200);

        final int[] id = {-1};
        final String[] nameGiven = {""};
        final String[] errorMessages = {""};
        final boolean[] errorOccured = {false};
        final TrainRoute[] routeGiven = new TrainRoute[]{new TrainRoute()};
        final int[] seatsGiven = {0};
        final int[] speedGiven = {0};
        final int[] costGiven = {0};
        final double[] profitGiven = {0};

        Button save = new Button("Dodaj poci\u0105g");
        save.setOnAction(e -> {

            //getting last train made ID
            for(Train currentEntry : routeManager.getTrains().values()){
                if(id[0] == -1)  id[0] = currentEntry.getTrainID();
                if(currentEntry.getTrainID() > id[0]) id[0] = currentEntry.getTrainID();
            }
            //new train id
            if (id[0] == -1) {
                id[0] = 1;
            }else id[0]++;
            //getting name

            if (name.getCharacters().length() != 0) {
                nameGiven[0] = name.getCharacters().toString();
            } else {
                errorMessages[0] += "\nNie podano nazwy."; errorOccured[0] = true;};

            //getting currentTrainRoute
            int routeIDGiven;
            if(currentRouteID.getCharacters().length() != 0){
                try{
                    routeIDGiven = Integer.parseInt(currentRouteID.getCharacters().toString());
                    if(routeManager.getRoutes().containsKey(routeIDGiven) && routeManager.getRouteByID(routeIDGiven).stopsByIDSize > 1) {
                    routeGiven[0] = routeManager.getRouteByID(routeIDGiven);
                    } else {
                        errorMessages[0] += "\nNie istnieje trasa o podanym ID: " + routeIDGiven;
                        errorOccured[0] = true;
                    }
                }catch (NumberFormatException ex) {
                    errorMessages[0] += "\nID trasy: Wprowadzono nieprawid\u0142ow\u0105 warto\u015b\u0107.: " + currentRouteID.getCharacters().toString();
                    errorOccured[0] = true;
                }
            }else {
                errorMessages[0] += "\nNie wprowadzono ID trasy.";
                errorOccured[0] = true;
            }

            //getting seats

            if(seats.getCharacters().length() != 0){
                int s;
                try{
                    s = Integer.parseInt(seats.getCharacters().toString());
                    if(s>0 && s<10001){
                        seatsGiven[0] = s;
                    } else {
                        errorMessages[0] += "\nIlo\u015b\u0107 siedze\u0144: Wprowadzono nieprawid\u0142ow\u0105 warto\u015b\u0107 : " + s + " Wprowad\u017a warto\u015b\u0107 z przedzia\u0142u (0;10000>";
                        errorOccured[0] = true;
                    }
                }catch (NumberFormatException ex) {
                    errorMessages[0] += "\nIlo\u015b\u0107 siedze\u0144: Wprowadzono nieprawid\u0142ow\u0105 warto\u015b\u0107: " + seats.getCharacters().toString();
                    errorOccured[0] = true;
                }
            } else {
                errorMessages[0] += "\nNie wprowadzono liczby siedze\u0144."; errorOccured[0] =true;}
            //getting speed

            if(speed.getCharacters().length() != 0){
                int s;
                try{
                    s = Integer.parseInt(speed.getCharacters().toString());
                    if(s<1225){
                        if(s>0) {
                            speedGiven[0] = s;
                        } else { errorMessages[0] += "\nPr\u0119dko\u015b\u0107 musi by\u0107 wi\u0119ksza od zera."; errorOccured[0] = true; }
                    } else {
                        errorMessages[0] += "\nPoci\u0105gi nie je\u017cd\u017c\u0105 tak szybko ;)\nWprowad\u017a warto\u015b\u0107 pr\u0119dko\u015bci mniejsz\u0105 ni\u017c pr\u0119dko\u015b\u0107 d\u017awi\u0119ku w powietrzu o temperaturze 15 stopni Celsjusza.";
                        errorOccured[0] = true;
                    }
                }catch (NumberFormatException ex) {
                    errorMessages[0] += "\nPr\u0119dko\u015b\u0107: Wprowadzono nieprawid\u0142ow\u0105 warto\u015b\u0107: " + speed.getCharacters().toString();
                    errorOccured[0] = true;
                }
            } else {
                errorMessages[0] += "\nNie wprowadzono pr\u0119dko\u015bci."; errorOccured[0] = true;}
            //getting cost

            int c;
            if(costPerKM.getCharacters().length() != 0) {
                try {
                    c = Integer.parseInt(costPerKM.getCharacters().toString());
                    if (c >= 0) {
                        costGiven[0] = c;
                    } else {
                        errorMessages[0] += "\nKoszt nie mo\u017ce by\u0107 ujemny."; errorOccured[0] = true;}
                } catch (NumberFormatException ex) {
                    errorMessages[0] += "\nKoszt przejazdu jednego kilometra: Wprowadzono nieprawid\u0142ow\u0105 warto\u015b\u0107: " + costPerKM.getCharacters().toString();
                    errorOccured[0] = true;
                }
            } else {
                errorMessages[0] += "\nNie wprowadzono kosztu przejazdu jednego kilometra."; errorOccured[0] = true;}
            //getting profit

            double s;
            if(profitPerPassenger.getCharacters().length() != 0) {
                try {
                    s = Double.parseDouble(profitPerPassenger.getCharacters().toString());
                    if (s >= 0) {
                        profitGiven[0] = s;
                    } else {
                        errorMessages[0] += "\nZysk nie mo\u017ce by\u0107 ujemny.";
                        errorOccured[0] = true;
                    }
                } catch (NumberFormatException ex) {
                    errorMessages[0] += "\nZysk z przewozu jednego pasa\u017cera na trasie o d\u0142ugo\u015bci kilometra: Wprowadzono nieprawid\u0142ow\u0105 warto\u015b\u0107: " + profitPerPassenger.getCharacters().toString();
                    errorOccured[0] = true;
                }
            } else {errorMessages[0] += "\nNie wprowadzono zysku z przewozu jednego pasa\u017cera na trasie o d\u0142ugo\u015bci kilometra."; errorOccured[0] = true;}
            for (TrainRoute route :routeManager.getRoutes().values()){
                //sprawd\u017a czy jakakolwiek trasa ma wi\u0119cej ni\u017c jeden przystanek
            }
            if(!errorOccured[0]){
                train[0] = new Train(id[0],nameGiven[0], costGiven[0], profitGiven[0], seatsGiven[0], speedGiven[0], routeGiven[0]);
                trainMaker.close();
            } else {
                showError(errorMessages[0]);
                errorMessages[0] = "";
                errorOccured[0] = false;
            }


        });
        VBox box = new VBox();
        box.getChildren().addAll(name, currentRouteID, routeShower, seats, speed, costPerKM, profitPerPassenger, save);
        box.setPadding(new Insets(30));
        trainMaker.setScene(new Scene(box));
        trainMaker.showAndWait();

        return train[0];
    }

}
