package view;

import controller.BlockWorldController;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("Starting program");
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Please enter the file path to be read: ");
           // String filePath = scanner.nextLine();
            String filePath = "resources/testing2.txt";
            System.out.println("Filepath is: " + filePath);
            BlockWorldController controller = new BlockWorldController();
            controller.loadBlockWorld(filePath);
            controller.start();
            scanner.close();
        } catch(Exception ex) {
            //TODO: catch any errors specially loading blocksworld and at least show some message
            ex.printStackTrace();
        }
    }

}
