package sample;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Paths;

import javafx.animation.*;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PrintingImage extends Application{
		private HeaderBitmapImage image; // приватне поле, яке зберігає об'єкт з інформацією про заголовок зображення
	private int numberOfPixels; // приватне поле для збереження кількості пікселів з чорним кольором

	Color umbrella = Color.rgb(130, 17, 60);
	Color underUmbrella = Color.rgb(225, 0, 132, 0.4);
	Color handleColor = Color.rgb(245,245,245);
	Color whiteGlimmers = Color.rgb(255, 255, 255, 0.2);

	public PrintingImage(){}

	public PrintingImage(HeaderBitmapImage image) // перевизначений стандартний конструктор
	{
		this.image = image;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		String path = Paths.get(".", "sources", "trajectory.bmp").toString();
		ReadingImageFromFile.loadBitmapImage(path);
		this.image = ReadingImageFromFile.pr.image;
		int width = (int)this.image.getWidth();
		int height = (int)this.image.getHeight();
		int half = (int)image.getHalfOfWidth();
		Group root = new Group();
		Scene scene = new Scene (root, width, height);
		Circle cir;
		int let = 0;
		int let1 = 0;
		int let2 = 0;
		char[][] map = new char[width][height];

		// виконуємо зчитування даних про пікселі
		BufferedInputStream reader = new BufferedInputStream (new FileInputStream("pixels.txt"));

		for(int i=0;i<height;i++)  // поки не кінець зображення по висоті
		{
			for(int j=0;j<half;j++)  // поки не кінець зображення по довжині
			{
				let = reader.read();  // зчитуємо один символ з файлу
				let1 = let;
				let2 = let;
				let1 = let1&(0xf0);  // старший байт - перший піксель
				let1 = let1>>4;  // зсув на 4 розряди
				let2 = let2&(0x0f);  // молодший байт - другий піксель
				if(j*2<width) // так як 1 символ кодує 2 пікселі нам необхідно пройти до середини ширини зображення
				{
					cir = new Circle ((j)*2,(height-1-i),1,Color.valueOf((returnPixelColor(let1)))); // за допомогою стандартного
					// примітива Коло радіусом в 1 піксель та кольором визначеним за допомогою методу returnPixelColor малюємо піксель
					//root.getChildren().add(cir); //додаємо об'єкт в сцену
					if (returnPixelColor(let1) == "BLACK") // якщо колір пікселя чорний, то ставимо в масиві 1
					{
						map[j*2][height-1-i] = '1';
						numberOfPixels++; // збільшуємо кількість чорних пікселів
					}
					else
					{
						map[j*2][height-1-i] = '0';
					}
				}

				if(j*2+1<width) // для другого пікселя
				{
					cir = new Circle ((j)*2+1,(height-1-i),1,Color.valueOf((returnPixelColor(let2))));
					//root.getChildren().add(cir);
					if (returnPixelColor(let2) == "BLACK")
					{
						map[j*2+1][height-1-i] = '1';
						numberOfPixels++;
					}
					else
					{
						map[j*2+1][height-1-i] = '0';
					}
				}
			}
		}
		primaryStage.setScene(scene); // ініціалізуємо сцену
		primaryStage.show(); // візуалізуємо сцену
		reader.close();

		int[][] black;
		black = new int[numberOfPixels][2];
		int lich = 0;

		BufferedOutputStream writer = new BufferedOutputStream (new FileOutputStream("map.txt")); // записуємо карту для руху по траекторії в файл
		for(int i=0;i<height;i++)     // поки не кінець зображення по висоті
		{
			for(int j=0;j<width;j++)         // поки не кінець зображення по довжині
			{
				if (map[j][i] == '1')
				{
					black[lich][0] = j;
					black[lich][1] = i;
					lich++;
				}
				writer.write(map[j][i]);
			}
			writer.write(10);
		}
		writer.close();

		System.out.println("number of black color pixels = " + numberOfPixels);

		Path path2 = new Path();
		for (int l=0; l<numberOfPixels-1; l++)
		{
			path2.getElements().addAll(
					new MoveTo(black[l][0],black[l][1]),
					new LineTo(black[l+1][0],black[l+1][1])
			);
		}

		// Under umbrella
		Ellipse el1 = new Ellipse(228, 167, 80.5, 80.5);
		el1.setFill(underUmbrella);
		root.getChildren().add(el1);

		Ellipse el2 = new Ellipse(177, 185, 40, 40);
		el2.setFill(Color.WHITE);
		root.getChildren().add(el2);

		Ellipse el3 = new Ellipse(231, 178, 40, 40);
		el3.setFill(Color.WHITE);
		root.getChildren().add(el3);

		Ellipse el4 = new Ellipse(277, 172, 44, 44);
		el4.setFill(Color.WHITE);
		root.getChildren().add(el4);

		Ellipse el5 = new Ellipse(231, 218, 64, 32);
		el5.setFill(Color.WHITE);
		root.getChildren().add(el5);

		// Handle under umbrella
		MoveTo mt1 = new MoveTo(218, 118);
		LineTo lt2 = new LineTo(230, 220);
		QuadCurveTo qt = new QuadCurveTo(238, 228, 245, 220);
		LineTo lt3 = new LineTo(245, 212);
		Path handle = new Path();
		handle.setStrokeWidth(2);
		handle.setStroke(handleColor);
		handle.getElements().addAll(mt1, lt2, qt, lt3);
		root.getChildren().add(handle);

		// Pin on top of umbrella
		MoveTo mt2 = new MoveTo(212, 86);
		LineTo lt1 = new LineTo(211, 76);
		Path pin = new Path();
		pin.setStrokeWidth(2);
		pin.setStroke(handleColor);
		pin.getElements().addAll(mt2, lt1);
		root.getChildren().add(pin);

		// White lines on top of umbrella
		QuadCurveTo qt1 = new QuadCurveTo(195, 110, 200, 151);
		QuadCurveTo qt2 = new QuadCurveTo(230, 100, 246, 140);
		Path bottomLines = new Path();
		bottomLines.setStrokeWidth(2);
		bottomLines.setStroke(handleColor);
		bottomLines.getElements().addAll(mt2, qt1, mt2, qt2);
		root.getChildren().add(bottomLines);

		// Top of umbrella
		MoveTo mt3 = new MoveTo(146, 161.5);
		QuadCurveTo qt3 = new QuadCurveTo(160, 100, 211, 86);
		QuadCurveTo qt4 = new QuadCurveTo(277, 119, 250, 126);
		QuadCurveTo qt5 = new QuadCurveTo(217, 117, 188, 134);
		QuadCurveTo qt6 = new QuadCurveTo(164, 140, 146, 161.5);
		QuadCurveTo qt7 = new QuadCurveTo(275, 76, 302, 135);
		Path top = new Path();
		top.setStrokeWidth(2);
		top.setStroke(handleColor);
		top.setFill(umbrella);
		top.getElements().addAll(mt3, qt3, qt7, qt4, qt5, qt6);
		root.getChildren().add(top);

		// White lines on top of umbrella
		QuadCurveTo qt8 = new QuadCurveTo(191, 110, 190, 132);
		QuadCurveTo qt9 = new QuadCurveTo(240, 100, 250, 126);
		Path topLines = new Path();
		topLines.setStrokeWidth(2);
		topLines.setStroke(handleColor);
		topLines.getElements().addAll(mt2, qt8, mt2, qt9);
		root.getChildren().add(topLines);

		// White glimmers
		QuadCurveTo glimmer1 = new QuadCurveTo(214, 83, 200, 122);
		Path glimmerPath1 = new Path();
		glimmerPath1.setStroke(Color.TRANSPARENT);
		glimmerPath1.setFill(whiteGlimmers);
		glimmerPath1.getElements().addAll(new MoveTo(238, 116), glimmer1);
		root.getChildren().add(glimmerPath1);

		Ellipse glimmer2 = new Ellipse(255, 105, 8, 18);
		glimmer2.setFill(whiteGlimmers);
		glimmer2.setRotate(-50);
		root.getChildren().add(glimmer2);

		MoveTo mt4 = new MoveTo(197, 97);
		QuadCurveTo glimmer3 = new QuadCurveTo(182, 138, 164, 140);
		QuadCurveTo glimmer4 = new QuadCurveTo(178, 106, 164, 140);
		Path glimmerPath2 = new Path();
		glimmerPath2.setStroke(Color.TRANSPARENT);
		glimmerPath2.setFill(whiteGlimmers);
		glimmerPath2.getElements().addAll(mt4, glimmer3, mt4, glimmer4);
		root.getChildren().add(glimmerPath2);

		PathTransition pathTransition = new PathTransition();
		pathTransition.setDuration(Duration.millis(5000));
		pathTransition.setPath(path2);
		pathTransition.setNode(root);
		pathTransition.setAutoReverse(true);

		RotateTransition rotateTransition = new RotateTransition(Duration.millis(2000), root);
		rotateTransition.setByAngle(360f);
		rotateTransition.setCycleCount(3);

		ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(5000), root);
		scaleTransition.setToX(0.5f);
		scaleTransition.setToY(0.5f);
		scaleTransition.setAutoReverse(true);

		ParallelTransition parallelTransition = new ParallelTransition();
		parallelTransition.getChildren().addAll(
				rotateTransition,
				scaleTransition,
				pathTransition
		);
		parallelTransition.setCycleCount(Timeline.INDEFINITE);
		parallelTransition.setAutoReverse(true);
		parallelTransition.play();
	}

	// далі необхідно зробити рух об'єкту по заданій траеторії
	private String returnPixelColor (int color) // метод для співставлення кольорів 16-бітного зображення
	{
		String col = "BLACK";
		switch(color)
		{
			case 0: return "BLACK";
			case 1: return "LIGHTCORAL";
			case 2: return "GREEN";
			case 3: return "BROWN";
			case 4: return "BLUE";
			case 5: return "MAGENTA";
			case 6: return "CYAN";
			case 7: return "LIGHTGRAY";
			case 8: return "DARKGRAY";
			case 9: return "RED";
			case 10:return "LIGHTGREEN";
			case 11:return "YELLOW";
			case 12:return "LIGHTBLUE";
			case 13:return "LIGHTPINK";
			case 14:return "LIGHTCYAN";
			case 15:return "WHITE";
		}
		return col;
	}

	public static void main (String args[])
	{
		launch(args);
	}
}
