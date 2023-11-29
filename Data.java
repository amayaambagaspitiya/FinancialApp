package com.example.moneyminder;

public class Data {

    // Properties of the Data class
    private int amount;
    private String type;
    private String description;
    private String id;
    private String date;

    // Constructor with parameters
    public Data(int amount, String type, String description, String id, String date) {
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.id = id;
        this.date = date;
    }
    // Default constructor (empty)
    public Data() {

    };
    // Getter and Setter methods for each property
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
