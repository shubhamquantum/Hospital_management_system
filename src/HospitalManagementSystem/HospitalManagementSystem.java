package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static  final  String url="jdbc:mysql://localhost:3306/hospital";
    private  static  final  String username="root";
    private  static  final String  password="8771";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Scanner scanner =new Scanner(System.in);
        try {
            Connection connection= DriverManager.getConnection(url,username,password);
            Patient patient=new Patient(connection,scanner);
            Doctor doctor=new Doctor(connection);
            while(true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. Add Patients");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctor");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.println( "Enter your choice");
                int choice=scanner.nextInt();
                switch(choice)
                {
                    case 1:
                        //add patients
                        patient.addPatient();
                        System.out.println();
                        break;
                    case  2:
                        //view patients
                        patient.viewPatient();
                        System.out.println();
                        break;
                    case 3:
                        //view doctors
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        //book appointment
                        bookAppointment(patient,doctor,connection,scanner);
                        System.out.println();
                        break;
                    case 5:
                        return;
                    default:
                        System.out.println("enter valid choice");
                }
            }

        }catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    public static  void bookAppointment(Patient patient,Doctor doctor,Connection connection,Scanner scanner){
        System.out.println("enter patient id");
        int patientid=scanner.nextInt();
        System.out.println("enter doctor id");
        int doctorid=scanner.nextInt();
        System.out.println("Enter appointment date (YYYY-MM_DD):");
        String appointment_date=scanner.next();
        if(patient.getPatientbyId(patientid)&&doctor.getDoctorbyId(doctorid)){
            if(checkedDoctorAvailability(doctorid,appointment_date,connection)){
                String appointmentQuery="insert into appointments(patient_id,doctor_id,appointment_date)values (?,?,?)";
                try{
                    PreparedStatement preparedStatement=connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1,patientid);
                    preparedStatement.setInt(2,doctorid);
                    preparedStatement.setString(3,appointment_date);
                    int aff=preparedStatement.executeUpdate();
                    if(aff>0)
                    {
                        System.out.println("appointment booked");
                    }
                    else {
                        System.out.println("failed to appointment book");
                    }
                }catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
            else {
                System.out.println("doctor not available");
            }
        }
        else {
            System.out.println("Either doctor or patient not exists");
        }
    }
    public static boolean checkedDoctorAvailability(int doctorid,String appointmentDate,Connection connection){
        String query="select count(*) from appointments where doctor_id= ? and appointment_date = ?";
        try{
            PreparedStatement preparedStatement =connection.prepareStatement(query);
            preparedStatement.setInt(1,doctorid);
            preparedStatement.setString(2,appointmentDate);
            ResultSet resultSet=preparedStatement.executeQuery();
            if(resultSet.next())
            {
                int count=resultSet.getInt(1);
                if(count==0)
                {
                    return true;
                }
                else {
                    return false;
                }
            }

        }catch ( SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
