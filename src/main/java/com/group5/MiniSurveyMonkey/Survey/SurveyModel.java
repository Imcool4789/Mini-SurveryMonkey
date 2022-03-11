package com.group5.MiniSurveyMonkey.Survey;

import com.group5.MiniSurveyMonkey.Question.QuestionModel;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Entity
public class SurveyModel
{
    private final AtomicLong counter = new AtomicLong();

    @Id
    private long id;
    private String name;
    private int responseCount;

    private ArrayList<QuestionModel> surveyQuestions;

    public SurveyModel()
    {
        this.id = 1;
        this.name = "Survey 1";
        this.responseCount = 0;
        surveyQuestions = new ArrayList<>();
    }

    public SurveyModel(String name)
    {
        this.id = 1;
        this.name = name;
        this.responseCount = 0;
        surveyQuestions = new ArrayList<>();
    }

    public void addQuestion(QuestionModel newQuestion)
    {
        if(newQuestion != null)
        {
            newQuestion.setId(counter.incrementAndGet());
            surveyQuestions.add(newQuestion);
        }
    }

    public void removeQuestion(int index)
    {
        if(index >= 0 && index < surveyQuestions.size())
        {
            surveyQuestions.remove(index);
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResponseCount() {
        return responseCount;
    }

    public void setResponseCount(int responseCount) {
        this.responseCount = responseCount;
    }

    public ArrayList<QuestionModel> getSurveyQuestions() {
        return surveyQuestions;
    }

    @Override
    public String toString() {
        return "SurveyModel{" +
                "counter=" + counter +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", responseCount=" + responseCount +
                ", surveyQuestions=" + surveyQuestions +
                '}';
    }
}