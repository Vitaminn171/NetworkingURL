package course.examples.networking.url;

import java.io.Serializable;

/*
Tên: Lý Quốc An
MSSV: 3119410002
 */



public class Currency implements Serializable {
    private String name;
    private String currency;

    public Currency(String iName, String iCur) {
        name = iName;
        currency = iCur;
    }

    public Currency() {
    }

    public void setName(String iName){
        name = iName;
    }

    public void setCurrency(String iCur){
        currency = iCur;
    }

    public String getName(){
        return name;
    }

    public String getCurrency(){
        return currency;
    }
}
