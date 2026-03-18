package guiRole1;

import java.sql.SQLException;
import java.util.Optional;
import entityClasses.Reply;
import database.Database;

import entityClasses.Post;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import java.util.List;
import javafx.scene.control.ButtonType;

/*******
 * 
 * <p> Title: ControllerRole1Home Class. </p>
 * 
 * <p> Description: The Java/FX-based Role 1 Home Page.  This class provides the controller
 * actions basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page is a stub for establish future roles for the application.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-17 Initial version
 * @version 1.01		2025-09-16 Update Javadoc documentation *  
 */

public class ControllerRole1Home {

	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */
	private static String author = "";
	private static String content = "";
	private static String thread = "General";
	private static String role = "";
	private static int replyNumber;
	private static String replyContent = "";
	protected static Database theDatabase = applicationMain.FoundationsMain.database;
	
	
	/**
	 * Default constructor is not used.
	 */
	public ControllerRole1Home() {
	}

	/**********
	 * <p> Method: performUpdate() </p>
	 * 
	 * <p> Description: This method directs the user to the User Update Page so the user can change
	 * the user account attributes. </p>
	 * 
	 */
	protected static void performUpdate () {
		guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewRole1Home.theStage, ViewRole1Home.theUser);
	}	

	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal login
	 * page where existing users can log in or potential new users with a invitation code can
	 * start the process of setting up an account. </p>
	 * 
	 */
	protected static void setAuthor() {
		author = ViewRole1Home.theUser.getUserName();
	}
	
	protected static void setContent() {
		content = ViewRole1Home.text_PostContent.getText();
	}
	

	
	protected static void setRole() {
		if(ViewRole1Home.theUser.getAdminRole()) {
			role = "ADMIN";}
		if(ViewRole1Home.theUser.getNewRole1()) {
			role = "STUDENT";
			}
		if(ViewRole1Home.theUser.getNewRole2()) {
			role = "STAFF";
		}
	}
	
	
	
	
	//set thread
	
	
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewRole1Home.theStage);
	}
	
	protected static void performNewPost() {
		setAuthor();
		setContent();
		setRole();
		//input validation
		if(content.isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText("Problem with posting");
			alert.setContentText("You have to write something in the text field in order to post");
			alert.showAndWait();
			return;
		}
		
		Post post = new Post(author, role, content, thread);
		
		
		try {
			theDatabase.addPost(post);
		}
		catch (SQLException e) {
            System.err.println("*** ERROR *** Database error trying to create a post: " + 
            		e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }
		ViewRole1Home.text_PostContent.setText("");
		ViewRole1Home.alertPosted.setHeaderText("Posting Successful!");
		ViewRole1Home.alertPosted.setContentText("Your post will be displayed for all students and staff");
		ViewRole1Home.alertPosted.showAndWait();
		
		


		
		
	}
	//hw2 deletepost
	protected static boolean deletePost(int post) {
		Alert alert = new Alert(Alert.AlertType.NONE);
		alert.setTitle("Delete post?");
		alert.setHeaderText("This is your own post, do you want to view replies or delete it?");
		ButtonType reply = new ButtonType("View Replies");
		ButtonType delete = new ButtonType("Delete Post");
		
		alert.getButtonTypes().addAll(reply, delete);
		
		Optional<ButtonType> result = alert.showAndWait();
		
		if(result.isPresent()) {
			if(result.get() == reply) {
				performNewReply(post);
				return false;
			}
			if(result.get() == delete) {
				boolean postDeleted;
				postDeleted = theDatabase.deletePost(post);
				if(postDeleted) {
					alert.setHeaderText("POST DELETED");
					alert.setContentText("YOU HAVE SUCCESSFULLY DELETED THIS POST");
					alert.showAndWait();
					return true;
				}
				else {
					alert.setHeaderText("COULD NOT DELETE POST");
					alert.setContentText("THERE WAS AN ISSUE DELETING POST FROM DATABASE");
					alert.showAndWait();
					return false;
				}
			}
		}
		return false;
		
	}
	

	//hw2 viewreplies
	protected static void viewReplies(int ogPost) {
		List<String[]> replies = theDatabase.getRepliesFromPost(ogPost);
		Alert alert = new Alert(Alert.AlertType.NONE);
		StringBuilder replyList = new StringBuilder();
		if(replies.isEmpty()) {
			replyList.append("There are currently no replies to this post");
		}
		else {
			for (String[] reply : replies) {
				replyList.append(String.format("%s %s to %s thread: %s \n\n",
				reply[0], reply[2], reply[3], reply[1]));}}
	
		alert.setTitle("Replies");
		alert.setHeaderText("These are the replies to this post");
		alert.setContentText(replyList.toString());
	    ButtonType reply = new ButtonType("Reply to this Post");
	    ButtonType exit = new ButtonType("Exit");
	    
	    alert.getButtonTypes().addAll(reply,exit);
	    
	    Optional<ButtonType> result = alert.showAndWait();
	    
	    if(result.isPresent()) {
	    	if(result.get() == reply) {
	    		performNewReply(ogPost);
	    	}
	    	if(result.get() == exit) {
	    		return;
	    	}
	    }
	    
		

		
	}
	
	/**********
	 * <p> Method: performReply() </p>
	 * 
	 * <p> Description: This method adds a reply to the database after accepting user input for the reply</p>
	 * @param int ogPost
	 */	
	
//hw2 performnewreply
	protected static void performNewReply(int ogPost) {
		setAuthor();
		setRole();
		
		TextInputDialog writeAReply = new TextInputDialog();
		writeAReply.setTitle(String.format("Write A Reply to %s \n", ogPost));
		writeAReply.setHeaderText("Reply to this user");
		replyContent = writeAReply.showAndWait().orElse("");
		replyNumber = ogPost;
		
		//input validation
		if(replyContent.isEmpty() || replyContent == "") {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText("Problem with posting");
			alert.setContentText("You have to write something in the text field in order to post this reply");
			alert.showAndWait();
			return;
				}
				
		
		Reply reply = new Reply(author, role, replyContent, thread, replyNumber);
		
		
		try {
			theDatabase.addReply(reply);
		}
		catch (SQLException e) {
            System.err.println("*** ERROR *** Database error trying to create a reply: " + 
            		e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }
		ViewRole1Home.text_PostContent.setText("");
		ViewRole1Home.alertPosted.setHeaderText("Reply Successful!");
		ViewRole1Home.alertPosted.setContentText("Your reply will be added to this posts list of replies");
		ViewRole1Home.alertPosted.showAndWait();
		
		


		
		
	}
	
	
	
	
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program.  It leaves the
	 * database in a state where the normal login page will be displayed when the application is
	 * restarted.</p>
	 * 
	 */	
	

	protected static void performQuit() {
		System.exit(0);
	}
}
