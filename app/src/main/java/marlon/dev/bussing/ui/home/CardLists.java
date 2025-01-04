package marlon.dev.bussing.ui.home;

public class CardLists {
    private String BusNumber;
    private String FromLocation;
    private String ToWhereLocation;
    private int BusCompanyImage;
    private String CurrentLocation;
    private String DepartureTime;

    public CardLists(String busNumber, String fromLocation, String toWhereLocation, int busCompanyImage, String currentLocation, String departureTime) {
        this.BusNumber = busNumber;
        this.FromLocation = fromLocation;
        this.ToWhereLocation = toWhereLocation;
        this.BusCompanyImage = busCompanyImage;
        this.CurrentLocation = currentLocation;
        this.DepartureTime = departureTime;

    }

    public String getBusNumber() {
        return BusNumber;
    }

    public void setBusNumber(String busNumber) {
        BusNumber = busNumber;
    }

    public String getFromLocation() {
        return FromLocation;
    }

    public void setFromLocation(String fromLocation) {
        FromLocation = fromLocation;
    }

    public String getToWhereLocation() {
        return ToWhereLocation;
    }

    public void setToWhereLocation(String toWhereLocation) {
        ToWhereLocation = toWhereLocation;
    }

    public int getBusCompanyImage() {
        return BusCompanyImage;
    }

    public void setBusCompanyImage(int busCompanyImage) {
        BusCompanyImage = busCompanyImage;
    }

    public String getCurrentLocation() {
        return CurrentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        CurrentLocation = currentLocation;
    }

    public String getDepartureTime() {
        return DepartureTime;
    }

    public void setDepartureTime(String departureTime) {
        DepartureTime = departureTime;
    }
}