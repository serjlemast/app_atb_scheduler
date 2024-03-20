package com.dimitrii.maksymov.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class Items {
    @JsonProperty("id")
    private String itemId;
    @JsonIgnore
    private String id;
    private String url;
    private boolean is_presence;
    private String name;
    private String picture;
    private String brand;
    private String currency;
    private double price;

}
