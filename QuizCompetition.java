package JDBC_Projects;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class QuizCompetition {

	// ================= DB CONNECTION =================
	static class DBConnection {
		public static Connection getConnection() throws Exception {
			Class.forName("com.mysql.cj.jdbc.Driver");
			return DriverManager.getConnection("jdbc:mysql://localhost:3306/quiz_db", "root", "root");
		}
	}

	// ================= ADMIN =================
	static class Admin {

		public static void adminLogin() {
			Scanner sc = new Scanner(System.in);

			try {
				Connection con = DBConnection.getConnection();

				System.out.print("Enter Admin Username: ");
				String user = sc.next();

				System.out.print("Enter Admin Password: ");
				String pass = sc.next();

				String sql = "SELECT * FROM admin WHERE username=? AND password=?";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setString(1, user);
				ps.setString(2, pass);

				ResultSet rs = ps.executeQuery();

				if (rs.next()) {
					adminMenu(con, sc);
				} else {
					System.out.println("Invalid Admin Credentials!");
				}

				con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private static void adminMenu(Connection con, Scanner sc) throws Exception {
			int choice;

			do {
				System.out.println("\n--- Admin Menu ---");
				System.out.println("1. Add Question");
				System.out.println("2. View All Results");
				System.out.println("3. Exit");
				System.out.print("Enter choice: ");
				choice = sc.nextInt();
				sc.nextLine();

				switch (choice) {
				case 1:
					addQuestion(con, sc);
					break;
				case 2:
					viewResults(con);
					break;
				case 3:
					System.out.println("Admin Logged Out");
					break;
				default:
					System.out.println("Invalid Choice");
				}
			} while (choice != 3);
		}

		private static void addQuestion(Connection con, Scanner sc) throws Exception {
			String sql = "INSERT INTO questions(question,option1,option2,option3,option4,correctOption) VALUES (?,?,?,?,?,?)";
			PreparedStatement ps = con.prepareStatement(sql);

			System.out.println("Enter Question:");
			ps.setString(1, sc.nextLine());

			System.out.println("Option 1:");
			ps.setString(2, sc.nextLine());

			System.out.println("Option 2:");
			ps.setString(3, sc.nextLine());

			System.out.println("Option 3:");
			ps.setString(4, sc.nextLine());

			System.out.println("Option 4:");
			ps.setString(5, sc.nextLine());

			System.out.print("Correct Option (1-4): ");
			ps.setInt(6, sc.nextInt());
			sc.nextLine();

			ps.executeUpdate();
			System.out.println("Question Added Successfully!");
		}

		private static void viewResults(Connection con) throws Exception {
			String sql = "SELECT * FROM scores";
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			System.out.println("\n--- User Scores ---");
			System.out.println("Username\tScore");

			while (rs.next()) {
				System.out.println(rs.getString("username") + "\t\t" + rs.getInt("score"));
			}
		}
	}

	// ================= USER =================
	static class User {

		public static void startQuiz() {
			Scanner sc = new Scanner(System.in);
			int score = 0;

			try {
				Connection con = DBConnection.getConnection();

				System.out.print("Enter Your Username: ");
				String username = sc.next();

				String sql = "SELECT * FROM questions ORDER BY RAND() LIMIT 5";
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();

				while (rs.next()) {
					System.out.println("\n" + rs.getString("question"));
					System.out.println("1. " + rs.getString("option1"));
					System.out.println("2. " + rs.getString("option2"));
					System.out.println("3. " + rs.getString("option3"));
					System.out.println("4. " + rs.getString("option4"));

					System.out.print("Choose option (1-4): ");
					int ans = sc.nextInt();

					if (ans == rs.getInt("correctOption")) {
						score++;
					}
				}

				System.out.println("\nQuiz Finished!");
				System.out.println("Score: " + score + "/5");

				String insert = "INSERT INTO scores(username,score) VALUES (?,?)";
				PreparedStatement ps2 = con.prepareStatement(insert);
				ps2.setString(1, username);
				ps2.setInt(2, score);
				ps2.executeUpdate();

				con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// ================= MAIN =================
	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		int choice;

		do {
			System.out.println("\n===== Quiz Competition =====");
			System.out.println("1. Admin");
			System.out.println("2. User");
			System.out.println("3. Exit");
			System.out.print("Enter choice: ");
			choice = sc.nextInt();

			switch (choice) {
			case 1:
				Admin.adminLogin();
				break;
			case 2:
				User.startQuiz();
				break;
			case 3:
				System.out.println("Thank You!");
				break;
			default:
				System.out.println("Invalid Choice");
			}
		} while (choice != 3);

		sc.close();
	}
}
