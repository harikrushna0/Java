package Book;
import java.util.Scanner;
public class Book_details
{
    String name,author;
    String genre;
    float price;
    int year;

    public void display()
    {   
        System.out.println(name+" "+author+"  "+price+"  "+year);
        System.out.println("Book name: "+name);
        System.out.println("Author name: "+author);
        System.out.println("Genre: "+genre);
        System.out.println("Price: "+price);
        System.out.println("Year of publication: "+year);

        System.out.println("Book details displayed successfully.");
    }  

    public void Modified()
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the name of the book: ");
        name = sc.nextLine();
        System.out.println("Enter the author of the book: ");
        author = sc.nextLine();
        System.out.println("Enter the genre of the book: ");
        genre = sc.nextLine();
        System.out.println("Enter the price of the book: ");
        price = sc.nextFloat();
        System.out.println("Enter the year of publication: ");
        year = sc.nextInt();
        System.out.println("Book details modified successfully.");
    }
}
