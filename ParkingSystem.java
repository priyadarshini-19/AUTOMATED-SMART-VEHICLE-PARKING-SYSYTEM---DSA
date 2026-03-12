import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

class ParkingSystem {

    static final int TOTAL_SLOTS = 10;

    static String[] vehicleInSlot = new String[TOTAL_SLOTS];
    static LocalDate[] date = new LocalDate[TOTAL_SLOTS];
    static LocalTime[] entryTime = new LocalTime[TOTAL_SLOTS];
    static LocalTime[] exitTime = new LocalTime[TOTAL_SLOTS];
    static long[] entryMillis = new long[TOTAL_SLOTS];

    static Scanner sc = new Scanner(System.in);

    static DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

    // ================= HASH FUNCTION =================
    static HashMap<String,Integer> vehicleMap = new HashMap<>();


    // ================= SINGLY LINKED LIST =================
    static class Node {
        String vehicle;
        Node next;

        Node(String v){
            vehicle = v;
            next = null;
        }
    }

    static Node head = null;

    static void addVehicleList(String v){
        Node newNode = new Node(v);

        if(head == null){
            head = newNode;
        }
        else{
            Node temp = head;
            while(temp.next != null){
                temp = temp.next;
            }
            temp.next = newNode;
        }
    }

    static void displayList(){
        Node temp = head;

        System.out.println("Vehicles in Linked List:");

        while(temp != null){
            System.out.println(temp.vehicle);
            temp = temp.next;
        }
    }


    // ================= CIRCULAR QUEUE =================

    static String[] queue = new String[10];
    static int front = -1;
    static int rear = -1;

    static void enqueue(String vehicle){

        if((rear + 1) % queue.length == front){
            System.out.println("Waiting Queue Full");
            return;
        }

        if(front == -1)
            front = 0;

        rear = (rear + 1) % queue.length;
        queue[rear] = vehicle;

        System.out.println("Vehicle added to waiting queue");
    }

    static String dequeue(){

        if(front == -1)
            return null;

        String v = queue[front];

        if(front == rear){
            front = rear = -1;
        }
        else{
            front = (front + 1) % queue.length;
        }

        return v;
    }



    // ================= MAIN =================
public static void main(String[] args) {

    int choice;

    do {

        System.out.println("\n===== SMART PARKING MENU =====");
        System.out.println("1. Park Vehicle");
        System.out.println("2. Free Slot");
        System.out.println("3. View Parking Status");
        System.out.println("4. Exit");

        System.out.print("Choose option: ");

        choice = sc.nextInt();
        sc.nextLine();

        switch(choice){

            case 1:
                parkVehicle();
                break;

            case 2:
                freeSlot();
                break;

            case 3:
                showStatus();
                break;

            case 4:
                System.out.println("Thank you for using Smart Parking System.");
                break;

            default:
                System.out.println("Invalid Choice");

        }

    } while(choice != 4);
}
    // ================= SHOW AVAILABLE SLOTS =================

    static void showAvailableSlots() {

        System.out.print("Available Slots: ");
        boolean found = false;

        for (int i = 0; i < TOTAL_SLOTS; i++) {
            if (vehicleInSlot[i] == null) {
                System.out.print((i + 1) + " ");
                found = true;
            }
        }

        if (!found)
            System.out.print("None");

        System.out.println();
    }



    // ================= PARK VEHICLE =================

    static void parkVehicle() {

        System.out.print("Enter Vehicle Number: ");
        String vehicle = sc.nextLine();

        showAvailableSlots();

        int availableSlot = -1;

        for(int i=0;i<TOTAL_SLOTS;i++){
            if(vehicleInSlot[i] == null){
                availableSlot = i;
                break;
            }
        }

        if(availableSlot == -1){

            System.out.println("Parking Full");

            enqueue(vehicle); // circular queue
            return;
        }

        vehicleInSlot[availableSlot] = vehicle;
        date[availableSlot] = LocalDate.now();
        entryTime[availableSlot] = LocalTime.now();
        entryMillis[availableSlot] = System.currentTimeMillis();

        vehicleMap.put(vehicle,availableSlot); // hash function

        addVehicleList(vehicle); // singly linked list

        System.out.println("Vehicle Parked in Slot: " + (availableSlot + 1));
        System.out.println("Date: " + date[availableSlot]);
        System.out.println("Entry Time: " + entryTime[availableSlot].format(timeFormat));

        showAvailableSlots();
    }



    // ================= FREE SLOT =================

    static void freeSlot() {

        System.out.print("Enter Vehicle Number to Free: ");
        String vehicle = sc.nextLine();

        Integer slotIndex = vehicleMap.get(vehicle);

        if(slotIndex == null){
            System.out.println("Vehicle Not Found!");
            return;
        }

        exitTime[slotIndex] = LocalTime.now();
        long exitMillis = System.currentTimeMillis();

        long durationMinutes = (exitMillis - entryMillis[slotIndex]) / (1000 * 60);

        double ratePerMinute = 50.0 / 60.0;
        int price = (int) Math.max(1, Math.round(durationMinutes * ratePerMinute));

        System.out.println("Slot " + (slotIndex + 1) + " Freed.");
        System.out.println("Date: " + date[slotIndex]);
        System.out.println("Entry Time: " + entryTime[slotIndex].format(timeFormat));
        System.out.println("Exit Time: " + exitTime[slotIndex].format(timeFormat));
        System.out.println("Parking Charge: \u20B9" + price);

        vehicleInSlot[slotIndex] = null;
        vehicleMap.remove(vehicle);

        date[slotIndex] = null;
        entryTime[slotIndex] = null;
        exitTime[slotIndex] = null;
        entryMillis[slotIndex] = 0;

        String next = dequeue();

        if(next != null){
            System.out.println("Next vehicle from waiting queue parked: " + next);
            parkVehicleFromQueue(next);
        }

        showAvailableSlots();
    }



    static void parkVehicleFromQueue(String vehicle){

        for(int i=0;i<TOTAL_SLOTS;i++){

            if(vehicleInSlot[i] == null){

                vehicleInSlot[i] = vehicle;
                vehicleMap.put(vehicle,i);

                System.out.println("Vehicle "+vehicle+" parked in slot "+(i+1));
                return;
            }

        }

    }



    // ================= VIEW STATUS =================

    static void showStatus() {

        System.out.println("\n===== PARKING STATUS =====");

        for (int i = 0; i < TOTAL_SLOTS; i++) {

            if (vehicleInSlot[i] == null) {

                System.out.println("Slot " + (i + 1) + " : Available");

            } else {

                System.out.println("Slot " + (i + 1) + " : Occupied");
                System.out.println("Vehicle: " + vehicleInSlot[i]);
                System.out.println("Date: " + date[i]);
                System.out.println("Entry Time: " + entryTime[i].format(timeFormat));

            }
        }
    }



    // ================= BUBBLE SORT =================

    static void bubbleSortVehicles(){

        String[] arr = Arrays.copyOf(vehicleInSlot,TOTAL_SLOTS);

        for(int i=0;i<arr.length-1;i++){

            for(int j=0;j<arr.length-i-1;j++){

                if(arr[j]!=null && arr[j+1]!=null){

                    if(arr[j].compareTo(arr[j+1]) > 0){

                        String temp = arr[j];
                        arr[j] = arr[j+1];
                        arr[j+1] = temp;

                    }

                }

            }

        }

        System.out.println("Sorted Vehicles:");

        for(String v : arr){

            if(v != null)
                System.out.println(v);

        }

    }

}