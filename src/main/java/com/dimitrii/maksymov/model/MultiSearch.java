package com.dimitrii.maksymov.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MultiSearch {
    private String id;
    private String query;
    private int total;
    private Results results;

    @Data
    public static class Results {
        @JsonProperty(namespace = "item_groups")
        private List<ItemsGroups> item_groups;
    }

    @Data
    public static class ItemsGroups {
        private Category category;
        private List<List<Items>> items;
    }

    @Data
    public static class Category {
        private String url;
        private String name;
    }
}
