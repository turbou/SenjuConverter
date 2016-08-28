package jp.co.tabocom.senjuconverter.preference;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

public class PreferenceDialog extends Dialog<Settings> {

    public PreferenceDialog() {
        setTitle("千手変換ツール");
        setHeaderText("基本設定");
        getDialogPane().setPrefSize(720, 320);
        getDialogPane().setStyle("-fx-background-color: lightgray;");
        ButtonType saveButtonType = new ButtonType("Save", ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));
        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();
        ColumnConstraints col3 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2, col3);

        TextField localRuleXmlTxt = new TextField();
        localRuleXmlTxt.setPromptText("C:\\Users\\turbou\\Desktop\\rule_common.xml");
        grid.add(new Label("ローカルのルール定義："), 0, 0, 1, 1);
        grid.add(localRuleXmlTxt, 1, 0, 1, 1);
        grid.add(new Button("参照"), 2, 0, 1, 1);

        GridPane otherGridPane = new GridPane();
        otherGridPane.setVgap(10);
        otherGridPane.setHgap(10);
        otherGridPane.setPadding(new Insets(5, 5, 5, 5));
        otherGridPane.setStyle("-fx-border-color: gray;");
        ColumnConstraints column1 = new ColumnConstraints();
        ColumnConstraints column2 = new ColumnConstraints();
        ColumnConstraints column3 = new ColumnConstraints();
        column2.setHgrow(Priority.ALWAYS);
        otherGridPane.getColumnConstraints().addAll(column1, column2, column3);

        TextField defaultSnjDirTxt = new TextField();
        defaultSnjDirTxt.setPromptText("C:\\Users\\oyoyo\\Desktop");
        otherGridPane.add(new Label("デフォルト千手ディレクトリ："), 0, 0, 1, 1);
        otherGridPane.add(defaultSnjDirTxt, 1, 0, 1, 1);
        otherGridPane.add(new Button("参照"), 2, 0, 1, 1);

        TextField defaultOutDirTxt = new TextField();
        defaultOutDirTxt.setPromptText("C:\\Users\\oyoyo\\Desktop");
        otherGridPane.add(new Label("デフォルト出力ディレクトリ："), 0, 1, 1, 1);
        otherGridPane.add(defaultOutDirTxt, 1, 1, 1, 1);
        otherGridPane.add(new Button("参照"), 2, 1, 1, 1);

        grid.add(otherGridPane, 0, 1, 3, 1);

//        Group group1 = new Group();
        GridPane    grid2        = new GridPane();
        Text        gridNode1   = new Text("【その１abc】") ;
        Text        gridNode2   = new Text("【その２】") ;
        Text        gridNode3   = new Text("【その３】") ;
        Text        gridNode4   = new Text("【その４】") ;
        Text        gridNode5   = new Text("【その５】") ;
        grid2.add( gridNode1 , 0 , 0 , 1 , 1 );
        grid2.add( gridNode2 , 1 , 0 , 1 , 1 );
        grid2.add( gridNode3 , 0 , 1 , 2 , 1 );
        grid2.add( gridNode4 , 2 , 1 , 1 , 2 );
        grid2.add( gridNode5 , 0 , 2 , 2 , 1 );
//        group1.getChildren().add(grid2);
        grid2.setStyle("-fx-border-color: blue green red darkOliveGreen;");
        TextField proxyHost = new TextField();
        proxyHost.setPrefWidth(400);
        proxyHost.setPromptText("172.95.100.104");
        TextField proxyPort = new TextField();
        proxyPort.setPromptText("80");
        TextField deliveryDefUrl = new TextField();
        deliveryDefUrl.setPrefWidth(450);
        deliveryDefUrl.setPromptText("http://apf-devcloud/stash/projects/ALLAPF/repos/testdeliverydef/browse/");
//        grid.add(new Label("Proxy:"), 0, 0, 1, 1);
//        grid.add(proxyHost, 1, 0, 1, 1);
//        grid.add(proxyPort, 2, 0, 1, 1);
//        grid.add(new Label("DeliveryDef URL:"), 0, 1, 1, 1);
//        grid.add(deliveryDefUrl, 1, 1, 2, 1);
//        grid.add(gridPane, 0, 2, 2, 1);
        getDialogPane().setContent(grid);

        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Settings settings = new Settings();
                settings.setDefaultSnjDir("oyoyo");
                return settings;
            }
            return null;
        });
    }
}
