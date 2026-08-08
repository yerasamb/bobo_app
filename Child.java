package com.bobo.app.model;

public class Child {
    private String name;
    private String image;
    private String group;

    public Child(String name, String image, String group) {
        this.name = name;
        this.image = image;
        this.group = group;
    }

    public Child() {
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getGroup() {
        return group;
    }
}
