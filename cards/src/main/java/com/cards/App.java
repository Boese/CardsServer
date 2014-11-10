package com.cards;

import java.util.Scanner;

import com.cards.server.Server;

public class App 
{
    public static void main( String[] args )
    {
        new Server();
        System.out.println("**Server Started**");
        
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        System.out.println("**Server Terminated**");
        scanner.close();
        System.exit(0);
    }
}
