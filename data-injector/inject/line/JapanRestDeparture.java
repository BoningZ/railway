package inject.line;

import DAO.line.DepartureDAO;

import java.util.List;
import java.util.Random;

public class JapanRestDeparture {
    public static DepartureDAO departureDAO=new DepartureDAO();

    public static void main(String[] args) {
        List<String> rest=departureDAO.getRestJP();
        for(String r:rest){
            int hour=5; Random random=new Random();
            while(hour<24){
                departureDAO.reInject(r+String.format("%02d",hour)+"00",7,hour*60,r,"JP-001");
                hour+=random.nextInt(4);
            }
        }
    }

}
