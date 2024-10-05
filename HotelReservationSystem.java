package com.JDBC.mysql;

import java.sql.*;
import java.util.Scanner;
public class HotelReservationSystem {

    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String userName = "root";
    private static final String password = "rohit";

    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            Connection connection = DriverManager.getConnection(url, userName, password);
            while (true) {
                System.out.println();
                System.out.println("Hotel Management System ");
                Scanner scanner = new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");
                int in = scanner.nextInt();

                switch (in) {
                    case 1:
                        reserveRoom(connection, scanner);
                        break;
                    case 2:
                        viewReservation(connection, scanner);
                        break;
                    case 3:
                        getRoomNumber(connection, scanner);
                        break;
                    case 4:
                        updateReservation(connection, scanner);
                        break;
                    case 5:
                        deleteReservarion(connection, scanner);
                        break;
                    case 0:
                        exit();
                        scanner.close();
                        return;
                    default:
                        System.out.println("invalid input ");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private static void deleteReservarion(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter reservation ID to delete: ");
            int reservationId = scanner.nextInt();

            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation deleted successfully!");
                } else {
                    System.out.println("Reservation deletion failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateReservation(Connection connection, Scanner scanner) {

        System.out.println("Enter Reservation ID");
        int reservationId = scanner.nextInt();
        scanner.nextLine();
        if (!reservationExists(connection, reservationId)) {
            System.out.println("Reservation Id does not exit ");
            return;
        }
        System.out.print("Enter guest name: ");
        String newGuestName = scanner.next();
        scanner.nextLine();
        System.out.print("Enter room number: ");
        int newRoomNumber = scanner.nextInt();
        System.out.print("Enter contact number: ");
        String newContactNumber = scanner.next();

        String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', " +
                "room_number = " + newRoomNumber + ", " +
                "contact_number = '" + newContactNumber + "' " +
                "WHERE reservation_id = " + reservationId;

        try {
            Statement statement = connection.createStatement();
            int affectedRows = statement.executeUpdate(sql);
            if (affectedRows > 0) {
                System.out.println("Reservation Updated .");
            } else {
                System.out.println("Reservation update failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void getRoomNumber(Connection connection, Scanner scanner) {
        System.out.println("Enter Reservation ID :");
        int reservationId = scanner.nextInt();
        System.out.println("Enter Guest Name :");
        String guestName = scanner.next();

        String sql = "SELECT room_number FROM reservations " +
                "WHERE reservation_id = " + reservationId +
                " AND guest_name = '" + guestName + "'";


        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {
                int roomNumber = resultSet.getInt("room_number");
                System.out.println("Room number for Reservation ID " + reservationId +
                        " and Guest " + guestName + " is: " + roomNumber);
            } else {
                System.out.println("Reservation not found for the given ID and guest name.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void reserveRoom(Connection connection, Scanner scanner) {

        System.out.print("Enter guest name: ");
        String guestName = scanner.next();
        scanner.nextLine();
        System.out.print("Enter room number: ");
        int roomNumber = scanner.nextInt();
        System.out.print("Enter contact number: ");
        String contactNumber = scanner.next();

        String sql = "INSERT INTO reservations (guest_name, room_number, contact_number) " +
                "VALUES ('" + guestName + "', " + roomNumber + ", '" + contactNumber + "')";

        try {
            Statement stm = connection.createStatement();
            int affectedRows = stm.executeUpdate(sql);

            if (affectedRows > 0) {
                System.out.println("Reservation successful!");
            } else {
                System.out.println("Reservation failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewReservation(Connection connection, Scanner scanner) {

        String sql = "select * from reservations";
        try {

            Statement stm = connection.createStatement();
            ResultSet rs = stm.executeQuery(sql);

            System.out.println("Current Reservations:");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

            while (rs.next()) {
                int reservationId = rs.getInt(1);
                String guestName = rs.getString(2);
                int roomNumber = rs.getInt(3);
                String contactNumber = rs.getString(4);
                String reservationDate = rs.getString(5);

                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        reservationId, guestName, roomNumber, contactNumber, reservationDate);
            }

            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean reservationExists(Connection connection, int reservationId) {
        try {
            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                return resultSet.next(); // If there's a result, the reservation exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Handle database errors as needed
        }
    }

    public static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        int i = 5;
        while (i != 0) {
            System.out.print(".");
            Thread.sleep(1000);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!!");
    }
}