module com.kpouer.roadwork {
    requires spring.context;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;
    requires java.desktop;
    requires spring.core;
    requires org.slf4j;
    requires com.kpouer.mapview;
    requires com.fasterxml.jackson.annotation;
    requires static lombok;
    requires com.kpouer.wkt;
    requires com.kpouer.roadwork.lib;
    requires json.smart;
    requires org.fife.RSyntaxTextArea;
    requires com.miglayout.swing;
    requires json.path;
    requires com.fasterxml.jackson.databind;
    requires spring.web;
    requires spring.beans;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires java.annotation;

    opens com.kpouer.roadwork.configuration;
    opens com.kpouer.roadwork.service;
    opens com.kpouer.roadwork.ui;
    opens com.kpouer.roadwork.ui.menu;
    opens com.kpouer.roadwork.action;
    opens com.kpouer.roadwork;
    opens com.kpouer.roadwork.opendata.json.model;
}