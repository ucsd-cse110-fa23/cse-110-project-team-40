/* Code initially adapted from Lab1 */

package PantryPal;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.application.Application;
import javafx.event.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

class Footer extends HBox {
	private static final String buttonStyle =
			    "-fx-font-style: italic; -fx-background-color: #FFFFFF;  -fx-font-weight: bold; -fx-font: 11 arial;";
	private void format() {
		this.setSpacing(5); // sets spacing between tasks
		this.setPrefSize(500, 560);
		this.setStyle("-fx-background-color: #F0F8FF;");
		this.setAlignment(Pos.CENTER); // aligning the buttons to center
	}
	public void addButton(String buttontext, EventHandler<ActionEvent> callback) {
		Button button = new Button(buttontext);
		button.setStyle(buttonStyle);
		this.getChildren().add(button);
		button.setOnAction(callback);
	}
	Footer() {
		format();
		this.setAlignment(Pos.CENTER); // aligning the buttons to center
	}
}

class Header extends HBox {
	private void format() {
        	this.setPrefSize(500, 60); // Size of the header
        	this.setStyle("-fx-background-color: #F0F8FF;");
	}

	Header(String title) {
		format();

		Text titleText = new Text(title); // Text of the Header
		titleText.setStyle("-fx-font-weight: bold; -fx-font-size: 20;");
		this.getChildren().add(titleText);
		this.setAlignment(Pos.CENTER);
	}
}

abstract class ScrollablePage extends BorderPane {
	private Header header;
	protected Footer footer;
	protected Node center;

	ScrollablePage(String title, Node center) {
		// Initialise the header Object
		header = new Header(title);
		// Initialise the Footer Object
		footer = new Footer();

		this.center = center;

		ScrollPane sp = new ScrollPane(center);
		sp.setFitToWidth(true);
		sp.setFitToHeight(true);

		// Add header to the top of the BorderPane
		this.setTop(header);
		// Add scroller to the centre of the BorderPane
		this.setCenter(sp);
		// Add footer to the bottom of the BorderPane
		this.setBottom(footer);
	}
}
