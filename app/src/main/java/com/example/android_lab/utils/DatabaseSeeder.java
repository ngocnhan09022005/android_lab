package com.example.android_lab.utils;

import com.example.android_lab.data.model.Food;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseSeeder {
    private DatabaseReference databaseRef;

    public DatabaseSeeder() {
        databaseRef = FirebaseDatabase.getInstance().getReference();
    }

    public void seedFoods() {
        // Food 1
        Food food1 = new Food();
        food1.setName("Hamburger Special");
        food1.setPrice(8.99);
        food1.setImageUrl("https://firebasestorage.googleapis.com/v0/b/your-app/o/foods%2Fhamburger.jpg");
        food1.setPopular(true);
        databaseRef.child("foods").child("food1").setValue(food1);

        // Food 2
        Food food2 = new Food();
        food2.setName("Pizza Margherita");
        food2.setPrice(12.99);
        food2.setImageUrl("https://firebasestorage.googleapis.com/v0/b/your-app/o/foods%2Fpizza.jpg");
        food2.setPopular(true);
        databaseRef.child("foods").child("food2").setValue(food2);

        Food food3 = new Food();
        food3.setName("Sushi Roll Set");
        food3.setPrice(15.99);
        food3.setImageUrl("https://firebasestorage.googleapis.com/v0/b/your-app/o/foods%2Fsushi.jpg");
        food3.setPopular(true);
        databaseRef.child("foods").child("food3").setValue(food3);

        // Food 4
        Food food4 = new Food();
        food4.setName("Pasta Carbonara");
        food4.setPrice(10.99);
        food4.setImageUrl("https://firebasestorage.googleapis.com/v0/b/your-app/o/foods%2Fpasta.jpg");
        food4.setPopular(true);
        databaseRef.child("foods").child("food4").setValue(food4);

        // Food 5
        Food food5 = new Food();
        food5.setName("Caesar Salad");
        food5.setPrice(7.99);
        food5.setImageUrl("https://firebasestorage.googleapis.com/v0/b/your-app/o/foods%2Fsalad.jpg");
        food5.setPopular(false);
        databaseRef.child("foods").child("food5").setValue(food5);
    }
}
