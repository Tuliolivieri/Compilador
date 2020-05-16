    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import com.jfoenix.controls.JFXButton;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Border;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

/**
 *
 * @author tulio
 */
public class FXMLDocumentController implements Initializable {

    private Label label;
    @FXML
    private Button btCompilar;
    @FXML
    private Button btClear;
    @FXML
    private TextArea taOutput;
    @FXML
    private Button btClearInput;
    @FXML
    private Button btOpenFile;
    @FXML
    private Button btSaveFile;
    @FXML
    private JFXButton btSaveAs;
    
    private FileChooser fileChooser = new FileChooser();
    private BufferedReader br;
    private File file;
    @FXML
    private CodeArea codeArea;

    private static final String[] KEYWORDS          = {"begin","end"};
    private static final String[] KEYTYPES          = {"int", "flut", "frase"};
    private static final String[] KEYCOMMAND        = {"se", "senao", "enquanto", "para"};
    private static final String KEYWORD_PATTERN     = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String TYPES_PATTERN       = "\\b(" + String.join("|", KEYTYPES) + ")\\b";
    private static final String COMMAND_PATTERN     = "\\b(" + String.join("|", KEYCOMMAND) + ")\\b";
    private static final String SEMICOLON_PATTERN   = "\\;";
    private static final String NUMBER_PATTERN      = "[0-9]+[.0-9]+";
    private static final String STRING_PATTERN      = "'.*'";
    private static final String PAREN_PATTERN       = "\\(|\\)";
    private static final String BRACE_PATTERN       = "\\{|\\}";
    private static final String BRACKET_PATTERN     = "\\[|\\]";
    private static final String COMMENT_PATTERN     = "///.*";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>"       + KEYWORD_PATTERN + ')'
            + "|(?<TYPE>"       + TYPES_PATTERN + ')'
            + "|(?<COMMAND>"    + COMMAND_PATTERN + ')'
            + "|(?<SEMICOLON>"  + SEMICOLON_PATTERN + ')'
            + "|(?<NUMBER>"     + NUMBER_PATTERN + ')'
            + "|(?<STRING>"     + STRING_PATTERN + ')'
            + "|(?<PAREN>"      + PAREN_PATTERN + ')'
            + "|(?<BRACE>"      + BRACE_PATTERN + ')'
            + "|(?<BRACKET>"    + BRACKET_PATTERN + ')'
            + "|(?<COMMENT>"    + COMMENT_PATTERN + ')');
    @FXML
    private TableView<Simbolo> tbSimbolos;
    @FXML
    private TableColumn<Simbolo, String> colLinha;
    @FXML
    private TableColumn<Simbolo, String> colToken;
    @FXML
    private TableColumn<Simbolo, String> colLexema;
    @FXML
    private TableColumn<Simbolo, String> colValor;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        /*colLinha.setCellValueFactory(new PropertyValueFactory("colLinha"));
        colToken.setCellValueFactory(new PropertyValueFactory("colToken"));
        colLexema.setCellValueFactory(new PropertyValueFactory("colLexema"));
        colValor.setCellValueFactory(new PropertyValueFactory("colValor"));*/
        colLexema.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        colToken.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getToken()));
        colLinha.setCellValueFactory(c -> new SimpleStringProperty((c.getValue().getLinha()) + ""));
        colValor.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getValor()));
        colLinha.setComparator((String o1, String o2) -> Integer.parseInt(o1) - Integer.parseInt(o2));
        
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        
        codeArea.getStylesheets().add(getClass().getResource("estilos.css").toExternalForm());
        
        // recompute the syntax highlighting 50 ms after user stops editing area
        Subscription cleanupWhenNoLongerNeedIt = codeArea
                // plain changes = ignore style changes that are emitted when syntax highlighting is reapplied
                // multi plain changes = save computation by not rerunning the code multiple times
                //   when making multiple changes (e.g. renaming a method at multiple parts in file)
                .multiPlainChanges()
                // do not emit an event until 50 ms have passed since the last emission of previous stream
                .successionEnds(Duration.ofMillis(50))
                // run the following code block when previous stream emits an event
                .subscribe(ignore -> codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText())));

        // when no longer need syntax highlighting and wish to clean up memory leaks
        // run: `cleanupWhenNoLongerNeedIt.unsubscribe();`
        codeArea.appendText("begin\n\nend");
        codeArea.moveTo(codeArea.getCaretPosition() - 4);
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass
                    = matcher.group("KEYWORD")      != null ? "keyword"
                    : matcher.group("TYPE")         != null ? "type"
                    : matcher.group("COMMAND")      != null ? "command"
                    : matcher.group("SEMICOLON")    != null ? "semicolon"
                    : matcher.group("NUMBER")       != null ? "number"
                    : matcher.group("STRING")       != null ? "string"
                    : matcher.group("PAREN")        != null ? "paren"
                    : matcher.group("BRACE")        != null ? "brace"
                    : matcher.group("BRACKET")      != null ? "bracket"
                    : matcher.group("COMMENT")      != null ? "comment"
                    : null;

            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
    
    @FXML
    private void clkCompilar(ActionEvent event) 
    {
        clkClear(event);
        AnaliseSintatica anasin = new AnaliseSintatica(codeArea.getText());
        //Platform.runLater(() -> {
            ArrayList<Erro> le;
            le = anasin.start();
            if (le.size() > 0)
            {
                for (Erro e : le) 
                    taOutput.appendText(e + "\n");
                
                taOutput.setStyle("-fx-border-color: red; -fx-border-width: 2px");
                taOutput.appendText("Compilado com erros...");
            }
            else
            {
                taOutput.setStyle("-fx-border-color: green; -fx-border-width: 2px");
                taOutput.appendText("Compilado com sucesso...");
            }
            //Platform.runLater(() -> {
                    this.tbSimbolos.getItems().clear();
                    this.tbSimbolos.getItems().addAll(anasin.getTabelaSimbolos().getTable());
                //});
        //});
    }

    @FXML
    private void clkClear(ActionEvent event) {
        taOutput.clear();
        
        taOutput.setStyle("-fx-border-color: lightgray; -fx-border-style: dotted; -fx-border-width: 1px");
    }

    @FXML
    private void clkClearInput(ActionEvent event) {
        codeArea.clear();
        taOutput.clear();
    }

    @FXML
    private void clkOpenFile(ActionEvent event) throws FileNotFoundException, IOException 
    {
        fileChooser.getExtensionFilters().add(new ExtensionFilter("TIRULIPA files (*.trlp)", "*.trlp"));

        file = fileChooser.showOpenDialog(btOpenFile.getParent().getScene().getWindow());
        if (file != null) {
            codeArea.clear();

            br = new BufferedReader(new FileReader(file));

            String code = br.readLine();

            while (code != null) {
                code += "\n";
                codeArea.appendText(code);
                code = br.readLine();
            }
        }
    }

    @FXML
    private void clkSaveFile(ActionEvent event) throws FileNotFoundException 
    {
        if(file != null)
        {
            try {
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                raf.setLength(0);
                raf.writeBytes(codeArea.getText());
                raf.close();
            } catch (Exception e) 
            {
                System.out.println(e.getMessage());
            }
        }
    }

    @FXML
    private void clkSaveAs(ActionEvent event) 
    {
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TIRULIPA File (*.trlp)", "*.trlp"));
        
        File newfile = fileChooser.showSaveDialog(null);
        
        if (newfile != null) 
        {
            file = newfile;
            System.out.println(file.getName());
            if (!file.getName().endsWith(".trlp")) {
                file = new File(file.getPath() + ".trlp");
            }
            try {
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                raf.setLength(0);
                raf.writeBytes(codeArea.getText());
                raf.close();
            } catch (Exception e) 
            {
                System.out.println(e.getMessage());
            }
        }
    }

    @FXML
    private void clkTeclaCodearea(KeyEvent event) 
    {
//        if(event.isShiftDown())
//        {    
//            if(event.getCode() == KeyCode.DIGIT9)
//            {
//                codeArea.appendText(")");
//                codeArea.moveTo(codeArea.getCaretPosition() - 1);
//            }
//            else if(event.getCode() == KeyCode.OPEN_BRACKET)
//            {
//                codeArea.appendText("}");
//                codeArea.moveTo(codeArea.getCaretPosition() - 1);
//            }
//        }
//        else if(event.getCode() == KeyCode.OPEN_BRACKET)
//        {
//            codeArea.appendText("]");
//            codeArea.moveTo(codeArea.getCaretPosition() - 1);
//        }
    }

}
