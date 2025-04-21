package Book;
import java.util.*;
import java.time.LocalDate;

    // Added new methods
    public void addReview(String review) {
        // Validate review content
        if (review == null || review.trim().isEmpty()) {
            throw new IllegalArgumentException("Review cannot be empty");
        }

        // Limit review length
        if (review.length() > 500) {
            throw new IllegalArgumentException("Review cannot exceed 500 characters");
        }

        // Add timestamp to review
        String timestampedReview = String.format("[%s] %s", 
            LocalDate.now().toString(), 
            review.trim());

        // Add review and update metadata
        reviews.add(timestampedReview);
        lastModified = LocalDate.now();

        // Optional: Limit total number of reviews
        if (reviews.size() > 100) {
            reviews.remove(0); // Remove oldest review if limit exceeded
        }
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
