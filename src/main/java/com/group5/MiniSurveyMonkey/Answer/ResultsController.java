package com.group5.MiniSurveyMonkey.Answer;

import com.group5.MiniSurveyMonkey.Question.MCQuestion;
import com.group5.MiniSurveyMonkey.Question.NumberRangeQuestion;
import com.group5.MiniSurveyMonkey.Question.OpenQuestion;
import com.group5.MiniSurveyMonkey.Question.QuestionModel;
import com.group5.MiniSurveyMonkey.Survey.SurveyModel;
import com.group5.MiniSurveyMonkey.Survey.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

@Controller
public class ResultsController {
    @Autowired
    private SurveyRepository surveyRepository;

    @GetMapping("/surveyorIndex/Question/{id}/Result")
    public String viewIndividualResult(@PathVariable String id, Model model) {
        SurveyModel surveyModel = surveyRepository.findById(1);

        if (surveyModel == null) {
            surveyModel = new SurveyModel();
            surveyRepository.save(surveyModel);
        }

        int questionID = Integer.parseInt(id) - 1;
        List<QuestionModel> questions = surveyModel.getSurveyQuestions();
        QuestionModel question = questions.get(questionID);
        String type = question.getClass().getSimpleName();

        String questionTitle;
        String graphTitle = "Survey Results for Question: '" + question.getName() + "'";
        Random random = new Random();
        model.addAttribute("question", question);
        switch (type) {
            case "OpenQuestion":
                List<AnswerModel> responses = question.getResponses();
                questionTitle = "Responses for Question: " + question.getName() + "[id =" + id + "]";
                model.addAttribute("questionTitle", questionTitle);
                model.addAttribute("responses", responses);
                return "OpenResult";
            case "NumberRangeQuestion":
                NumberRangeQuestion numberRangeQuestion = (NumberRangeQuestion) question;
                /*TESTING*/
                int max = 10;
                int min = 1;
                for (int i = 0; i < 100; i++) {
                    int value = random.nextInt(max) + min;
                    NumberRangeAnswer answer = new NumberRangeAnswer(value, numberRangeQuestion);
                }
                questionTitle = "Responses for Question: " + question.getName() + "[id =" + id + "]";
                model.addAttribute("graphTitle", graphTitle);
                model.addAttribute("numberRangeData", numberRangeQuestion.convertTo2DArray());
                return "NumberRangeResult";
            case "MCQuestion":
                MCQuestion mcQuestion = (MCQuestion) question;
                Map<String, Integer> mcResponses = mcQuestion.getResponseMap();
                /*TESTING*/
                List<String> keys = new ArrayList<>(mcResponses.keySet());
                int maxIndex = keys.size();
                int minIndex = 0;
                for (int i = 0; i < 100; i++) {
                    int value = random.nextInt(maxIndex) + minIndex;
                    String randomAnswer = keys.get(value);
                    MCAnswer answer = new MCAnswer(randomAnswer, mcQuestion);
                }
                questionTitle = "Responses for Question: " + question.getName() + "[id =" + id + "]";
                model.addAttribute("graphTitle", graphTitle);
                model.addAttribute("MCData", mcQuestion.convertTo2DArray());
                return "MCResult";

        }
        return "viewSurvey";
    }

    @GetMapping("/surveyorIndex/Result")
    public String viewAllResult(Model model) {
        SurveyModel surveyModel = surveyRepository.findById(1);
        if (surveyModel == null) {
            surveyModel = new SurveyModel();
            surveyRepository.save(surveyModel);
        }

        /*TESTING*/
        OpenQuestion question = new OpenQuestion("question 1", surveyModel);
        OpenAnswer test = new OpenAnswer("answer 1", question);
        OpenAnswer test2 = new OpenAnswer("answer 2", question);
        OpenAnswer test3 = new OpenAnswer("answer 3", question);
        surveyModel.addQuestion(question);

        MCQuestion question2 = new MCQuestion("question 2", surveyModel);
        question2.setMc1("option 1");
        question2.setMc2("option 2");
        question2.setMc3("option 3");
        question2.setMc4("option 4");
        question2.addOpt();

        Random random = new Random();
        List<String> keys = new ArrayList<>(question2.getResponseMap().keySet());
        int maxIndex = keys.size();
        int minIndex = 0;
        for (int i = 0; i < 100; i++) {
            int value = random.nextInt(maxIndex) + minIndex;
            String randomAnswer = keys.get(value);
            MCAnswer answer = new MCAnswer(randomAnswer, question2);
        }
        surveyModel.addQuestion(question2);

        NumberRangeQuestion question3 = new NumberRangeQuestion("question 3", surveyModel, 1, 10);
        int max = 10;
        int min = 1;
        for (int i = 0; i < 100; i++) {
            int value = random.nextInt(max) + min;
            NumberRangeAnswer answer = new NumberRangeAnswer(value, question3);
        }
        surveyModel.addQuestion(question3);
        surveyRepository.save(surveyModel);

        List<QuestionModel> surveyQuestions = surveyModel.getSurveyQuestions();
        List<Object[][]> graphDatas = new ArrayList<>();
        List<String> graphTitles = new ArrayList<>();
        List<String> questionTypes = new ArrayList<>();

        IntStream.range(0, surveyQuestions.size()).filter(i -> surveyQuestions.get(i).getClass().getSimpleName() != "OpenQuestion").mapToObj(surveyQuestions::get).forEach(currentQuestion -> {
            if (currentQuestion instanceof MCQuestion) {
                MCQuestion mcQuestion = (MCQuestion) currentQuestion;
                Object[][] graphData = mcQuestion.convertTo2DArray();
                graphDatas.add(graphData);
                String graphTitle = "Survey Results for Question: '" + currentQuestion.getName() + "'";
                graphTitles.add(graphTitle);
                questionTypes.add(MCQuestion.class.getSimpleName());
            }
            if (currentQuestion instanceof NumberRangeQuestion) {
                NumberRangeQuestion numberRangeQuestion = (NumberRangeQuestion) currentQuestion;
                Object[][] graphData = numberRangeQuestion.convertTo2DArray();
                graphDatas.add(graphData);
                String graphTitle = "Survey Results for Question: '" + currentQuestion.getName() + "'";
                graphTitles.add(graphTitle);
                questionTypes.add(NumberRangeQuestion.class.getSimpleName());
            }
            if (currentQuestion instanceof OpenQuestion) {
                OpenQuestion openQuestion = (OpenQuestion) currentQuestion;
                Object[][] graphData = openQuestion.convertTo2DArray();
                graphDatas.add(graphData);
                String graphTitle = "Survey Results for Question: '" + currentQuestion.getName() + "'";
                graphTitles.add(graphTitle);
                questionTypes.add(OpenQuestion.class.getSimpleName());
            }
        });

        OpenAnswer answer = new OpenAnswer("answer", question);
        question.addResponse(answer);

        model.addAttribute("surveyModel", surveyModel);
        model.addAttribute("questionTypes", questionTypes);
        model.addAttribute("graphDatas", graphDatas);
        model.addAttribute("graphTitles", graphTitles);
        return "results";
    }
}
