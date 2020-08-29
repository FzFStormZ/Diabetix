package diabetix;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author Dylan
 */
public class Diabetix {

    private static int sugar_g = 0; // Sum of sugar
    private static String language = "fr"; // Language of the result of the request

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {

        while (true) {

            Scanner in = new Scanner(System.in);
            System.out.println("[+] Choose your language: ");
            System.out.println("1. en");
            System.out.println("2. fr");
            System.out.println(">");
            language = in.nextLine().trim();
            
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
                                .uri(URI.create("https://" + language + ".openfoodfacts.org/api/v0/product/" + barcode + ".json"))
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
                                .uri(URI.create("https:/" + language + ".openfoodfacts.org/cgi/search.pl?action=process&tagtype_0=categories&tag_contains_0=contains&tag_0=" + categorie + "&search_terms2=" + food + "&json=true"))
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

            String sugar;

            if (type_searching.equals("2")) {
                HashMap<Integer, String> result = generateListProducts(response.body());
                printListProducts(result);

                int indexBegin = in.nextInt();
                String term = "\"carbohydrates_100g\"";
                int index = response.body().indexOf(term, indexBegin);
                System.out.println(index);

                index += term.length() + 1;
                sugar = response.body().substring(index).split(",")[0];

            } else {
                String term = "\"carbohydrates_100g\"";
                int index = response.body().indexOf(term);
                index += term.length() + 1;
                sugar = response.body().substring(index).split(",")[0];
            }

            System.out.println(sugar);
            sugar_g += Integer.parseInt(sugar);

            String choice;

            do {
                System.out.println("[!] Do you want to add an another product ? (y/n)");
                System.out.println(">");
                choice = in.nextLine().trim();
            } while (!(choice.equals("y") || choice.equals("n")));

            if (choice.equals("y")) {
            } else if (choice.equals("n")) {
                break;
            }

        }

        System.out.println("[-] The total of carbohydrates/100g of each of the products is " + sugar_g + "g");
    }

    /**
     * Generate a list of the products you have choose
     * 
     * @param response Response of the request
     * @return HashMap of the list
     */
    public static HashMap<Integer, String> generateListProducts(String response) {
        System.out.println(response);

        HashMap<Integer, String> result = new HashMap<>();
        int index = 0;
        String name;
        String term = "\"product_name_" + language + "\":";

        while (true) {
            index = response.indexOf(term, index);

            if (index == -1) {
                break;

            } else {
                index += term.length() + 1;
                name = response.substring(index).split("\",")[0];
                
                if (!name.isEmpty() || !name.equals("")) {
                    result.put(index, name);
                }            
            }
        }

        return result;
    }

    /**
     * Print the list of the products you have choose
     * 
     * @param list HashMap of the list of the products
     */
    public static void printListProducts(HashMap<Integer, String> list) {
        list.forEach((i, n) -> {
            System.out.println(i + ". " + n);
        });
        System.out.println("[+] Choose the product you want (number): ");
    }
}
