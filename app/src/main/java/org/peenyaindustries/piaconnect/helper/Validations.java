package org.peenyaindustries.piaconnect.helper;


import org.json.JSONObject;

public class Validations {

    public boolean contains(JSONObject obj, String field) {

        return obj.has(field);
    }
}
