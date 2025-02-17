package com.citizen.fmc.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * ======> Created by dheeraj-gangwar on 2018-04-21 <======
 */

public class FAQModel implements Serializable {
    @SerializedName("id")
    int id;

    @SerializedName("question")
    String question;

    @SerializedName("answer")
    String answer;

    @SerializedName("data_details")
    ArrayList<FAQModel> answersList;


    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public ArrayList<FAQModel> getAnswersList() {
        return answersList;
    }
}
