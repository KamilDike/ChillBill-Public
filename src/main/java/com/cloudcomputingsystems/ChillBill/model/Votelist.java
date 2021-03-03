package com.cloudcomputingsystems.ChillBill.model;

import org.springframework.data.jpa.repository.query.Procedure;

import javax.persistence.*;

@Entity
@Table(name = "votelists")
public class Votelist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer votes_id;
    private int blue;
    private int green;
    private int orange;
    private int purple;
    private int yellow;

    public Votelist() { }

    public Votelist(int blue, int green, int orange, int purple, int yellow) {
        this.blue = blue;
        this.green = green;
        this.orange = orange;
        this.purple = purple;
        this.yellow = yellow;
    }

    public Integer getVotes_id() {
        return votes_id;
    }

    public void setVotes_id(Integer votes_id) {
        this.votes_id = votes_id;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getOrange() {
        return orange;
    }

    public void setOrange(int orange) {
        this.orange = orange;
    }

    public int getPurple() {
        return purple;
    }

    public void setPurple(int purple) {
        this.purple = purple;
    }

    public int getYellow() {
        return yellow;
    }

    public void setYellow(int yellow) {
        this.yellow = yellow;
    }

    public void addVote(String category) {
        category = category.toLowerCase();
        switch (category) {
            case "blue":
                setBlue(getBlue() + 1);
                break;
            case "green":
                setGreen(getGreen() + 1);
                break;
            case "orange":
                setOrange(getOrange() + 1);
                break;
            case "purple":
                setPurple(getPurple() + 1);
                break;
            case "yellow":
                setYellow(getYellow() + 1);
                break;
        }
    }

    public void removeVote(String category) {
        category = category.toLowerCase();
        int current;
        switch (category) {
            case "blue":
                current = getBlue();
                if (current > 0) {
                    setBlue(current - 1);
                }
                break;
            case "green":
                current = getGreen();
                if (current > 0) {
                    setGreen(current - 1);
                }
                break;
            case "orange":
                current = getOrange();
                if (current > 0) {
                    setOrange(current - 1);
                }
                break;
            case "purple":
                current = getPurple();
                if (current > 0) {
                    setPurple(current - 1);
                }
                break;
            case "yellow":
                current = getYellow();
                if (current > 0) {
                    setYellow(current - 1);
                }
                break;
        }
    }
}
