import com.example.android.sunshine.app.WeatherDataParser;

import org.junit.Test;


import java.io.IOException;
import java.io.InputStream;


public class WeatherDataParserTest {


   public String readJsonFromFile(){

       String json = null;
       try {

           InputStream is = getClass().getResourceAsStream("forecast.json");

           int size = is.available();

           byte[] buffer = new byte[size];

           is.read(buffer);

           is.close();

           json = new String(buffer, "UTF-8");


       } catch (IOException ex) {
           ex.printStackTrace();
           return null;
       }
       return json;

   }

    @Test
   public void parseJsonFor3HourWeather() throws Exception {
        WeatherDataParser weathertest = new WeatherDataParser();

        String jsonToParser = readJsonFromFile();
       // String[] expectedWeather = ["Fri Nov 11 - light rain - "","",",""];

        String[] resultWeather = weathertest.parseJsonFor3HourWeather(jsonToParser);
      //  Assert.assertArrayEquals(expectedWeather,resultWeather);

    }

//    @Test
//    public void parseJsonFor3HourWeather() throws Exception {
//
//        JSONObject o = new JSONObject(weatherJsonStr);
//        JSONArray arrayOfTests = (JSONArray) o.get("list");
//
//        LinkedHashMap<String, Double> map = new LinkedHashMap<>();
//
//        String date = "";
//        int j = 0;
//        Double tempMaxLast = 0.0;
//        Double tempMinLast = 0.0;
//        String[] forecast = new String[5];
//        for (int i = 0; i < arrayOfTests.length(); i++) {
//
//            JSONObject item = (JSONObject) arrayOfTests.get(i);
//
//
//            date = returnDay(item.get("dt_txt").toString());
//
//            JSONObject Main = (JSONObject) item.get("main");
//            Double tempMax = Double.parseDouble(Main.get("temp_max").toString());
//            tempMinLast = tempMax;
//            JSONObject weatherObject = item.getJSONArray("weather").getJSONObject(0);
//            String description = weatherObject.getString("description");
//
//            if (map.containsKey(date)){
//
//                if (tempMaxLast < tempMax){
//                    tempMaxLast = tempMax;
//                }else
//                {
//                    if (tempMax < tempMinLast){
//                        tempMinLast = tempMax;
//                    }
//
//                }
//
//
//                //    String mapValue = map.get(date).toString();
//                //  Double d = Double.parseDouble(mapValue);
//                //   if (tempMax > d){
//                map.put(date, tempMax);
//
//                //   }
//            }else {
//                if (i != 0){
//
//                    forecast[j] = date + " - " + description + " - " + tempMaxLast + " / " + tempMinLast;
//                    j++;
//                }
//
//
//                map.put(date, tempMax);
//            }
//
//        }
//        //  Double d = (Double) getElementByIndex(map,0);
//        return forecast;
//    }
//
//    @Test
//    public Object getElementByIndex(LinkedHashMap map, int i) throws Exception {
//        return map.get(map.keySet().toArray()[i]);
//    }
//
//    @Test
//    public void returnDay() throws Exception {
//        String str_date = date;
//        DateFormat formatter;
//
//        formatter = new SimpleDateFormat("yyyy-MM-dd");
//        String day = "";
//        Date newdate = null;
//        try {
//
//            newdate = formatter.parse(str_date);
//            String[] splitDate = (newdate.toString()).split(" ");
//            day = (""+splitDate[0]+", "+splitDate[1]+" "+ splitDate[2]+"");
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return day;
//    }
//    }

}