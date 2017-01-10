package com.parthib.parth.psamadderyaleflickr;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class XMLParser {

    private ArrayList<String> photoid;
    private ArrayList<String> secret;
    private ArrayList<String> farm;
    private ArrayList<String> server;

    private int parsingIndex = 0;

    public XMLParser() {
        photoid = new ArrayList<String>();
        secret = new ArrayList<String>();
        farm = new ArrayList<String>();
        server = new ArrayList<String>();
    }

    public ArrayList<String> getPhotoid() {
        return photoid;
    }

    public ArrayList<String> getSecret() {
        return secret;
    }

    public ArrayList<String> getServer() {
        return server;
    }

    public ArrayList<String> getFarm() {
        return farm;
    }

    public void parse (String XML) {
        while (XML.indexOf("photo id=", parsingIndex) != -1){

            int photoidStart = 10 + XML.indexOf("photo id=", parsingIndex);
            int photoidEnd = XML.indexOf("\" secret=", parsingIndex);

            photoid.add(XML.substring(photoidStart, photoidEnd));

            //This makes sure that the other farm value that appears before the first
            //<photo id tag does not get considered
            parsingIndex = photoidStart;

            int secretStart = photoidEnd + 10;
            int secretEnd = XML.indexOf("\" server=", parsingIndex);

            secret.add(XML.substring(secretStart, secretEnd));

            int serverStart = secretEnd + 10;
            int serverEnd = XML.indexOf("\" farm=", parsingIndex);

            server.add(XML.substring(serverStart, serverEnd));

            int farmStart = serverEnd + 8;
            int farmEnd = XML.indexOf("\" title=", parsingIndex);

            farm.add(XML.substring(farmStart, farmEnd));

            parsingIndex = farmEnd;
        }
    }

}
