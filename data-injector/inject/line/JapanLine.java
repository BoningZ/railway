package inject.line;

import DAO.line.LineDAO;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;

public class JapanLine {
    private static LineDAO lineDAO=new LineDAO();

    public static void main(String[] args) throws IOException {
        Path path= Paths.get("D:\\Java_demo\\railway\\railway\\data-injector\\inject\\line\\jp-line.csv");
        Scanner sc1=new Scanner(path);
        sc1.nextLine();
        while(sc1.hasNextLine()) {
            String[] info=sc1.nextLine().split(",");
            String trainId=null;
            if(info[0].length()==4)trainId="JP-E7";
                    else trainId="JP-7200";
            lineDAO.reInject("JP-r"+info[0],info[2],trainId,"JP-"+info[1],true);
        }
    }
}
