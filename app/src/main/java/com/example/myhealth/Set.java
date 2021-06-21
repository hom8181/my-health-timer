package com.example.myhealth;

public class Set {
    
    private int set;
    private String exerciseTime;

    public void setSet(int set) {
        this.set = set;
    }

    public void setExerciseTime(String exerciseTime) {
        this.exerciseTime = exerciseTime;
    }

    public void setRestTime(String restTime) {
        this.restTime = restTime;
    }

    private String restTime;

    public int getSet() {
        return set;
    }

    public String getExerciseTime() {
        return exerciseTime;
    }

    public String getRestTime() {
        return restTime;
    }

    public Set(int set, String exerciseTime, String restTime) {
        this.set = set;
        this.exerciseTime = exerciseTime;
        this.restTime = restTime;
    }
}
