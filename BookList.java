/*
      Written by: Emil Ljung
         Project: Arbetsprov
              OS: Windows 7 Ultimate 64-bit, Service Pack 1
        Platform: NetBeans IDE 8.1
    Java version: Java 8 Update 73
*/

public interface BookList 
{
    //I assumed I was allowed to add my functions here.
    //If not, then I would put my functions in StoreHandler.
    
    public boolean setup();
    public void addToStore(Book book, int amount);
    public Book[] getAllBooks();
    public boolean removeFromCart(Book book, int amount);
    
    public Book[] list(String searchString);
    public boolean add(Book book, int amount);
    public int[] buy(Book... books);
}