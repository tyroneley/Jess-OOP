import java.io.*;

public class Datastore {
    public static int highScore = 0;

    public static void saveHighScore(int score) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("data.txt"))) {
            outputStream.writeObject(score);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void resetHighScore() {
        saveHighScore(0);
    }

    public static void loadHighScore() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("data.txt"))) {
            highScore = (int) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            highScore = 0;
        }
    }

    public static int getHighScore() {
        return highScore;
    }
}
