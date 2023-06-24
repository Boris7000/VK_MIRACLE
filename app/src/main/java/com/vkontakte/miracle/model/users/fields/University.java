package com.vkontakte.miracle.model.users.fields;

import org.json.JSONException;
import org.json.JSONObject;

public class University {

    private String university;
    private String university_name;
    private String faculty;
    private String faculty_name;

    public University(JSONObject jsonObject) throws JSONException {
        if(jsonObject.has("university")){
            university = jsonObject.getString("university");
        }
        if(jsonObject.has("university_name")){
            university_name = jsonObject.getString("university_name");
        }
        if(jsonObject.has("faculty")){
            faculty = jsonObject.getString("faculty");
        }
        if(jsonObject.has("faculty_name")){
            faculty_name = jsonObject.getString("faculty_name");
        }
    }

    public String getUniversity() {
        return university;
    }

    public String getUniversityName() {
        return university_name;
    }

    public String getFaculty() {
        return faculty;
    }

    public String getFacultyName() {
        return faculty_name;
    }
}
