package inject.line;

import DAO.line.StoppingDAO;

public class ChinaStopping {
    public static void main(String[] args) {
        StoppingDAO stoppingDAO=new StoppingDAO();
        stoppingDAO.addChinaStopping();
    }
}
