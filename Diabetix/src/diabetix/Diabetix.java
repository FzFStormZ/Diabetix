package diabetix;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

/**
 *
 * @author Dylan
 */
public class Diabetix {
    
    private static int sugar_g = 0;
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        
        while (true) {

            Scanner in = new Scanner(System.in);
            System.out.println("[+] Choose the option you want (1 or 2): ");
            System.out.println("1. search by barcode");
            System.out.println("2. search by name (the entire title of the product is recommended");
            System.out.println(">");
            String type_searching = in.nextLine().trim();

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = null;       
            boolean correct = true;        
            while (correct) {
                switch (type_searching) {
                    case "1":
                        System.out.println("[1] Enter your barcode:");
                        System.out.println(">");
                        String barcode = in.nextLine().trim();

                        request = HttpRequest.newBuilder()
                                .uri(URI.create("https://fr.openfoodfacts.org/api/v0/product/" + barcode + ".json"))
                                .GET()
                                .build();
                        correct = false;
                        break;
                    case "2":
                        System.out.println("[2] Enter your categorie of food (pastas, cereales, soda ...):");
                        System.out.println(">");
                        String categorie = in.nextLine().trim();

                        System.out.println("[2] Enter the name of the specific product:");
                        System.out.println(">");
                        String food = in.nextLine().trim();

                        request = HttpRequest.newBuilder()
                                .uri(URI.create("https://fr.openfoodfacts.org/cgi/search.pl?action=process&tagtype_0=categories&tag_contains_0=contains&tag_0=" + categorie + "&search_terms2=" + food))
                                .GET()
                                .build();
                        correct = false;
                        break;
                    default:
                        System.out.println("[!] Error !!"); 
                        break;
                }
            }


            HttpResponse<String> response = client.send(request, 
                    HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body());
            
            String term = "\"carbohydrates_100g\"";
            int index = response.body().indexOf(term);
            index += term.length() + 1;
            String sugar = response.body().substring(index).split(",")[0];

            System.out.println(sugar);
            sugar_g += Integer.parseInt(sugar);
            
            System.out.println("[!] Do you want to add an another product ? (y/n)");
            System.out.println(">");
            String choice = in.nextLine().trim();
            
            if (choice.equals("y")) {
            } else {
                break;
            }
        }
        
        System.out.println("[-] The total of carbohydrates/100g of each of the products is " + sugar_g + "g");
    }   
}
