package course.examples.networking.url;

import java.io.Serializable;
import java.util.Locale;

public class Country implements Serializable {
    public String name;
    public String code;
    public String urlMapImage;
    public String urlFlagImage;
    public String population;
    public String areaInSqKm;
    public String capital;

    public Country(String iName, String iCode, String iPop, String iArea, String iCap) {
        name = iName;
        population = iPop;
        areaInSqKm = iArea;
        capital = iCap;
        code = iCode;
        urlFlagImage = String.format("https://img.geonames.org/flags/x/%s.gif", code.toLowerCase());
        urlMapImage = String.format("https://img.geonames.org/img/country/250/%s.png", code);
    }

    public Country() {
    }


    public String getName(){
        return name;
    }

    public String getPopulation(){
        return population;
    }

    public String getCode(){
        return code;
    }
    public String getArea(){
        return areaInSqKm;
    }

    public String getCapital(){
        return capital;
    }

    public String getURLFlag(){
        return urlFlagImage = String.format("https://img.geonames.org/flags/x/%s.gif", code.toLowerCase());
    }

    public String getURLMap(){
        return urlMapImage = String.format("https://img.geonames.org/img/country/250/%s.png", code);
    }

}
