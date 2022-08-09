package inject.line;

import DAO.line.LineDAO;
import DAO.station.StationDAO;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.*;

public class KoreaLine {
    public static LineDAO lineDAO=new LineDAO();

    public static void main(String[] args) {
        List<String> lines=new ArrayList<>();
        Map<String,String> name=new HashMap<>();
        List<String> allStation=new StationDAO().getAllKorea();
        for(String s1:allStation)
            for(String s2:allStation)
                if(!s1.equals(s2)){
                    try {
                        String result=getByString("https://smart.letskorail.com/classes/com.korail.mobile.seatMovie.ScheduleView?Device=IP&Version=190617001&adjStnScdlOfrFlg=Y&ebizCrossCheck=Y&key=korail1234567890&radJobId=1&rtYn=N&selGoTrain=109&srtCheckYn=Y&txtGoAbrdDt=20220805&txtGoHour=100000&txtMenuId=11&txtPsgFlg_1=01&txtPsgFlg_2=00&txtPsgFlg_3=00&txtPsgFlg_4=00&txtPsgFlg_5=00&txtSeatAttCd_2=000&txtSeatAttCd_3=000&txtSeatAttCd_4=015&txtTrnGpCd=109&txtGoEnd="+s1+"&txtGoStart="+s2);
                        Map<String,Map<String,List<Map<String,String>>>> json=(Map<String,Map<String,List<Map<String,String>>>>) JSON.parse(result);
                        List<Map<String,String>> line=json.get("trn_infos").get("trn_info");
                        for(Map<String,String> l:line){
                            String num=l.get("h_trn_no");
                            if(!lines.contains(num)){
                                System.out.println("adding:"+num+":"+l.get("h_trn_clsf_nm"));
                                lines.add(num);
                                name.put(num,l.get("h_trn_clsf_nm"));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        for(String l:lines)lineDAO.reInject("KR-"+l,l+name.get(l),name.get(l).contains("KTX")?"KR-100000":"KR-210000","KR-01",true);

    }
    public final static String getByString(String url) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        try {
            HttpGet httpget = new HttpGet(url);
            httpget.addHeader("Accept", "text/html");
            httpget.addHeader("Accept-Charset", "utf-8");
            httpget.addHeader("Accept-Encoding", "gzip");
            httpget.addHeader("Accept-Language", "en-US,en");
            httpget.addHeader("User-Agent",
                    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.160 Safari/537.22");
            ResponseHandler responseHandler = new ResponseHandler() {

                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        System.out.println(status);
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        System.out.println(status);
                        Date date=new Date();
                        System.out.println(date);
                        System.exit(0);
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }
            };
            String responseBody = (String) httpclient.execute(httpget, responseHandler);


            Thread.currentThread().sleep(200);
            return responseBody;
        } finally {
            httpclient.close();
        }
    }
}
