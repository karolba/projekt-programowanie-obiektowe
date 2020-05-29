package projekt;

public class Train extends MapObject{
    public int trainID;
    public String name;
    public int costPerKM;
    public double profitPerPassenger; //profit for one passenger per ONE KILOMETER
    public int seats;
    public StationLink currentLink;
    double linkProgress;

    Train(int trainID, String name, int costPerKM, double profitPerPassenger, int seats, StationLink currentLink){
        this.trainID = trainID;
        this.name = name;
        this.costPerKM = costPerKM;
        this.profitPerPassenger = profitPerPassenger;
        this.seats = seats;
        this.currentLink = currentLink;
        this.linkProgress = 0;
        this.coordX = currentLink.from.coordX;
        this.coordY = currentLink.from.coordY;

    }
    public Train(){
        this(0,"defaultTrainName",0,0, 0,new StationLink());
    }

    public String getTrain(){
        return "\nprojekt.Train ID: " + this.trainID + "\nName: " + this.name + "\nCost (per KM): " + this.costPerKM
                + "\nProfit per passenger for KM: " + this.profitPerPassenger + "\nSeats: " + this.seats
                + "\nCurrent link ID: " + this.currentLink.linkID + "\nLink Progress (%): " + (this.linkProgress*100)
                + "\nCoordX: " + this.coordX + "\nCoordY: " + this.coordY;
    }

    @Override
    public void draw(){
        //draws projekt.Train
    }
}