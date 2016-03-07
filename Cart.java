import java.util.ArrayList;

/*
      Written by: Emil Ljung
         Project: Arbetsprov
              OS: Windows 7 Ultimate 64-bit, Service Pack 1
        Platform: NetBeans IDE 8.1
    Java version: Java 8 Update 73
*/

public class Cart
{
    private ArrayList<Book> books;
    private ArrayList<Integer> nrOfBooks;   //Sort this one correctly if books is being sorted!
    
    public Cart()
    {
        //Amount of the book books.get(i) is nrOfBooks.get(i)
        this.books = new ArrayList<>();
        this.nrOfBooks = new ArrayList<>();
    }
    
    public void add(Book book, int amount)
    {
        this.books.add(book);
        this.nrOfBooks.add(amount);
    }
    
    public void remove(int i)
    {        
        this.books.remove(i);
        this.nrOfBooks.remove(i);
    }
    
    public void setNrOfBook(int i, int amount)
    {
        this.nrOfBooks.set(i, amount);
    }
    
    public Book[] getBookArray()
    {
        return this.books.toArray(new Book[this.books.size()]);
    }
    
    public Integer[] getNrOfBooksArray()
    {
        return this.nrOfBooks.toArray(new Integer[this.nrOfBooks.size()]);
    }
}
