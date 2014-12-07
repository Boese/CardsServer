package com.cards;

import java.util.Scanner;

import com.cards.server.Server;

public class App 
{
    public static void main( String[] args )
    {
        try {
    	new Server();
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        scanner.close();
        System.out.println("**Server Terminated**");
        System.exit(0);
        } catch(Exception e) {}
    }
}
