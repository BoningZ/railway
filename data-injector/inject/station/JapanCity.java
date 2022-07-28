package inject.station;

import DAO.station.StationDAO;

import java.io.IOException;

public class JapanCity {
    public static void main(String[] args) throws IOException {
        StationDAO stationDAO=new StationDAO();
        stationDAO.addJapanCity();

    }
}
