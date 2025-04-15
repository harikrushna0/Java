package library;

import java.time.LocalDate;
import java.util.*;
import java.io.*;

/**
 * Library Management System
 * A comprehensive system to manage books, members, and lending operations
 */
public class LibraryManagementSystem {
    private List<Book> books;
    private List<Member> members;
    private Map<String, List<LendingRecord>> lendingHistory;
    private static final int MAX_BOOKS_PER_MEMBER = 5;
    private static final int LENDING_PERIOD_DAYS = 14;

    public LibraryManagementSystem() {
        this.books = new ArrayList<>();
        this.members = new ArrayList<>();
        this.lendingHistory = new HashMap<>();
    }

    // Book Class
    public static class Book implements Serializable {
        private String isbn;
        private String title;
        private String author;
        private String genre;
        private int publicationYear;
        private boolean isAvailable;
        private int totalCopies;
        private int availableCopies;
        private List<Review> reviews;

        public Book(String isbn, String title, String author, String genre, int publicationYear, int totalCopies) {
            this.isbn = isbn;
            this.title = title;
            this.author = author;
            this.genre = genre;
            this.publicationYear = publicationYear;
            this.totalCopies = totalCopies;
            this.availableCopies = totalCopies;
            this.isAvailable = true;
            this.reviews = new ArrayList<>();
        }

        // Getters and Setters
        public String getIsbn() { return isbn; }
        public void setIsbn(String isbn) { this.isbn = isbn; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        public String getGenre() { return genre; }
        public void setGenre(String genre) { this.genre = genre; }
        public int getPublicationYear() { return publicationYear; }
        public void setPublicationYear(int year) { this.publicationYear = year; }
        public boolean isAvailable() { return isAvailable; }
        public void setAvailable(boolean available) { this.isAvailable = available; }
        public int getTotalCopies() { return totalCopies; }
        public int getAvailableCopies() { return availableCopies; }

        public void addReview(Review review) {
            reviews.add(review);
        }

        public double getAverageRating() {
            if (reviews.isEmpty()) return 0.0;
            return reviews.stream()
                         .mapToDouble(Review::getRating)
                         .average()
                         .orElse(0.0);
        }

        @Override
        public String toString() {
            return String.format("Book{isbn='%s', title='%s', author='%s', genre='%s', " +
                               "publicationYear=%d, available=%b, copies=%d/%d, rating=%.2f}",
                               isbn, title, author, genre, publicationYear, isAvailable, 
                               availableCopies, totalCopies, getAverageRating());
        }
    }

    // Member Class
    public static class Member implements Serializable {
        private String memberId;
        private String name;
        private String email;
        private String phone;
        private LocalDate joinDate;
        private MembershipType membershipType;
        private List<Book> borrowedBooks;
        private List<Fine> fines;

        public Member(String memberId, String name, String email, String phone, MembershipType membershipType) {
            this.memberId = memberId;
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.joinDate = LocalDate.now();
            this.membershipType = membershipType;
            this.borrowedBooks = new ArrayList<>();
            this.fines = new ArrayList<>();
        }

     
        public boolean canBorrowBooks() {
            return borrowedBooks.size() < MAX_BOOKS_PER_MEMBER && getTotalUnpaidFines() == 0;
        }

        public double getTotalUnpaidFines() {
            return fines.stream()
                       .filter(fine -> !fine.isPaid())
                       .mapToDouble(Fine::getAmount)
                       .sum();
        }

        @Override
        public String toString() {
            return String.format("Member{id='%s', name='%s', email='%s', phone='%s', " +
                               "joinDate=%s, membershipType=%s, borrowedBooks=%d, unpaidFines=%.2f}",
                               memberId, name, email, phone, joinDate, membershipType, 
                               borrowedBooks.size(), getTotalUnpaidFines());
        }
    }

    // LendingRecord Class
    public static class LendingRecord implements Serializable {
        private String recordId;
        private Book book;
        private Member member;
        private LocalDate borrowDate;
        private LocalDate dueDate;
        private LocalDate returnDate;
        private Status status;

        public LendingRecord(String recordId, Book book, Member member) {
            this.recordId = recordId;
            this.book = book;
            this.member = member;
            this.borrowDate = LocalDate.now();
            this.dueDate = borrowDate.plusDays(LENDING_PERIOD_DAYS);
            this.status = Status.BORROWED;
        }

        // Getters and Setters
        public String getRecordId() { return recordId; }
        public Book getBook() { return book; }
        public Member getMember() { return member; }
        public LocalDate getBorrowDate() { return borrowDate; }
        public LocalDate getDueDate() { return dueDate; }
        public LocalDate getReturnDate() { return returnDate; }
        public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
        public Status getStatus() { return status; }
        public void setStatus(Status status) { this.status = status; }

        public boolean isOverdue() {
            return status == Status.BORROWED && LocalDate.now().isAfter(dueDate);
        }

        public long getOverdueDays() {
            if (!isOverdue()) return 0;
            return java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        }

        @Override
        public String toString() {
            return String.format("LendingRecord{id='%s', book='%s', member='%s', " +
                               "borrowDate=%s, dueDate=%s, returnDate=%s, status=%s}",
                               recordId, book.getTitle(), member.getName(), borrowDate, 
                               dueDate, returnDate, status);
        }
    }

    // Review Class
    public static class Review implements Serializable {
        private String reviewId;
        private Member member;
        private Book book;
        private int rating;
        private String comment;
        private LocalDate reviewDate;

        public Review(String reviewId, Member member, Book book, int rating, String comment) {
            this.reviewId = reviewId;
            this.member = member;
            this.book = book;
            this.rating = rating;
            this.comment = comment;
            this.reviewDate = LocalDate.now();
        }

        // Getters and Setters
        public String getReviewId() { return reviewId; }
        public Member getMember() { return member; }
        public Book getBook() { return book; }
        public int getRating() { return rating; }
        public void setRating(int rating) { this.rating = rating; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
        public LocalDate getReviewDate() { return reviewDate; }
    }

    // Fine Class
    public static class Fine implements Serializable {
        private String fineId;
        private Member member;
        private LendingRecord lendingRecord;
        private double amount;
        private String reason;
        private boolean paid;
        private LocalDate issueDate;
        private LocalDate paymentDate;

        public Fine(String fineId, Member member, LendingRecord record, double amount, String reason) {
            this.fineId = fineId;
            this.member = member;
            this.lendingRecord = record;
            this.amount = amount;
            this.reason = reason;
            this.paid = false;
            this.issueDate = LocalDate.now();
        }

        // Getters and Setters
        public String getFineId() { return fineId; }
        public Member getMember() { return member; }
        public LendingRecord getLendingRecord() { return lendingRecord; }
        public double getAmount() { return amount; }
        public String getReason() { return reason; }
        public boolean isPaid() { return paid; }
        public LocalDate getIssueDate() { return issueDate; }
        public LocalDate getPaymentDate() { return paymentDate; }

        public void payFine() {
            this.paid = true;
            this.paymentDate = LocalDate.now();
        }
    }

    // Enums
    public enum Status {
        BORROWED, RETURNED, LOST, DAMAGED
    }

    public enum MembershipType {
        STANDARD, PREMIUM, STUDENT, SENIOR
    }

    // Main Library Management Methods
    public void addBook(Book book) {
        books.add(book);
    }

    public void removeBook(String isbn) {
        books.removeIf(book -> book.getIsbn().equals(isbn));
    }

    public void addMember(Member member) {
        members.add(member);
    }

    public void removeMember(String memberId) {
        members.removeIf(member -> member.getMemberId().equals(memberId));
    }

    public LendingRecord lendBook(String isbn, String memberId) throws LibraryException {
        Book book = findBook(isbn);
        Member member = findMember(memberId);

        if (book == null || member == null) {
            throw new LibraryException("Book or member not found");
        }

        if (!book.isAvailable()) {
            throw new LibraryException("Book is not available");
        }

        if (!member.canBorrowBooks()) {
            throw new LibraryException("Member cannot borrow more books");
        }

        String recordId = UUID.randomUUID().toString();
        LendingRecord record = new LendingRecord(recordId, book, member);
        
        book.availableCopies--;
        if (book.availableCopies == 0) {
            book.setAvailable(false);
        }

        member.borrowedBooks.add(book);
        lendingHistory.computeIfAbsent(memberId, k -> new ArrayList<>()).add(record);

        return record;
    }

    public void returnBook(String recordId) throws LibraryException {
        LendingRecord record = findLendingRecord(recordId);
        if (record == null) {
            throw new LibraryException("Lending record not found");
        }

        if (record.getStatus() != Status.BORROWED) {
            throw new LibraryException("Book is not currently borrowed");
        }

        record.setStatus(Status.RETURNED);
        record.setReturnDate(LocalDate.now());

        Book book = record.getBook();
        Member member = record.getMember();

        book.availableCopies++;
        if (book.availableCopies > 0) {
            book.setAvailable(true);
        }

        member.borrowedBooks.remove(book);

        if (record.isOverdue()) {
            createFine(record);
        }
    }

    private void createFine(LendingRecord record) {
        double fineAmount = calculateFine(record);
        if (fineAmount > 0) {
            String fineId = UUID.randomUUID().toString();
            Fine fine = new Fine(fineId, record.getMember(), record, fineAmount, 
                               "Overdue book return");
            record.getMember().getFines().add(fine);
        }
    }

    private double calculateFine(LendingRecord record) {
        long overdueDays = record.getOverdueDays();
        return overdueDays * 0.50; // $0.50 per day
    }

    // Helper Methods
    private Book findBook(String isbn) {
        return books.stream()
                   .filter(book -> book.getIsbn().equals(isbn))
                   .findFirst()
                   .orElse(null);
    }

    private Member findMember(String memberId) {
        return members.stream()
                     .filter(member -> member.getMemberId().equals(memberId))
                     .findFirst()
                     .orElse(null);
    }

    private LendingRecord findLendingRecord(String recordId) {
        return lendingHistory.values().stream()
                           .flatMap(List::stream)
                           .filter(record -> record.getRecordId().equals(recordId))
                           .findFirst()
                           .orElse(null);
    }

    // Custom Exception
    public static class LibraryException extends Exception {
        public LibraryException(String message) {
            super(message);
        }
    }

    // New Analytics Classes
    public static class LibraryAnalytics {
        private final List<Book> books;
        private final List<Member> members;
        private final Map<String, List<LendingRecord>> lendingHistory;

        public LibraryAnalytics(List<Book> books, List<Member> members, 
                              Map<String, List<LendingRecord>> lendingHistory) {
            this.books = books;
            this.members = members;
            this.lendingHistory = lendingHistory;
        }

        public Map<String, Integer> getGenreDistribution() {
            Map<String, Integer> distribution = new HashMap<>();
            books.stream()
                 .map(Book::getGenre)
                 .forEach(genre -> distribution.merge(genre, 1, Integer::sum));
            return distribution;
        }

        public Map<MembershipType, Long> getMembershipDistribution() {
            return members.stream()
                         .collect(Collectors.groupingBy(
                             Member::getMembershipType, 
                             Collectors.counting()));
        }

        public double getAverageOverdueDays() {
            return lendingHistory.values().stream()
                               .flatMap(List::stream)
                               .filter(LendingRecord::isOverdue)
                               .mapToLong(LendingRecord::getOverdueDays)
                               .average()
                               .orElse(0.0);
        }

        public List<BookPopularityStats> getMostPopularBooks(int limit) {
            Map<Book, Long> borrowCount = lendingHistory.values().stream()
                .flatMap(List::stream)
                .map(LendingRecord::getBook)
                .collect(Collectors.groupingBy(
                    book -> book,
                    Collectors.counting()
                ));

            return books.stream()
                       .map(book -> new BookPopularityStats(
                           book, 
                           borrowCount.getOrDefault(book, 0L),
                           book.getAverageRating()))
                       .sorted()
                       .limit(limit)
                       .toList();
        }

        public List<Member> getTopBorrowers(int limit) {
            return members.stream()
                         .sorted((m1, m2) -> 
                             Integer.compare(
                                 getLendingCount(m2.getMemberId()), 
                                 getLendingCount(m1.getMemberId())))
                         .limit(limit)
                         .toList();
        }

        private int getLendingCount(String memberId) {
            return lendingHistory.getOrDefault(memberId, List.of()).size();
        }
    }

    public static class BookPopularityStats implements Comparable<BookPopularityStats> {
        private final Book book;
        private final long borrowCount;
        private final double averageRating;

        public BookPopularityStats(Book book, long borrowCount, double averageRating) {
            this.book = book;
            this.borrowCount = borrowCount;
            this.averageRating = averageRating;
        }

        @Override
        public int compareTo(BookPopularityStats other) {
            int compareByBorrows = Long.compare(other.borrowCount, this.borrowCount);
            if (compareByBorrows != 0) return compareByBorrows;
            return Double.compare(other.averageRating, this.averageRating);
        }

        @Override
        public String toString() {
            return String.format("%s (Borrowed: %d times, Rating: %.2f)",
                book.getTitle(), borrowCount, averageRating);
        }
    }

    // Add these methods to the main LibraryManagementSystem class
    public LibraryAnalytics getAnalytics() {
        return new LibraryAnalytics(books, members, lendingHistory);
    }

    public void generateReport() {
        LibraryAnalytics analytics = getAnalytics();
        System.out.println("=== Library System Report ===");
        
        System.out.println("\nGenre Distribution:");
        analytics.getGenreDistribution()
                .forEach((genre, count) -> 
                    System.out.printf("  %s: %d books%n", genre, count));

        System.out.println("\nMembership Distribution:");
        analytics.getMembershipDistribution()
                .forEach((type, count) -> 
                    System.out.printf("  %s: %d members%n", type, count));

        System.out.printf("\nAverage Overdue Days: %.2f%n", 
                         analytics.getAverageOverdueDays());

        System.out.println("\nTop 5 Most Popular Books:");
        analytics.getMostPopularBooks(5)
                .forEach(stats -> System.out.println("  " + stats));

        System.out.println("\nTop 5 Most Active Borrowers:");
        analytics.getTopBorrowers(5)
                .forEach(member -> System.out.println("  " + member.getName()));
    }
}