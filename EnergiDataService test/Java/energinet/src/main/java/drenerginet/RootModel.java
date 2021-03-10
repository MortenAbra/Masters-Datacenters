package drenerginet;

import java.io.File;
import java.sql.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import drenerginet.Models.Root;


public class RootModel {
    
    

    public RootModel(){
        try {
            ObjectMapper om = new ObjectMapper();
            Root root = om.readValue(new File("energinet\\src\\main\\java\\drenerginet\\Results\\spotpricing.json"), Root.class);
            System.out.println(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}




