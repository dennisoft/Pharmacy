
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.*;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class PharmacyManagementSystem extends Application {

    String db_host = "localhost";
    String db_user = "root";
    String db_pass = "kevo";
    String url = "jdbc:mysql://localhost:3306/";
    String db_name = "pharmacy";
    String jdbc = "com.mysql.jdbc.Driver";
    String[] drugs = new String[]{"Hedex", "Panadol", "Amoxyll", "Penicillin", "Piriton", "Celastamine"};
    String[] suppliers = new String[]{"KEMRI", "Theranos", "Medical Laboratores"};
    String usernameSession;
    String passwordSession;

    PreparedStatement ps;
    Connection conn;
    ResultSet rs;

    private final TableView<Drug> table = new TableView<>();
    private final ObservableList<Drug> data = FXCollections.observableArrayList();

    private final TableView<Patient> table_patients = new TableView<>();
    private final ObservableList<Patient> table_patients_data = FXCollections.observableArrayList();

    private final TableView<Presc> table_add_presc = new TableView<>();
    private final ObservableList<Presc> table_add_presc_data = FXCollections.observableArrayList();

    private final TableView<Clerk> clerktable = new TableView<>();
    private final ObservableList<Clerk> clerktabledata = FXCollections.observableArrayList();

    private final TableView<Duty> dutytable = new TableView<>();
    private final ObservableList<Duty> dutytabledata = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {

        //***************** scene13 **************
        BorderPane bp13 = new BorderPane();
        Scene scene13 = new Scene(bp13, 1300, 600);
        scene13.getStylesheets().add("pham.css");

        Label searchDutyL = new Label("Search By Phone Number:");
        searchDutyL.setId("label");

        TextField searchByDutyTF = new TextField();
        searchByDutyTF.setPrefWidth(200);
        searchByDutyTF.setPrefHeight(20);

        Button searchDutyB = new Button("Search");
        searchDutyB.setId("button");

        //search Duty Button onclick
        searchDutyB.setOnAction((ActionEvent t) -> {
            try {
                Class.forName(jdbc);
                conn = DriverManager.getConnection(url + db_name, db_user, db_pass);
                String search_dutyS = searchByDutyTF.getText();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM duties WHERE pnumber='" + search_dutyS + "'");
                rs = ps.executeQuery();
                int count_searchD = 0;
                while (rs.next()) {
                    dutytable.getItems().clear();
                    count_searchD = count_searchD + 1;
                    dutytabledata.add(new Duty(rs.getString("duty_id"), rs.getString("username"), rs.getString("pnumber"), rs.getString("date_recorded")));

                }
                if (count_searchD == 0) {
                    Alert err = new Alert(AlertType.ERROR);
                    err.setTitle("Error Dialog");
                    err.setHeaderText("Error Dialog For Failure In Getting Records");
                    err.setContentText("Empty Records Detected.Please Contact The Administrator.");
                    err.showAndWait();
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            }

        });

        FlowPane bp13FP = new FlowPane();
        bp13FP.setPadding(new Insets(10, 0, 10, 5));
        bp13FP.setHgap(5);
        bp13FP.getChildren().addAll(searchDutyL, searchByDutyTF, searchDutyB);
        bp13.setTop(bp13FP);

        GridPane addDutyGP = new GridPane();
        addDutyGP.setHgap(5);
        addDutyGP.setAlignment(Pos.CENTER_LEFT);
        addDutyGP.setPadding(new Insets(0, 10, 0, 10));

        Text addDutyT = new Text("Assign Duty");
        addDutyT.setFont(Font.font("TAHOMA", FontWeight.BOLD, 20));
        addDutyT.setFill(Color.TOMATO);
        addDutyGP.add(addDutyT, 0, 0, 2, 1);

        Label addDutyNameL = new Label("Username:");
        addDutyNameL.setId("label");
        addDutyGP.add(addDutyNameL, 0, 1);

        TextField addDutyNameTF = new TextField();
        addDutyNameTF.setPrefWidth(200);
        addDutyNameTF.setPrefHeight(20);
        addDutyGP.add(addDutyNameTF, 1, 1);

        Label addDutyNumberL = new Label("Phone Number:");
        addDutyNumberL.setId("label");
        addDutyGP.add(addDutyNumberL, 0, 2);

        TextField addDutyNumberTF = new TextField();
        addDutyNumberTF.setPrefWidth(200);
        addDutyNumberTF.setPrefHeight(20);
        addDutyGP.add(addDutyNumberTF, 1, 2);

        Label addDutyDateL = new Label("Add Date:");
        addDutyDateL.setId("label");
        addDutyGP.add(addDutyDateL, 0, 3);

        DatePicker addDutyDateF = new DatePicker();
        addDutyDateF.setPrefWidth(200);
        addDutyDateF.setPrefHeight(20);
        addDutyGP.add(addDutyDateF, 1, 3);

        Button addDutyB = new Button("Add");
        addDutyB.setId("button");

        Button clearDutyB = new Button("Clear");
        clearDutyB.setId("button");

        HBox dutyHB = new HBox(10);
        dutyHB.getChildren().addAll(addDutyB, clearDutyB);
        dutyHB.setAlignment(Pos.BOTTOM_RIGHT);
        addDutyGP.add(dutyHB, 1, 4);
        bp13.setLeft(addDutyGP);

        //setting action of adddutyB button'
        addDutyB.setOnAction((ActionEvent y) -> {
            //code logic here
            String usernameD = addDutyNameTF.getText();
            String pnumberD = addDutyNumberTF.getText();
            String dateD = ((TextField) addDutyDateF.getEditor()).getText();

            try {
                Class.forName(jdbc);
                conn = DriverManager.getConnection(url + db_name, db_user, db_pass);
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM clerks WHERE username='" + usernameD + "'");
                rs = ps.executeQuery();
                int username_count = 0;
                while (rs.next()) {
                    username_count = username_count + 1;
                }
                if (username_count == 0) {
                    Alert err = new Alert(AlertType.ERROR);
                    err.setTitle("Error Dialog");
                    err.setHeaderText("Error Dialog For No Clerk Found.");
                    err.setContentText("That Username has no corressponding clerk.");
                    err.showAndWait();
                } else {
                    PreparedStatement pz = conn.prepareStatement("SELECT * FROM clerks WHERE pnumber='" + pnumberD + "'");
                    rs = pz.executeQuery();
                    int pnumber_count = 0;
                    while (rs.next()) {
                        pnumber_count = pnumber_count + 1;
                    }
                    if (pnumber_count == 0) {
                        Alert err = new Alert(AlertType.ERROR);
                        err.setTitle("Error Dialog");
                        err.setHeaderText("Error Dialog For No Clerk Found.");
                        err.setContentText("That Phone Number has no corressponding clerk.");
                        err.showAndWait();
                    } else {

                        String usernameDs = addDutyNameTF.getText();
                        String pnumberDs = addDutyNumberTF.getText();
                        String dateDs = ((TextField) addDutyDateF.getEditor()).getText();

                        PreparedStatement pi = conn.prepareStatement("INSERT INTO duties(username,pnumber,date_recorded)VALUES(?,?,?)");
                        pi.setString(1, usernameDs);
                        pi.setString(2, pnumberDs);
                        pi.setString(3, dateDs);

                        int s_insert = pi.executeUpdate();
                        if (s_insert > 0) {
                            Alert info = new Alert(AlertType.INFORMATION);
                            info.setTitle("Information Dialog.");
                            info.setHeaderText("Information Dialog For Successfulrecord made.");
                            info.setContentText("Records Made Successfully!");
                            info.showAndWait();

                            //populate the table
                            PreparedStatement psx = conn.prepareStatement("SELECT * FROM duties");
                            rs = psx.executeQuery();
                            int duty_table = 0;
                            while (rs.next()) {
                                dutytable.getItems().clear();

                                //clear the fields
                                addDutyNameTF.clear();
                                addDutyNumberTF.clear();
                                ((TextField) addDutyDateF.getEditor()).clear();

                                duty_table = duty_table + 1;
                                dutytabledata.add(new Duty(rs.getString("duty_id"), rs.getString("username"), rs.getString("pnumber"), rs.getString("date_recorded")));
                            }
                            if (duty_table == 0) {

                                Alert err = new Alert(AlertType.ERROR);
                                err.setTitle("Error Dialog");
                                err.setHeaderText("Error Dialog For Failure In Making A Record");
                                err.setContentText("Errors Occured While Making The Record.Please Contact The Administrator");
                                err.showAndWait();

                            }

                        } else {
                            Alert err = new Alert(AlertType.ERROR);
                            err.setTitle("Error Dialog");
                            err.setHeaderText("Error Dialog For Failure In Making Records");
                            err.setContentText("Errors Occured While Making Records.Please Contact The Administrator");
                            err.showAndWait();
                        }
                    }
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        TableColumn duty_idC = new TableColumn("duty_id");
        duty_idC.setMinWidth(100);
        duty_idC.setCellValueFactory(new PropertyValueFactory<>("Duty_id"));

        TableColumn duty_usernameC = new TableColumn("username");
        duty_usernameC.setMinWidth(100);
        duty_usernameC.setCellValueFactory(new PropertyValueFactory<>("Username"));

        TableColumn duty_pnumberC = new TableColumn("mobile");
        duty_pnumberC.setMinWidth(100);
        duty_pnumberC.setCellValueFactory(new PropertyValueFactory<>("Mobile"));

        TableColumn duty_dateC = new TableColumn("date");
        duty_dateC.setMinWidth(100);
        duty_dateC.setCellValueFactory(new PropertyValueFactory<>("Date"));

        bp13.setCenter(dutytable);
        dutytable.setItems(dutytabledata);
        dutytable.getColumns().addAll(duty_idC, duty_usernameC, duty_pnumberC, duty_dateC);

        dutytable.setOnMouseClicked((javafx.scene.input.MouseEvent e) -> {

            Duty duty = (Duty) dutytable.getSelectionModel().getSelectedItem();

            String duty_id = duty.getDuty_id();

            Alert confirm = new Alert(AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation Dialog");
            confirm.setHeaderText("Confirmation Dialog For Prescription");
            confirm.setContentText("Do You Want to Delete This Thread?");
            confirm.showAndWait();

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.get() == ButtonType.OK) {

                try {
                    Class.forName(jdbc);
                    conn = DriverManager.getConnection(url + db_name, db_user, db_pass);
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM duties WHERE duty_id='" + duty_id + "'");
                    int delete_S = ps.executeUpdate();
                    if (delete_S > 0) {
                        Alert info = new Alert(AlertType.INFORMATION);
                        info.setTitle("Information Dialog.");
                        info.setHeaderText("Information Dialog For Successful Deletion.");
                        info.setContentText("Deletion Made Successfully!");
                        info.showAndWait();
                    } else if (delete_S == 0) {
                        Alert err = new Alert(AlertType.ERROR);
                        err.setTitle("Error Dialog");
                        err.setHeaderText("Error Dialog For Failure In Deleting record");
                        err.setContentText("Errors Occured While Making Deleting Record.Please Contact The Administrator");
                        err.showAndWait();
                    }
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        Button syncBP13 = new Button("sync");
        syncBP13.setId("button");

        syncBP13.setOnAction((ActionEvent t) -> {
            dutytable.getItems().clear();
            try {
                Class.forName(jdbc);
                conn = DriverManager.getConnection(url + db_name, db_user, db_pass);
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM duties");
                rs = ps.executeQuery();
                int counts = 0;
                while (rs.next()) {

                    counts = counts + 1;
                    //populate the table with sync button
                    dutytabledata.add(new Duty(rs.getString("duty_id"), rs.getString("username"), rs.getString("pnumber"), rs.getString("date_recorded")));
                }
                if (counts == 0) {
                    Alert err = new Alert(AlertType.ERROR);
                    err.setTitle("Error Dialog");
                    err.setHeaderText("Error Dialog For Failure In Getting Records");
                    err.setContentText("Empty Records Detected.Please Contact The Administrator.");
                    err.showAndWait();
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        Button backFromScene13 = new Button("back");
        backFromScene13.setId("button");

        FlowPane bottomBFP = new FlowPane();
        bottomBFP.setHgap(5);
        bottomBFP.setPadding(new Insets(10, 0, 10, 5));
        bottomBFP.getChildren().addAll(syncBP13, backFromScene13);
        bp13.setBottom(bottomBFP);

        //***************************** scene12 *****************
        BorderPane bp12 = new BorderPane();
        Scene scene12 = new Scene(bp12, 1300, 600);
        scene12.getStylesheets().add("pham.css");

        //clerk grid
        GridPane gridClerk = new GridPane();
        gridClerk.setAlignment(Pos.CENTER_LEFT);
        gridClerk.setHgap(5);
        bp12.setLeft(gridClerk);
        //clerk title
        Text clerktitle = new Text("Add Clerk");
        clerktitle.setFill(Color.TOMATO);
        clerktitle.setFont(Font.font("TAHOMA", FontWeight.BOLD, 20));
        gridClerk.add(clerktitle, 0, 0, 2, 1);

        Label clerkUN = new Label("Username:");
        clerkUN.setId("label");
        gridClerk.add(clerkUN, 0, 1);

        TextField clerkUTF = new TextField();
        clerkUTF.setPrefWidth(200);
        clerkUTF.setPrefHeight(20);
        clerkUTF.setPromptText("Username");
        gridClerk.add(clerkUTF, 1, 1);

        Label clerkPL = new Label("Password:");
        clerkPL.setId("label");
        gridClerk.add(clerkPL, 0, 2);

        PasswordField clerkPF = new PasswordField();
        clerkPF.setPromptText("Password");
        clerkPF.setPrefWidth(200);
        clerkPF.setPrefHeight(20);
        gridClerk.add(clerkPF, 1, 2);

        Label clerkPN = new Label("Phone Number:");
        clerkPN.setId("label");
        gridClerk.add(clerkPN, 0, 3);

        TextField clerkPNF = new TextField();
        clerkPNF.setPrefWidth(200);
        clerkPNF.setPrefHeight(20);
        clerkPNF.setPromptText("Phone Number");
        gridClerk.add(clerkPNF, 1, 3);

        Button addB = new Button("Add");
        addB.setId("button");
        Image buttonIcon = new Image(getClass().getResourceAsStream("user.png"));
        ImageView buttonIconView = new ImageView(buttonIcon);
        buttonIconView.setFitWidth(15);
        buttonIconView.setFitHeight(15);
        addB.setGraphic(buttonIconView);

        Button clearClerkB = new Button("Clear");
        clearClerkB.setId("button");
        Image clearClerkIcon = new Image(getClass().getResourceAsStream("brush.png"));
        ImageView clerkIconView = new ImageView(clearClerkIcon);
        clerkIconView.setFitWidth(15);
        clerkIconView.setFitHeight(15);
        clearClerkB.setGraphic(clerkIconView);

        HBox addclerkHB = new HBox(10);
        addclerkHB.setAlignment(Pos.BOTTOM_RIGHT);
        addclerkHB.getChildren().addAll(addB, clearClerkB);
        gridClerk.add(addclerkHB, 1, 4);

        //clear button
        clearClerkB.setOnAction((ActionEvent t) -> {
            clerkUTF.clear();
            clerkPF.clear();
            clerkPNF.clear();
        });
        //onclick button addclerk register clerk
        addB.setOnAction((ActionEvent t) -> {
            clerktable.getItems().clear();
            try {
                Class.forName(jdbc);
                conn = DriverManager.getConnection(url + db_name, db_user, db_pass);
                String clerk_userN = clerkUTF.getText();
                String clerk_passN = clerkPF.getText();
                String clerk_pnN = clerkPNF.getText();
                PreparedStatement psy = conn.prepareStatement("INSERT INTO clerks(username,password,pnumber)VALUES(?,?,?)");
                psy.setString(1, clerk_userN);
                psy.setString(2, clerk_passN);
                psy.setString(3, clerk_pnN);

                int clerk_insert = psy.executeUpdate();
                if (clerk_insert > 0) {
                    Alert info = new Alert(AlertType.INFORMATION);
                    info.setTitle("Information Dialog");
                    info.setHeaderText("Information Dialog  For Successful Update");
                    info.setContentText("You Succesfully Created A Clerk Account");
                    info.showAndWait();

                    clerkUTF.clear();
                    clerkPF.clear();
                    clerkPNF.clear();

                    Class.forName(jdbc);
                    conn = DriverManager.getConnection(url + db_name, db_user, db_pass);
                    PreparedStatement psz = conn.prepareStatement("SELECT * FROM clerks");
                    int count_clerk_sync = 0;
                    rs = psz.executeQuery();

                    while (rs.next()) {
                        count_clerk_sync = count_clerk_sync + 1;
                        clerktabledata.add(new Clerk(rs.getString("clerk_id"), rs.getString("username"), rs.getString("password"), rs.getString("pnumber")));
                    }
                } else {
                    Alert err = new Alert(AlertType.ERROR);
                    err.setTitle("Error Dialog");
                    err.setHeaderText("Error Dialog  For Failed Update");
                    err.setContentText("There Was An Error Creating A Clerk Account Please Contact The Administrator");
                    err.showAndWait();
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        Label searchClerk = new Label("Search By PhoneNumber:");
        searchClerk.setId("label");

        TextField searchClerkTF = new TextField();
        searchClerkTF.setPrefWidth(200);
        searchClerkTF.setPrefHeight(20);
        searchClerkTF.setPromptText("PhoneNumber");

        Button searchClerkB = new Button("search");
        searchClerkB.setId("button");

        //add an action to search button
        searchClerkB.setOnAction((ActionEvent t) -> {
            clerktable.getItems().clear();
            try {
                Class.forName(jdbc);
                conn = DriverManager.getConnection(url + db_name, db_user, db_pass);
                String clerkS = searchClerkTF.getText();
                PreparedStatement psc = conn.prepareStatement("SELECT * FROM clerks WHERE pnumber='" + clerkS + "'");
                rs = psc.executeQuery();
                int count_searchC = 0;
                while (rs.next()) {
                    count_searchC = count_searchC + 1;
                    clerktabledata.add(new Clerk(rs.getString("clerk_id"), rs.getString("username"), rs.getString("password"), rs.getString("pnumber")));
                }
                if (count_searchC == 0) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Dialog");
                    alert.setHeaderText("There Was An Error");
                    alert.setContentText("No recorded details!");
                    alert.showAndWait();
                }

                conn.close();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            }

        });
        FlowPane scene12TopFP = new FlowPane();
        scene12TopFP.setHgap(5);
        scene12TopFP.setPadding(new Insets(10, 0, 10, 5));
        scene12TopFP.getChildren().addAll(searchClerk, searchClerkTF, searchClerkB);
        bp12.setTop(scene12TopFP);

        //table to display at the center
        TableColumn clerk_idC = new TableColumn("clerk_id");
        clerk_idC.setMinWidth(120);
        clerk_idC.setCellValueFactory(new PropertyValueFactory<>("Clerk_id"));

        TableColumn clerk_usernameC = new TableColumn("username");
        clerk_usernameC.setMinWidth(120);
        clerk_usernameC.setCellValueFactory(new PropertyValueFactory<>("Username"));

        TableColumn clerk_passwordC = new TableColumn("password");
        clerk_passwordC.setMinWidth(120);
        clerk_passwordC.setCellValueFactory(new PropertyValueFactory<>("Password"));

        TableColumn clerk_pnumberC = new TableColumn("mobile");
        clerk_pnumberC.setMinWidth(120);
        clerk_pnumberC.setCellValueFactory(new PropertyValueFactory<>("Mobile"));

        clerktable.getColumns().addAll(clerk_idC, clerk_usernameC, clerk_passwordC, clerk_pnumberC);
        clerktable.setItems(clerktabledata);
        bp12.setCenter(clerktable);

        Button syncScene12B = new Button("Sync");
        syncScene12B.setId("button");
        syncScene12B.setOnAction((ActionEvent t) -> {
            clerktable.getItems().clear();
            try {
                Class.forName(jdbc);
                conn = DriverManager.getConnection(url + db_name, db_user, db_pass);
                PreparedStatement psx = conn.prepareStatement("SELECT * FROM clerks");
                int count_clerk_sync = 0;
                rs = psx.executeQuery();

                while (rs.next()) {
                    count_clerk_sync = count_clerk_sync + 1;
                    clerktabledata.add(new Clerk(rs.getString("clerk_id"), rs.getString("username"), rs.getString("password"), rs.getString("pnumber")));
                }
                if (count_clerk_sync == 0) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Dialog");
                    alert.setHeaderText("There Was An Error");
                    alert.setContentText("Username and Password Combiation is Wrong!");
                    alert.showAndWait();
                }

                conn.close();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            }

        });

        Button backFromScene12 = new Button("Back");
        backFromScene12.setId("button");

        Button dutiesB = new Button("Add Duty");
        dutiesB.setId("button");

        dutiesB.setOnAction((ActionEvent t) -> {
            primaryStage.setScene(scene13);
        });

        FlowPane scene12FP = new FlowPane();
        scene12FP.setPadding(new Insets(10, 0, 10, 5));
        scene12FP.setHgap(10);
        scene12FP.getChildren().addAll(syncScene12B, backFromScene12, dutiesB);
        bp12.setBottom(scene12FP);

        clerktable.setOnMouseClicked((javafx.scene.input.MouseEvent e) -> {

            Alert confirmation = new Alert(AlertType.CONFIRMATION);
            confirmation.setTitle("Confimation Dialog");
            confirmation.setHeaderText("Confirmation Dialog For Edit Or Remove");
            confirmation.setContentText("Do You Want To Make Changes");
            confirmation.showAndWait();

            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.get() == ButtonType.OK) {
                Clerk clerk = (Clerk) clerktable.getSelectionModel().getSelectedItem();

                String clerk_idx = clerk.getClerk_id();
                String clerk_unamex = clerk.getUsername();
                String clerk_passwordx = clerk.getPassword();
                String clerk_pnumberx = clerk.getMobile();

                GridPane gridEditC = new GridPane();
                Scene editClerkScene = new Scene(gridEditC, 500, 400);
                editClerkScene.getStylesheets().add("pham.css");
                gridEditC.setAlignment(Pos.CENTER);
                gridEditC.setHgap(5);

                Text c_title = new Text("Edit Or Delete User");
                c_title.setFill(Color.TOMATO);
                c_title.setFont(Font.font("TAHOMA", FontWeight.BOLD, 20));
                gridEditC.add(c_title, 0, 0, 2, 1);

                Label c_uname = new Label("Username:");
                c_uname.setId("label");
                gridEditC.add(c_uname, 0, 1);

                TextField c_uTF = new TextField();
                c_uTF.setPrefWidth(200);
                c_uTF.setPrefHeight(20);
                c_uTF.setText(clerk_unamex);
                gridEditC.add(c_uTF, 1, 1);

                Label c_passwordL = new Label("Password:");
                c_passwordL.setId("label");
                gridEditC.add(c_passwordL, 0, 2);

                TextField c_passwordTF = new TextField();
                c_passwordTF.setPrefWidth(200);
                c_passwordTF.setPrefHeight(20);
                c_passwordTF.setText(clerk_passwordx);
                gridEditC.add(c_passwordTF, 1, 2);

                Label c_pnumberL = new Label("Mobile:");
                c_pnumberL.setId("label");
                gridEditC.add(c_pnumberL, 0, 3);

                TextField c_pnumberTF = new TextField();
                c_pnumberTF.setPrefWidth(200);
                c_pnumberTF.setPrefHeight(20);
                c_pnumberTF.setText(clerk_pnumberx);
                gridEditC.add(c_pnumberTF, 1, 3);

                Button update_c_B = new Button("Save");
                update_c_B.setId("button");

                Button remove_c_B = new Button("Remove");
                remove_c_B.setId("button");

                HBox button_HB = new HBox(10);
                button_HB.setAlignment(Pos.BOTTOM_RIGHT);
                button_HB.getChildren().addAll(update_c_B, remove_c_B);
                gridEditC.add(button_HB, 1, 4);

                //adding action to buttons
                update_c_B.setOnAction((ActionEvent t) -> {
                    if (c_uTF.getText().equals("") || c_passwordTF.getText().equals("") || c_pnumberTF.getText().equals("")) {
                        Alert warn = new Alert(AlertType.WARNING);
                        warn.setTitle("Warning Dialog.");
                        warn.setHeaderText("Warning Dialog For Empty Fields");
                        warn.setContentText("Make Sure No Field Is Empty!");
                        warn.showAndWait();

                    } else {
                        try {
                            Class.forName(jdbc);
                            conn = DriverManager.getConnection(url + db_name, db_user, db_pass);
                            String c_US = c_uTF.getText();
                            String c_passwordS = c_passwordTF.getText();
                            String c_pnumberS = c_pnumberTF.getText();

                            PreparedStatement ps = conn.prepareStatement("UPDATE clerks SET username='" + c_US + "',password='" + c_passwordS + "',pnumber='" + c_pnumberS + "' WHERE clerk_id='" + clerk_idx + "'");
                            int update_clerk = ps.executeUpdate();
                            if (update_clerk > 0) {
                                Alert info = new Alert(AlertType.INFORMATION);
                                info.setTitle("Information Dialog");
                                info.setHeaderText("Information Dialog For Successful Update");
                                info.setContentText("Information Was Successfully Updated");
                                info.showAndWait();
                            } else if (update_clerk == 0) {
                                Alert err = new Alert(AlertType.ERROR);
                                err.setTitle("Error Dialog");
                                err.setHeaderText("Error Dialog For Failed Update");
                                err.setContentText("There Was An Error Saving The Details Please Contact The Administrator");
                            }
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (SQLException ex) {
                            Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
                remove_c_B.setOnAction((ActionEvent t) -> {
                    try {
                         Class.forName(jdbc);
                        conn = DriverManager.getConnection(url + db_name, db_user, db_pass);
                        PreparedStatement ps1 = conn.prepareStatement("DELETE FROM clerks WHERE clerk_id='" + clerk_idx + "'");
                        int del_success = ps1.executeUpdate();
                        if (del_success > 0) {
                            Alert info = new Alert(AlertType.INFORMATION);
                            info.setTitle("Information Dialog");
                            info.setHeaderText("Information Dialog For Successful Removal");
                            info.setContentText("Clerk Was Successfully removed");
                            info.showAndWait();
                        }
                    }catch (ClassNotFoundException ex) {
                        Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
                    }catch (SQLException ex) {
                        Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });

                Stage editClerk = new Stage();
                editClerk.setX(primaryStage.getX() + 250);
                editClerk.setX(primaryStage.getY() + 100);
                editClerk.setScene(editClerkScene);

                Image sceneClerkIcon = new Image(getClass().getResourceAsStream("box.png"));
                editClerk.getIcons().add(sceneClerkIcon);
                editClerk.show();
            } else {
                //code logic here.
            }

        });

        
        //********************************* scene8 ***************************
        BorderPane bp8 = new BorderPane();
        Scene scene8 = new Scene(bp8, 800, 600);
        scene8.getStylesheets().add("pham.css");

        Label searchPresc = new Label("Search By Email Or Phone Number:");
        searchPresc.setId("label");

        TextField searchPrescTF = new TextField();
        searchPrescTF.setPrefWidth(200);
        searchPrescTF.setPrefHeight(20);

        Button searchPrescB = new Button("search");
        searchPrescB.setId("button");

        FlowPane scene8FP = new FlowPane();
        scene8FP.setHgap(5);
        scene8FP.setPadding(new Insets(10, 0, 10, 5));
        scene8FP.getChildren().addAll(searchPresc, searchPrescTF, searchPrescB);
        bp8.setTop(scene8FP);

        //grid for bp8
        GridPane gridPresc1 = new GridPane();
        bp8.setCenter(gridPresc1);
        gridPresc1.setHgap(5);
        gridPresc1.setAlignment(Pos.CENTER);

        //adding gridpane controls
        Text addPrescT1 = new Text("Add Prescription");
        addPrescT1.setFill(Color.TOMATO);
        addPrescT1.setFont(Font.font("TAHOMA", FontWeight.BOLD, 20));
        gridPresc1.add(addPrescT1, 0, 0, 2, 1);

        Label unitsPrescL1 = new Label("Units Bought:");
        unitsPrescL1.setId("label");
        gridPresc1.add(unitsPrescL1, 0, 1);

        TextField unitsPrescTF1 = new TextField();

        unitsPrescTF1.setPrefWidth(200);
        unitsPrescTF1.setPrefHeight(20);
        gridPresc1.add(unitsPrescTF1, 1, 1);

        Label pricePrescL1 = new Label("Price Of Prescription:");
        pricePrescL1.setId("label");
        gridPresc1.add(pricePrescL1, 2, 1);

        TextField pricePrescTF1 = new TextField();

        pricePrescTF1.setPrefHeight(20);
        pricePrescTF1.setPromptText("Enter Price");
        gridPresc1.add(pricePrescTF1, 3, 1);

        Label selectPrescP1 = new Label("Enter Drug:");
        selectPrescP1.setId("label");
        gridPresc1.add(selectPrescP1, 0, 2);

        ChoiceBox<String> cbprescP1 = new ChoiceBox<String>(FXCollections.observableArrayList(drugs));
        cbprescP1.setPrefWidth(200);
        cbprescP1.setPrefHeight(20);
        gridPresc1.add(cbprescP1, 1, 2);

        Label selectedPrescP1 = new Label("Selected Drug:");
        selectedPrescP1.setId("label");
        gridPresc1.add(selectedPrescP1, 2, 2);

        TextField selectedPrescPTF1 = new TextField();

        selectedPrescPTF1.setPrefHeight(20);
        selectedPrescPTF1.setPrefWidth(200);
        gridPresc1.add(selectedPrescPTF1, 3, 2);

        Label prescDateL1 = new Label("Date Of Prescription:");
        prescDateL1.setId("label");
        gridPresc1.add(prescDateL1, 0, 4);

        DatePicker prescDateF1 = new DatePicker();
        prescDateF1.setPrefWidth(200);
        prescDateF1.setPrefHeight(20);
        gridPresc1.add(prescDateF1, 1, 4);

        Label prescEmail = new Label("Patient Email:");
        prescEmail.setId("label");
        gridPresc1.add(prescEmail, 2, 4);

        TextField prescEmailTF = new TextField();
        prescEmailTF.setEditable(false);
        prescEmailTF.setPrefWidth(200);
        prescEmailTF.setPrefHeight(20);
        gridPresc1.add(prescEmailTF, 3, 4);

        cbprescP1.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue ov, Number old_val, Number new_val) {
                String drugs_given1 = drugs[new_val.intValue()];
                selectedPrescPTF1.setText(drugs_given1);
            }
        });

        searchPrescB.setOnAction((ActionEvent y) -> {
            try {
                Class.forName(jdbc);
                conn = DriverManager.getConnection(url + db_name, db_user, db_pass);
                String searchPrescV = searchPrescTF.getText();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM prescription WHERE patient_email='" + searchPrescV + "' OR patient_pnumber='" + searchPrescV + "'");
                rs = ps.executeQuery();
                int count = 0;
                while (rs.next()) {
                    count = count + 1;

                    String patient_emailQ = rs.getString("patient_email");
                    String patient_pnumberQ = rs.getString("patient_pnumber");
                    String unitsQ = rs.getString("units");
                    String priceQ = rs.getString("price");
                    String drugQ = rs.getString("drug");
                    String dateQ = rs.getString("date_recorded");

                    selectedPrescPTF1.setText(drugQ);
                    pricePrescTF1.setText(priceQ);
                    unitsPrescTF1.setText(unitsQ);
                    prescEmailTF.setText(patient_emailQ);
                    ((TextField) prescDateF1.getEditor()).setText(dateQ);
                }
                if (count == 0) {
                    Alert err = new Alert(AlertType.ERROR);
                    err.setTitle("Error Dialog");
                    err.setHeaderText("Error Dialog For Empty Query");
                    err.setContentText("Errors Occured To Show Empty Values Returned.Please Contact The Administrator");
                    err.showAndWait();
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            }

        });

        Button savePrescB1 = new Button("save");
        Button backFromScene8 = new Button("back");
        backFromScene8.setId("button");

        savePrescB1.setId("button");
        HBox prescHB = new HBox(10);
        prescHB.setAlignment(Pos.BOTTOM_RIGHT);
        prescHB.getChildren().addAll(savePrescB1, backFromScene8);
        gridPresc1.add(prescHB, 2, 5);

        savePrescB1.setOnAction((ActionEvent t) -> {
            try {
                Class.forName(jdbc);
                conn = DriverManager.getConnection(url + db_name, db_user, db_pass);
                String unitsP1 = unitsPrescTF1.getText();
                String priceP1 = pricePrescTF1.getText();
                String drugP1 = selectedPrescPTF1.getText();
                String dateP1 = ((TextField) prescDateF1.getEditor()).getText();
                if (unitsPrescTF1.getText().equals("") || pricePrescTF1.getText().equals("") || selectedPrescPTF1.getText().equals("") || ((TextField) prescDateF1.getEditor()).getText().equals("")) {
                    Alert warning = new Alert(AlertType.WARNING);
                    warning.setTitle("Warning Dialog");
                    warning.setHeaderText("Warning Alert For Empty Fields");
                    warning.setContentText("Please Do Not Leave Any Of The Fields Empty!");
                    warning.showAndWait();
                } else {
                    if (unitsP1.matches("^[-+]?\\d+(\\.\\d+)?$") == false) {
                        Alert warning = new Alert(AlertType.WARNING);
                        warning.setTitle("Warning Dialog");
                        warning.setHeaderText("Warning Alert For Numerical Fields");
                        warning.setContentText("Please Enter units in numbers");
                        warning.showAndWait();
                    } else {
                        if (priceP1.matches("^[-+]?\\d+(\\.\\d+)?$") == false) {
                            Alert warning = new Alert(AlertType.WARNING);
                            warning.setTitle("Warning Dialog");
                            warning.setHeaderText("Warning Alert For Numerical Fields");
                            warning.setContentText("Please Enter price in numbers");
                            warning.showAndWait();
                        } else {
                            String email_p = prescEmailTF.getText();

                            PreparedStatement ps = conn.prepareStatement("UPDATE prescription SET units='" + unitsP1 + "',price='" + priceP1 + "',drug='" + drugP1 + "',date_recorded='" + dateP1 + "' WHERE patient_email='" + email_p + "'");
                            int upd = ps.executeUpdate();
                            if (upd > 0) {
                                Alert info = new Alert(AlertType.INFORMATION);
                                info.setTitle("Information Dialog.");
                                info.setHeaderText("Information Dialog For Successful changes made.");
                                info.setContentText("Changes Made Successfully!");
                                info.showAndWait();
                                
                                
                                //updating drug units
                                PreparedStatement psu = conn.prepareStatement("SELECT * FROM drugs WHERE drug_name='" + drugP1 + "'");
                                        rs = psu.executeQuery();
                                        while (rs.next()) {
                                            String total_drug_units = rs.getString("units");

                                            int I_drug_units = Integer.parseInt(total_drug_units);
                                            int E_drug_units = Integer.parseInt(unitsP1);

                                            int drug_units_update = I_drug_units - E_drug_units;

                                            PreparedStatement psz = conn.prepareStatement("UPDATE drugs SET units='" + drug_units_update + "' WHERE drug_name='"+drugP1+"'");
                                            int drug_update = psz.executeUpdate();
                                            if (drug_update > 0) {
                                                Alert infox = new Alert(AlertType.INFORMATION);
                                                infox.setTitle("Information Dialog.");
                                                infox.setHeaderText("Information Dialog For Successful Drug Unit Update.");
                                                infox.setContentText("Drug Update Captured Successfully!");
                                                infox.showAndWait();
                                            } else if (drug_update == 0) {
                                                Alert err = new Alert(AlertType.ERROR);
                                                err.setTitle("Error Dialog");
                                                err.setHeaderText("Error Dialog For Failure In Making A Drug Units Update");
                                                err.setContentText("Errors Occured While Making Drug Unit Update.Please Contact The Administrator");
                                                err.showAndWait();
                                            }
                                        }
                            } else {
                                Alert err = new Alert(AlertType.ERROR);
                                err.setTitle("Error Dialog");
                                err.setHeaderText("Error Dialog For Failure In Making Changes");
                                err.setContentText("Errors Occured While Making Changes.Please Contact The Administrator");
                                err.showAndWait();
                            }
                        }
                    }

                }

            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            }

        });

        //********************* scene for recording prescription ************
        BorderPane bp7 = new BorderPane();
        Scene scene7 = new Scene(bp7, 1000, 600);
        scene7.getStylesheets().add("pham.css");

        Label searchBinaryL = new Label("Search By Phone Or Email:");
        searchBinaryL.setId("label");

        TextField searchBinaryTF = new TextField();
        searchBinaryTF.setPrefWidth(200);
        searchBinaryTF.setPrefHeight(20);

        Button searchBinaryB = new Button("search");
        searchBinaryB.setId("button");

        FlowPane scene7FP = new FlowPane();
        scene7FP.setPadding(new Insets(10, 0, 10, 5));
        scene7FP.setHgap(5);
        scene7FP.getChildren().addAll(searchBinaryL, searchBinaryTF, searchBinaryB);
        bp7.setTop(scene7FP);

        //action for searchBinaryB
        searchBinaryB.setOnAction((ActionEvent t) -> {
            table_add_presc.getItems().clear();
            try {
                Class.forName(jdbc);
                conn = DriverManager.getConnection(url + db_name, db_user, db_pass);
                String binaryV = searchBinaryTF.getText();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM patients WHERE email='" + binaryV + "' OR pnumber='" + binaryV + "'");
                rs = ps.executeQuery();
                int status = 0;
                while (rs.next()) {
                    status = status + 1;
                    table_add_presc_data.add(new Presc(rs.getString("patient_id"), rs.getString("fname"), rs.getString("sname"), rs.getString("tname"), rs.getString("pnumber"), rs.getString("email"), rs.getString("address")));
                }
                if (status == 0) {
                    Alert err = new Alert(AlertType.ERROR);
                    err.setTitle("Error Dialog");
                    err.setHeaderText("Error Dialog In Getting Details");
                    err.setContentText("No Details Detected.Please Contact The Administrator");
                    err.showAndWait();

                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        //table to display patients then add their prescriptions
        TableColumn patient_idP = new TableColumn("patient_id");
        patient_idP.setMinWidth(120);
        patient_idP.setCellValueFactory(new PropertyValueFactory<>("Patient_id"));

        TableColumn fnameP = new TableColumn("first_name");
        fnameP.setMinWidth(120);
        fnameP.setCellValueFactory(new PropertyValueFactory<>("First_name"));

        TableColumn snameP = new TableColumn("second_name");
        snameP.setMinWidth(120);
        snameP.setCellValueFactory(new PropertyValueFactory<>("Second_name"));

        TableColumn tnameP = new TableColumn("third_name");
        tnameP.setMinWidth(120);
        tnameP.setCellValueFactory(new PropertyValueFactory<>("Third_name"));

        TableColumn pnumberP = new TableColumn("phone_number");
        pnumberP.setMinWidth(120);
        pnumberP.setCellValueFactory(new PropertyValueFactory<>("Phone_number"));

        TableColumn emailP = new TableColumn("email");
        emailP.setMinWidth(120);
        emailP.setCellValueFactory(new PropertyValueFactory<>("Email"));

        TableColumn addressP = new TableColumn("address");
        addressP.setMinWidth(120);
        addressP.setCellValueFactory(new PropertyValueFactory<>("Address"));
        bp7.setCenter(table_add_presc);
        table_add_presc.getColumns().addAll(patient_idP, fnameP, snameP, tnameP, pnumberP, emailP, addressP);
        table_add_presc.setItems(table_add_presc_data);

        Button syncScene7B = new Button("sync");
        syncScene7B.setId("button");

        Button backFromScene7 = new Button("back");
        backFromScene7.setId("button");

        HBox scene7HB = new HBox(10);
        scene7HB.setAlignment(Pos.BOTTOM_LEFT);
        scene7HB.setPadding(new Insets(10, 0, 10, 10));
        scene7HB.getChildren().addAll(syncScene7B, backFromScene7);
        bp7.setBottom(scene7HB);

        syncScene7B.setOnAction((ActionEvent t) -> {
            table_add_presc.getItems().clear();
            try {
                Class.forName(jdbc);
                conn = DriverManager.getConnection(url + db_name, db_user, db_pass);
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM patients");
                rs = ps.executeQuery();
                int status = 0;
                while (rs.next()) {
                    status = status + 1;
                    table_add_presc_data.add(new Presc(rs.getString("patient_id"), rs.getString("fname"), rs.getString("sname"), rs.getString("tname"), rs.getString("pnumber"), rs.getString("email"), rs.getString("address")));
                }
                if (status == 0) {
                    Alert err = new Alert(AlertType.ERROR);
                    err.setTitle("Error Dialog");
                    err.setHeaderText("Error Dialog In Getting Details");
                    err.setContentText("No Details Detected.Please Contact The Administrator");
                    err.showAndWait();

                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            }

        });

        table_add_presc.setOnMouseClicked((javafx.scene.input.MouseEvent e) -> {

            Presc presc = (Presc) table_add_presc.getSelectionModel().getSelectedItem();

            String patient_email = presc.getEmail();
            String patient_number = presc.getPhone_number();

            Alert confirm = new Alert(AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation Dialog");
            confirm.setHeaderText("Confirmation Dialog For Prescription");
            confirm.setContentText("Do You Want to Add Prescription For This Patient?");
            confirm.showAndWait();

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.get() == ButtonType.OK) {

                GridPane gridPresc = new GridPane();
                Scene prescene = new Scene(gridPresc, 800, 400);
                prescene.getStylesheets().add("pham.css");

                gridPresc.setHgap(5);
                gridPresc.setAlignment(Pos.CENTER);

                //adding gridpane controls
                Text addPrescT = new Text("Add Prescription");
                addPrescT.setFill(Color.TOMATO);
                addPrescT.setFont(Font.font("TAHOMA", FontWeight.BOLD, 20));
                gridPresc.add(addPrescT, 0, 0, 2, 1);

                Label unitsPrescL = new Label("Units Bought:");
                unitsPrescL.setId("label");
                gridPresc.add(unitsPrescL, 0, 1);

                TextField unitsPrescTF = new TextField();
                unitsPrescTF.setPrefWidth(200);
                unitsPrescTF.setPrefHeight(20);
                gridPresc.add(unitsPrescTF, 1, 1);

                Label pricePrescL = new Label("Price Of Prescription:");
                pricePrescL.setId("label");
                gridPresc.add(pricePrescL, 2, 1);

                TextField pricePrescTF = new TextField();
                pricePrescTF.setPrefWidth(200);
                pricePrescTF.setPrefHeight(20);
                pricePrescTF.setPromptText("Enter Price");
                gridPresc.add(pricePrescTF, 3, 1);

                Label selectPrescP = new Label("Enter Drug:");
                selectPrescP.setId("label");
                gridPresc.add(selectPrescP, 0, 2);

                ChoiceBox<String> cbprescP = new ChoiceBox<String>(FXCollections.observableArrayList(drugs));
                cbprescP.setPrefWidth(200);
                cbprescP.setPrefHeight(20);
                gridPresc.add(cbprescP, 1, 2);

                Label selectedPrescP = new Label("Selected Drug:");
                selectedPrescP.setId("label");
                gridPresc.add(selectedPrescP, 2, 2);

                TextField selectedPrescPTF = new TextField();
                selectedPrescPTF.setPrefHeight(20);
                selectedPrescPTF.setPrefWidth(200);
                gridPresc.add(selectedPrescPTF, 3, 2);

                Label prescDateL = new Label("Date Of Prescription:");
                prescDateL.setId("label");
                gridPresc.add(prescDateL, 0, 4);

                DatePicker prescDateF = new DatePicker();
                prescDateF.setPrefWidth(200);
                prescDateF.setPrefHeight(20);
                gridPresc.add(prescDateF, 1, 4);

                cbprescP.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue ov, Number old_val, Number new_val) {
                        String drugs_given = drugs[new_val.intValue()];
                        selectedPrescPTF.setText(drugs_given);
                    }
                });

                Button savePrescB = new Button("save");
                savePrescB.setId("button");
                gridPresc.add(savePrescB, 2, 5);

                savePrescB.setOnAction((ActionEvent t) -> {
                    try {
                        Class.forName(jdbc);
                        conn = DriverManager.getConnection(url + db_name, db_user, db_pass);
                        String unitsP = unitsPrescTF.getText();
                        String priceP = pricePrescTF.getText();
                        String drugP = selectedPrescPTF.getText();
                        String dateP = ((TextField) prescDateF.getEditor()).getText();
                        if (unitsPrescTF.getText().equals("") || pricePrescTF.getText().equals("") || selectedPrescPTF.getText().equals("") || ((TextField) prescDateF.getEditor()).getText().equals("")) {

                            Alert warning = new Alert(AlertType.WARNING);
                            warning.setTitle("Warning Dialog");
                            warning.setHeaderText("Warning Alert For Empty Fields");
                            warning.setContentText("Please Do Not Leave Any Of The Fields Empty!");
                            warning.showAndWait();
                        } else {
                            if (unitsP.matches("^[-+]?\\d+(\\.\\d+)?$") == false) {
                                Alert warning = new Alert(AlertType.WARNING);
                                warning.setTitle("Warning Dialog");
                                warning.setHeaderText("Warning Alert For Numerical Fields");
                                warning.setContentText("Please Enter units in numbers");
                                warning.showAndWait();
                            } else {
                                if (priceP.matches("^[-+]?\\d+(\\.\\d+)?$") == false) {
                                    Alert warning = new Alert(AlertType.WARNING);
                                    warning.setTitle("Warning Dialog");
                                    warning.setHeaderText("Warning Alert For Numerical Fields");
                                    warning.setContentText("Please Enter price in numbers");
                                    warning.showAndWait();
                                } else {
                                    PreparedStatement ps = conn.prepareStatement("INSERT INTO prescription(patient_email,patient_pnumber,units,price,drug,date_recorded)VALUES(?,?,?,?,?,?)");
                                    ps.setString(1, patient_email);
                                    ps.setString(2, patient_number);
                                    ps.setString(3, unitsP);
                                    ps.setString(4, priceP);
                                    ps.setString(5, drugP);
                                    ps.setString(6, dateP);

                                    int status = ps.executeUpdate();
                                    if (status > 0) {
                                        Alert info = new Alert(AlertType.INFORMATION);
                                        info.setTitle("Information Dialog.");
                                        info.setHeaderText("Information Dialog For Successful Records Made.");
                                        info.setContentText("Record Made Successfully!");
                                        info.showAndWait();

                                        unitsPrescTF.clear();
                                        pricePrescTF.clear();
                                        selectedPrescPTF.clear();

                                        PreparedStatement psu = conn.prepareStatement("SELECT * FROM drugs WHERE drug_name='" + drugP + "'");
                                        rs = psu.executeQuery();
                                        while (rs.next()) {
                                            String total_drug_units = rs.getString("units");

                                            int I_drug_units = Integer.parseInt(total_drug_units);
                                            int E_drug_units = Integer.parseInt(unitsP);

                                            int drug_units_update = I_drug_units - E_drug_units;

                                            PreparedStatement psz = conn.prepareStatement("UPDATE drugs SET units='" + drug_units_update + "' WHERE drug_name='"+drugP+"'");
                                            int drug_update = psz.executeUpdate();
                                            if (drug_update > 0) {
                                                Alert infox = new Alert(AlertType.INFORMATION);
                                                infox.setTitle("Information Dialog.");
                                                infox.setHeaderText("Information Dialog For Successful Drug Unit Update.");
                                                infox.setContentText("Drug Update Captured Successfully!");
                                                infox.showAndWait();
                                            } else if (drug_update == 0) {
                                                Alert err = new Alert(AlertType.ERROR);
                                                err.setTitle("Error Dialog");
                                                err.setHeaderText("Error Dialog For Failure In Making A Drug Units Update");
                                                err.setContentText("Errors Occured While Making Drug Unit Update.Please Contact The Administrator");
                                                err.showAndWait();
                                            }
                                        }
                                    } else if (status == 0) {
                                        Alert err = new Alert(AlertType.ERROR);
                                        err.setTitle("Error Dialog");
                                        err.setHeaderText("Error Dialog For Failure In Making A Recrd");
                                        err.setContentText("Errors Occured While Making The Record.Please Contact The Administrator");
                                        err.showAndWait();
                                    }

                                }
                            }
                        }

                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SQLException ex) {
                        Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });

                Stage add_PrescS = new Stage();
                add_PrescS.setScene(prescene);
                add_PrescS.show();
                add_PrescS.setX(primaryStage.getX() + 250);
                add_PrescS.setX(primaryStage.getY() + 100);

                Image scene5Icon = new Image(getClass().getResourceAsStream("star-128.png"));
                add_PrescS.getIcons().add(scene5Icon);
            }

        });

        //**************** create edit patients screen ***********************
        BorderPane bp5 = new BorderPane();
        Scene scene5 = new Scene(bp5, 1000, 600);
        scene5.getStylesheets().add("pham.css");

        Label searchByDetailsL = new Label("Search By Email Or Phone:");
        searchByDetailsL.setId("label");

        TextField searchByDetailsTF = new TextField();
        searchByDetailsTF.setPrefWidth(200);
        searchByDetailsTF.setPrefHeight(20);

        Button searchDetailsB = new Button("search");
        searchDetailsB.setId("button");

        FlowPane scene5FP = new FlowPane();
        scene5FP.setHgap(5);
        scene5FP.setPadding(new Insets(10, 0, 10, 5));
        scene5FP.getChildren().addAll(searchByDetailsL, searchByDetailsTF, searchDetailsB);
        bp5.setTop(scene5FP);

        //search details button
        searchDetailsB.setOnAction((ActionEvent t) -> {
            table_patients.getItems().clear();
            try {
                Class.forName(jdbc);
                conn = DriverManager.getConnection(url + db_name, db_user, db_pass);
                String uniqueV = searchByDetailsTF.getText();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM patients WHERE pnumber='" + uniqueV + "' OR email='" + uniqueV + "'");
                rs = ps.executeQuery();
                while (rs.next()) {
                    table_patients_data.add(new Patient(rs.getString("patient_id"), rs.getString("fname"), rs.getString("sname"), rs.getString("tname"), rs.getString("pnumber"), rs.getString("email"), rs.getString("address")));
                }

            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        //sync button
        Button syncPatientBP5 = new Button("sync");
        syncPatientBP5.setId("button");
        syncPatientBP5.setOnAction((ActionEvent t) -> {
            table_patients.getItems().clear();
            try {
                Class.forName(jdbc);
                conn = DriverManager.getConnection(url + db_name, db_user, db_pass);
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM patients");
                rs = ps.executeQuery();
                while (rs.next()) {
                    table_patients_data.add(new Patient(rs.getString("patient_id"), rs.getString("fname"), rs.getString("sname"), rs.getString("tname"), rs.getString("pnumber"), rs.getString("email"), rs.getString("address")));
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        Button backFromScene5 = new Button("back");
        backFromScene5.setId("button");

        FlowPane sceneBelow5FP = new FlowPane();
        sceneBelow5FP.setHgap(5);
        sceneBelow5FP.setPadding(new Insets(10, 0, 10, 5));
        sceneBelow5FP.getChildren().addAll(syncPatientBP5, backFromScene5);
        bp5.setBottom(sceneBelow5FP);

        //table with patients details
        TableColumn patient_idC = new TableColumn("patient_id");
        patient_idC.setMinWidth(120);
        patient_idC.setCellValueFactory(new PropertyValueFactory<>("Patient_id"));

        TableColumn fnameC = new TableColumn("first_name");
        fnameC.setMinWidth(120);
        fnameC.setCellValueFactory(new PropertyValueFactory<>("First_name"));

        TableColumn snameC = new TableColumn("second_name");
        snameC.setMinWidth(120);
        snameC.setCellValueFactory(new PropertyValueFactory<>("Second_name"));

        TableColumn tnameC = new TableColumn("third_name");
        tnameC.setMinWidth(120);
        tnameC.setCellValueFactory(new PropertyValueFactory<>("Third_name"));

        TableColumn pnumberC = new TableColumn("phone_number");
        pnumberC.setMinWidth(120);
        pnumberC.setCellValueFactory(new PropertyValueFactory<>("Phone_number"));

        TableColumn emailC = new TableColumn("email");
        emailC.setMinWidth(120);
        emailC.setCellValueFactory(new PropertyValueFactory<>("Email"));

        TableColumn addressC = new TableColumn("address");
        addressC.setMinWidth(120);
        addressC.setCellValueFactory(new PropertyValueFactory<>("Address"));
        bp5.setCenter(table_patients);
        table_patients.getColumns().addAll(patient_idC, fnameC, snameC, tnameC, pnumberC, emailC, addressC);
        table_patients.setItems(table_patients_data);

        table_patients.setOnMouseClicked((javafx.scene.input.MouseEvent e) -> {
            Patient patient = (Patient) table_patients.getSelectionModel().getSelectedItem();
            String patient_idx = patient.getPatient_id();
            String fnamex = patient.getFirst_name();
            String snamex = patient.getSecond_name();
            String tnamex = patient.getThird_name();
            String pnumberx = patient.getPhone_number();
            String emailx = patient.getEmail();
            String addressx = patient.getAddress();

            //Editing Patient Details
            GridPane addPatientGrid1 = new GridPane();
            Scene scene6 = new Scene(addPatientGrid1, 1100, 500);
            scene6.getStylesheets().add("pham.css");

            //grid details
            addPatientGrid1.setHgap(5);
            addPatientGrid1.setAlignment(Pos.CENTER);

            //create fields and controls for the grid       
            Text addPatientText1 = new Text("Edit Patient Details");
            addPatientText1.setFill(Color.TOMATO);
            addPatientText1.setFont(Font.font("TAHOMA", FontWeight.BOLD, 20));
            addPatientGrid1.add(addPatientText1, 0, 0, 2, 1);

            Label firstNameL1 = new Label("First Name:");
            firstNameL1.setId("label");
            addPatientGrid1.add(firstNameL1, 0, 1);

            TextField firstNameTF1 = new TextField();
            firstNameTF1.setText(fnamex);
            firstNameTF1.setPrefWidth(200);
            firstNameTF1.setPrefHeight(20);
            addPatientGrid1.add(firstNameTF1, 1, 1);

            Label secondNameL1 = new Label("Second Name:");
            secondNameL1.setId("label");
            addPatientGrid1.add(secondNameL1, 2, 1);

            TextField secondNameTF1 = new TextField();
            secondNameTF1.setText(snamex);
            secondNameTF1.setPrefWidth(200);
            secondNameTF1.setPrefHeight(20);
            addPatientGrid1.add(secondNameTF1, 3, 1);

            Label thirdNameL1 = new Label("Third Name");
            thirdNameL1.setId("label");
            addPatientGrid1.add(thirdNameL1, 4, 1);

            TextField thirdNameTF1 = new TextField();
            thirdNameTF1.setText(tnamex);
            thirdNameTF1.setPrefWidth(200);
            thirdNameTF1.setPrefHeight(20);
            addPatientGrid1.add(thirdNameTF1, 5, 1);

            Label pnumberL1 = new Label("Phone Number:");
            pnumberL1.setId("label");
            addPatientGrid1.add(pnumberL1, 0, 2);

            TextField pnumberTF1 = new TextField();
            pnumberTF1.setText(pnumberx);
            pnumberTF1.setPrefWidth(200);
            pnumberTF1.setPrefHeight(20);
            addPatientGrid1.add(pnumberTF1, 1, 2);

            Label emailL1 = new Label("Email:");
            emailL1.setId("label");
            addPatientGrid1.add(emailL1, 2, 2);

            TextField emailTF1 = new TextField();
            emailTF1.setText(emailx);
            emailTF1.setPrefWidth(200);
            emailTF1.setPrefHeight(20);
            addPatientGrid1.add(emailTF1, 3, 2);

            Label addressL1 = new Label("Address:");
            addressL1.setId("label");
            addPatientGrid1.add(addressL1, 4, 2);

            TextField addressTF1 = new TextField();
            addressTF1.setText(addressx);
            addressTF1.setPrefWidth(200);
            addressTF1.setPrefHeight(20);
            addPatientGrid1.add(addressTF1, 5, 2);

            //buttons to be added
            Button saveB1 = new Button("Save");
            saveB1.setId("button");

            Button deletePatientB = new Button("Remove");
            deletePatientB.setId("button");

            //HBox for buttons
            HBox scene4HB1 = new HBox(10);
            scene4HB1.setAlignment(Pos.BOTTOM_RIGHT);
            scene4HB1.getChildren().addAll(saveB1, deletePatientB);
            addPatientGrid1.add(scene4HB1, 5, 4);

            saveB1.setOnAction((ActionEvent t) -> {
                if (firstNameTF1.getText().equals("") || secondNameTF1.getText().equals("") || thirdNameTF1.getText().equals("") || pnumberTF1.getText().equals("") || emailTF1.getText().equals("") || addressTF1.getText().equals("")) {
                    Alert warning = new Alert(AlertType.WARNING);
                    warning.setTitle("Warning Dialog");
                    warning.setHeaderText("Warning Alert For Empty Fields");
                    warning.setContentText("Please Do Not Leave Any Of The Fields Empty!");
                    warning.showAndWait();
                } else {
                    String fname1 = firstNameTF1.getText();
                    String sname1 = secondNameTF1.getText();
                    String tname1 = thirdNameTF1.getText();
                    String pnumber1 = pnumberTF1.getText();
                    String email1 = emailTF1.getText();
                    String address1 = addressTF1.getText();

                    try {
                        Class.forName(jdbc);
                        conn = DriverManager.getConnection(url + db_name, db_user, db_pass);
                        PreparedStatement psc = conn.prepareStatement("SELECT * FROM patients WHERE pnumber='" + pnumber1 + "'");
                        rs = psc.executeQuery();
                        int count1 = 0;
                        while (rs.next()) {
                            count1 = count1 + 1;
                        }
                        if (count1 > 1) {
                            Alert warning = new Alert(AlertType.WARNING);
                            warning.setTitle("Warning Dialog");
                            warning.setHeaderText("Warning Alert For Duplicate Records");
                            warning.setContentText("That phone number  exists in the system!");
                            warning.showAndWait();
                        } else if (count1 == 0 || count1 == 1) {
                            PreparedStatement pse = conn.prepareStatement("SELECT * FROM patients WHERE email='" + email1 + "'");
                            rs = pse.executeQuery();
                            int emailCount1 = 0;
                            while (rs.next()) {
                                emailCount1 = emailCount1 + 1;
                            }
                            if (emailCount1 > 1) {
                                Alert warning = new Alert(AlertType.WARNING);
                                warning.setTitle("Warning Dialog");
                                warning.setHeaderText("Warning Alert For Duplicate Records");
                                warning.setContentText("That email already exists in the system");
                                warning.showAndWait();
                            } else if (emailCount1 == 0 || emailCount1 == 1) {
                                PreparedStatement psi = conn.prepareStatement("UPDATE patients SET fname='" + fname1 + "',sname='" + sname1 + "',tname='" + tname1 + "',pnumber='" + pnumber1 + "',email='" + email1 + "',address='" + address1 + "' WHERE patient_id='" + patient_idx + "'");

                                int insert_pdetails1 = psi.executeUpdate();
                                if (insert_pdetails1 > 0) {
                                    Alert info = new Alert(AlertType.INFORMATION);
                                    info.setTitle("Information Dialog.");
                                    info.setHeaderText("Information Dialog For Successful record update.");
                                    info.setContentText("Record Update Made Successfully!");
                                    info.showAndWait();

                                    firstNameTF1.clear();
                                    secondNameTF1.clear();
                                    thirdNameTF1.clear();
                                    pnumberTF1.clear();
                                    emailTF1.clear();
                                    addressTF1.clear();
                                } else if (insert_pdetails1 == 0) {
                                    Alert err = new Alert(AlertType.ERROR);
                                    err.setTitle("Error Dialog");
                                    err.setHeaderText("Error Dialog For Failure In Record Updation");
                                    err.setContentText("Errors Occured While Updating The Records.Please Contact The Administrator");
                                    err.showAndWait();
                                }
                            }
                        }
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SQLException ex) {
                        Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            Stage editPatientS = new Stage();
            editPatientS.show();
            editPatientS.setX(primaryStage.getX() + 250);
            editPatientS.setX(primaryStage.getY() + 100);
            editPatientS.setScene(scene6);

            Image editPatientIcon = new Image(getClass().getResourceAsStream("star-128.png"));
            editPatientS.getIcons().add(editPatientIcon);

            deletePatientB.setOnAction((ActionEvent x) -> {
                try {
                    Class.forName(jdbc);
                    conn = DriverManager.getConnection(url + db_name, db_user, db_pass);
                    PreparedStatement psz = conn.prepareStatement("DELETE FROM patients WHERE patient_id='" + patient_idx + "'");
                    int deleted = psz.executeUpdate();
                    if (deleted > 0) {
                        Alert info = new Alert(AlertType.INFORMATION);
                        info.setTitle("Information Dialog.");
                        info.setHeaderText("Information Dialog For Successful Removal.");
                        info.setContentText("Patient Removed Successfully!");
                        info.showAndWait();
                    } else if (deleted == 0) {
                        Alert err = new Alert(AlertType.ERROR);
                        err.setTitle("Error Dialog");
                        err.setHeaderText("Error Dialog For Failure In Making Changes");
                        err.setContentText("Errors Occured While Deleting Patient.Please Contact The Administrator");
                        err.showAndWait();

                    }
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
                }

            });

        });

        //**************************** create screen for entering patients details **********************
        GridPane addPatientGrid = new GridPane();
        Scene scene4 = new Scene(addPatientGrid, 1100, 500);
        scene4.getStylesheets().add("pham.css");

        //grid details
        addPatientGrid.setHgap(5);
        addPatientGrid.setAlignment(Pos.CENTER);
        //create fields and controls for the grid       
        Text addPatientText = new Text("Add Patient Details");
        addPatientText.setFill(Color.TOMATO);
        addPatientText.setFont(Font.font("TAHOMA", FontWeight.BOLD, 20));
        addPatientGrid.add(addPatientText, 0, 0, 2, 1);

        Label firstNameL = new Label("First Name:");
        firstNameL.setId("label");
        addPatientGrid.add(firstNameL, 0, 1);

        TextField firstNameTF = new TextField();
        firstNameTF.setPrefWidth(200);
        firstNameTF.setPrefHeight(20);
        addPatientGrid.add(firstNameTF, 1, 1);

        Label secondNameL = new Label("Second Name:");
        secondNameL.setId("label");
        addPatientGrid.add(secondNameL, 2, 1);

        TextField secondNameTF = new TextField();
        secondNameTF.setPrefWidth(200);
        secondNameTF.setPrefHeight(20);
        addPatientGrid.add(secondNameTF, 3, 1);

        Label thirdNameL = new Label("Third Name");
        thirdNameL.setId("label");
        addPatientGrid.add(thirdNameL, 4, 1);

        TextField thirdNameTF = new TextField();
        thirdNameTF.setPrefWidth(200);
        thirdNameTF.setPrefHeight(20);
        addPatientGrid.add(thirdNameTF, 5, 1);

        Label pnumberL = new Label("Phone Number:");
        pnumberL.setId("label");
        addPatientGrid.add(pnumberL, 0, 2);

        TextField pnumberTF = new TextField();
        pnumberTF.setPrefWidth(200);
        pnumberTF.setPrefHeight(20);
        addPatientGrid.add(pnumberTF, 1, 2);

        Label emailL = new Label("Email:");
        emailL.setId("label");
        addPatientGrid.add(emailL, 2, 2);

        TextField emailTF = new TextField();
        emailTF.setPrefWidth(200);
        emailTF.setPrefHeight(20);
        addPatientGrid.add(emailTF, 3, 2);

        Label addressL = new Label("Address:");
        addressL.setId("label");
        addPatientGrid.add(addressL, 4, 2);

        TextField addressTF = new TextField();
        addressTF.setPrefWidth(200);
        addressTF.setPrefHeight(20);
        addPatientGrid.add(addressTF, 5, 2);

        //Buttons for add patient screen
        Button saveB = new Button("Save");
        saveB.setId("button");

        Button backFromScene4 = new Button("Back");
        backFromScene4.setId("button");
        //HBox for buttons

        HBox scene4HB = new HBox(10);
        scene4HB.setAlignment(Pos.BOTTOM_RIGHT);
        scene4HB.getChildren().addAll(saveB, backFromScene4);
        addPatientGrid.add(scene4HB, 5, 4);
        //save button action
        saveB.setOnAction((ActionEvent t) -> {
            if (firstNameTF.getText().equals("") || secondNameTF.getText().equals("") || thirdNameTF.getText().equals("") || pnumberTF.getText().equals("") || emailTF.getText().equals("") || addressTF.getText().equals("")) {
                Alert warning = new Alert(AlertType.WARNING);
                warning.setTitle("Warning Dialog");
                warning.setHeaderText("Warning Alert For Empty Fields");
                warning.setContentText("Please Do Not Leave Any Of The Fields Empty!");
                warning.showAndWait();
            } else {
                String fname = firstNameTF.getText();
                String sname = secondNameTF.getText();
                String tname = thirdNameTF.getText();
                String pnumber = pnumberTF.getText();
                String email = emailTF.getText();
                String address = addressTF.getText();

                try {
                    Class.forName(jdbc);
                    conn = DriverManager.getConnection(url + db_name, db_user, db_pass);
                    PreparedStatement psx = conn.prepareStatement("SELECT * FROM patients WHERE pnumber='" + pnumber + "'");
                    rs = psx.executeQuery();
                    int count = 0;
                    while (rs.next()) {
                        count = count + 1;
                    }
                    if (count > 0) {
                        Alert warning = new Alert(AlertType.WARNING);
                        warning.setTitle("Warning Dialog");
                        warning.setHeaderText("Warning Alert For Duplicate Records");
                        warning.setContentText("That phone number  exists in the system!");
                        warning.showAndWait();
                    } else if (count == 0) {
                        PreparedStatement ps = conn.prepareStatement("SELECT * FROM patients WHERE email='" + email + "'");
                        rs = ps.executeQuery();
                        int emailCount = 0;
                        while (rs.next()) {
                            emailCount = emailCount + 1;
                        }
                        if (emailCount > 0) {
                            Alert warning = new Alert(AlertType.WARNING);
                            warning.setTitle("Warning Dialog");
                            warning.setHeaderText("Warning Alert For Duplicate Records");
                            warning.setContentText("That email already exists in the system");
                            warning.showAndWait();
                        } else if (emailCount == 0) {
                            PreparedStatement psy = conn.prepareStatement("INSERT INTO patients(fname,sname,tname,pnumber,email,address)VALUES(?,?,?,?,?,?)");
                            psy.setString(1, fname);
                            psy.setString(2, sname);
                            psy.setString(3, tname);
                            psy.setString(4, pnumber);
                            psy.setString(5, email);
                            psy.setString(6, address);

                            int insert_pdetails = psy.executeUpdate();
                            if (insert_pdetails > 0) {
                                Alert info = new Alert(AlertType.INFORMATION);
                                info.setTitle("Information Dialog.");
                                info.setHeaderText("Information Dialog For Successful record made.");
                                info.setContentText("Record Made Successfully!");
                                info.showAndWait();

                                firstNameTF.clear();
                                secondNameTF.clear();
                                thirdNameTF.clear();
                                pnumberTF.clear();
                                emailTF.clear();
                                addressTF.clear();
                            } else if (insert_pdetails == 0) {
                                Alert err = new Alert(AlertType.ERROR);
                                err.setTitle("Error Dialog");
                                err.setHeaderText("Error Dialog For Failure In Record Making");
                                err.setContentText("Errors Occured While Making The Records.Please Contact The Administrator");
                                err.showAndWait();
                            }
                        }
                    }
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        //*************************************** create drug edit screen ******************************
        BorderPane bp3 = new BorderPane();
        Scene scene3 = new Scene(bp3, 1000, 500);
        scene3.getStylesheets().add("pham.css");

        TableColumn drug_idC = new TableColumn("drug_id");
        drug_idC.setMinWidth(100);
        drug_idC.setCellValueFactory(new PropertyValueFactory<>("Drug_id"));

        TableColumn drug_unitsC = new TableColumn("units");
        drug_unitsC.setMinWidth(100);
        drug_idC.setCellValueFactory(new PropertyValueFactory<>("Units"));

        TableColumn drug_priceC = new TableColumn("price");
        drug_priceC.setMinWidth(100);
        drug_priceC.setCellValueFactory(new PropertyValueFactory<>("Price"));

        TableColumn drug_nameC = new TableColumn("drug_name");
        drug_nameC.setMinWidth(100);
        drug_nameC.setCellValueFactory(new PropertyValueFactory<>("Drug_name"));

        TableColumn supplier_nameC = new TableColumn("supplier_name");
        supplier_nameC.setMinWidth(100);
        supplier_nameC.setCellValueFactory(new PropertyValueFactory<>("Supplier_name"));

        table.getColumns().addAll(drug_idC, drug_unitsC, drug_priceC, drug_nameC, supplier_nameC);
        table.setItems(data);
        bp3.setCenter(table);

        //creating the buttons
        Button syncBP3 = new Button("sync");
        syncBP3.setId("button");

        Button backFromScene3 = new Button("back");
        backFromScene3.setId("button");

        //action onclicking tablecolumn
        table.setOnMouseClicked((javafx.scene.input.MouseEvent e) -> {
            Drug drug = (Drug) table.getSelectionModel().getSelectedItem();
            String drug_ids = drug.getDrug_id();
            String unitsS = drug.getUnits();
            String priceS = drug.getPrice();
            String drug_nameS = drug.getDrug_name();
            String supplier_nameS = drug.getSupplier_name();

            GridPane drugGrid1 = new GridPane();
            Scene drugGridScene1 = new Scene(drugGrid1, 800, 500);
            drugGridScene1.getStylesheets().add("pham.css");
            drugGrid1.setHgap(5);
            drugGrid1.setAlignment(Pos.CENTER);

            Text drugTitle1 = new Text("Add Drugs");
            drugTitle1.setFill(Color.TOMATO);
            drugTitle1.setFont(Font.font("TAHOMA", FontWeight.BOLD, 20));
            drugGrid1.add(drugTitle1, 0, 0, 2, 1);

            Label enter_unitsL1 = new Label("Enter Units:");
            enter_unitsL1.setId("label");
            drugGrid1.add(enter_unitsL1, 0, 1);

            TextField enter_unitsTF1 = new TextField();
            enter_unitsTF1.setText(unitsS);
            enter_unitsTF1.setPrefWidth(200);
            enter_unitsTF1.setPrefHeight(20);
            drugGrid1.add(enter_unitsTF1, 1, 1);

            Label enter_priceL1 = new Label("Enter Price:");
            enter_priceL1.setId("label");
            drugGrid1.add(enter_priceL1, 2, 1);

            TextField enter_priceTF1 = new TextField();
            enter_priceTF1.setText(priceS);
            enter_priceTF1.setPrefWidth(200);
            enter_priceTF1.setPrefHeight(20);
            drugGrid1.add(enter_priceTF1, 3, 1);

            Label choose_drugL1 = new Label("Choose Drug");
            choose_drugL1.setId("label");
            drugGrid1.add(choose_drugL1, 0, 2);

            ChoiceBox<String> cbdrugs1 = new ChoiceBox<String>(FXCollections.observableArrayList(drugs));
            cbdrugs1.setPrefWidth(200);
            cbdrugs1.setPrefHeight(20);
            drugGrid1.add(cbdrugs1, 1, 2);

            Label chosen_drugL1 = new Label("Chosen Drug:");
            chosen_drugL1.setId("label");
            drugGrid1.add(chosen_drugL1, 2, 2);

            TextField chosen_drugTF1 = new TextField();
            chosen_drugTF1.setText(drug_nameS);
            chosen_drugTF1.setEditable(false);
            chosen_drugTF1.setPrefWidth(200);
            chosen_drugTF1.setPrefHeight(20);
            drugGrid1.add(chosen_drugTF1, 3, 2);

            Label choose_supplierL1 = new Label("Choose Supplier:");
            choose_supplierL1.setId("label");
            drugGrid1.add(choose_supplierL1, 0, 3);

            ChoiceBox<String> cbsuppliers1 = new ChoiceBox<String>(FXCollections.observableArrayList(suppliers));
            cbsuppliers1.setPrefWidth(200);
            cbsuppliers1.setPrefHeight(20);
            drugGrid1.add(cbsuppliers1, 1, 3);

            Label chosen_supplierL1 = new Label("Chosen Supplier:");
            chosen_supplierL1.setId("label");
            drugGrid1.add(chosen_supplierL1, 2, 3);

            TextField chosen_supplierTF1 = new TextField();
            chosen_supplierTF1.setText(supplier_nameS);
            chosen_supplierTF1.setEditable(false);
            chosen_supplierTF1.setPrefWidth(200);
            chosen_supplierTF1.setPrefHeight(20);
            drugGrid1.add(chosen_supplierTF1, 3, 3);

            Button saveDrugsB1 = new Button("Save");
            saveDrugsB1.setId("button");

            Button backFromDrugScene1 = new Button("Back");
            backFromDrugScene1.setId("button");

            HBox drugHB1 = new HBox(10);
            drugHB1.setAlignment(Pos.BOTTOM_RIGHT);
            drugHB1.getChildren().addAll(saveDrugsB1, backFromDrugScene1);
            drugGrid1.add(drugHB1, 3, 5);

            cbsuppliers1.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue ov, Number old_val, Number new_val) {
                    String supps1 = suppliers[new_val.intValue()];
                    chosen_supplierTF1.setText(supps1);

                }
            });

            cbdrugs1.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue ov, Number old_val, Number new_val) {
                    String drugsC1 = drugs[new_val.intValue()];
                    chosen_drugTF1.setText(drugsC1);
                }
            });

            saveDrugsB1.setOnAction((ActionEvent t) -> {

                String drug_units1 = enter_unitsTF1.getText();
                String drug_price1 = enter_priceTF1.getText();
                String drug_name1 = chosen_drugTF1.getText();
                String supplier_name1 = chosen_supplierTF1.getText();

                if (enter_unitsTF1.getText().equals("") || enter_priceTF1.getText().equals("") || chosen_drugTF1.getText().equals("") || chosen_supplierTF1.getText().equals("")) {
                    Alert warning = new Alert(AlertType.WARNING);
                    warning.setTitle("Warning Dialog");
                    warning.setHeaderText("Warning Alert For Empty Fields");
                    warning.setContentText("Please Do Not Leave Any Of The Fields Empty!");
                    warning.showAndWait();

                } else {
                    if (drug_units1.matches("^[-+]?\\d+(\\.\\d+)?$") == false) {
                        Alert warning = new Alert(AlertType.WARNING);
                        warning.setTitle("Warning Dialog");
                        warning.setHeaderText("Warning Alert For Numerical Fields");
                        warning.setContentText("Please Enter units in numbers");
                        warning.showAndWait();
                    } else {
                        if (drug_price1.matches("^[-+]?\\d+(\\.\\d+)?$") == false) {
                            Alert warning = new Alert(AlertType.WARNING);
                            warning.setTitle("Warning Dialog");
                            warning.setHeaderText("Warning Alert For Numerical Fields");
                            warning.setContentText("Please Enter price in numbers");
                            warning.showAndWait();
                        } else {
                            try {
                                //update drugs
                                Class.forName(jdbc);
                                conn = DriverManager.getConnection(url + db_name, db_user, db_pass);
                                PreparedStatement ps = conn.prepareStatement("UPDATE drugs SET units='" + drug_units1 + "',price='" + drug_price1 + "',drug_name='" + drug_name1 + "',supplier_name='" + supplier_name1 + "' WHERE drug_id='" + drug_ids + "'");
                                int update = ps.executeUpdate();
                                if (update > 0) {
                                    Alert info = new Alert(AlertType.INFORMATION);
                                    info.setTitle("Information Dialog.");
                                    info.setHeaderText("Information Dialog For Successful changes made.");
                                    info.setContentText("Changes Made Successfully!");
                                    info.showAndWait();
                                } else if (update == 0) {
                                    Alert err = new Alert(AlertType.ERROR);
                                    err.setTitle("Error Dialog");
                                    err.setHeaderText("Error Dialog For Failure In Making Changes");
                                    err.setContentText("Errors Occured While Making Changes.Please Contact The Administrator");
                                    err.showAndWait();
                                }
                            } catch (ClassNotFoundException ex) {
                                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (SQLException ex) {
                                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }
                    }
                }

            });

            Stage drugStage = new Stage();
            drugStage.setScene(drugGridScene1);
            drugStage.show();
            drugStage.setX(primaryStage.getX() + 250);
            drugStage.setX(primaryStage.getY() + 100);
            Image sceneGridIcon = new Image(getClass().getResourceAsStream("star-128.png"));
            drugStage.getIcons().add(sceneGridIcon);
        });

        //creating HBox
        HBox bp3HB = new HBox(10);
        bp3HB.setAlignment(Pos.BOTTOM_LEFT);
        bp3HB.setPadding(new Insets(10, 0, 10, 5));
        bp3HB.getChildren().addAll(syncBP3, backFromScene3);
        bp3.setBottom(bp3HB);

        //action for sync button
        syncBP3.setOnAction((ActionEvent z) -> {
            table.getItems().clear();
            try {
                Class.forName(jdbc);
                conn = DriverManager.getConnection(url + db_name, db_user, db_pass);
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM drugs");
                rs = ps.executeQuery();
                int count = 0;
                while (rs.next()) {
                    count = count + 1;
                    data.add(new Drug(rs.getString("drug_id"), rs.getString("units"), rs.getString("price"), rs.getString("drug_name"), rs.getString("supplier_name")));
                }
                if (count == 0) {
                    Alert err = new Alert(AlertType.ERROR);
                    err.setTitle("Error Dialog");
                    err.setHeaderText("Error Dialog For No Records");
                    err.setContentText("The systems found out there are no records in the database");
                    err.showAndWait();
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            }

        });

        //************creating drug register screen************
        GridPane drugGrid = new GridPane();
        Scene drugGridScene = new Scene(drugGrid, 800, 400);
        drugGridScene.getStylesheets().add("pham.css");
        drugGrid.setHgap(5);
        drugGrid.setAlignment(Pos.CENTER);

        Text drugTitle = new Text("Add Drugs");
        drugTitle.setFill(Color.TOMATO);
        drugTitle.setFont(Font.font("TAHOMA", FontWeight.BOLD, 20));
        drugGrid.add(drugTitle, 0, 0, 2, 1);

        Label enter_unitsL = new Label("Enter Units:");
        enter_unitsL.setId("label");
        drugGrid.add(enter_unitsL, 0, 1);

        TextField enter_unitsTF = new TextField();
        enter_unitsTF.setPrefWidth(200);
        enter_unitsTF.setPrefHeight(20);
        drugGrid.add(enter_unitsTF, 1, 1);

        Label enter_priceL = new Label("Enter Price:");
        enter_priceL.setId("label");
        drugGrid.add(enter_priceL, 2, 1);

        TextField enter_priceTF = new TextField();
        enter_priceTF.setPrefWidth(200);
        enter_priceTF.setPrefHeight(20);
        drugGrid.add(enter_priceTF, 3, 1);

        Label choose_drugL = new Label("Choose Drug");
        choose_drugL.setId("label");
        drugGrid.add(choose_drugL, 0, 2);

        ChoiceBox<String> cbdrugs = new ChoiceBox<String>(FXCollections.observableArrayList(drugs));
        cbdrugs.setPrefWidth(200);
        cbdrugs.setPrefHeight(20);
        drugGrid.add(cbdrugs, 1, 2);

        Label chosen_drugL = new Label("Chosen Drug:");
        chosen_drugL.setId("label");
        drugGrid.add(chosen_drugL, 2, 2);

        TextField chosen_drugTF = new TextField();
        chosen_drugTF.setEditable(false);
        chosen_drugTF.setPrefWidth(200);
        chosen_drugTF.setPrefHeight(20);
        drugGrid.add(chosen_drugTF, 3, 2);

        Label choose_supplierL = new Label("Choose Supplier:");
        choose_supplierL.setId("label");
        drugGrid.add(choose_supplierL, 0, 3);

        ChoiceBox<String> cbsuppliers = new ChoiceBox<String>(FXCollections.observableArrayList(suppliers));
        cbsuppliers.setPrefWidth(200);
        cbsuppliers.setPrefHeight(20);
        drugGrid.add(cbsuppliers, 1, 3);

        Label chosen_supplierL = new Label("Chosen Supplier:");
        chosen_supplierL.setId("label");
        drugGrid.add(chosen_supplierL, 2, 3);

        TextField chosen_supplierTF = new TextField();
        chosen_supplierTF.setEditable(false);
        chosen_supplierTF.setPrefWidth(200);
        chosen_supplierTF.setPrefHeight(20);
        drugGrid.add(chosen_supplierTF, 3, 3);

        //creating buttons
        Button saveDrugsB = new Button("Save");
        saveDrugsB.setId("button");

        Button backFromDrugScene = new Button("Back");
        backFromDrugScene.setId("button");

        HBox drugHB = new HBox(10);
        drugHB.setAlignment(Pos.BOTTOM_RIGHT);
        drugHB.getChildren().addAll(saveDrugsB, backFromDrugScene);
        drugGrid.add(drugHB, 3, 5);

        cbsuppliers.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue ov, Number old_val, Number new_val) {
                String supps = suppliers[new_val.intValue()];
                chosen_supplierTF.setText(supps);

            }
        });

        cbdrugs.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue ov, Number old_val, Number new_val) {
                String drugsC = drugs[new_val.intValue()];
                chosen_drugTF.setText(drugsC);
            }
        });

        saveDrugsB.setOnAction((ActionEvent t) -> {
            try {
                Class.forName(jdbc);
                conn = DriverManager.getConnection(url + db_name, db_user, db_pass);
                String drug_units = enter_unitsTF.getText();
                String drug_price = enter_priceTF.getText();
                String drug_name = chosen_drugTF.getText();
                String supplier_name = chosen_supplierTF.getText();

                if (enter_unitsTF.getText().equals("") || enter_priceTF.getText().equals("") || chosen_drugTF.getText().equals("") || chosen_supplierTF.getText().equals("")) {
                    Alert warning = new Alert(AlertType.WARNING);
                    warning.setTitle("Warning Dialog");
                    warning.setHeaderText("Warning Alert For Empty Fields");
                    warning.setContentText("Please Do Not Leave Any Of The Fields Empty!");
                    warning.showAndWait();
                } else {
                    if (drug_units.matches("^[-+]?\\d+(\\.\\d+)?$") == false) {
                        Alert warning = new Alert(AlertType.WARNING);
                        warning.setTitle("Warning Dialog");
                        warning.setHeaderText("Warning Alert For Numerical Fields");
                        warning.setContentText("Please Enter units in numbers");
                        warning.showAndWait();
                    } else {
                        if (drug_price.matches("^[-+]?\\d+(\\.\\d+)?$") == false) {
                            Alert warning = new Alert(AlertType.WARNING);
                            warning.setTitle("Warning Dialog");
                            warning.setHeaderText("Warning Alert For Numerical Fields");
                            warning.setContentText("Please Enter price in numbers");
                            warning.showAndWait();
                        } else {
                            PreparedStatement ps = conn.prepareStatement("INSERT INTO drugs(units,price,drug_name,supplier_name)VALUES(?,?,?,?)");
                            ps.setString(1, drug_units);
                            ps.setString(2, drug_price);
                            ps.setString(3, drug_name);
                            ps.setString(4, supplier_name);

                            int count = ps.executeUpdate();
                            if (count > 0) {
                                Alert info = new Alert(AlertType.INFORMATION);
                                info.setTitle("Information Dialog");
                                info.setHeaderText("Information Dialog For Success");
                                info.setContentText("Details Recorded Successfully!");
                                info.showAndWait();

                                enter_unitsTF.clear();
                                enter_priceTF.clear();
                                chosen_drugTF.clear();
                                chosen_supplierTF.clear();
                            } else if (count == 0) {
                                Alert err = new Alert(AlertType.ERROR);
                                err.setTitle("Error Dialog");
                                err.setHeaderText("Error Dialog For Failed Record");
                                err.setContentText("Details Have Not Been Recorded Successfully!");
                                err.showAndWait();
                            }

                        }
                    }
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            }

        });

        //****************** creating menubar scene **************
        BorderPane bp1 = new BorderPane();
        Scene scene1 = new Scene(bp1, 600, 400);
        scene1.getStylesheets().add("pham.css");

        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        Menu drugM = new Menu("Drugs");

        Image drugMenuIcon = new Image(getClass().getResourceAsStream("hourglass-128.png"));
        ImageView drugMenuIconView = new ImageView(drugMenuIcon);
        drugMenuIconView.setFitHeight(15);
        drugMenuIconView.setFitWidth(15);

        Image add_drugs_icon = new Image(getClass().getResourceAsStream("art-128.png"));
        ImageView add_drugs_iconV = new ImageView(add_drugs_icon);
        add_drugs_iconV.setFitHeight(15);
        add_drugs_iconV.setFitWidth(15);

        Image search_drugs_icon = new Image(getClass().getResourceAsStream("search.png"));
        ImageView search_drugs_iconView = new ImageView(search_drugs_icon);
        search_drugs_iconView.setFitHeight(15);
        search_drugs_iconView.setFitWidth(15);

        Image prescription_icon = new Image(getClass().getResourceAsStream("clipboard-128.png"));
        ImageView prescription_iconV = new ImageView(prescription_icon);
        prescription_iconV.setFitWidth(15);
        prescription_iconV.setFitHeight(15);

        Image patients_icon = new Image(getClass().getResourceAsStream("friends.png"));
        ImageView patients_icon_view = new ImageView(patients_icon);
        patients_icon_view.setFitWidth(15);
        patients_icon_view.setFitHeight(15);

        Image add_patient_icon = new Image(getClass().getResourceAsStream("add_icon.png"));
        ImageView add_patient_iconV = new ImageView(add_patient_icon);
        add_patient_iconV.setFitWidth(15);
        add_patient_iconV.setFitHeight(15);

        Image edit_patient_icon = new Image(getClass().getResourceAsStream("pencil-128.png"));
        ImageView edit_patient_iconV = new ImageView(edit_patient_icon);
        edit_patient_iconV.setFitWidth(15);
        edit_patient_iconV.setFitHeight(15);

        Image patients_prescIcon = new Image(getClass().getResourceAsStream("presc.png"));
        ImageView patients_prescIconV = new ImageView(patients_prescIcon);
        patients_prescIconV.setFitWidth(15);
        patients_prescIconV.setFitHeight(15);

        Image record_patients_presc = new Image(getClass().getResourceAsStream("profile-128.png"));
        ImageView record_patients_prescV = new ImageView(record_patients_presc);
        record_patients_prescV.setFitWidth(15);
        record_patients_prescV.setFitHeight(15);

        Image edit_patients_presc = new Image(getClass().getResourceAsStream("scissors-128.png"));
        ImageView edit_patients_prescV = new ImageView(edit_patients_presc);
        edit_patients_prescV.setFitWidth(15);
        edit_patients_prescV.setFitHeight(15);

        Image user_accIcon = new Image(getClass().getResourceAsStream("user.png"));
        ImageView user_accIconView = new ImageView(user_accIcon);
        user_accIconView.setFitWidth(15);
        user_accIconView.setFitHeight(15);

        Image log_outIcon = new Image(getClass().getResourceAsStream("unlocked-128.png"));
        ImageView log_outIconView = new ImageView(log_outIcon);
        log_outIconView.setFitWidth(15);
        log_outIconView.setFitHeight(15);

        //menu items for drugs menu
        MenuItem enter_drugs_MI = new MenuItem("Enter Drugs");
        enter_drugs_MI.setGraphic(add_drugs_iconV);
        enter_drugs_MI.setOnAction((ActionEvent t) -> {
            primaryStage.setScene(drugGridScene);
        });

        MenuItem search_drugs_MI = new MenuItem("Search Drugs");
        search_drugs_MI.setGraphic(search_drugs_iconView);
        search_drugs_MI.setOnAction((ActionEvent t) -> {
            primaryStage.setScene(scene3);
        });

        drugM.setGraphic(drugMenuIconView);
        drugM.getItems().addAll(enter_drugs_MI, search_drugs_MI);

        //Menu For prescription
        Menu prescriptionM = new Menu("Patients");
        prescriptionM.setGraphic(patients_icon_view);

        //MenuItems For presciption Menu
        MenuItem add_patientM = new MenuItem("Add Patients");
        add_patientM.setGraphic(add_patient_iconV);
        add_patientM.setOnAction((ActionEvent t) -> {
            primaryStage.setScene(scene4);
        });
        MenuItem edit_patientM = new MenuItem("Edit Or Delete Patient");
        edit_patientM.setGraphic(edit_patient_iconV);
        edit_patientM.setOnAction((ActionEvent t) -> {
            primaryStage.setScene(scene5);
        });
        //sub menu
        Menu patients_PM = new Menu("Patients Prescriptions");
        patients_PM.setGraphic(patients_prescIconV);

        MenuItem add_patientPM = new MenuItem("Add Patients Prescription");
        add_patientPM.setGraphic(add_patient_iconV);
        add_patientPM.setOnAction((ActionEvent t) -> {
            primaryStage.setScene(scene7);
        });
        MenuItem edit_patientPM = new MenuItem("Edit Patients Precription");
        edit_patientPM.setGraphic(edit_patients_prescV);
        edit_patientPM.setOnAction((ActionEvent t) -> {
            primaryStage.setScene(scene8);
        });
        patients_PM.getItems().addAll(add_patientPM, edit_patientPM);
        //menu user account

        Menu user_accountM = new Menu("Account");
        user_accountM.setGraphic(user_accIconView);

        //menuitem
        MenuItem user_accountItem = new MenuItem("Logout");
        user_accountItem.setGraphic(log_outIconView);

        user_accountM.getItems().add(user_accountItem);

        prescriptionM.getItems().addAll(add_patientM, edit_patientM, patients_PM);
        menuBar.getMenus().addAll(drugM, prescriptionM, user_accountM);

        bp1.setTop(menuBar);

        Path path = new Path();
        path.getElements().add(new MoveTo(20, 120));
        path.getElements().add(new CubicCurveTo(180, 60, 250, 340, 420, 240));

        Circle circle = new Circle(20, 120, 10);
        circle.setFill(Color.CADETBLUE);

        PathTransition ptr = new PathTransition();

        ptr.setDuration(Duration.seconds(6));
        ptr.setDelay(Duration.seconds(2));
        ptr.setPath(path);
        ptr.setNode(circle);
        ptr.setCycleCount(2);
        ptr.setAutoReverse(true);
        ptr.play();

        Pane root = new Pane();
        root.getChildren().addAll(path, circle);
        bp1.setCenter(root);

        //*********** creating login scene ***********
        BorderPane bp2 = new BorderPane();
        Scene loginScene = new Scene(bp2, 600, 400);
        loginScene.getStylesheets().add("pham.css");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);
        bp2.setCenter(grid);

        Text loginTitle = new Text("Please Login Below.");
        loginTitle.setFill(Color.TOMATO);
        loginTitle.setFont(Font.font("TAHOMA", FontWeight.BOLD, 20));
        grid.add(loginTitle, 0, 0, 2, 1);

        Label usernameL = new Label("Username:");
        usernameL.setId("label");
        grid.add(usernameL, 0, 1);

        TextField usernameTF = new TextField();
        usernameTF.setPromptText("Enter Username");
        usernameTF.setPrefWidth(250);
        usernameTF.setPrefHeight(20);
        grid.add(usernameTF, 1, 1);

        Label passwordL = new Label("Password:");
        passwordL.setId("label");
        grid.add(passwordL, 0, 2);

        PasswordField passwordTF = new PasswordField();
        passwordTF.setPromptText("Enter Password");
        passwordTF.setPrefWidth(250);
        passwordTF.setPrefHeight(20);
        grid.add(passwordTF, 1, 2);

        HBox hbBtn = new HBox(10);

        Image unlockIcon = new Image(getClass().getResourceAsStream("unlocked-128.png"));
        ImageView unlockIconView = new ImageView(unlockIcon);
        unlockIconView.setFitHeight(15);
        unlockIconView.setFitWidth(15);

        Image adminIcon = new Image(getClass().getResourceAsStream("eye-128.png"));
        ImageView adminIconView = new ImageView(adminIcon);
        adminIconView.setFitWidth(15);
        adminIconView.setFitHeight(15);

        Button loginB = new Button("login");
        loginB.setGraphic(unlockIconView);
        loginB.setId("button");

        Button adminB = new Button("admin");
        adminB.setGraphic(adminIconView);
        adminB.setId("button");

        hbBtn.getChildren().addAll(loginB, adminB);

        grid.add(hbBtn, 1, 4);

        loginB.setOnAction((ActionEvent t) -> {
            try {
                Class.forName(jdbc);
                conn = DriverManager.getConnection(url + db_name, db_user, db_pass);
                String username = usernameTF.getText();
                String password = passwordTF.getText();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM clerks WHERE username='" + username + "' AND password='" + password + "'");
                rs = ps.executeQuery();
                int count = 0;
                while (rs.next()) {
                    count = count + 1;
                }
                if (count == 1) {
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Success Dialog.");
                    success.setHeaderText("Information Dialog For Success Login.");
                    success.setContentText("Login Successful!");
                    success.showAndWait();
                    usernameSession = username;
                    passwordSession = password;
                    //clearing username and password fields
                    usernameTF.clear();
                    passwordTF.clear();

                    primaryStage.setScene(scene1);
                } else if (count > 1) {
                    Alert warning = new Alert(Alert.AlertType.WARNING);
                    warning.setTitle("Warning Dialog.");
                    warning.setHeaderText("Warning Dialog For Duplicate Records.");
                    warning.setContentText("Duplicate Records");
                    warning.showAndWait();
                } else {
                    Alert error = new Alert(Alert.AlertType.WARNING);
                    error.setTitle("Error Dialog.");
                    error.setHeaderText("Error Dialog For Non-Existing Records.Please Contact The Administrator.");
                    error.setContentText("No Records Exist");
                    error.showAndWait();
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        //****************setting action for admin button****************
        adminB.setOnAction((ActionEvent t) -> {
            //code logic here
        });

        //************************initial scene***********8
        BorderPane bp = new BorderPane();
        Scene scene = new Scene(bp, 800, 400);
        scene.getStylesheets().add("pham.css");
        HBox hb = new HBox();
        hb.setId("hb");
        bp.setTop(hb);

        Text softwareHeaderText = new Text("Pharmacy  Software.");
        softwareHeaderText.setId("sht");
        softwareHeaderText.setFont(Font.font("TAHOMA", FontWeight.BOLD, 20));
        softwareHeaderText.setFill(Color.WHITE);

        Text copyrightInfo = new Text("Jacaranda Pharmacy Management System");
        copyrightInfo.setFont(Font.font("TAHOMA", FontWeight.NORMAL, 15));
        copyrightInfo.setFill(Color.WHITE);

        Text authContacts = new Text("Phone Number:+254702500937  Email:kennedykariuki@gmail.com");
        authContacts.setFont(Font.font("TAHOMA", FontWeight.NORMAL, 15));
        authContacts.setFill(Color.WHITE);

        Text assuranceInfo = new Text("This software is an intellectual property of Jacaranda.All Rights reserved to the company.");
        assuranceInfo.setFont(Font.font("TAHOMA", FontWeight.NORMAL, 15));
        assuranceInfo.setFill(Color.WHITE);

        VBox vb = new VBox(5);
        vb.setAlignment(Pos.CENTER);
        vb.getChildren().addAll(softwareHeaderText, copyrightInfo, authContacts, assuranceInfo);
        hb.setPrefWidth(800);
        hb.setPrefHeight(200);
        Image headerImage = new Image(getClass().getResourceAsStream("compass.png"));
        ImageView imgV = new ImageView(headerImage);
        imgV.setFitHeight(100);
        imgV.setFitWidth(100);
        hb.getChildren().addAll(imgV, vb);

        HBox bottom = new HBox(10);
        bottom.setPrefWidth(600);
        bottom.setPrefHeight(200);
        bottom.setPadding(new Insets(50));

        bp.setBottom(bottom);
        //progress bar for index page
        ProgressBar pbar = new ProgressBar(0);
        pbar.setPrefWidth(600);
        pbar.setPrefHeight(20);
        pbar.setProgress(1.00);

        KeyFrame frame1 = new KeyFrame(Duration.ZERO, new KeyValue(pbar.progressProperty(), 0));
        KeyFrame frame2 = new KeyFrame(Duration.seconds(8), new KeyValue(pbar.progressProperty(), 1));
        Timeline task = new Timeline(frame1, frame2);
        task.play();
        //creating button
        Button next = new Button("next");
        next.setOnAction((ActionEvent f) -> {
            primaryStage.setScene(loginScene);
        });
        next.setDisable(true);
        next.setId("button");
        task.setOnFinished((ActionEvent t) -> {
            next.setDisable(false);
        });
        bottom.getChildren().addAll(pbar, next);

        //scene buttons actions
        user_accountItem.setOnAction((ActionEvent t) -> {
            primaryStage.setScene(loginScene);
        });
        backFromScene3.setOnAction((ActionEvent t) -> {
            primaryStage.setScene(scene1);
        });
        backFromScene4.setOnAction((ActionEvent t) -> {
            primaryStage.setScene(scene1);
        });

        backFromDrugScene.setOnAction((ActionEvent t) -> {
            primaryStage.setScene(scene1);
        });
        backFromScene5.setOnAction((ActionEvent t) -> {
            primaryStage.setScene(scene1);
        });
        backFromScene7.setOnAction((ActionEvent t) -> {
            primaryStage.setScene(scene1);
        });
        backFromScene8.setOnAction((ActionEvent t) -> {
            primaryStage.setScene(scene1);
        });
        backFromScene12.setOnAction((ActionEvent t) -> {
            primaryStage.setScene(loginScene);
        });
        backFromScene13.setOnAction((ActionEvent t) -> {
            primaryStage.setScene(scene12);
        });
        adminB.setOnAction((ActionEvent t) -> {

            GridPane adminGP = new GridPane();
            Scene sceneAL = new Scene(adminGP, 400, 200);
            sceneAL.getStylesheets().add("pham.css");

            adminGP.setHgap(5);
            adminGP.setAlignment(Pos.CENTER);
            //******** create gridpane components ********
            Text adminTitle = new Text("Admin Login:");
            adminTitle.setFont(Font.font("TAHOMA", FontWeight.THIN, 20));
            adminTitle.setFill(Color.TOMATO);
            adminGP.add(adminTitle, 0, 0, 2, 1);

            Label adminUL = new Label("Username:");
            adminUL.setId("label");
            adminGP.add(adminUL, 0, 1);

            TextField adminUTF = new TextField();
            adminUTF.setPromptText("Enter Username");
            adminUTF.setPrefWidth(200);
            adminUTF.setPrefHeight(20);
            adminGP.add(adminUTF, 1, 1);

            Label adminPL = new Label("Password:");
            adminPL.setId("label");
            adminGP.add(adminPL, 0, 2);

            PasswordField adminPF = new PasswordField();
            adminPF.setPromptText("Enter Password");
            adminPF.setPrefWidth(200);
            adminPF.setPrefHeight(20);
            adminGP.add(adminPF, 1, 2);

            //Buttons
            Button loginAB = new Button("Login");
            loginAB.setId("button");

            Button clearCB = new Button("Clear");
            clearCB.setId("button");

            HBox adminHB = new HBox(10);
            adminHB.setAlignment(Pos.BOTTOM_RIGHT);
            adminHB.getChildren().addAll(loginAB, clearCB);
            adminGP.add(adminHB, 1, 4);

            Stage adminStage = new Stage();
            Image adminl_icon = new Image(getClass().getResourceAsStream("star-128.png"));
            adminStage.getIcons().add(adminl_icon);

            adminStage.setScene(sceneAL);

            //setting actions for buttons
            clearCB.setOnAction((ActionEvent t1) -> {
                adminUTF.clear();
                adminPF.clear();
            });
            loginAB.setOnAction((ActionEvent t1) -> {
                try {
                    Class.forName(jdbc);
                    conn = DriverManager.getConnection(url + db_name, db_user, db_pass);
                    String admin_uname = adminUTF.getText();
                    String admin_pass = adminPF.getText();
                    PreparedStatement ps = conn.prepareStatement("SELECT * FROM admin WHERE username='" + admin_uname + "' AND password='" + admin_pass + "'");
                    int count_admin = 0;
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        count_admin = count_admin + 1;
                    }
                    if (count_admin == 0) {
                        Alert err = new Alert(AlertType.ERROR);
                        err.setTitle("Error Dialog");
                        err.setHeaderText("Error Dialog For Failed Login.");
                        err.setContentText("Your Username And Password Combination Is Wrong!");
                        err.showAndWait();
                    } else if (count_admin > 0) {
                        Alert info = new Alert(AlertType.INFORMATION);
                        info.setTitle("Information Dialog");
                        info.setHeaderText("Information Dialog For Successful Login");
                        info.setContentText("Successful Login");
                        info.showAndWait();

                        //clear the fields
                        adminUTF.clear();
                        adminPF.clear();
                        adminStage.close();
                        //now after successful login open scene12
                        primaryStage.setScene(scene12);

                    }
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(PharmacyManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            adminStage.setX(primaryStage.getX() + 250);
            adminStage.setX(primaryStage.getY() + 100);
            adminStage.show();

        });

        //image icon
        Image stageIcon = new Image(getClass().getResourceAsStream("star-128.png"));
        //*****************primary stage ***********
        primaryStage.setTitle("Pharmacy Management System");
        primaryStage.getIcons().add(stageIcon);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static class Drug {

        private final SimpleStringProperty Drug_id;
        private final SimpleStringProperty Units;
        private final SimpleStringProperty Price;
        private final SimpleStringProperty Drug_name;
        private final SimpleStringProperty Supplier_name;

        private Drug(String drug_id, String units, String price, String drug_name, String supplier_name) {

            this.Drug_id = new SimpleStringProperty(drug_id);
            this.Units = new SimpleStringProperty(units);
            this.Price = new SimpleStringProperty(price);
            this.Drug_name = new SimpleStringProperty(drug_name);
            this.Supplier_name = new SimpleStringProperty(supplier_name);
        }

        public String getDrug_id() {
            return Drug_id.get();
        }

        public void setDrug_id(String drug_id) {
            Drug_id.set(drug_id);
        }

        public String getUnits() {
            return Units.get();
        }

        public void setUnits(String units) {
            Units.set(units);
        }

        public String getPrice() {
            return Price.get();
        }

        public void setPrice(String price) {
            Price.set(price);
        }

        public String getDrug_name() {
            return Drug_name.get();
        }

        public void setDrug_name(String drug_name) {
            Drug_name.set(drug_name);
        }

        public String getSupplier_name() {
            return Supplier_name.get();
        }

        public void setSupplier_name(String supplier_name) {
            Drug_id.set(supplier_name);
        }

    }

    public static class Patient {

        private final SimpleStringProperty Patient_id;
        private final SimpleStringProperty First_name;
        private final SimpleStringProperty Second_name;
        private final SimpleStringProperty Third_name;
        private final SimpleStringProperty Phone_number;
        private final SimpleStringProperty Email;
        private final SimpleStringProperty Address;

        private Patient(String patient_id, String fname, String sname, String tname, String pnumber, String email, String address) {
            this.Patient_id = new SimpleStringProperty(patient_id);
            this.First_name = new SimpleStringProperty(fname);
            this.Second_name = new SimpleStringProperty(sname);
            this.Third_name = new SimpleStringProperty(tname);
            this.Phone_number = new SimpleStringProperty(pnumber);
            this.Email = new SimpleStringProperty(email);
            this.Address = new SimpleStringProperty(address);

        }

        public String getPatient_id() {
            return Patient_id.get();
        }

        public void setParcel_id(String patient_id) {
            Patient_id.set(patient_id);
        }

        public String getFirst_name() {
            return First_name.get();
        }

        public void setFirst_name(String fname) {
            First_name.set(fname);
        }

        public String getSecond_name() {
            return Second_name.get();
        }

        public void setSecond_name(String sname) {
            Second_name.set(sname);
        }

        public String getThird_name() {
            return Third_name.get();
        }

        public void setThird_name(String tname) {
            Third_name.set(tname);
        }

        public String getPhone_number() {
            return Phone_number.get();
        }

        public void setPhone_number(String pnumber) {
            Phone_number.set(pnumber);
        }

        public String getEmail() {
            return Email.get();
        }

        public void setEmail(String email) {
            Email.set(email);
        }

        public String getAddress() {
            return Address.get();
        }

        public void setAddress(String address) {
            Address.set(address);
        }
    }

    public static class Presc {

        private final SimpleStringProperty Patient_id;
        private final SimpleStringProperty First_name;
        private final SimpleStringProperty Second_name;
        private final SimpleStringProperty Third_name;
        private final SimpleStringProperty Phone_number;
        private final SimpleStringProperty Email;
        private final SimpleStringProperty Address;

        private Presc(String patient_id, String fname, String sname, String tname, String pnumber, String email, String address) {
            this.Patient_id = new SimpleStringProperty(patient_id);
            this.First_name = new SimpleStringProperty(fname);
            this.Second_name = new SimpleStringProperty(sname);
            this.Third_name = new SimpleStringProperty(tname);
            this.Phone_number = new SimpleStringProperty(pnumber);
            this.Email = new SimpleStringProperty(email);
            this.Address = new SimpleStringProperty(address);

        }

        public String getPatient_id() {
            return Patient_id.get();
        }

        public void setParcel_id(String patient_id) {
            Patient_id.set(patient_id);
        }

        public String getFirst_name() {
            return First_name.get();
        }

        public void setFirst_name(String fname) {
            First_name.set(fname);
        }

        public String getSecond_name() {
            return Second_name.get();
        }

        public void setSecond_name(String sname) {
            Second_name.set(sname);
        }

        public String getThird_name() {
            return Third_name.get();
        }

        public void setThird_name(String tname) {
            Third_name.set(tname);
        }

        public String getPhone_number() {
            return Phone_number.get();
        }

        public void setPhone_number(String pnumber) {
            Phone_number.set(pnumber);
        }

        public String getEmail() {
            return Email.get();
        }

        public void setEmail(String email) {
            Email.set(email);
        }

        public String getAddress() {
            return Address.get();
        }

        public void setAddress(String address) {
            Address.set(address);
        }
    }

    public static class Clerk {

        private final SimpleStringProperty Clerk_id;
        private final SimpleStringProperty Username;
        private final SimpleStringProperty Password;
        private final SimpleStringProperty Mobile;

        private Clerk(String clerk_id, String username, String password, String pnumber) {
            this.Clerk_id = new SimpleStringProperty(clerk_id);
            this.Username = new SimpleStringProperty(username);
            this.Password = new SimpleStringProperty(password);
            this.Mobile = new SimpleStringProperty(pnumber);
        }

        public String getClerk_id() {
            return Clerk_id.get();
        }

        public void setClerk_id(String clerk_id) {
            Clerk_id.set(clerk_id);
        }

        public String getUsername() {
            return Username.get();
        }

        public void setUsername(String username) {
            Username.set(username);
        }

        public String getPassword() {
            return Password.get();
        }

        public void setPassword(String password) {
            Password.set(password);
        }

        public String getMobile() {
            return Mobile.get();
        }

        public void setMobile(String pnumber) {
            Mobile.set(pnumber);
        }
    }

    public static class Duty {

        private final SimpleStringProperty Duty_id;
        private final SimpleStringProperty Username;
        private final SimpleStringProperty Mobile;
        private final SimpleStringProperty Date;

        private Duty(String duty_id, String username, String pnumber, String date_recorded) {
            this.Duty_id = new SimpleStringProperty(duty_id);
            this.Username = new SimpleStringProperty(username);
            this.Mobile = new SimpleStringProperty(pnumber);
            this.Date = new SimpleStringProperty(date_recorded);

        }

        public String getMobile() {
            return Mobile.get();
        }

        public void setMobile(String pnumber) {
            Mobile.set(pnumber);
        }

        public String getUsername() {
            return Username.get();
        }

        public void setUsername(String username) {
            Username.set(username);
        }

        public String getDate() {
            return Date.get();
        }

        public void setDate(String date_recorded) {
            Date.set(date_recorded);
        }

        public String getDuty_id() {
            return Duty_id.get();
        }

        public void setDuty_id(String duty_id) {
            Duty_id.set(duty_id);
        }
    }

}
