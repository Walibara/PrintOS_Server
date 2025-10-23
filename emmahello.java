import java.util.ArrayList;
class HelloWorld {
    public static void main(String[] args) {
        ArrayList<String> messages = new ArrayList<String>(); 
        messages.add("Hello");
        messages.add("World"); 
        String fullMessage = "";
        Integer c = 0;  
        for (String message : messages) {
            if ( c == 0 ) fullMessage += message;
            if ( c != 0 ) fullMessage += " " + message;
            c++;  
        }
        System.out.println(fullMessage); 
    }
}