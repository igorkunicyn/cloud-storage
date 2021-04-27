package client;

import client.factory.Factory;
import client.service.*;
import client.service.impl.IONetworkService;
import service.Command;
import service.FileUpload;
import client.service.impl.WorkWithFiles;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private HBox authPanel;
    @FXML
    private HBox buttonPanel;
    @FXML
    private MenuBar menuBarPanel;
    @FXML
    private SplitPane splitPanel;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextArea textArea;
    @FXML
    private TextField leftTextField;
    @FXML
    private TextArea leftTextArea;
    @FXML
    private TextArea rightTextArea;
    @FXML
    private TextField rightTextField;
    private String nickname;
    public  Socket socket;
    private Stage stage;
    private Stage regStage;
    private RegController regController;
    private FileUpload fileUpload;
    private IONetworkService networkService;
    private WithFileWorkable withFileWorkable;

    public Controller() {
    }

    // осуществляет переключение между окнами - при авторизации убирает элементы
    // окна авторизации и появляется основное рабочее окно
    public void setAuthenticated(boolean authenticated) {
        buttonPanel.setVisible(authenticated);
        buttonPanel.setManaged(authenticated);
        splitPanel.setVisible(authenticated);
        splitPanel.setManaged(authenticated);
        menuBarPanel.setVisible(authenticated);
        menuBarPanel.setManaged(authenticated);
        textArea.setVisible(!authenticated);
        textArea.setManaged(!authenticated);
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);
        if (!authenticated) {
            nickname = "";
        }
        setTitle(nickname);
        textArea.clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        networkService = Factory.getNetworkService(); // запускает соединение с сервером
        // Открываем окно входа (или регистрации, если вход осуществляется впервые)
        withFileWorkable = Factory.getWithFileWorkable();
        Platform.runLater(() -> {
            stage = ( Stage ) textArea.getScene().getWindow();
            stage.setOnCloseRequest(event -> { //если клиент закрыл окно, нажав на крестик, то отправляеи на сервер команду END
                System.out.println("bye");
                networkService.sendCommand(Command.END.getInstruction());
                if (socket != null && !socket.isClosed()) {
                    networkService.sendCommand(Command.END.getInstruction());
                }
            });
        });
        setAuthenticated(false);
        createCommandResultHandler();//надо запускать после авторизации или регистрации
    }

// считывает входящий поток с сервера и выдает результат в левую часть основного рабочего окна
    private void createCommandResultHandler() {
        new Thread(() -> {
            while (true) {
                Object result = networkService.readCommandResult();
                if (result.getClass()== FileUpload.class){
                    fileUpload = (FileUpload ) result;
                    if (!fileUpload.getNameFile().equals(Command.DELETE.getInstruction())){
                        withFileWorkable.createDirectoryAndFileService(fileUpload.getPath());
                        WorkWithFiles.createByteArrayToFile(fileUpload);
                    }
                }
                if (result.getClass()==String.class){
                    String resultCommand = (String) result;
                    System.out.println(resultCommand+" resultCommand");
                    Platform.runLater(() -> {
                        if (resultCommand.startsWith(Command.AUTH_OK.getInstruction())) {
                            nickname = resultCommand.split("\\s")[1];
                            setAuthenticated(true);
                            updateWorkClientWindows(leftTextField,leftTextArea,Command.START_DIR.getInstruction());
                            updateWorkClientWindows(rightTextField,rightTextArea,Command.DIR_STORAGE.getInstruction());
                        }
                        if (resultCommand.equals(Command.REG_OK.getInstruction())) {
                            regController.regOk();
                            withFileWorkable.createDirectoryAndFileService(Command.DIR_STORAGE.getInstruction());

                        }
                        if (resultCommand.equals(Command.REG_NO.getInstruction())) {
                            regController.regNo();
                        }
                    if (resultCommand.equals(Command.END.getInstruction())) {
                        System.out.println("client disconnected");
                        throw new RuntimeException("server disconnected us");
                    }

                });
                }
            }
        }).start();
    }
 // при нажатии ввода считывает текст с левого поля рабочего окна и отправляет его
 // на сервер в виде команды
    @FXML
    private void readCommandOnStage(ActionEvent actionEvent) {
        networkService.sendCommand(leftTextField.getText());
        leftTextField.clear();
    }
// при нажатии ввод в поле пароля или кнопки логин соединяется с сервером и отправляет на него
    // логин и пароль , пытаясь авторизоваться (пароль и логин проверяются в базе данных)
    @FXML
    public void tryToAuth(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()) {
            networkService = Factory.getNetworkService();
        }
        String msg = String.format("%s %s %s",
                Command.AUTH.getInstruction(), loginField.getText().trim(), passwordField.getText().trim());
        networkService.sendCommand(msg);
    }
// после регистрации или авторизации устанавливает название рабочего окнапо имени пользователя
    private void setTitle(String nickname) {
        if (nickname.equals("")) {
            Platform.runLater(() -> stage.setTitle("Storage"));
        } else {
            Platform.runLater(() -> stage.setTitle(String.format("Storage [ %s ]", nickname)));
        }
    }

    @FXML
    public void tryToReg(String login, String password, String nickname) {
        if (socket == null || socket.isClosed()) {
            networkService = Factory.getNetworkService();
        }

        String msg = String.format("%s %s %s %s",
                Command.REG.getInstruction(), login, password, nickname);
        networkService.sendCommand(msg);
    }

    // нажимая, кнопку регистрации в окне авторизации , переходим в окно регистрации
    @FXML
    public void registration(ActionEvent actionEvent) {
        if (regStage == null) {
            createRegWindow();
        }
        regStage.show();
    }

    private void createRegWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/reg.fxml"));
            Parent root = fxmlLoader.load();
            regStage = new Stage();
            regStage.setTitle("GeekChat registration");
            regStage.setScene(new Scene(root, 400, 350));
            regController = fxmlLoader.getController();
            regController.setController(this);
            regStage.initModality(Modality.APPLICATION_MODAL);
            regStage.initStyle(StageStyle.UTILITY);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void copyFileUseButton(ActionEvent actionEvent){
        String command = "copy";
        moveAndCopyFile(command);
    }
    @FXML
    public void moveFileUseButton(ActionEvent actionEvent){
        String command = "move";
        moveAndCopyFile(command);
    }

    private void moveAndCopyFile(String command){
        String source = leftTextField.getText();
        String target = rightTextField.getText();
        if (!Files.exists(Paths.get(source))){
            System.out.println("Path is not correct");
            return;
        }
        if (target.startsWith(Command.DIR_STORAGE.getInstruction())){
// перемещаем или копируем из компьютера в хранилище
            networkService.sendCommand(WorkWithFiles.createFileForCopyOrMoveToServer(source,target,
                    nickname));
            withFileWorkable.createDirectoryAndFileService(target);
        }else if (source.startsWith(Command.DIR_STORAGE.getInstruction())){
            // перемещаем или копируем из хранилища на компьютер
            if (command.equals("move")){
                networkService.sendCommand(Command.MOVE.getInstruction()+" "
                +source.replace(Command.DIR_STORAGE.getInstruction(),nickname)+" " +target);
            }else {
                networkService.sendCommand(Command.COPY.getInstruction()+" "
                +source.replace(Command.DIR_STORAGE.getInstruction(),nickname)+" " +target);
            }
        }else {
// перемещаем или копируем из одной папки в другую на компьютере
            if (command.equals("move")){
                System.out.println(command + "900");
                withFileWorkable.deleteDirectoryAndFileService(source);
            }else {
                withFileWorkable.copyDirectoryAndFileService(source, target);
            }
        }
        updateWorkClientWindows(leftTextField,leftTextArea,Paths.get(source).getParent().toString());
        updateWorkClientWindows(rightTextField,rightTextArea,Paths.get(target).getParent().toString());
    }
    // удаляем файл или папку, если папка или файл лежат в C:\Storage, то удаляем изхранилища
    @FXML
    public void deleteFileAndDirectoryUseButton(ActionEvent actionEvent){
        TextField currentTextField = determineWorkedTextField();
        TextArea currentTextArea = determineWorkedTextArea(currentTextField);
        if (currentTextField.getText().startsWith(Command.DIR_STORAGE.getInstruction())) {
// удаляем в хранилище
            networkService.sendCommand(Command.DELETE.getInstruction()+" "+currentTextField.getText().
                    replace(Command.DIR_STORAGE.getInstruction(),nickname));
            withFileWorkable.deleteDirectoryAndFileService(currentTextField.getText());
        }else {
// удаляем на компьютере
            withFileWorkable.deleteDirectoryAndFileService(currentTextField.getText());
        }
        updateWorkClientWindows(currentTextField,currentTextArea,currentTextField.getText());
    }
    private void updateWorkClientWindows(TextField textField, TextArea textArea, String dirPath){
        textArea.clear();
        String path;
        if (dirPath.contains(".")){
            path = Paths.get(dirPath).getParent().toString();
        }else {
            path = Paths.get(dirPath).toString();
        }
        textArea.appendText(withFileWorkable.openDirectoryService(path));
        textField.setText(path);
    }
    private TextField determineWorkedTextField(){
        if (rightTextField.getText().equals("")) {
            return leftTextField;
        }
        return rightTextField;
    }
    private TextArea determineWorkedTextArea(TextField textField){
        if (textField.equals(leftTextField)){
            return leftTextArea;
        }
        return rightTextArea;
    }
    @FXML
    public void createFileUseButton(ActionEvent actionEvent){
        TextField currentTextField = determineWorkedTextField();
        TextArea currentTextArea = determineWorkedTextArea(currentTextField);
        withFileWorkable.createDirectoryAndFileService(currentTextField.getText());
        updateWorkClientWindows(currentTextField, currentTextArea,currentTextField.getText());
    }

    @FXML
    public void openDirectoryUseButton(ActionEvent actionEvent){
        TextField currentTextField = determineWorkedTextField();
        TextArea currentTextArea = determineWorkedTextArea(currentTextField);
        updateWorkClientWindows(currentTextField, currentTextArea,currentTextField.getText());
    }

    public void shutdown() {
        networkService.closeConnection();
    }

}