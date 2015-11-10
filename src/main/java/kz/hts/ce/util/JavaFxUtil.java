package kz.hts.ce.util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class JavaFxUtil {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
    private static BigDecimal left;
    private static String selectedOperator;
    private static boolean numberInputting;

    public static void getWatch(Label dateLabel) {
        Timeline watch = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dateLabel.setText(simpleDateFormat.format(Calendar.getInstance().getTime()));
            }
        }));
        watch.setCycleCount(Timeline.INDEFINITE);
        watch.play();
    }

    public static void calculator(String buttonText, TextField txtDisplay, TextField txtAdditionalDisplay) {
        if (buttonText.equals("C")) {
            selectedOperator = "";
            numberInputting = false;
            txtDisplay.setText("0");
            txtAdditionalDisplay.setText("");
            return;
        }
        if (buttonText.matches("[0-9]")) {
            if (txtAdditionalDisplay.getText().equals("")) {
                if (!numberInputting) {
                    numberInputting = true;
                    txtDisplay.clear();
                }
                txtDisplay.appendText(buttonText);
                return;
            } else {
                String text = txtAdditionalDisplay.getText();
                text += buttonText;
                txtAdditionalDisplay.setText(text);
                return;
            }
        }
        if (buttonText.matches("[\\.]")) {
            if (txtAdditionalDisplay.getText().equals("")) {
                if (!txtDisplay.getText().contains(".")) {
                    if (!numberInputting) {
                        numberInputting = true;
                        txtDisplay.clear();
                    }
                    txtDisplay.appendText(buttonText);
                    return;
                }
            } else {
                if (!txtAdditionalDisplay.getText().contains(".")) {
                    String text = txtAdditionalDisplay.getText();
                    text += buttonText;
                    txtAdditionalDisplay.setText(text);
                    return;
                }
            }
        }
        if (buttonText.matches("[－×]")) {
            if (buttonText.matches("[×]")) {
                if (txtDisplay.getText().equals("")) {
                    txtDisplay.setText("0");
                }
                txtAdditionalDisplay.setText(buttonText);
            }
            left = new BigDecimal(txtDisplay.getText());
            selectedOperator = buttonText;
            numberInputting = false;
            return;
        }
        if (buttonText.equals("=")) {
            final BigDecimal right = numberInputting ? new BigDecimal(txtDisplay.getText()) : left;
            left = calculate(selectedOperator, left, right);
            txtDisplay.setText(left.toString());
            numberInputting = false;
            return;
        }
    }

    public static BigDecimal calculate(String operator, BigDecimal left, BigDecimal right) {
        switch (operator) {
            case "－":
                return left.subtract(right);
            case "×":
                return left.multiply(right);
            default:
        }
        return right;
    }
}
