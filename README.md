# Roadwork
A desktop app to monitor roadwork opendata services

## Introduction

My main objective was to make life easier to Waze editors, especially closure teams, but this tool could be used for 
any other reason you like as it is not directly linked to Waze map editor.

The main problem of opendata services is to track changes. This Roadwork tool will allow you to flag roadworks that
you already know about and focus only on new elements.

For that, it will connect to various opendata services, get and parse the data then do an abstraction to store them
in a local cache.

![Screenshot](/doc/screenshot.jpg)

# Supported services

So far most supported services are French, but there is no restriction to include other countries.

## France

- Avignon
- Bordeaux
- Issy-les-Moulineaux
- Lyon
- Paris
- Rennes
- Toulouse

## Germany

- Berlin

# Technical information

This app depends on a library [Roadwork-lib](https://github.com/kpouer/Roadwork-lib)
 that is intended to share data with the [Roadwork-server](https://github.com/kpouer/Roadwork-server)

## Adding Opendata service

It is possible to add a new service by adding *code* (the initial method), or using the *json opendata descriptor*

### code

to be documented

### json opendata descriptor

A json file describing the opendata service in order to include new opendata services without adding any code.
Still in development, to be documented

# How to help

- Bug report
- New feature request
- Translation (If you want to submit a new translation, please contact me)
- Suggest new opendata service (please verify that the service you suggest provides at least circulation impact, begin and end date, latitude and longitude)
- Or better, implement one and submit it.