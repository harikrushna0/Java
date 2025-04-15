package Book;
import java.util.*;
import java.time.LocalDate;

public class Book_details {
    // Added new fields
    private String isbn;
    private String name;
    private String author;
    private String genre;
    private float price;
    private int year;
    private List<String> reviews;
    private boolean isAvailable;
    private int copiesAvailable;
    private LocalDate lastModified;
    private Set<String> categories;
    
    // Added constructor
    public Book_details(String isbn, String name, String author, String genre, float price, int year) {
        this.isbn = isbn;
        this.name = name;
        this.author = author;
        this.genre = genre;
        this.price = price;
        this.year = year;
        this.reviews = new ArrayList<>();
        this.isAvailable = true;
        this.copiesAvailable = 1;
        this.lastModified = LocalDate.now();
        this.categories = new HashSet<>();
    }

    // Modified display method
    public void display() {   
        System.out.println("=== Book Details ===");
        System.out.println("ISBN: " + isbn);
        System.out.println("Title: " + name);
        System.out.println("Author: " + author);
        System.out.println("Genre: " + genre);
        System.out.println("Price: $" + String.format("%.2f", price));
        System.out.println("Year: " + year);
        System.out.println("Available: " + (isAvailable ? "Yes" : "No"));
        System.out.println("Copies: " + copiesAvailable);
        System.out.println("Categories: " + String.join(", ", categories));
        if (!reviews.isEmpty()) {
            System.out.println("Reviews:");
            reviews.forEach(review -> System.out.println("- " + review));
        }
    }

    // Added new methods
    public void addReview(String review) {
        reviews.add(review);
        lastModified = LocalDate.now();
    }

    public void addCategory(String category) {
        categories.add(category);
    }

    public void updateAvailability(int copies) {
        this.copiesAvailable = copies;
        this.isAvailable = copies > 0;
        this.lastModified = LocalDate.now();
    }

    // Modified input method
    public void Modified() {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("=== Update Book Details ===");
            System.out.print("Enter ISBN: ");
            this.isbn = sc.nextLine();
            
            System.out.print("Enter book title: ");
            this.name = sc.nextLine();
            
            System.out.print("Enter author name: ");
            this.author = sc.nextLine();
            
            System.out.print("Enter genre: ");
            this.genre = sc.nextLine();
            
            System.out.print("Enter price: ");
            while (!sc.hasNextFloat()) {
                System.out.println("Invalid price. Please enter a number.");
                sc.next();
            }
            this.price = sc.nextFloat();
            
            System.out.print("Enter publication year: ");
            while (!sc.hasNextInt()) {
                System.out.println("Invalid year. Please enter a number.");
                sc.next();
            }
            this.year = sc.nextInt();
            
            System.out.print("Enter number of copies: ");
            while (!sc.hasNextInt()) {
                System.out.println("Invalid number. Please enter a number.");
                sc.next();
            }
            this.copiesAvailable = sc.nextInt();
            this.isAvailable = this.copiesAvailable > 0;
            
            this.lastModified = LocalDate.now();
        }
    }

    // Added validation methods
    public boolean isValid() {
        return isbn != null && !isbn.isEmpty() &&
               name != null && !name.isEmpty() &&
               author != null && !author.isEmpty() &&
               price > 0 &&
               year > 0 &&
               year <= LocalDate.now().getYear();
    }

    public boolean isClassic() {
        return year < 1950;
    }

    // Added getters and setters
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public float getPrice() { return price; }
    public void setPrice(float price) { this.price = price; }
    public boolean isAvailable() { return isAvailable; }
    public int getCopiesAvailable() { return copiesAvailable; }
    public LocalDate getLastModified() { return lastModified; }
    public List<String> getReviews() { return new ArrayList<>(reviews); }
    public Set<String> getCategories() { return new HashSet<>(categories); }
}
