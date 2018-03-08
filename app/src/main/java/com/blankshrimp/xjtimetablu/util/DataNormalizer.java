package com.blankshrimp.xjtimetablu.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Flabbergast13 on 18/2/24.
 */

public class DataNormalizer {

    private final String regularEX = "<td class=\"(.*?)\" width=\"250px\"></td>|<td class=\"R1\">(.*?)-(.*?)-(.*?)</td>\n" +
            "            </tr>\n" +
            "            <tr class=\"inR\">\n" +
            "              <td class=\"R2\">(.*?)</td>\n" +
            "            </tr>\n" +
            "            <tr class=\"inR\">\n" +
            "              <td class=\"R4\">(.*?)</td>\n" +
            "            </tr>\n" +
            "            <tr class=\"inR\">\n" +
            "              <td class=\"R4\">(.*?)</td>";
    private List<Integer> day = new ArrayList<>();

    public DataNormalizer() {
    }

    /**
     * This function gets data from html source code
     * @param input is html source file
     * @return
     */
    private String[][] firstNormalizer(String input) {
        Pattern pp = Pattern.compile("<td colspan=\"(.*?)\">Monday</td>|<td colspan=\"(.*?)\">Tuesday</td>|<td colspan=\"(.*?)\">Wednesday</td>|<td colspan=\"(.*?)\">Thursday</td>|<td colspan=\"(.*?)\">Friday</td>|<td colspan=\"(.*?)\">Saturday</td>|<td colspan=\"(.*?)\">Sunday</td>");
        Matcher mm = pp.matcher(input);
        int weekNum = 1;
        while (mm.find()) {
            if (mm.group(weekNum).length()>0) {
                for (int i = 0; i < Integer.parseInt(mm.group(weekNum)); i++) {
                    day.add(weekNum-1);
                }
            } else {
                day.add(weekNum-1);
            }
            weekNum++;
        }
        String[][] result = new String[22 * day.size()][6];
        Pattern p = Pattern.compile(regularEX);
        Matcher m = p.matcher(input);
        int i = 0;
        while (m.find()) {
            if (m.group(1) == null) {
                result[i][0] = m.group(2);
                result[i][1] = m.group(3);
                result[i][2] = m.group(4);
                result[i][3] = m.group(5);
                result[i][4] = m.group(6);
                result[i][5] = m.group(7);
            } else {
                result[i][0] = m.group(1);
            }
            i++;
        }
        return result;
    }

    /**
     * This function normalize string array to List-Map
     * @param input
     * @return
     */
    private List<List<Map<String, String>>> secondNormalizer(String[][] input) {
        List<List<Map<String, String>>> result = new ArrayList<>();

        for (int i = 0; i < day.size(); i++) {
            List<Map<String, String>> list = new ArrayList<>();
            for (int j = 0; j < 22; j ++) {
                Map<String, String> map = new HashMap<>();
                addToMap(i+ j*day.size(), input, map);
                list.add(map);
            }
            result.add(list);
        }
        return result;
    }

    /**
     * This function deletes gridcells and most importantly, add start time and end time
     * @param input
     * @return
     */
    private List<List<Map<String, String>>> thirdNormalization(List<List<Map<String, String>>> input) {

        for (int i = 0; i < day.size(); i++) {//i stands for days of the week
            for (int j = 0; j < 22; j++) {//j stands for classes
                Map<String, String> map = new HashMap();
                map = input.get(i).get(j);
                if (map.containsKey("leader")) {
                    int k = j + 1;
                    while (k < 22 && map.equals(input.get(i).get(k))) {
                        input.get(i).get(k).put("delete", "true");
                        k++;
                    }
                    map.put("startime", String.valueOf(j));
                    map.put("endtime", String.valueOf(k - 1));
                }
            }
        }

        //remove duplicated classes and gridcells
        for (int i = 0; i < day.size(); i++) {//i stands for days of the week
            for (int j = 0; j < input.get(i).size(); j++) {//j stands for classes
                Map<String, String> map = new HashMap();
                map = input.get(i).get(j);
                if (map.containsKey("delete") || map.containsValue("gridcell")) {
                    input.get(i).remove(j);
                    j--;
                }
            }
        }
        return input;
    }

    private void addToMap(int i, String[][] input, Map<String, String> map) {

        if (input[i][1] != null) {
            map.put("code", input[i][0]);
            map.put("type", input[i][1]);
            map.put("class", input[i][2]);
            map.put("leader", input[i][3]);
            input[i][4] = input[i][4].replaceAll(".*-", "-");
            map.put("location", input[i][4]);
            map.put("weeks", input[i][5].substring(5));
        } else {
            map.put("code", "gridcell");
        }
    }

    /**
     * This is a merged function for all normalizing process, I just call this function to reach data
     * @param input
     * @return
     */
    public List<List<Map<String, String>>> dataNormalize(String input) {
        return thirdNormalization(secondNormalizer(firstNormalizer(input)));
    }

    public List<Integer> getDay() {
        return day;
    }
}
