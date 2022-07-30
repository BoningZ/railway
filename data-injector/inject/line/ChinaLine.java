package inject.line;

import DAO.line.LineDAO;

public class ChinaLine {
    public static void main(String[] args) {
        LineDAO lineDAO=new LineDAO();
        lineDAO.chinaLine();
    }
}
