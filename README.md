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

## Belgium

- Liège

## France

### Cities
- Avignon
- Bordeaux
- Issy-les-Moulineaux
- Lyon
- Montpellier
- Paris
- Rennes
- Toulouse

### States
- Loire Atlantique (44)
- Sarthe (72)

## Germany

- Berlin

## USA

- San Francisco

# Technical information

This app depends on a library [Roadwork-lib](https://github.com/kpouer/Roadwork-lib)
 that is intended to share data with the [Roadwork-server](https://github.com/kpouer/Roadwork-server)

# Adding Opendata service

It is possible to add a new service by adding *code* (the initial method), or using the *json opendata descriptor*

## code

to be documented

## json opendata descriptor

If an opendata service provides a json, then it should be possible to describe it with a **json opendata descriptor**
Those descriptors must be added to the *opendata/json* folder.
Their naming convention is *country-city.json*. For example *France-Paris.json*

A descriptor contains a first object called **metadata** that contains all information about the Opendata service

| field                   | mandatory | example                            | description                                     |
|-------------------------|-----------|------------------------------------|-------------------------------------------------|
| metadata                | yes       | A metadata structure               | see next chapter                                |
| roadworkArray           | yes       | $.records                          | The path of the roadwork array                  |
| id                      | yes       | @.recordid                         | The path of the id field within a roadwork item |
| latitude                | yes       | @.geometry.coordinates[1]          | The path of the latitude                        |
| longitude               | yes       | @.geometry.coordinates[0]          | The path of the longitude                       |
| road                    | no        | @.fields.voie                      | The path of the road information                |
| impactCirculationDetail | no        | @.fields.impact_circulation_detail | The path for circulation impact                 |
| description             | no        | @.fields.description               | The path of the description                     |
| locationDetails         | no        | @.fields.precision_localisation    | The path for more location information          |
| url                     | no        | https://xxxx                       | An url for that exact roadwork item             |
| from                    | yes       | A date parser structure            |                                                 |
| to                      | yes       | A date parser structure            |                                                 |


### metadata

| field       | mandatory | example          | description                                   |
|-------------|-----------|------------------|-----------------------------------------------|
| country     | yes       | France           | The country of the service                    |
| name        | yes       | Paris            | The city (or region or anything)              |
| producer    | no        | Ville de Paris   | The owner/producer of the service             |
| licenceName | no        | Creative Commons | The name of the licence                       |
| licenceUrl  | no        | https://xxxx     | The url of the licence                        |
| sourceUrl   | no        | https://xxxx     | The homepage of the service                   |
| url         | yes       | https://xxxx     | The url that will be called to retrieve data  |
| locale      | yes       | fr_FR            | The locale that can be used to parse the date |

### Date parser structure

| field       | mandatory | example                    | description                        |
|-------------|-----------|----------------------------|------------------------------------|
| path        | yes       | @.properties.validity.from | the path of the value to be parsed |
| parsers     | yes       | Array of Parser structure  |                                    |

### Parser structure

| field   | mandatory | example            | description                                                  |
|---------|-----------|--------------------|--------------------------------------------------------------|
| matcher | yes       | .*                 | a regexp matcther (it can have a capture group if necessary) |
| format  | yes       | dd.MM.yyyy HH:mm   | a date pattern                                               |

# How to help

- Bug report
- New feature request
- Translation (If you want to submit a new translation, please contact me)
- Suggest new opendata service (please verify that the service you suggest provides at least circulation impact, begin and end date, latitude and longitude)
- Or better, implement one and submit it.