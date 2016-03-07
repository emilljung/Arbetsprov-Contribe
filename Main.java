import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Scanner;

/*
      Written by: Emil Ljung
         Project: Arbetsprov
              OS: Windows 7 Ultimate 64-bit, Service Pack 1
        Platform: NetBeans IDE 8.1
    Java version: Java 8 Update 73
*/

public class Main
{
    public static void main(String[] args)
    {      
        StoreHandler store = new StoreHandler();
                
        if(store.setup())
        {
            int choice = -1;
            
            //* So å, ä & ö is handled correctly on Windows 7.
            //* Also, I assume the user uses Windows, so errors might
            //happen on other Operating Systems. Since I don't have Mac
            //or Linux computers I, sadly, cannot see if this works on them.
            Scanner s = new Scanner(System.in, "CP1252"); 

            //Program loop
            while (choice != 0)
            {
                System.out.println("1. Show all books");
                System.out.println("2. Search for book/author");

                System.out.println("\nCart related");
                System.out.println("3. Add");
                System.out.println("4. Remove");
                System.out.println("5. Buy");

                System.out.println("\nAdmin");
                System.out.println("6. Add new book");

                System.out.println("\n0. Exit.");
                
                System.out.print("\nInput: ");
                String c = s.nextLine();
                
                try
                {
                    choice = Integer.parseInt(c);
                    
                    switch(choice)
                    {
                        case 1:
                            showAllBooks(store);
                            break;
                        case 2:
                            searchForBook(store, s);
                            break;
                        case 3:
                            addToCart(store, s);
                            break;                
                        case 4:
                            removeFromCart(store, s);
                            break;                
                        case 5:
                            buy(store);
                            break;                
                        case 6:
                            addToStore(store, s);
                            break;
                        case 0:
                            //Exit program, main loop breaks
                            break;
                        default:
                            System.out.println("\nPlease select any number between 0 and 6.");
                    }
                }
                catch(NumberFormatException e)
                {
                    System.out.println("\nThe input must be a number betweem 0 and 6!");
                }


                System.out.println();
            }
        }
        else
        {
            System.out.println("Something went wrong in the setup!");
        }
    }
    
    //The functions called in the menu
    public static void showAllBooks(StoreHandler store)
    {
        //* Prints all books in stock.
        //* I assumed that's what was meant when the user should
        //be able to get a list of all the books in the store.
        
        Book[] allBooks = store.getAllBooks();
        Integer[] amt = store.getAmountArray();
        
        System.out.println();
        
        for (int i = 0; i < allBooks.length; i++) 
        { 
            allBooks[i].printInfo();
            System.out.println("Amount: " + amt[i]);
            System.out.println();
        }
    }
    
    public static void searchForBook(StoreHandler store, Scanner s)
    {
        //* Search for and print book/-s based on title and/or author.
        //* Notice that you HAVE to search for exact title and/or author!
        //* I assumed that's what was meant when the user should
        //be able search for book/-s.
        
        System.out.print("\nSearch for: ");
                        
        String searchString = s.nextLine();
        Book[] books = store.list(searchString);
        Integer[] amt = store.getAmountArray();
        
        System.out.println();
        
        if(books.length > 0)
            for (int i = 0; i < books.length; i++) 
            { 
                books[i].printInfo();
                System.out.println("Amount: " + amt[i]);
                System.out.println();
            }
        else
            System.out.println("No book title or author was found.");
    }
    
    public static void addToCart(StoreHandler store, Scanner s)
    {
        //* Adds or increases the amount of a book to the cart.
        //* Notice that you can add a book that doesn't exist in the store or
        //is out of stock. This is controlled in buy() since I assumed that's
        //the reason why OK(0), NOT_IN_STOCK(1) & DOES_NOT_EXIST(2) is included.
        //* I assumed you should be able to increase the amount of a book if
        //it already exists in the cart.
        
        //Prepare for String -> BigDecimal convertion
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        String pattern = "#,##0.0#";
        DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
        decimalFormat.setParseBigDecimal(true);
        
        System.out.println("\nWhich book would you like to add to the cart?");
        
        //Get info about the book to add to the cart
        System.out.print("Title: ");
        String title = s.nextLine();              
       
        System.out.print("Author: ");
        String author = s.nextLine();
        
        System.out.print("Price: ");
        String price = s.nextLine();
        
        System.out.print("Amount: ");
        String amount = s.nextLine();
            
        int amt;
        BigDecimal bd;

        try
        {
            //If necessary, turn negative numbers positve before add(...)!
            bd = (BigDecimal)decimalFormat.parse(price);
            if(bd.compareTo(BigDecimal.ZERO) < 0)
            {
                bd = bd.negate();
                System.out.println("Price turned into positive: " + bd);
            }
            
            try
            {
                amt = Integer.parseInt(amount);
                
                if(amt < 0)
                {
                    amt = Math.abs(amt);
                    System.out.println("Amount turned into positive: " + amt);
                }
                
                store.add(new Book(title, author, bd), amt);
                System.out.println("\nAdded to the cart.");
            }
            catch(NumberFormatException e)
            {
                System.out.println("\nWrong input on Amount!");
            }
        }
        catch(ParseException pe)
        {
            System.out.println("\nWrong input on Price!");
        }   
    }
    
    public static void removeFromCart(StoreHandler store, Scanner s)
    {
        //* Removes or reduces the amount of a book in you cart.
        //* To make it easier for the user to remove books I made sure
        //the content of the cart is used even if though it wasn't necessary.
        //* I assumed you should be able to decrease the amount of a book if
        //it already exists in the cart.
        
        Book[] cartContent = store.getCartContent();
        Integer[] nrOfBooks = store.getNrOfBooksInCart();

        if(cartContent.length > 0)
        {
            //Show content of the cart
            System.out.println("\nContent of your cart:\n");
            for(int i = 0; i < cartContent.length; i++)
            {
                cartContent[i].printInfo();
                System.out.println("Amount: " + nrOfBooks[i] + "\n");
            }

            //Prepare for String -> BigDecimal conve1rtion
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator(',');
            symbols.setDecimalSeparator('.');
            String pattern = "#,##0.0#";
            DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
            decimalFormat.setParseBigDecimal(true);

            //Get info about the book to remove
            System.out.println("Which book would you like to remove?");
            System.out.print("Title: ");
            String title = s.nextLine();              

            System.out.print("Author: ");
            String author = s.nextLine();

            System.out.print("Price: ");
            String price = s.nextLine();

            System.out.print("Amount: ");
            String amount = s.nextLine();
            
            int amt;
            BigDecimal bd;

            try
            {
                //If necessary, turn negative numbers positve before removeFromCart(...)!
                bd = (BigDecimal)decimalFormat.parse(price);
                if(bd.compareTo(BigDecimal.ZERO) < 0)
                {
                    bd = bd.negate();
                    System.out.println("Price turned into positive: " + bd);
                }
                
                try
                {
                    amt = Integer.parseInt(amount);
                    if(amt < 0)
                    {
                        amt = Math.abs(amt);
                        System.out.println("Amount turned into positive: " + amt);
                    }
                    store.removeFromCart(new Book(title, author, bd), amt);
                }
                catch(NumberFormatException e)
                {
                    System.out.println("\nWrong input on Amount!");
                }
            }
            catch(ParseException pe)
            {
                System.out.println("\nWrong input on Price!");
            }
        }
        else
        {
            System.out.println("\nThe cart is empty.");
        }
    }
    
    public static void buy(StoreHandler store)
    {
        //* Sets status on all the books, prints the total price of all OK(0) books
        //and empties the entire cart.
        //* Even if the OK(0) etc aren't in the result array I assumed I was
        //supposed to have enum where OK = 0 etc and then return the value of
        //OK etc to the result array.
        //* I assume emptying the cart here is ok.
        
        int[] result;
        Book[] books = store.getCartContent();
        Integer[] amt = store.getNrOfBooksInCart();
        
        if(books.length > 0)
        {
            //Calculate total price, set status on books (which is returned as an
            //array, result) & remove all books from cart
            result = store.buy(books);
            
            //Print content of the cart and the status of each book.
            //result.length = books.length
            for(int i = 0; i < result.length; i++)
            {
                books[i].printInfo();
                System.out.println("Amount: " + amt[i]);
                
                System.out.print("Status: ");
                
                switch (result[i]) 
                {
                    case 0:
                        System.out.println("OK(" + result[i] + ")\n");
                        break;
                    case 1:
                        System.out.println("NOT_IN_STOCK(" + result[i] + ")\n");
                        break;
                    default:
                        System.out.println("DOES_NOT_EXIST(" + result[i] + ")\n");
                        break;
                }
            }
            
            System.out.println("The cart is now empty.");
        }
        else
           System.out.println("\nYour cart is empty!");
    }
    
    public static void addToStore(StoreHandler store, Scanner s)
    {
        //* Adds or increases the amount of a book to the store.
        //* I assumed you should be able to increase the amount of a book if
        //it already exists in the store.
        
        //Prepare for String -> BigDecimal convertion
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        String pattern = "#,##0.0#";
        DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
        decimalFormat.setParseBigDecimal(true);
        
        System.out.println("\nWhich book would you like to add to the store?");
        
        //Get info about the book to add to the store
        System.out.print("Title: ");
        String title = s.nextLine();              
       
        System.out.print("Author: ");
        String author = s.nextLine();
                        
        System.out.print("Price: ");
        String price = s.nextLine();
                        
        System.out.print("Amount: ");
        String amount = s.nextLine();
            
        int amt;
        BigDecimal bd;

        try
        {
            //If necessary, turn negative numbers positve before addToStore(...)!
            bd = (BigDecimal)decimalFormat.parse(price);
            if(bd.compareTo(BigDecimal.ZERO) < 0)
            {
                bd = bd.negate();
                System.out.println("Price turned into positive: " + bd);
            }
         
            try
            {
                amt = Integer.parseInt(amount);
                if(amt < 0)
                {
                    amt = Math.abs(amt);
                    System.out.println("Amount turned into positive: " + amt);
                }
                
                store.addToStore(new Book(title, author, bd), amt);
            }
            catch(NumberFormatException e)
            {
                System.out.println("\nWrong input on Amount!");
            }
        }
        catch(ParseException pe)
        {
            System.out.println("\nWrong input on Price!");
        }
    }
}