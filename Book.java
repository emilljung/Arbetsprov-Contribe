import java.math.BigDecimal;

/*
      Written by: Emil Ljung
         Project: Arbetsprov
              OS: Windows 7 Ultimate 64-bit, Service Pack 1
        Platform: NetBeans IDE 8.1
    Java version: Java 8 Update 73
*/

public class Book
{
    //I assumed I was allowed to add my functions here.
    
    private String title;
    private String author;
    private BigDecimal price;

    public Book(String title, String author, BigDecimal price)
    {
        this.title = title;
        this.author = author;
        this.price = price;
    }
    
    public void printInfo()
    {
        System.out.println("Title: " + this.title);
        System.out.println("Author: " + this.author);
        System.out.println("Price: " + this.price);
    }
    
    public BigDecimal getPrice()
    {
        return this.price;
    }
    
    public boolean isEqual(String searchString)
    {
        //Is used with searchForBook(...) from Main.java

        boolean equal = false;
        
        if(this.title.equals(searchString) || this.author.equals(searchString))
        {
            equal = true;
        }
        
        return equal;
    }
    
    public boolean isEqual(Book b)
    {
        //Check if "this book" is the same as book b.
        
        boolean equal = false;
        
        //x.compareTo(y) = 0 means x & y are equal, = 1 means x > y, = -1 means x < y
        if(this.title.equals(b.title) && this.author.equals(b.author) && this.price.compareTo(b.price) == 0)
        {
            equal = true;
        }
        
        return equal;
    }
}
