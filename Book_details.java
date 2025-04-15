package Book;
import java.util.Scanner;
public class Book_details
{
    String name,author;
    String genre; // New field for book genre

    float price;
    int year;

    // Add new method
    public void setAvailability(boolean available) {
        this.isAvailable = available;
    }

    // Added overloaded constructor
    public Book_details(String name, String author, float price, int year, String genre) {
        this.name = name;
        this.author = author;
        this.price = price;
        this.year = validateYear(year);
        this.genre = genre;
}

    public void display()
    {   
        System.out.println(name+" "+author+"  "+price+"  "+year);
    }  
}
