package server.handler;

import java.sql.*;

public class SQLHandler {
    private static Connection connection;
    private static PreparedStatement psGetNickname;
    private static PreparedStatement psRegistration;
    private static PreparedStatement psChangeNick;
    private static PreparedStatement psAddFiles;
    private static PreparedStatement psGetListDirAndFileFromStorage;
    private static PreparedStatement psGetFileFromStorage;
    private static PreparedStatement psDeleteFileFromStorage;

    // подключаем базу данных и создаем таблицу пользователей при первом подключениии
    public static boolean connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:mainstorage.db");
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS client" +
                    " ( client_id INTEGER PRIMARY KEY AUTOINCREMENT , login char(50) NOT NULL UNIQUE, password char(50) NOT NULL UNIQUE," +
                    "nickname char(50) NOT NULL UNIQUE);").execute();
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS storage" +
                    " ( storage_id INTEGER PRIMARY KEY AUTOINCREMENT , client_id INTEGER NOT NULL," +
                    "name_file char(50) , path char(100) NOT NULL, file BLOB, FOREIGN KEY (client_id)" +
                    " REFERENCES clients(client_id));").execute();
            prepareAllStatements();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // создаем шаблоны запросов для базы данных
    private static void prepareAllStatements() throws SQLException {
// таблица пользователей, запросы на полчение имени пользователя, регистрацию и изменение имени
        psGetNickname = connection.prepareStatement("SELECT nickname FROM client WHERE login = ? AND password = ?;");
        psRegistration = connection.prepareStatement("INSERT INTO client(login, password, nickname) VALUES (? ,? ,? );");
        psChangeNick = connection.prepareStatement("UPDATE client SET nickname = ? WHERE nickname = ?;");

 // добавление сведений о файлах и директориях клиента, которые он сохранил или переместил
// в хранилище в таблицу файлов и директорий в базе данных
        psAddFiles = connection.prepareStatement("INSERT INTO storage (client_id, name_file, path, file) VALUES (\n" +
                "(SELECT client_id FROM clients WHERE nickname=?),\n" +
                "?, ?, ?)");

// выбор из таблицы с файлами и директориями сведений об их местоположении
        psGetListDirAndFileFromStorage = connection.prepareStatement("SELECT name_file FROM storage \n" +
                "WHERE client_id = (SELECT client_id FROM client WHERE nickname=?) ORDER BY name_file");
// выбрать файл из базы данных для копирования или перемещения
        psGetFileFromStorage = connection.prepareStatement("SELECT file FROM storage \n" +
                "WHERE client_id = (SELECT client_id FROM client WHERE nickname=?) " +
                "AND name_file = ? AND path = ?");
// удалить файл из базы данных при пперемещении
        psDeleteFileFromStorage = connection.prepareStatement("DELETE FROM storage \n" +
                "WHERE client_id = (SELECT client_id FROM client WHERE nickname=?) " +
                "AND name_file = ? AND path = ?");

    }
// возвращает имя из базы данных по логину и паролю
    public static String getNicknameByLoginAndPassword(String login, String password) {
        String nick = null;
        try {
            psGetNickname.setString(1, login);
            psGetNickname.setString(2, password);
            ResultSet rs = psGetNickname.executeQuery();
            if (rs.next()) {
                nick = rs.getString(1);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nick;
    }
// вносит изменения в базу данных - данные о новом пользователе (логин, пароль, имя)
    public static boolean registration(String login, String password, String nickname) {
        try {
            psRegistration.setString(1, login);
            psRegistration.setString(2, password);
            psRegistration.setString(3, nickname);
            psRegistration.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
// вносит изменения в базу данных - меняет имя пользователя на новое
    public static boolean changeNick(String oldNickname, String newNickname) {
        try {
            psChangeNick.setString(1, newNickname);
            psChangeNick.setString(2, oldNickname);
            psChangeNick.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

// добавляет в базу данных файлы и сведения о их местоположении
    public static boolean addFiles(String nickname, String name_file, String path, byte[] file) {
        try {
            psAddFiles.setString(1, nickname);
            psAddFiles.setString(2, name_file);
            psAddFiles.setString(3, path);
            psAddFiles.setBytes(4, file);
            psAddFiles.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
// получаем список файлов из директории
    public static String getListDirAndFileFromStorage(String nickname) {
        StringBuilder sb = new StringBuilder();

        try {
            psGetListDirAndFileFromStorage.setString(1, nickname);
            ResultSet rs = psGetListDirAndFileFromStorage.executeQuery();

            while (rs.next()) {
                String string = rs.getString(1);
                sb.append(String.format("%s\n", string));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    // получаем файл из базы данных
    public static byte[] getFileFromStorage(String nickname, String name_file, String path) {
        System.out.println(nickname+" "+name_file+" "+path);
        byte[] bytes = {};
        try {
            psGetFileFromStorage.setString(1, nickname);
            psGetFileFromStorage.setString(2, name_file);
            psGetFileFromStorage.setString(3, path);
            ResultSet rs = psGetFileFromStorage.executeQuery();
            if (rs.next()){
                bytes = rs.getBytes(1);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bytes;
    }
    // удаляем файл из базы данных
    public static void getDeleteFromStorage(String nickname, String name_file, String path) {
        System.out.println(nickname+" "+name_file+" "+path);
        try {
            psDeleteFileFromStorage.setString(1, nickname);
            psDeleteFileFromStorage.setString(2, name_file);
            psDeleteFileFromStorage.setString(3, path);
            psDeleteFileFromStorage.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void disconnect() {
        try {
            psRegistration.close();
            psGetNickname.close();
            psChangeNick.close();
            psAddFiles.close();
            psGetListDirAndFileFromStorage.close();
            psGetFileFromStorage.close();
            psDeleteFileFromStorage.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
