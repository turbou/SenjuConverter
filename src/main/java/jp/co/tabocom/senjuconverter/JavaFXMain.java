package jp.co.tabocom.senjuconverter;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import jp.co.tabocom.senjuconverter.preference.PreferenceConstants;
import jp.co.tabocom.senjuconverter.preference.PreferenceDialog;
import jp.co.tabocom.senjuconverter.preference.Settings;

public class JavaFXMain extends Application {

    // この辺はGUIになったら基本設定とかかな
    public static final String PROXY_HOST = "172.95.100.104";
    public static final int PROXY_PORT = 80;
    public static final String RLSDEF_URL_SOGO = "http://apf-devcloud/stash/projects/ALLAPF/repos/sogodeliverydef/browse/";
    public static final String RLSDEF_URL_HONBAN = "http://apf-devcloud/stash/projects/ALLAPF/repos/honbandeliverydef/browse/";

    final ComboBox<String> systemComboBox = new ComboBox<String>();
    final TextField releaseIdText = new TextField("");
    final ScrollPane scrollPane = new ScrollPane();
    final TextFlow textFlow = new TextFlow();
    RadioButton sogoRadio;
    RadioButton honbanRadio;
    Button checkButton;
    Button editorButton;

    Button impSjTxtButton;
    Button impRuleButton;
    Button showRuleButton;
    Button duplicateChkButton;
    Button convertButton;
    Label statusTitleLabel;
    Label statusCountLabel;
    Label statusTotalLabel;
    Button settingButton;

    private StringBuilder buffer;

    private Properties properties;
    private Settings settings;
    public static final String TOOL_PROPERTIES_FILE = "senjutool.properties";

    @Override
    public void start(Stage stage) throws Exception {
        loadPreferences();
        stage.setTitle("千手変換ツール (v1.0.0)");
        stage.getIcons().add(new Image("icon16.png"));
        stage.getIcons().add(new Image("icon32.png"));
        stage.getIcons().add(new Image("icon48.png"));
        stage.setResizable(false);
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 400, 270, Color.WHITE);

        releaseIdText.setStyle("-fx-min-height: 30;-fx-font-size: 16; -fx-font-family: Meiryo UI;");
        releaseIdText.setPromptText("MH_CRM_20160412_01");
        releaseIdText.setText("MH_CRM_20160412_01");

        impSjTxtButton = new Button("千手データ取り込み（増幅・変換対象）");
        impSjTxtButton.setStyle("-fx-font-size: 13; -fx-font-family: Meiryo UI;");
        impSjTxtButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser dialog = new FileChooser();
                dialog.setTitle("千手オフライザファイルを選択してください。");
                dialog.getExtensionFilters().add(new ExtensionFilter("Text Files", "*.txt"));
                String defaultDir = properties.getProperty(PreferenceConstants.DEFAULT_SNJ_DIR);
                if (defaultDir == null || defaultDir.isEmpty()) {
                    dialog.setInitialDirectory(new File("C:\\"));
                } else {
                    dialog.setInitialDirectory(new File(defaultDir));
                }
                List<File> selectedFiles = dialog.showOpenMultipleDialog(stage);
                if (selectedFiles == null) {
                    // ファイルが選択されなければそのまま終わり
                    return;
                }
            }
        });
        impRuleButton = new Button("変換ルールファイル取り込み");
        impRuleButton.setStyle("-fx-font-size: 14; -fx-font-family: Meiryo UI;");
        showRuleButton = new Button("定義確認");
        showRuleButton.setStyle("-fx-font-size: 12; -fx-font-family: Meiryo UI;");
        duplicateChkButton = new Button("重複チェック");
        duplicateChkButton.setStyle("-fx-font-size: 12; -fx-font-family: Meiryo UI;");
        convertButton = new Button("変換実行");
        convertButton.setStyle("-fx-font-size: 18; -fx-font-family: Meiryo UI;");
        settingButton = new Button("基本設定");
        settingButton.setStyle("-fx-font-family: Meiryo UI;");
        settingButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Settings curSettings = new Settings();
                curSettings.setDefaultSnjDir(properties.getProperty(PreferenceConstants.DEFAULT_SNJ_DIR));
                PreferenceDialog dialog = new PreferenceDialog(curSettings);
                Optional<Settings> result = dialog.showAndWait();
                result.ifPresent(settings -> {
                    properties.setProperty(PreferenceConstants.DEFAULT_SNJ_DIR, settings.getDefaultSnjDir());
                    savePreferences();
                });
            }
        });
        statusTitleLabel = new Label();
        statusCountLabel = new Label();
        statusTotalLabel = new Label();

        checkButton = new Button("Check");
        checkButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                final String rlsId = releaseIdText.getText();
                if (rlsId.isEmpty()) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("千手変換ツール (v1.0.0)");
                    alert.setHeaderText("リリースIDが入力されていません。");
                    alert.setContentText("http://apf-devcloud/stash/projects/ALLAPF/repos/testdeliverydef/browse");
                    alert.showAndWait();
                    return;
                }
                final String project = systemComboBox.getValue();
                buffer = new StringBuilder();
                textFlow.getChildren().clear();
                Task<Void> task = new Task<Void>() {
                    public Void call() throws Exception {
                        return null;
                    }

                    @Override
                    protected void running() {
                        systemComboBox.setDisable(true);
                        releaseIdText.setEditable(false);
                        checkButton.setDisable(true);
                        editorButton.setDisable(true);
                    }

                    @Override
                    protected void succeeded() {
                        systemComboBox.setDisable(false);
                        releaseIdText.setEditable(true);
                        checkButton.setDisable(false);
                        editorButton.setDisable(false);
                    }

                };
                Thread th = new Thread(task);
                th.setDaemon(true);
                th.start();
            }
        });
        // textFlow.setPrefSize(600, 605);
        textFlow.setStyle("-fx-background-color: white;");
        textFlow.getChildren().addListener(new ListChangeListener<javafx.scene.Node>() {
            @Override
            public void onChanged(javafx.collections.ListChangeListener.Change<? extends javafx.scene.Node> arg0) {
                textFlow.layout();
                scrollPane.layout();
                scrollPane.setVvalue(1.0f);
            }
        });
        scrollPane.setStyle("-fx-fit-to-height: true; -fx-fit-to-width: true;");
        scrollPane.setContent(textFlow);

        ToggleGroup deliveryDefGroup = new ToggleGroup();
        sogoRadio = new RadioButton("総合");
        sogoRadio.setToggleGroup(deliveryDefGroup);
        honbanRadio = new RadioButton("本番");
        honbanRadio.setToggleGroup(deliveryDefGroup);
        HBox deliveryDefBox = new HBox();
        deliveryDefBox.getChildren().add(sogoRadio);
        deliveryDefBox.getChildren().add(honbanRadio);
        deliveryDefBox.setSpacing(10);

        systemComboBox.getItems().addAll("apf", "apf-mh");
        systemComboBox.setValue("apf-mh");
        systemComboBox.setTooltip(new Tooltip("Stashのプロジェクトを指定してください。"));

        editorButton = new Button("TextEditor");
        editorButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                final String rlsId = releaseIdText.getText();
                if (rlsId.isEmpty() || buffer == null) {
                    return;
                }
                try {
                    File tempFile = File.createTempFile(rlsId, ".txt");
                    FileWriter filewriter = new FileWriter(tempFile);
                    filewriter.write(buffer.toString());
                    filewriter.close();
                    tempFile.deleteOnExit();
                    Desktop.getDesktop().edit(tempFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.setStyle("-fx-background-color: lightgray;");
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setPadding(new Insets(5, 5, 5, 5));

        // 想定として３列、６行の１２マス
        ColumnConstraints column1 = new ColumnConstraints();
        ColumnConstraints column2 = new ColumnConstraints();
        ColumnConstraints column3 = new ColumnConstraints();
        column2.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(column1, column2, column3);
        RowConstraints row1 = new RowConstraints(30);
        RowConstraints row2 = new RowConstraints();
        RowConstraints row3 = new RowConstraints();
        RowConstraints row4 = new RowConstraints(45);
        RowConstraints row5 = new RowConstraints(5);
        RowConstraints row6 = new RowConstraints(15);
        RowConstraints row7 = new RowConstraints(5);
        RowConstraints row8 = new RowConstraints();
        // row5.setVgrow(Priority.ALWAYS);
        gridPane.getRowConstraints().addAll(row1, row2, row3, row4, row5, row6, row7, row8);

        // １行目
        impSjTxtButton.setMaxWidth(Double.MAX_VALUE);
        impSjTxtButton.setMaxHeight(Double.MAX_VALUE);
        gridPane.add(impSjTxtButton, 0, 0, 3, 1);

        // ２行目, ３行目
        impRuleButton.setMaxWidth(Double.MAX_VALUE);
        impRuleButton.setMaxHeight(Double.MAX_VALUE);
        gridPane.add(impRuleButton, 0, 1, 2, 2);
        showRuleButton.setMaxWidth(Double.MAX_VALUE);
        showRuleButton.setMaxHeight(Double.MAX_VALUE);
        gridPane.add(showRuleButton, 2, 1, 1, 1);
        duplicateChkButton.setMaxWidth(Double.MAX_VALUE);
        duplicateChkButton.setMaxHeight(Double.MAX_VALUE);
        gridPane.add(duplicateChkButton, 2, 2, 1, 1);

        // ４行目
        convertButton.setMaxWidth(Double.MAX_VALUE);
        convertButton.setMaxHeight(Double.MAX_VALUE);
        gridPane.add(convertButton, 0, 3, 3, 1);

        // ５行目
        gridPane.add(new Separator(), 0, 4, 3, 1);

        // ６行目
        HBox statusBox = new HBox();
        statusBox.getChildren().add(statusTitleLabel);
        statusBox.getChildren().add(statusCountLabel);
        statusBox.getChildren().add(statusTotalLabel);
        gridPane.add(statusBox, 0, 5, 3, 1);

        // ７行目
        gridPane.add(new Separator(), 0, 6, 3, 1);

        // ８行目
        settingButton.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(settingButton, 2, 7, 1, 1);

        root.setCenter(gridPane);
        stage.setScene(scene);
        stage.show();
    }

    private void loadPreferences() {
        this.properties = new Properties();
        try {
            InputStream inputStream = new FileInputStream(TOOL_PROPERTIES_FILE);
            this.properties.load(inputStream);
            inputStream.close();
            this.settings.setDefaultSnjDir(properties.getProperty(PreferenceConstants.DEFAULT_SNJ_DIR));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void savePreferences() {
        try {
            OutputStream outputStream = new FileOutputStream(TOOL_PROPERTIES_FILE);
            this.properties.store(outputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
