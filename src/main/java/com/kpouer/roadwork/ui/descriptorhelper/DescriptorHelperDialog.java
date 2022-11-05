package com.kpouer.roadwork.ui.descriptorhelper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import com.kpouer.roadwork.opendata.json.DefaultJsonService;
import com.kpouer.roadwork.opendata.json.model.DateParser;
import com.kpouer.roadwork.opendata.json.model.Metadata;
import com.kpouer.roadwork.opendata.json.model.Parser;
import com.kpouer.roadwork.opendata.json.model.ServiceDescriptor;
import com.kpouer.roadwork.service.LocalizationService;
import com.kpouer.roadwork.ui.MainPanel;
import net.miginfocom.swing.MigLayout;
import net.minidev.json.JSONArray;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Lazy
@Scope("prototype")
public class DescriptorHelperDialog extends JDialog {
    private static final Logger logger = LoggerFactory.getLogger(DescriptorHelperDialog.class);

    private final JTextField urlTextField;
    /**
     * The editor
     */
    private final RSyntaxTextArea editor;
    /**
     * A json sample (can be retrieved using the {@link #urlTextField})
     */
    private final RSyntaxTextArea sampleTextArea;
    private final RSyntaxTextArea preview;
    private final RSyntaxTextArea roadworkPreview;

    private final LocalizationService localizationService;
    private final Color defaultColor;
    private Object sample;
    private final ObjectWriter objectWriter;
    private final ObjectMapper objectMapper;
    private ServiceDescriptor serviceDescriptor;
    private String currentRoadworkPath;
    private Object currentRoadwork;
    private Color ERROR_COLOR = new Color(255, 0, 0, 30);

    public DescriptorHelperDialog(MainPanel parent, LocalizationService localizationService) {
        super(parent);
        objectMapper = new ObjectMapper();
        this.localizationService = localizationService;
        objectWriter = new ObjectMapper()
                .setDefaultPropertyInclusion(JsonInclude.Include.ALWAYS)
                .writerWithDefaultPrettyPrinter();
        serviceDescriptor = createDefaultServiceDescriptor();
        var topPanel = new JPanel(new MigLayout());
        topPanel.add(new JLabel("url"));
        topPanel.add(urlTextField = new JTextField(80), "growx");
        getContentPane().add(topPanel, BorderLayout.PAGE_START);

        var sampleScrollPane = new RTextScrollPane(sampleTextArea = createTextArea());
        sampleScrollPane.setBorder(BorderFactory.createTitledBorder("Sample"));
        var roadworkScrollPane = new RTextScrollPane(roadworkPreview = createTextArea());
        roadworkScrollPane.setBorder(BorderFactory.createTitledBorder("Roadwork"));
        var rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, sampleScrollPane, roadworkScrollPane);
        rightSplitPane.setDividerLocation(0.5);
        var editorScrollPane = new RTextScrollPane(editor = createTextArea());
        var previewScrollPanel = new RTextScrollPane(preview = createTextArea());
        previewScrollPanel.setBorder(BorderFactory.createTitledBorder("Preview"));
        getContentPane().add(
                new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                        new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, editorScrollPane, rightSplitPane),
                        previewScrollPanel));
        sampleTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                EventQueue.invokeLater(() -> sampleUpdated());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                EventQueue.invokeLater(() -> sampleUpdated());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                EventQueue.invokeLater(() -> updated());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                EventQueue.invokeLater(() -> updated());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        try {
            editor.setText(objectWriter.writeValueAsString(serviceDescriptor));
        } catch (JsonProcessingException e) {
            logger.error("Error processing json", e);
        }
        defaultColor = editor.getBackground();
        setSize(1024, 768);
        urlTextField.addActionListener(e -> callUrl());
    }

    private static ServiceDescriptor createDefaultServiceDescriptor() {
        var serviceDescriptor = new ServiceDescriptor();
        serviceDescriptor.setMetadata(new Metadata());
        serviceDescriptor.setRoadworkArray("$.records");
        serviceDescriptor.setId("@.recordid");
        serviceDescriptor.setLatitude("@.geometry.coordinates[1]");
        serviceDescriptor.setLongitude("@.geometry.coordinates[0]");
        var from = new DateParser();
        List<Parser> parsers = new ArrayList<>();
        var parser = new Parser();
        parser.setMatcher(".*");
        parser.setFormat("yyyy-MM-dd");
        parsers.add(parser);
        from.setParsers(parsers);
        serviceDescriptor.setFrom(from);
        serviceDescriptor.setTo(from);
        return serviceDescriptor;
    }

    private void updated() {
        logger.debug("updated");
        try {
            serviceDescriptor = objectMapper.readValue(editor.getText(), ServiceDescriptor.class);
            var roadworkArray = serviceDescriptor.getRoadworkArray();
            if (!Objects.equals(roadworkArray, this.currentRoadworkPath)) {
                // if the roadwork array path did not change, do not parse it again
                currentRoadworkPath = roadworkArray;
                var nodeOptional = getRoadworkArrayNode();
                if (nodeOptional.isEmpty()) {
                    logger.error("Unable to get a sample roadwork");
                    return;
                }
                currentRoadwork = nodeOptional.get();
                roadworkPreview.setText(objectWriter.writeValueAsString(currentRoadwork));
                roadworkPreview.setCaretPosition(0);
            }

            if (currentRoadwork == null) {
                return;
            }
            var roadwork = DefaultJsonService.buildRoadwork(serviceDescriptor, currentRoadwork);
            preview.setText(objectWriter.writeValueAsString(roadwork));
            preview.setCaretPosition(0);
            editor.setBackground(defaultColor);
        } catch (InvalidPathException e) {
            editor.setBackground(ERROR_COLOR);
            logger.warn("Invalid path {}", e.getMessage());
        } catch (JsonProcessingException e) {
            logger.error("Error processing json", e);
            editor.setBackground(ERROR_COLOR);
        } catch (Exception e) {
            logger.error("Error processing", e);
            editor.setBackground(ERROR_COLOR);
        }
    }

    private Optional<Object> getRoadworkArrayNode() {
        if (sample == null) {
            return Optional.empty();
        }
        var roadworkArrayPath = serviceDescriptor.getRoadworkArray();
        if (roadworkArrayPath == null || roadworkArrayPath.trim().isEmpty()) {
            logger.debug("Roadwork array path cannot be null or empty");
            editor.setBackground(ERROR_COLOR);
            return Optional.empty();
        }
        try {
            JSONArray roadworkArray = JsonPath.read(sample, roadworkArrayPath);
            return Optional.of(roadworkArray.get(0));
        } catch (ClassCastException e) {
            logger.error("Not an array");
        }
        return Optional.empty();
    }

    @NotNull
    private static RSyntaxTextArea createTextArea() {
        var textArea = new TextEditorPane(RTextArea.INSERT_MODE, false);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
        textArea.setCodeFoldingEnabled(true);
        return textArea;
    }

    private void callUrl() {
        var url = urlTextField.getText().trim();
        logger.info("callUrl {}", url);
        var restTemplate = new RestTemplate();
        try {
            var json = restTemplate.getForObject(new URI(url), ObjectNode.class);

            var jsonString = objectWriter.writeValueAsString(json);
            sampleTextArea.setText(jsonString);
            sampleTextArea.setCaretPosition(0);
            sampleUpdated();
        } catch (URISyntaxException e) {
            JOptionPane.showMessageDialog(this, "Invalid url");
        } catch (JsonProcessingException e) {
            logger.error("Error", e);
        }
    }

    /**
     * A new sample was inserted in the sample textarea.
     */
    private void sampleUpdated() {
        try {
            logger.debug("sampleUpdated");
            var jsonString = sampleTextArea.getText();
            sample = Configuration.defaultConfiguration().jsonProvider().parse(jsonString);
            currentRoadworkPath = null;
            currentRoadwork = null;
            updated();
        } catch (Throwable e) {
            logger.error("Error", e);
        }
    }
}
