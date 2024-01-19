import com.sun.security.jgss.GSSUtil;

import java.io.InterruptedIOException;
import java.sql.*;
import java.util.Scanner;


public class hotelReservationSystem {
//Creating connection through the database

    // hotel_database is database create on the sql database
    private  static  final  String url = "jdbc:mysql://localhost:3306/hotel_database";
    private static final String username = "root";
    private static final String password = "password";//Please Enter your sql password

    public static void main(String[] args) throws ClassNotFoundException , SQLClientInfoException, SQLException {

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");

        }catch (ClassNotFoundException e ){
            System.out.println(e.getMessage());
        }
      try{
          Connection connection = DriverManager.getConnection(url,username,password);
          while (true){
              System.out.println();
              System.out.println("----Hotel Management System-----");
              Scanner sc= new Scanner(System.in);
              System.out.println("1. Reserve a room");
              System.out.println("2. View Reservation");
              System.out.println("3. Get a room");
              System.out.println("4. Update Reservation");
              System.out.println("5. Delete Reservation");
              System.out.println("0. Exit");
              System.out.println("Choose an option");

              int choice = sc.nextInt();
              switch (choice) {
                  case 1 -> reserveRoom(connection, sc);
                  case 2 -> viewReservations(connection);
                  case 3 -> getRoomNo(connection, sc);
                  case 4 -> updateReservation(connection, sc);
                  case 5 -> deleteReservation(connection, sc);
                  case 0 -> {
                      exit();
                      sc.close();
                      return;
                  }
                  default -> System.out.println("Invalid Choice : Try Again !!!!!!");
              }
          }

      }catch (SQLException e){
          System.out.println(e.getMessage());
      }catch (InterruptedIOException e){
          throw new RuntimeException(e);
      } catch (InterruptedException e) {
          throw new RuntimeException(e);
      }


    }
    private static void reserveRoom(Connection connection , Scanner sc){
     try {
         System.out.print("Enter guest name: ");
         String guestName =  sc.next();
         System.out.print("Enter room Number: ");
         int roomNumber = sc.nextInt();
         System.out.print("Enter Contact Number: ");
         String contactNumber = sc.next();

         String sql = "insert into reservations( guest_name , room_number , contact_number)"+
                 "values('"+ guestName +"', "+ roomNumber +",'"+ contactNumber +" ' )";
         try (Statement statement = connection.createStatement()){
             int affectRow = statement.executeUpdate(sql);

             if(affectRow>0){
                 System.out.println("Reservation Successfull !!!");

             }else {
                 System.out.println("Reservation Failed");
             }

         }
     }catch (SQLException e){
           e.printStackTrace();
     }
    }
    private  static  void viewReservations(Connection connection) throws SQLException{
        String sql = "SELECT reservationId,guest_name,room_number,contact_number,reservation_date  FROM reservations";
        try (Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql)) {
            System.out.println("Current Reservation:");
            System.out.println("+---------------+-------------+-------------+----------------+-----------+");
            System.out.println("| ReservationID | Guest        | Room Number | Contact number | reservation Date");
            System.out.println("+---------------+-------------+-------------+----------------+-----------+");
          while(resultSet.next()){
              int reservationID = resultSet.getInt("reservationId");
              String guest_name = resultSet.getString("guest_name");
              int room_number = resultSet.getInt("room_number");
              String contact_number = resultSet.getString("contact_number");
              String reservation_date = resultSet.getString("reservation_date");


              // Formate and display the reservation date in a table like foramte
              System.out.printf("| %-14d   |   %-15s     |   %-13d   |     %-20s   |  %-19s | \n",
                              reservationID,guest_name,room_number,contact_number,reservation_date);

          }
            System.out.println("+---------------+-------------+-------------+----------------+-----------+");

        }
    }
    /*
     private static void getRoomNo(Connection connection , Scanner sc){
        try {
            System.out.println("Enter Reservation ID: ");
            int reservationId = sc.nextInt();
            System.out.println("Enter Guest Name: ");
            String guestName = sc.next();

            String sql = "SELECT room_number From reservations " +
                    "WHERE reservationId = " + reservationId +
                    "AND guest_name = '"+guestName+"'";

            try (Statement statement = connection.createStatement();
            ResultSet resultSet  = statement.executeQuery(sql)){
            if(resultSet.next()){
                int roomNumber = resultSet.getInt("room_number");
                System.out.println("Room Number for Reservation ID "+ reservationId +"and Guest "+ guestName + "is"+ roomNumber);
            }else {
                System.out.println("Reservation Not found too the given Room number and ID ");
            }

            }


        }catch (SQLException e ){
            e.printStackTrace();
        }

     }
*/
    private static void getRoomNo(Connection connection, Scanner sc) {
        try {
            System.out.print("Enter reservation ID: ");
            int reservationId = sc.nextInt();
            System.out.print("Enter guest name: ");
            String guestName = sc.next();

            String sql = "SELECT room_number FROM reservations " +
                    "WHERE reservationId = " + reservationId +
                    " AND guest_name = '" + guestName + "'";

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                if (resultSet.next()) {
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room number for Reservation ID " + reservationId +
                            " and Guest " + guestName + " is: " + roomNumber);
                } else {
                    System.out.println("Reservation not found for the given ID and guest name.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static  void updateReservation(Connection connection , Scanner sc){
        try {
            System.out.println("Enter Reservation ID to update: ");
            int reservationId = sc.nextInt();
            sc.nextLine();// Come to new line character

            if(!reservationExists(connection, reservationId)){
                System.out.println("Reservation Not found for the given ID ");
                return;
            }
            System.out.print("Enter the guest name: ");
            String newGuestName = sc.nextLine();
            System.out.print("Enter new room number: ");
            int newRoomNumber = sc.nextInt();
            System.out.print("Enter new contact number: ");
            String newContactNumber= sc.next();

            String sql = "UPDATE reservations SET guest_name = '"+ newGuestName + "'," +
                    "room_number = "+ newRoomNumber + " , "+
                    "contact_number = '"+ newContactNumber + "' "+
                    "WHERE reservationID = " + reservationId;

            try (Statement statement = connection.createStatement()){
                int affectedRow = statement.executeUpdate(sql);
                if(affectedRow>0){
                    System.out.println("Reservation Updated Successfully");

                }else {
                    System.out.println("Reservation Updation failed");
                }


            }
        }catch (SQLException e){
            e.printStackTrace();
        }

     }
     private  static void deleteReservation(Connection connection , Scanner sc){
        try {
            System.out.println("Enter Reservation ID to delete: ");
            int reservationID = sc.nextInt();

            if(!reservationExists(connection, reservationID)){
                System.out.println("Reservation not found for the given ID ");
                return;

            }
            String sql = "DELETE FROM reservations WHERE reservationID = "+ reservationID;

            try (Statement statement = connection.createStatement()){
               int affectedRow = statement.executeUpdate(sql);
               if(affectedRow>0){
                   System.out.println("Reservation Deleted Successfully");
               }else {
                   System.out.println("Reservation deletion failed");
               }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
     }
     private  static  boolean reservationExists(Connection connection , int reservationID){
        try {

            String sql= "SELECT reservationID from reservations WHERE reservationID = "+reservationID;

            try {
                Statement statement  = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);


                return resultSet.next();//IF there is a reault , reservation exixts
            }finally {
                System.out.println("Reservation exits ");
            }
        }catch (SQLException e){
            e.printStackTrace();
            return false;// Handle database errors as needed
        }
     }

     private static void exit() throws InterruptedIOException, InterruptedException {
         System.out.print("Exiting from System");
         int i = 5;
         while (i!=0){
             System.out.print(".");
             Thread.sleep(450);
             i--;
         }
         System.out.println();
         System.out.println("Thanks for using Hotel Management System!!!!");
     }
}
