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
   public static void main(String[] args) {
    if (args.length != 2) {  // Modified condition
        System.err.println("Exactly two arguments required: numbers and target");
        System.err.println("Example: java CountDownProblem 1,3,7,10,25,50 765");
        return;
    }
}

    public void display()
    {   
        System.out.println(name+" "+author+"  "+price+"  "+year);
    }  
}
