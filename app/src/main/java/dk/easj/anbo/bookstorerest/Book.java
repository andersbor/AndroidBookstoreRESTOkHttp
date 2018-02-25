package dk.easj.anbo.bookstorerest;

import java.io.Serializable;

public class Book implements Serializable {
    private int id;
    private String title, publisher, author;
    private double price;

    public Book() {
    }

    public Book(int id, String author, String title, String publisher, double price) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.publisher = publisher;
        this.price = price;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    @Override
    public String toString() {
        return title;
    }
}
