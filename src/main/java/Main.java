import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    static int mayor = 0;
    static String gana="";

    public static void main(String[] args)  {

        ArrayList<Hilos> l = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            Hilos hilo = new Hilos(false, 0);
            hilo.setName("Jugador " + i);
            hilo.start();
            l.add(hilo);

        }

        for (Hilos h : l) {
            try {
                h.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (Hilos h : l) {
            if (mayor < h.puntuacion){
                mayor = h.puntuacion;
               gana = "El " + h.getName() + " ha ganado con " + mayor + " puntos\n";
            }
        }
        System.out.println();
        System.out.println();
        System.out.println(gana);
    }
}

class Hilos extends Thread{

    boolean bonus;
    int puntuacion;
    public Hilos(boolean bonus, int puntuacion){
        this.bonus = bonus;
        this.puntuacion = puntuacion;
    }
    @Override
    public void run() {
        Batalla.AddParticipante(this);
    }
}

class Batalla {

    static AtomicInteger entero = new AtomicInteger(0);
    private static final Random r = new Random();
    static AtomicBoolean primero = new AtomicBoolean(false);
    private static final int numero = 10;
    private static final int mueren = 5;
    static Semaphore s = new Semaphore(numero);

    public static void AddParticipante(Hilos hilo) {
        try {
            s.acquire();
            Thread.sleep(r.nextInt(5000) + 1000);
            entero.getAndIncrement();
            if (!primero.compareAndExchange(false, true)) {
                hilo.bonus = true;
            }

            if (hilo.bonus) {
                System.out.println("El " + hilo.getName() +  " tiene un bonus \n");
            }

            if (entero.get() <= mueren ) {
                System.out.println("El " + hilo.getName() + " sigue vivo\n");
                PuntacionFinal(hilo);
            }

            if (entero.get() == mueren) {
                System.out.println("Mueren 5 Jugadores \n");
            }

            if (entero.get() > mueren && entero.get() <= numero) {
                System.out.println(hilo.getName() + " Ha muerto \n");
            }

            if (entero.get() == 10) {
                System.out.println("Entran 5 al combate \n");
                s.release(5);
            }

            if (entero.get() > 10) {
                System.out.println("El " + hilo.getName() + " entra al combate \n");
                PuntacionFinal(hilo);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public  static void PuntacionFinal(Hilos hilo) throws InterruptedException {
        int min = 1;
        int max = 10;
        Random random = new Random();
        hilo.puntuacion = random.nextInt(max) + min;
        if (hilo.bonus){
            hilo.puntuacion = hilo.puntuacion * 2;
        }
        System.out.println("La puntacion de " + hilo.getName() + " es " + hilo.puntuacion + "\n");

    }
}

class Ganador {
    static int mayor = 0;
    public synchronized static void campeon (Hilos hilo) throws InterruptedException {
        if (mayor < hilo.puntuacion){
            mayor = hilo.puntuacion;
            System.out.println("El " + hilo.getName() + " va ganando con " + mayor+ " puntos\n");
        }
    }
}
