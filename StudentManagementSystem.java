package education;

import java.time.LocalDate;
import java.util.*;
import java.io.*;

/**
 * Student Management System
 * A system to manage students, courses, grades, and academic records
 */
public class StudentManagementSystem {
    private List<Student> students;
    private List<Course> courses;
    private Map<String, List<Enrollment>> enrollmentHistory;
    private static final int MAX_COURSES_PER_SEMESTER = 6;
    private static final double MIN_GPA_FOR_HONORS = 3.5;

    public StudentManagementSystem() {
        this.students = new ArrayList<>();
        this.courses = new ArrayList<>();
        this.enrollmentHistory = new HashMap<>();
    }

    // Student Class
    public static class Student implements Serializable {
        private String studentId;
        private String name;
        private String email;
        private LocalDate dateOfBirth;
        private String major;
        private int yearLevel;
        private List<Course> currentCourses;
        private List<Grade> grades;

        public Student(String studentId, String name, String email, 
                      LocalDate dateOfBirth, String major, int yearLevel) {
            this.studentId = studentId;
            this.name = name;
            this.email = email;
            this.dateOfBirth = dateOfBirth;
            this.major = major;
            this.yearLevel = yearLevel;
            this.currentCourses = new ArrayList<>();
            this.grades = new ArrayList<>();
        }

        public double calculateGPA() {
            if (grades.isEmpty()) return 0.0;
            double totalPoints = grades.stream()
                .mapToDouble(grade -> grade.getGradePoint() * grade.getCourse().getCredits())
                .sum();
            double totalCredits = grades.stream()
                .mapToDouble(grade -> grade.getCourse().getCredits())
                .sum();
            return totalPoints / totalCredits;
        }

        public boolean isEligibleForHonors() {
            return calculateGPA() >= MIN_GPA_FOR_HONORS;
        }

        // Getters and setters
        public String getStudentId() { return studentId; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public LocalDate getDateOfBirth() { return dateOfBirth; }
        public String getMajor() { return major; }
        public int getYearLevel() { return yearLevel; }
        public List<Course> getCurrentCourses() { return new ArrayList<>(currentCourses); }
        public List<Grade> getGrades() { return new ArrayList<>(grades); }

        @Override
        public String toString() {
            return String.format("Student{id='%s', name='%s', major='%s', GPA=%.2f}",
                               studentId, name, major, calculateGPA());
        }
    }

    // Course Class
    public static class Course implements Serializable {
        private String courseCode;
        private String title;
        private String description;
        private int credits;
        private String department;
        private List<String> prerequisites;
        private int maxCapacity;
        private int currentEnrollment;

        public Course(String courseCode, String title, String description, 
                     int credits, String department, int maxCapacity) {
            this.courseCode = courseCode;
            this.title = title;
            this.description = description;
            this.credits = credits;
            this.department = department;
            this.maxCapacity = maxCapacity;
            this.currentEnrollment = 0;
            this.prerequisites = new ArrayList<>();
        }

        public boolean hasAvailableSpace() {
            return currentEnrollment < maxCapacity;
        }

        // Getters and setters
        public String getCourseCode() { return courseCode; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public int getCredits() { return credits; }
        public String getDepartment() { return department; }
        public List<String> getPrerequisites() { return new ArrayList<>(prerequisites); }
        public int getMaxCapacity() { return maxCapacity; }
        public int getCurrentEnrollment() { return currentEnrollment; }

        @Override
        public String toString() {
            return String.format("Course{code='%s', title='%s', credits=%d, enrolled=%d/%d}",
                               courseCode, title, credits, currentEnrollment, maxCapacity);
        }
    }

    // Grade Class
    public static class Grade implements Serializable {
        private Student student;
        private Course course;
        private String letterGrade;
        private LocalDate gradingDate;

        public Grade(Student student, Course course, String letterGrade) {
            this.student = student;
            this.course = course;
            this.letterGrade = letterGrade;
            this.gradingDate = LocalDate.now();
        }

        public double getGradePoint() {
            return switch (letterGrade.toUpperCase()) {
                case "A" -> 4.0;
                case "A-" -> 3.7;
                case "B+" -> 3.3;
                case "B" -> 3.0;
                case "B-" -> 2.7;
                case "C+" -> 2.3;
                case "C" -> 2.0;
                case "C-" -> 1.7;
                case "D+" -> 1.3;
                case "D" -> 1.0;
                default -> 0.0;
            };
        }

        // Getters
        public Student getStudent() { return student; }
        public Course getCourse() { return course; }
        public String getLetterGrade() { return letterGrade; }
        public LocalDate getGradingDate() { return gradingDate; }
    }

    // Enrollment Class
    public static class Enrollment implements Serializable {
        private String enrollmentId;
        private Student student;
        private Course course;
        private LocalDate enrollmentDate;
        private String semester;
        private EnrollmentStatus status;

        public Enrollment(String enrollmentId, Student student, Course course, String semester) {
            this.enrollmentId = enrollmentId;
            this.student = student;
            this.course = course;
            this.enrollmentDate = LocalDate.now();
            this.semester = semester;
            this.status = EnrollmentStatus.ENROLLED;
        }

        // Getters
        public String getEnrollmentId() { return enrollmentId; }
        public Student getStudent() { return student; }
        public Course getCourse() { return course; }
        public LocalDate getEnrollmentDate() { return enrollmentDate; }
        public String getSemester() { return semester; }
        public EnrollmentStatus getStatus() { return status; }
    }

    // Enum for Enrollment Status
    public enum EnrollmentStatus {
        ENROLLED, DROPPED, COMPLETED, FAILED
    }

    // Main System Methods
    public void addStudent(Student student) {
        students.add(student);
    }

    public void addCourse(Course course) {
        courses.add(course);
    }

    public Enrollment enrollStudent(String studentId, String courseCode, String semester) 
            throws SystemException {
        Student student = findStudent(studentId);
        Course course = findCourse(courseCode);

        if (student == null || course == null) {
            throw new SystemException("Student or course not found");
        }

        if (!course.hasAvailableSpace()) {
            throw new SystemException("Course is full");
        }

        if (student.getCurrentCourses().size() >= MAX_COURSES_PER_SEMESTER) {
            throw new SystemException("Student has reached maximum course load");
        }

        String enrollmentId = UUID.randomUUID().toString();
        Enrollment enrollment = new Enrollment(enrollmentId, student, course, semester);
        
        student.currentCourses.add(course);
        course.currentEnrollment++;
        enrollmentHistory.computeIfAbsent(studentId, k -> new ArrayList<>())
                        .add(enrollment);

        return enrollment;
    }

    // Helper Methods
    private Student findStudent(String studentId) {
        return students.stream()
                      .filter(student -> student.getStudentId().equals(studentId))
                      .findFirst()
                      .orElse(null);
    }

    private Course findCourse(String courseCode) {
        return courses.stream()
                     .filter(course -> course.getCourseCode().equals(courseCode))
                     .findFirst()
                     .orElse(null);
    }

    // Custom Exception
    public static class SystemException extends Exception {
        public SystemException(String message) {
            super(message);
        }
    }
}