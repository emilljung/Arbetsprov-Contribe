import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;

/*
      Written by: Emil Ljung
         Project: Arbetsprov
              OS: Windows 7 Ultimate 64-bit, Service Pack 1
        Platform: NetBeans IDE 8.1
    Java version: Java 8 Update 73
*/

public class StoreHandler implements BookList 
{
    private ArrayList<Book> inStock;
    private ArrayList<Integer> amtBooksInStore; //Amount of the book inStock.get(i) is amtBooks.get(i)
    private Cart cart;
    private Integer[] searchAmt;
    
    public StoreHandler()
    {
        this.inStock = new ArrayList<>();
        this.amtBooksInStore = new ArrayList<>(); //Remeber to sort this correctly if this.inStock is sorted!
        this.cart = new Cart();
    }
    
    public enum Status
    {
        //Used in buy(Book... books)
        
        OK(0), NOT_IN_STOCK(1), DOES_NOT_EXIST(2);
        private int value;
        
        private Status(int value)
        {
            this.value = value;
        }
    }
    
    @Override
    public boolean setup()
    {
        //Put all books in the URL in the store.
        
        boolean success = true;
        
        try
        {
            //Read info from this URL
            URL url = new URL("http://www.contribe.se/bookstoredata/bookstoredata.txt");
            Scanner s = new Scanner(url.openStream());

            String[] lineContent;

            //Prepare for String -> BigDecimal convertion
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator(',');
            symbols.setDecimalSeparator('.');
            String pattern = "#,##0.0#";
            DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
            decimalFormat.setParseBigDecimal(true);
            
            while(s.hasNextLine())
            {
                //Split line content and put them in an array
                lineContent = s.nextLine().split(";");
                
                try
                {
                    //Add the books to the store
                    this.amtBooksInStore.add(Integer.parseInt(lineContent[3]));
                    this.inStock.add(new Book(lineContent[0], lineContent[1], (BigDecimal)decimalFormat.parse(lineContent[2])));
                }
                catch(ParseException pe)
                { success = false; }
            }
        }
        catch(IOException e)
        { success = false; }
        
        return success;
    }

    @Override
    public void addToStore(Book book, int amount)
    {
        //Adds a book to the store or increases the amount of a book
        
        boolean addNewBook = true;
        int i = 0;
        
        while(i < this.inStock.size() && addNewBook != false)
        {
            if(this.inStock.get(i).isEqual(book))
            {
                //Only increase amount that specific book if the book already exists in the store
                this.amtBooksInStore.set(i, amount + this.amtBooksInStore.get(i));
                System.out.println("\nThe amount of that book is increased.");
                addNewBook = false;
            }
            i++;
        }
        
        if(addNewBook == true)
        {
            //Add a new book to the store
            this.amtBooksInStore.add(amount);
            this.inStock.add(book);
            System.out.println("\nThe book has been added to the store!");
        }
    }
    
    @Override
    public Book[] getAllBooks()
    {
        //Returns an array of all books in the store (even the ones not in stock)
        
        //Prepare for getAmountArray() in main loop
        this.searchAmt = new Integer[this.amtBooksInStore.size()];
        this.searchAmt = this.amtBooksInStore.toArray(this.searchAmt);
        
        return this.inStock.toArray(new Book[this.inStock.size()]);
    }
    
    //@Override
    public boolean removeFromCart(Book book, int amount)
    {
        //Removes x amount of a specific book from the cart.
        //If amount variable equals the amount of that book it is removed from the cart.
        
        boolean success = false;
        
        int i = 0; 
        while(i < this.cart.getBookArray().length && success == false)
        {
            if(this.cart.getBookArray()[i].isEqual(book))
            {
                if(this.cart.getNrOfBooksArray()[i] > amount)
                {
                    //Reduce the amount of that book in the cart with x 
                    System.out.print("\nAmount is reduced from " + this.cart.getNrOfBooksArray()[i]);
                    this.cart.setNrOfBook(i, this.cart.getNrOfBooksArray()[i] - amount);
                    System.out.print(" to " + this.cart.getNrOfBooksArray()[i] + "\n");
                    success = true;
                }
                else if(this.cart.getNrOfBooksArray()[i] == amount)
                {
                    //Remove the book from the cart
                    this.cart.remove(i);
                    System.out.println("\nRemoved from the cart.");
                    success = true;
                }
                else
                {
                    //The amount of books the user tries to remove is too high!
                    System.out.println("\nThe amount of that book in your cart is " + this.cart.getNrOfBooksArray()[i] + "!");
                    success = true;
                }
            }
            i++;
        }
        
        if(success == false)
        {
            System.out.println("\nThat book doesn't exist in the cart!");
        }
        
        return success;
    }
    
    @Override
    public Book[] list(String searchString) 
    {
        //Returns an array of all books with the title and/or author the user searched for.
        
        ArrayList<Book> list = new ArrayList<>();
        ArrayList<Integer> amtList = new ArrayList<>();
        
        for(int i = 0; i < this.inStock.size(); i++)
            if(this.inStock.get(i).isEqual(searchString))
            {
                //Only add books that matches searchString
                list.add(this.inStock.get(i));
                amtList.add(this.amtBooksInStore.get(i));
            }
        
        //Prepare for getAmountArray() in main loop
        this.searchAmt = new Integer[amtList.size()];
        this.searchAmt = amtList.toArray(this.searchAmt);
        
        //An array of the matched books is returned
        return list.toArray(new Book[list.size()]);
    }

    @Override
    public boolean add(Book book, int amount) 
    {
        //Adds x numbers of a book to the cart.
        
        this.cart.add(book, amount);
        return true;
    }

    @Override
    public int[] buy(Book... books)
    {
        //Sets and returns status of all books, 
        //prints the sum of all OK(0) books and empties the cart.
        
        int[] returnValue = new int[books.length];
        BigDecimal total_price = new BigDecimal("0.00");
        Integer[] amtBooks = this.cart.getNrOfBooksArray();
        
        //Loop through the cart
        for(int i = 0; i < books.length; i++)
        {
            int k = 0;
            boolean breakLoop = false;
            
            //Loop through the book store
            while(k < this.inStock.size() && breakLoop == false)
            {
                if(books[i].isEqual(this.inStock.get(k)))
                {
                    if(amtBooks[i] == 0)
                    {
                        //It would be strange to buy 0 books, right?
                        returnValue[i] = Status.NOT_IN_STOCK.value;
                        breakLoop = true;
                    }
                    else if(this.amtBooksInStore.get(k) >= amtBooks[i])
                    {
                        //The amount of books in store is reduced & total_price is increased
                        returnValue[i] = Status.OK.value;
                        this.amtBooksInStore.set(k, this.amtBooksInStore.get(k) - amtBooks[i]);
                        
                        BigDecimal amount = new BigDecimal(amtBooks[i]);
                        total_price = (total_price.add(amount.multiply(books[i].getPrice())));
                        
                        breakLoop = true;
                    }
                    else
                    {
                        //The book is not in stock
                        returnValue[i] = Status.NOT_IN_STOCK.value;
                        breakLoop = true;
                    }
                }
                k++;
            }
            
            if (breakLoop == false)
            {
                //The book doesn't exist in the store
                returnValue[i] = Status.DOES_NOT_EXIST.value;
            }
            //When the loop is done the cart is will be empty
            this.cart.remove(0);
        }
        
        System.out.println("\nTotal price for all OK: " + total_price);
        
        //Return an array of statuses of the books which were in the cart.
        return returnValue;
    }
    
    public Book[] getCartContent()
    {
        return this.cart.getBookArray();
    }
    
    public Integer[] getNrOfBooksInCart()
    {
        return this.cart.getNrOfBooksArray();
    }
    
    public Integer[] getAmountArray()
    {
        return this.searchAmt;
    }
}