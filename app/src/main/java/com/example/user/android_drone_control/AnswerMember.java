package com.example.user.android_drone_control;

public class AnswerMember { // VO- Value Object
    private int id;
    private int image;
    private String name;
    private String answer;

    public AnswerMember() {
        super();
    }

    public AnswerMember(int id, int image, String name,String answer) {
        super();
        this.id = id;
        this.image = image;
        this.name = name;
        this.answer=answer;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
