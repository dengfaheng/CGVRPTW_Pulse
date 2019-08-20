package com.dfh.sometests;

import java.util.ArrayList;
import java.util.Collections;


public class TestSort {
	
	public static void main(String args[]){
	
        ArrayList<Student> arraylist = new ArrayList<Student>();
        arraylist.add(new Student( "Chaitanya", 26));
        arraylist.add(new Student( "Rahul", 24));
        arraylist.add(new Student( "Ajeet", 32));
        arraylist.add(new Student( "aa", 10));

        Collections.sort(arraylist);

        for(Student str: arraylist){
            System.out.println(str);
        }
    }
	

}







