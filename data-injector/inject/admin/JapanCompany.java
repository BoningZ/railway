package inject.admin;

import DAO.admin.CompanyDAO;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class JapanCompany {
    public static void main(String[] args) throws IOException {
        CompanyDAO companyDAO=new CompanyDAO();
        Path path = Paths.get("D:\\Java_demo\\railway\\railway\\data-injector\\inject\\admin\\jp_com.csv");
        Scanner sc = new Scanner(path);
        sc.nextLine();
        while(sc.hasNextLine()){
            String infos=sc.nextLine();
            String[] info=infos.split(",");
            companyDAO.reInject("JP-"+info[0],info[2],info[6],1,"JP");
        }
    }
}
