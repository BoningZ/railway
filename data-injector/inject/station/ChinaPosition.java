package inject.station;

import DAO.station.StationDAO;
import inject.util.Requester;

public class ChinaPosition {
    public static void main(String[] args) {
        StationDAO stationDAO=new StationDAO();
        stationDAO.editPosition();
    }
}
