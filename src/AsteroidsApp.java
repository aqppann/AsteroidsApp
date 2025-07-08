import javafx.animation.AnimationTimer; // Імпортуємо AnimationTimer для анімації
import javafx.application.Application; // Імпортуємо клас Application для запуску JavaFX додатка
import javafx.geometry.Point2D; // Імпортуємо клас Point2D для збереження координат
import javafx.scene.Scene; // Імпортуємо клас Scene для створення сцени
import javafx.scene.control.Button; // Імпортуємо клас Button для створення кнопок
import javafx.scene.input.KeyCode; // Імпортуємо клас KeyCode для обробки натискання клавіш
import javafx.scene.layout.*; // Імпортуємо різні типи Layout для компонування елементів
import javafx.scene.paint.Color; // Імпортуємо клас Color для кольорів
import javafx.scene.text.Font; // Імпортуємо клас Font для налаштування шрифтів
import javafx.scene.text.Text; // Імпортуємо клас Text для створення текстових елементів
import javafx.stage.Stage; // Імпортуємо клас Stage для створення вікна додатку
import java.util.ArrayList; // Імпортуємо клас ArrayList для роботи з динамічними списками
import java.util.List; // Імпортуємо інтерфейс List для роботи зі списками

public class AsteroidsApp extends Application { // Основний клас гри, що розширює Application для роботи з JavaFX
    private Pane gameRoot; // Основний контейнер для елементів гри
    private StackPane rootStack; // Контейнер для основної гри та накладання елементів
    private VBox overlay; // Контейнер для накладання елементів після завершення гри
    private Scene menuScene, gameScene, rulesScene; // Сцени для меню, гри та правил
    private final List<GameObject> bullets = new ArrayList<>(); // Список куль (снарядів)
    private final List<GameObject> enemies = new ArrayList<>(); // Список ворогів (астероїдів)
    private Player player; // Гравець
    private AnimationTimer timer; // Таймер для оновлення гри
    private Stage primaryStage; // Головне вікно гри
    private int score = 0; // Лічильник балів
    private int level = 1; // Поточний рівень гри
    private double enemySpawnRate = 0.01; // Швидкість появи ворогів
    private Text scoreText; // Текст для відображення балів
    private Text levelText; // Текст для відображення рівня
    private Text congratsText; // Текст для відображення виграшу

    @Override
    public void start(Stage stage) { // Метод для запуску додатку
        this.primaryStage = stage; // Присвоюємо головне вікно гри
        createMenu(); // Меню
        createRules(); // Правила гри
        createGame(); // Гра

        primaryStage.setTitle("Asteroids Game"); // Встановлюємо заголовок вікна
        primaryStage.setScene(menuScene); // Встановлюємо сцену меню
        primaryStage.show(); // Показуємо вікно гри
    }

    private void createMenu() { // Створюємо меню гри
        VBox menu = new VBox(30); // Контейнер для кнопок меню, з відступами між елементами
        menu.setStyle("-fx-alignment: center; -fx-background-color: black;"); // Стилі для меню
        menu.setPrefSize(600, 600); // Розміри меню

        Button startBtn = new Button("START"); // Кнопка для початку гри
        Button rulesBtn = new Button("RULES"); // Кнопка для перегляду правил гри

        startBtn.setPrefSize(75, 35); // Розміри кнопки Start
        rulesBtn.setPrefSize(75, 35); // Розміри кнопки Rules
        startBtn.setOnAction(e -> { // Натискання на кнопку Start
            resetGame(); // Скидаємо гру
            primaryStage.setScene(gameScene); // Перемикаємо на сцену гри
            timer.start(); // Запускаємо таймер гри
        });

        rulesBtn.setOnAction(e -> primaryStage.setScene(rulesScene)); // Натискання на кнопку Rules

        menu.getChildren().addAll(startBtn, rulesBtn); // Додаємо кнопки до меню
        menuScene = new Scene(menu); // Створюємо сцену для меню
    }

    private void createRules() { // Створюємо сцену з правилами гри
        VBox rules = new VBox(20); // Контейнер для тексту правил та кнопки назад
        rules.setStyle("-fx-alignment: center; -fx-background-color: black;"); // Стилі для сцени з правилами
        rules.setPrefSize(600, 600); // Розміри сцени з правилами

        Text ruleText = new Text("Rules:\n- Avoid asteroids\n- Shoot them with SPACE\n- Use arrows to rotate\n- Colliding = Game Over"); // Текст з правилами
        ruleText.setFill(Color.WHITE); // Колір тексту
        ruleText.setFont(Font.font("Verdana", 20)); // Шрифт тексту

        Button backBtn = new Button("BACK TO MAIN MENU"); // Кнопка для повернення до головного меню
        backBtn.setOnAction(e -> primaryStage.setScene(menuScene)); // Натискання на кнопку Back

        rules.getChildren().addAll(ruleText, backBtn); // Додаємо текст правил і кнопку назад до контейнера
        rulesScene = new Scene(rules); // Створюємо сцену з правилами
    }

    private void createGame() { // Створюємо сцену гри
        gameRoot = new Pane(); // Панель для елементів гри
        gameRoot.setPrefSize(600, 600); // Розміри панелі
        gameRoot.setStyle("-fx-background-color: black;"); // Стиль для панелі гри (чорний фон)

        scoreText = new Text("Score: 0"); // Текст для відображення балів
        scoreText.setFill(Color.WHITE); // Колір тексту
        scoreText.setFont(Font.font(20)); // Шрифт тексту
        scoreText.setTranslateX(10); // Позиція тексту по осі X
        scoreText.setTranslateY(20); // Позиція тексту по осі Y

        levelText = new Text("Level: 1"); // Текст для відображення рівня
        levelText.setFill(Color.WHITE); // Колір тексту
        levelText.setFont(Font.font(20)); // Шрифт тексту
        levelText.setTranslateX(10); // Позиція тексту по осі X
        levelText.setTranslateY(50); // Позиція тексту по осі Y

        congratsText = new Text("CONGRATULATION!"); // Текст повідомлення про перемогу
        congratsText.setFill(Color.WHITE); // Колір тексту
        congratsText.setFont(Font.font("Verdana", 40)); // Шрифт тексту
        congratsText.setVisible(false); // Спочатку приховуємо текст
        congratsText.setTranslateX(125); // Позиція тексту по осі X
        congratsText.setTranslateY(500); // Позиція тексту по осі Y

        overlay = new VBox(20); // Контейнер для накладання елементів після завершення гри
        overlay.setStyle("-fx-alignment: center;"); // Стиль для накладання
        overlay.setPrefSize(600, 600); // Розміри накладу
        overlay.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.7), null, null))); // Фон накладання

        Button restartBtn = new Button("RESTART"); // Кнопка для перезапуску гри
        Button menuBtn = new Button("MENU"); // Кнопка для повернення до головного меню

        restartBtn.setPrefSize(75, 35); // Розміри кнопки Restart
        menuBtn.setPrefSize(75, 35); // Розміри кнопки Menu

        restartBtn.setOnAction(e -> { // Натискання на кнопку Restart
            resetGame(); // Скидаємо гру
            timer.start(); // Запускаємо таймер гри
        });

        menuBtn.setOnAction(e -> primaryStage.setScene(menuScene)); // Натискання на кнопку Menu

        overlay.getChildren().addAll(restartBtn, menuBtn); // Додаємо кнопки до накладання
        overlay.setVisible(false); // Приховуємо накладання

        gameRoot.getChildren().addAll(scoreText, levelText, congratsText); // Додаємо текстові елементи до гри
        rootStack = new StackPane(gameRoot, overlay); // Обгортаємо гру та накладання у StackPane

        gameScene = new Scene(rootStack); // Створюємо сцену гри

        timer = new AnimationTimer() { // Таймер для оновлення гри
            @Override
            public void handle(long now) {
                updateGame(); // Оновлення гри
            }
        };

        gameScene.setOnKeyPressed(e -> { // Обробка натискання клавіш
            if (e.getCode() == KeyCode.LEFT) player.rotateLeft(); // Поворот вліво
            else if (e.getCode() == KeyCode.RIGHT) player.rotateRight(); // Поворот вправо
            else if (e.getCode() == KeyCode.SPACE) shoot(); // Стрільба
        });
    }

    private void resetGame() { // Скидаємо гру до початкового стану
        overlay.setVisible(false); // Приховуємо накладання
        gameRoot.getChildren().clear(); // Очищаємо панель гри
        bullets.clear(); // Очищаємо список снарядів
        enemies.clear(); // Очищаємо список астероїдів
        score = 0; // Скидаємо бали
        level = 1; // Скидаємо рівень
        enemySpawnRate = 0.01; // Встановлюємо початкову швидкість появи ворогів
        congratsText.setVisible(false); // Приховуємо текст перемоги

        player = new Player(); // Створюємо нового гравця
        player.setVelocity(new Point2D(1, 0)); // Встановлюємо швидкість гравця
        addGameObject(player, 300, 300); // Додаємо гравця на екран

        gameRoot.getChildren().addAll(scoreText, levelText, congratsText); // Додаємо текстові елементи
        updateScoreDisplay(); // Оновлюємо відображення балів
    }

    private void shoot() { // Функція для стрільби
        Bullet bullet = new Bullet(); // Створюємо снаряд
        bullet.setVelocity(player.getVelocity().normalize().multiply(5)); // Встановлюємо швидкість
        addBullet(bullet, player.getView().getTranslateX(), player.getView().getTranslateY()); // Додаємо на екран
    }

    private void addBullet(GameObject bullet, double x, double y) { // Додаємо снаряд до гри
        bullets.add(bullet); // Додаємо снаряд до списку
        addGameObject(bullet, x, y); // Додаємо снаряд на екран
    }

    private void addEnemy(GameObject enemy, double x, double y) { // Додаємо астероїди до гри
        enemies.add(enemy); // Додаємо астероїди до списку
        addGameObject(enemy, x, y); // Додаємо астероїди на екран
    }

    private void addGameObject(GameObject object, double x, double y) { // Додаємо об'єкти (гравець, астероїди, снаряд) на екран
        object.getView().setTranslateX(x); // Встановлюємо позицію по осі X
        object.getView().setTranslateY(y); // Встановлюємо позицію по осі Y
        gameRoot.getChildren().add(object.getView()); // Додаємо об'єкти на екран
    }

    private void updateGame() { // Оновлюємо гру
        for (GameObject bullet : bullets) { // Оновлюємо всі снаряди
            bullet.update(); // Оновлюємо снаряд
            for (GameObject enemy : enemies) { // Перевіряємо зіткнення снаряду з астероїдами
                if (bullet.isColliding(enemy)) { // Якщо снаряд зіткнувся з астероїдом
                    bullet.setAlive(false); // Знищуємо снаряд
                    enemy.setAlive(false); // Знищуємо астероїд
                    gameRoot.getChildren().removeAll(bullet.getView(), enemy.getView()); // Видаляємо об'єкти з екрану

                    score++; // Збільшуємо бали
                    updateScoreDisplay(); // Оновлюємо відображення балів
                    checkLevelProgression(); // Перевіряємо, чи потрібно перейти на наступний рівень
                }
            }
        }

        bullets.removeIf(GameObject::isDead); // Видаляємо всі "використані" елементи зі списку
        enemies.removeIf(GameObject::isDead);

        for (GameObject enemy : enemies) { // Оновлюємо всі астероїди
            enemy.update();
            if (enemy.isColliding(player)) { // Якщо гравець зіткнувся з астероїдом
                gameOver(); // Завершуємо гру
                return;
            }
        }

        player.update(); // Оновлюємо гравця
        confineToBounds(player); // Обмежуємо рух гравця по екрану

        if (Math.random() < enemySpawnRate) { // Випадкова генерація астероїдів
            addEnemy(new Enemy(), Math.random() * gameRoot.getPrefWidth(), Math.random() * gameRoot.getPrefHeight());
        }
    }

    private void confineToBounds(GameObject obj) { // Обмежуємо об'єкти межами екрану
        double x = obj.getView().getTranslateX(); // Отримуємо поточну позицію по осі X
        double y = obj.getView().getTranslateY(); // Отримуємо поточну позицію по осі Y

        if (x < 0 || x > gameRoot.getPrefWidth() || y < 0 || y > gameRoot.getPrefHeight()) { // Якщо об'єкти вийшли за межі екрану
            obj.setVelocity(obj.getVelocity().multiply(-1)); // Відбиваємо об'єкти від меж екрану
        }
    }

    private void gameOver() { // Завершення гри
        timer.stop(); // Зупиняємо таймер гри
        overlay.setVisible(true); // Показуємо накладання
    }

    private void updateScoreDisplay() { // Оновлення відображення балів і рівня
        scoreText.setText("Score: " + score); // Оновлюємо текст балів
        levelText.setText("Level: " + level); // Оновлюємо текст рівня
    }

    private void checkLevelProgression() { // Перевірка прогресу гри
        if (level == 1 && score >= 5) { // Якщо на першому рівні набрано 5 очок
            level = 2; // Переходимо на другий рівень
            enemySpawnRate = 0.015; // Збільшуємо швидкість появи ворогів
        } else if (level == 2 && score >= 10) { // Якщо на другому рівні набрано 10 очок
            level = 3; // Переходимо на третій рівень
            enemySpawnRate = 0.025; // Збільшуємо швидкість появи ворогів
        } else if (level == 3 && score >= 15) { // Якщо на третьому рівні набрано 15 очок
            congratsText.setVisible(true); // Показуємо повідомлення про перемогу
            timer.stop(); // Зупиняємо гру
        }
        updateScoreDisplay(); // Оновлюємо відображення балів і рівня
    }

    public static void main(String[] args) { // Головний метод для запуску програми
        launch(args); // Запускаємо додаток
    }
}
