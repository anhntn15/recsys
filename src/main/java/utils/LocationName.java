package utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Map location id (cityId, districtId) to name, user for preview API
 */
public class LocationName {
    private static final Map<String, String> cities = readFile("data/city.txt");
    private static final Map<String, String> districts = readFile("data/district.txt");

    private static Map<String, String> readFile(String path) {
        List<String> data = FileUtils.readFileByLine(path);
        Map<String, String> result = new HashMap<>();

        for (String line : data) {
            String tmps[] = line.trim().split("\t");
            if (tmps.length >= 2) {
                result.put(tmps[0], tmps[1]);
            }
        }

        System.out.println("read file location name:" + result);
        return result;
    }

    public static String getCity(Object cityId) {
        String name = cities.get(String.valueOf(cityId));
        if (name == null || name.length() == 0) {
            return cityId + "";
        } else {
            return name;
        }
    }

    public static String getDistrict(Object districtId) {
        String name = districts.get(String.valueOf(districtId));
        if (name == null || name.length() == 0) {
            return districtId + "";
        } else {
            return name;
        }
    }
}
