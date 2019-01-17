package com.fr.data;

import com.fr.third.org.apache.http.client.methods.CloseableHttpResponse;
import com.fr.third.org.apache.http.client.methods.HttpGet;
import com.fr.third.org.apache.http.impl.client.CloseableHttpClient;
import com.fr.third.org.apache.http.impl.client.HttpClients;
import com.fr.third.org.apache.http.util.EntityUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: Francis
 * @Description:
 * @TIME: Created on 2018/12/18
 * @Modified by:
 */
@SuppressWarnings("unchecked")
public class FanRuanRequest extends AbstractTableData{

    /**
     * 列名数组，保存程序数据集所有列名
     */
    private String[] columnNames;
    /**
     * 定义程序数据集的列数量
     */
    private int columnNum = 17;
    /**
     * 定义程序数据集的行数量
     */
    private int rowNum;

    /**
     * 保存查询得到列值
     */
    private Object[][] valueList ;

    public FanRuanRequest() {

        String[] tableColumn={"openAvailable","wdNewBid","wdContinueBid","xPlanedUndertake","xBidPlanedReturn","xContinue","xBidReturnTomorrowExit","xNetIncome","totalPlanedReturn","expectContinueRate","expectContinue","expectRecharge","dailyCost","zichanzonge","mojizonge","zijinjieyu","offset_date"};

        columnNames=tableColumn;

    }

    private void init() {

        if(valueList!=null){
            return;
        }

        double hNewBid = Double.valueOf(this.getParameters(null)[0].getValue().toString())*10000;
        double wNewBid = Double.valueOf(this.getParameters(null)[1].getValue().toString())*10000;
        double hContinueRate = Double.valueOf(this.getParameters(null)[2].getValue().toString())/100;
        double wContinueRate = Double.valueOf(this.getParameters(null)[3].getValue().toString())/100;
        double hRecharge = Double.valueOf(this.getParameters(null)[4].getValue().toString())*10000;
        double wRecharge = Double.valueOf(this.getParameters(null)[5].getValue().toString())*10000;
        int getRowNum = Integer.valueOf(this.getParameters(null)[6].getValue().toString());

        Map<String, Object> param = new HashMap<String, Object>();

        param.put("hNewBid",hNewBid);
        param.put("wNewBid",wNewBid);
        param.put("hContinueRate",hContinueRate);
        param.put("wContinueRate",wContinueRate);
        param.put("hRecharge",hRecharge);
        param.put("wRecharge",wRecharge);

        String jsonData=doRequest("",param);

        this.rowNum=getRowNum;

        valueList=new Object[rowNum][];

        JSONObject report= JSONObject.fromObject(jsonData);

        Object reportList = report.get("report");

        JSONArray jsonObject = JSONArray.fromObject(reportList);

        Iterator<Object> iterator = jsonObject.iterator();

        for (int j=0;j<valueList.length;j++) {
            JSONObject job = (JSONObject) iterator.next();
            Object[] inner=new Object[columnNum];

            for(int i=0;i<columnNames.length-4;i++){
                if("expectContinueRate"!=columnNames[i]){
                    inner[i]=new DecimalFormat("0.00").format(job.getDouble(columnNames[i])/10000);
                }else {
                    inner[i]=new DecimalFormat("0.0000").format(job.get(columnNames[i]));
                }
//                System.out.print(columnNames[i]+"="+inner[i]);
            }

            inner[columnNames.length-4]=new DecimalFormat("0.00").format(job.getDouble("wdNewBid")/10000+job.getDouble("wdContinueBid")/10000);
            inner[columnNames.length-3]=new DecimalFormat("0.00").format(job.getDouble("expectContinue")/10000+job.getDouble("expectRecharge")/10000);
            inner[columnNames.length-2]=new DecimalFormat("0.00").format(job.getDouble("openAvailable")/10000-job.getDouble("dailyCost")/10000);

            String version = job.getString("extractTime");
            int offset_days = job.getInt("offsetDays");

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            Date offset_date=null;
            try {
                offset_date = format.parse(version);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar ca = Calendar.getInstance();
            ca.setTime(offset_date);
            ca.add(Calendar.DATE, offset_days);
            offset_date = ca.getTime();
            String enddate = format.format(offset_date);

            inner[columnNames.length-1]=enddate;
//            System.out.print(columnNames[columnNames.length-4]+"="+inner[columnNames.length-4]);
//            System.out.print(columnNames[columnNames.length-3]+"="+inner[columnNames.length-3]);
//            System.out.print(columnNames[columnNames.length-2]+"="+inner[columnNames.length-2]);
//            System.out.print(columnNames[columnNames.length-1]+"="+inner[columnNames.length-1]);
//            System.out.println();
            valueList[j]=inner;
        }

//        Object[][] obj={{"test1","col2"},{"test2","col2"}};
    }

    //test
//    public static void main(String[] arg) {
//
//        FanRuanRequest fr=new FanRuanRequest();
//        System.out.println(fr.getValueAt(0,1));
//        System.out.println(fr.getRowCount());
//        System.out.println(fr.getColumnCount());
//        System.out.println(fr.getColumnName(1));
//    }

    public String doRequest(String stringUrl, Map<String, Object> params) {

        stringUrl="http地址";

        String content = "";
        //请求结果
        CloseableHttpResponse response = null;
        //实例化httpclient
        CloseableHttpClient httpclient = HttpClients.createDefault();

        try {
            StringBuilder url = new StringBuilder(stringUrl);
            if(params!=null && params.size()>0) {
                url.append("?");
                for(Map.Entry<String, Object> entry : params.entrySet()) {
                    url.append(entry.getKey()+"="+entry.getValue()+"&");
                }
                url.substring(0, url.length()-1);
            }
            //实例化get方法
            HttpGet httpget = new HttpGet(url.toString());
            //执行get请求
            response = httpclient.execute(httpget);

            if(response.getStatusLine().getStatusCode()==200) {
                content = EntityUtils.toString(response.getEntity(),"utf-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    @Override
    public int getColumnCount() {
        return columnNum;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public int getRowCount() {

        init();
        return valueList.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        init();
        return valueList[rowIndex][columnIndex];
    }
}
