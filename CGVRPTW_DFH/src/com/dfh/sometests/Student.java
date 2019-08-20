package com.dfh.sometests;


public class Student implements Comparable {
    private String studentname;
    public int studentage;

    public Student(String studentname, int studentage) {
        this.studentname = studentname;
        this.studentage = studentage;
    }

    @Override
    public int compareTo(Object comparestu) {
        int compareage=((Student)comparestu).studentage;
        /* For Ascending order*/
        return this.studentage>compareage?1:-1;

    }

    @Override
    public String toString() {
        return "[ name=" + studentname + ", age=" + studentage + "]";
    }

}

