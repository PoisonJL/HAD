package com.team7.hadcontrolpanel;

public class Item {
    //declare varailbes
    private String itemID;
    private String todoItem;

    /**
     * Default Constructor
     */
    public Item(){
        this.itemID = " ";
        this.todoItem = " ";
    }

    /**
     * Constructor
     */

    public Item(String itemID, String todoItem) {
        this.itemID = itemID;
        this.todoItem = todoItem;
    }

    /**
     * Copy Constructor
     */
    public Item(Item orig) {
        this.itemID = orig.itemID;
        this.todoItem = orig.todoItem;
    }

    /**
     * Copy Constructor
     */
    public String getItemID() {
        return itemID;
    }

    public String getTodoItem() {
        return todoItem;
    }
}