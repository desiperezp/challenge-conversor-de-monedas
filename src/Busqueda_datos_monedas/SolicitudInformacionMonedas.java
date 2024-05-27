package Busqueda_datos_monedas;

import Modelos.Monedas;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class SolicitudInformacionMonedas {
    private static final String URL_API = "https://v6.exchangerate-api.com/v6/2fa985a11f108602b0f988da/latest/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();

        boolean salir = false;
        while (!salir) {
            try {
                mostrarMenu();
                int opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1:
                        convertirYMostrar(scanner, gson, "USD", "ARS");
                        break;
                    case 2:
                        convertirYMostrar(scanner, gson, "ARS", "USD");
                        break;
                    case 3:
                        convertirYMostrar(scanner, gson, "USD", "BRL");
                        break;
                    case 4:
                        convertirYMostrar(scanner, gson, "BRL", "USD");
                        break;
                    case 5:
                        convertirYMostrar(scanner, gson, "USD", "COP");
                        break;
                    case 6:
                        convertirYMostrar(scanner, gson, "COP", "USD");
                        break;
                    case 7:
                        salir = true;
                        System.out.println("Saliendo..."+"Gracias por utilizar el conversor de monedas");
                        break;
                    default:
                        System.out.println("Elija una opción válida.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, ingrese una opción del 1 al 7.");
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Ocurrió un error: " + e.getMessage());
            }
        }
    }

    private static void mostrarMenu() {
        System.out.println("*********************************************");
        System.out.println("Sea bienvenido/a al Conversor de Moneda =]");
        System.out.println("*********************************************");
        System.out.println("1) Dólar =>> Peso argentino");
        System.out.println("2) Peso argentino =>> Dólar");
        System.out.println("3) Dólar =>> Real brasileño");
        System.out.println("4) Real brasileño =>> Dólar");
        System.out.println("5) Dólar =>> Peso colombiano");
        System.out.println("6) Peso colombiano =>> Dólar");
        System.out.println("7) Salir");
        System.out.println("Elija una opción válida:");
        System.out.println("*********************************************");
    }

    private static void convertirYMostrar(Scanner scanner, Gson gson, String monedaOrigen, String monedaDestino) {
        try {
            System.out.println("Ingrese la cantidad en " + monedaOrigen + ": ");
            double cantidad = scanner.nextDouble();
            scanner.nextLine();

            double cantidadConvertida = convertirMoneda(cantidad, monedaOrigen, monedaDestino, gson);
            System.out.println("El valor de " + cantidad + " " + monedaOrigen + " corresponde a " + cantidadConvertida + " " + monedaDestino);
        } catch (InputMismatchException e) {
            System.out.println("Entrada no válida. Por favor, ingrese un número válido.");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("Error al realizar la conversión: " + e.getMessage());
        }
    }

    private static double convertirMoneda(double cantidad, String monedaOrigen, String monedaDestino, Gson gson) throws IOException, InterruptedException {
        String url = URL_API + monedaOrigen;
        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> respuesta = cliente.send(solicitud, HttpResponse.BodyHandlers.ofString());

        // Verificar si la solicitud fue exitosa (código 200 de ok)
        if (respuesta.statusCode() != 200) {
            throw new RuntimeException("Fallo al obtener las tasas de conversión de moneda");
        }

        Monedas monedas = gson.fromJson(respuesta.body(), Monedas.class);
        Map<String, Double> tasas = monedas.getConversionRates();

        if (tasas == null) {
            throw new RuntimeException("No se pudieron obtener las tasas de conversión de la respuesta de la API");
        }

        Double tasaCambio = tasas.get(monedaDestino);

        if (tasaCambio == null) {
            throw new RuntimeException("No se encontró la tasa de conversión para " + monedaDestino);
        }

        return cantidad * tasaCambio;
    }
}