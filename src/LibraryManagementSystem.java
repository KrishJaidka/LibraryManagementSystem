import java.io.*;
import java.util.*;

class Book implements Comparable<Book> {
    int bookId;
    String title;
    String author;
    String category;
    boolean isIssued;
    static int baseBookId = 100;

    Book(String title, String author, String category) {
        this.bookId = ++baseBookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.isIssued = false;
    }
    Book(int id, String title, String author, String category, boolean isIssued) {
        this.bookId = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.isIssued = isIssued;

        if (id > baseBookId) {
            baseBookId = id;
        }
    }
    void displayBookDetails() {
        System.out.println("ID: " + bookId);
        System.out.println("Title: " + title);
        System.out.println("Author: " + author);
        System.out.println("Category: " + category);
        System.out.println("Available: " + (!isIssued));
    }
    public int compareTo(Book other) {
        return this.title.compareToIgnoreCase(other.title);
    }
}

class AuthorComparator implements Comparator<Book> {
    public int compare(Book b1, Book b2) {
        return b1.author.compareToIgnoreCase(b2.author);
    }
}

class Member {
    int memberId;
    String name;
    String email;
    ArrayList<Integer> issuedBooks = new ArrayList<>();
    static int baseMemberId = 200;

    Member(String name, String email) {
        this.memberId = ++baseMemberId;
        this.name = name;
        this.email = email;
    }
    Member(int id, String name, String email) {
        this.memberId = id;
        this.name = name;
        this.email = email;

        if (id > baseMemberId) {
            baseMemberId = id;
        }
    }
    void displayMemberDetails() {
        System.out.println("ID: " + memberId);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Issued Books IDs: " + issuedBooks.toString());
    }
    void addIssuedBook(int bookId) {
        issuedBooks.add(bookId);
    }
    void returnIssuedBook(int bookId) {
        issuedBooks.remove(Integer.valueOf(bookId));
    }
}

class LibraryManager {
    HashMap<Integer, Book> books = new HashMap<>();
    HashMap<Integer, Member> members = new HashMap<>();
    Scanner sc = new Scanner(System.in);

    String BOOK_FILE = "books.txt";
    String MEMBER_FILE = "members.txt";

    void saveToFile() {
        try (BufferedWriter bwBook = new BufferedWriter(new FileWriter(BOOK_FILE));
             BufferedWriter bwMember = new BufferedWriter(new FileWriter(MEMBER_FILE))) {

            for (Book b : books.values()) {
                bwBook.write(String.valueOf(b.bookId));
                bwBook.newLine();
                bwBook.write(b.title);
                bwBook.newLine();
                bwBook.write(b.author);
                bwBook.newLine();
                bwBook.write(b.category);
                bwBook.newLine();
                bwBook.write(String.valueOf(b.isIssued));
                bwBook.newLine();
            }

            for (Member m : members.values()) {
                bwMember.write(String.valueOf(m.memberId));
                bwMember.newLine();
                bwMember.write(m.name);
                bwMember.newLine();
                bwMember.write(m.email);
                bwMember.newLine();

                String issued = "";
                for (int id : m.issuedBooks) {
                    issued += id + ",";
                }
                if (!issued.isEmpty()) {
                    issued = issued.substring(0, issued.length() - 1);
                }
                bwMember.write(issued);
                bwMember.newLine();
            }
            System.out.println("Data saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    void loadFromFile() {
        File bFile = new File(BOOK_FILE);
        if (bFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(bFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    int id = Integer.parseInt(line);
                    String title = br.readLine();
                    String author = br.readLine();
                    String category = br.readLine();
                    boolean isIssued = Boolean.parseBoolean(br.readLine());

                    Book b = new Book(id, title, author, category, isIssued);
                    books.put(b.bookId, b);
                }
            } catch (Exception e) {
                System.out.println("Error loading books.");
            }
        }

        File mFile = new File(MEMBER_FILE);
        if (mFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(mFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    int id = Integer.parseInt(line);
                    String name = br.readLine();
                    String email = br.readLine();
                    String booksLine = br.readLine();

                    Member m = new Member(id, name, email);
                    if (booksLine != null && !booksLine.isEmpty()) {
                        String[] bookIds = booksLine.split(",");
                        for (String bid : bookIds) {
                            if (!bid.trim().isEmpty()) {
                                m.addIssuedBook(Integer.parseInt(bid.trim()));
                            }
                        }
                    }
                    members.put(m.memberId, m);
                }
            } catch (Exception e) {
                System.out.println("Error loading members.");
            }
        }
    }

    void addBook() {
        System.out.print("Enter Title: ");
        String title = sc.nextLine();
        System.out.print("Enter Author: ");
        String author = sc.nextLine();
        System.out.print("Enter Category: ");
        String cat = sc.nextLine();

        Book newBook = new Book(title, author, cat);
        books.put(newBook.bookId, newBook);
        System.out.println("Book added: " + newBook.bookId);
        saveToFile();
    }

    void addMember() {
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Email: ");
        String email = sc.nextLine();

        Member newMember = new Member(name, email);
        members.put(newMember.memberId, newMember);
        System.out.println("Member added: " + newMember.memberId);
        saveToFile();
    }

    void issueBook() {
        System.out.print("Enter Book ID: ");
        int bid = sc.nextInt();
        System.out.print("Enter Member ID: ");
        int mid = sc.nextInt();
        sc.nextLine();

        if (books.containsKey(bid) && members.containsKey(mid)) {
            Book b = books.get(bid);
            Member m = members.get(mid);

            if (!b.isIssued) {
                b.isIssued = true;
                m.addIssuedBook(bid);
                System.out.println("Book issued successfully.");
                saveToFile();
            } else {
                System.out.println("Book is already issued.");
            }
        } else {
            System.out.println("Invalid IDs.");
        }
    }

    void returnBook() {
        System.out.print("Enter Book ID to return: ");
        int bid = sc.nextInt();
        System.out.print("Enter Member ID returning: ");
        int mid = sc.nextInt();
        sc.nextLine();

        if (books.containsKey(bid) && members.containsKey(mid)) {
            Book b = books.get(bid);
            Member m = members.get(mid);

            if (b.isIssued && m.issuedBooks.contains(bid)) {
                b.isIssued = false;
                m.returnIssuedBook(bid);
                System.out.println("Book returned successfully.");
                saveToFile();
            } else {
                System.out.println("This member does not have this book.");
            }
        } else {
            System.out.println("Invalid IDs.");
        }
    }

    void searchBooks() {
        System.out.print("Enter search term (Title/Author/Category): ");
        String term = sc.nextLine().toLowerCase();
        boolean found = false;

        for (Book b : books.values()) {
            if (b.title.toLowerCase().contains(term) ||
                    b.author.toLowerCase().contains(term) ||
                    b.category.toLowerCase().contains(term)) {
                b.displayBookDetails();
                found = true;
            }
        }
        if (!found) {
            System.out.println("No books found.");
        }
    }

    void sortBooks() {
        System.out.println("1. Sort by Title\n2. Sort by Author");
        int option = sc.nextInt();
        sc.nextLine();

        List<Book> bookList = new ArrayList<>(books.values());
        if (option == 1) {
            Collections.sort(bookList);
        } else {
            Collections.sort(bookList, new AuthorComparator());
        }
        for (Book b : bookList) {
            b.displayBookDetails();
        }
    }

    void menu() {
        loadFromFile();
        while (true) {
            System.out.println("====== MENU ======");
            System.out.println("1) Add Book");
            System.out.println("2) Add Member");
            System.out.println("3) Issue Book");
            System.out.println("4) Return Book");
            System.out.println("5) Search Books");
            System.out.println("6) Sort Books");
            System.out.println("7) Exit");
            System.out.print("Choose your option: ");

            try {
                int option = sc.nextInt();
                sc.nextLine();

                switch (option) {
                    case 1:
                        addBook();
                        break;
                    case 2:
                        addMember();
                        break;
                    case 3:
                        issueBook();
                        break;
                    case 4:
                        returnBook();
                        break;
                    case 5:
                        searchBooks();
                        break;
                    case 6:
                        sortBooks();
                        break;
                    case 7:
                        saveToFile();
                        System.out.println("Exiting program ...");
                        return;
                    default:
                        System.out.println("Invalid option!");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                sc.nextLine();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    public static void main(String[] args) {
        LibraryManager ui =  new LibraryManager();
        ui.menu();
    }
}