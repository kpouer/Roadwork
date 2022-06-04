package com.kpouer.roadwork.ui.opendatainfo;

import com.kpouer.roadwork.opendata.json.model.Metadata;
import com.kpouer.roadwork.service.LocalizationService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class OpendataInformationPanel extends JPanel {
    private final JTextField sourceUrl;
    private final JTextField url;
    private final JTextField producer;
    private final JTextField licenceName;
    private final JTextField licenceUrl;

    public OpendataInformationPanel(LocalizationService localizationService) {
        super(new MigLayout());
        add(sourceUrl = new JTextField(40), "wrap, span 3");
        add(url = new JTextField(40), "wrap, span 3");
        add(producer = new JTextField(40), "wrap, span 3");
        add(licenceName = new JTextField(40), "wrap, span 3");
        add(licenceUrl = new JTextField(40), "wrap, span 3");
        sourceUrl.setBorder(BorderFactory.createTitledBorder(localizationService.getMessage("opendataDetailPanel.sourceUrl")));
        url.setBorder(BorderFactory.createTitledBorder(localizationService.getMessage("opendataDetailPanel.url")));
        producer.setBorder(BorderFactory.createTitledBorder(localizationService.getMessage("opendataDetailPanel.producer")));
        licenceName.setBorder(BorderFactory.createTitledBorder(localizationService.getMessage("opendataDetailPanel.licence")));
        licenceUrl.setBorder(BorderFactory.createTitledBorder(localizationService.getMessage("opendataDetailPanel.licenceUrl")));
    }

    public void setMetadata(Metadata metadata) {
        sourceUrl.setText(metadata.getSourceUrl());
        sourceUrl.setCaretPosition(0);
        url.setText(metadata.getSourceUrl());
        url.setCaretPosition(0);
        producer.setText(metadata.getProducer());
        licenceName.setText(metadata.getLicenceName());
        licenceUrl.setText(metadata.getLicenceUrl());
        licenceUrl.setCaretPosition(0);
    }
}
