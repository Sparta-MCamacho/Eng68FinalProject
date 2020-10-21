package com.sparta.eng68.traineetracker.controllers;

import com.sparta.eng68.traineetracker.entities.Course;
import com.sparta.eng68.traineetracker.entities.WeekReport;
import com.sparta.eng68.traineetracker.services.*;
import com.sparta.eng68.traineetracker.entities.Trainee;
import com.sparta.eng68.traineetracker.entities.Trainer;
import com.sparta.eng68.traineetracker.utilities.Pages;
import com.sparta.eng68.traineetracker.utilities.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.Optional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class TrainerHomeController {

    public final TraineeService traineeService;
    public final TrainerService trainerService;
    public final WeekReportService weekReportService;
    public final CourseGroupService courseGroupService;
    public final CourseService courseService;

    @Autowired
    public TrainerHomeController(TraineeService traineeService, TrainerService trainerService, WeekReportService weekReportService, CourseGroupService courseGroupService, CourseService courseService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.weekReportService = weekReportService;
        this.courseGroupService = courseGroupService;
        this.courseService = courseService;
    }

    @GetMapping("/trainerHome")
    public String getTrainerHome(ModelMap modelMap, Principal principal) {
        List<Trainee> traineeList = traineeService.getTraineesByGroupId(trainerService.getTrainerByUsername(principal.getName()).get().getGroupId());
        List<Trainee> missedDeadlineList = new ArrayList<>();
        List<Trainee> needToCompleteList = new ArrayList<>();
        List<Integer> courseDurationList = new ArrayList<>();
        List<Integer> traineeCounter = new ArrayList<>();
        int counter = 0;
        List<List<WeekReport>> traineeCompletedList = new ArrayList<>();


        Integer groupId = trainerService.getTrainerByUsername(principal.getName()).get().getGroupId();
        Integer courseId = courseGroupService.getGroupByID(groupId).get().getCourseId();
        Integer courseDuration = courseService.getGroupByID(courseId).get().getDuration();

        for(int i = 1; i <= courseDuration; i++){
            courseDurationList.add(i);
        }
        for (Trainee trainee: traineeList) {
            traineeCounter.add(counter);
            counter++;
            List<WeekReport> weekReports = weekReportService.getReportsByTraineeID(trainee.getTraineeId());
            traineeCompletedList.add(weekReports);
            Optional<WeekReport> weekReport = weekReportService.getCurrentWeekReportByTraineeID(trainee.getTraineeId());
            if(weekReport.isPresent()){
                if(weekReport.get().getMostRecentEdit().compareTo(weekReport.get().getDeadline()) > 0){
                    missedDeadlineList.add(trainee);
                }
                System.out.println("Week num" + weekReport.get().getWeekNum());
                System.out.println("Trainee" + trainee.getFirstName());
                System.out.println(weekReport.get().getTrainerCompletedFlag() + "hey hey ");
                if(weekReport.get().getTrainerCompletedFlag() == 0){
                    needToCompleteList.add(trainee);
                }
            }
        }
        modelMap.addAttribute("traineeList", traineeList);
        modelMap.addAttribute("missedDeadlineList", missedDeadlineList);
        modelMap.addAttribute("toCompleteList", needToCompleteList);
        modelMap.addAttribute("courseDuration", courseDuration);
        modelMap.addAttribute("courseDurationList", courseDurationList);
        modelMap.addAttribute("traineeCompletedList", traineeCompletedList);
        modelMap.addAttribute("traineeCounter", traineeCounter);
        return Pages.accessPage(Role.TRAINER, Pages.TRAINER_HOME);
    }

    @GetMapping("/newUser")
    public String newUserForm() {
        return Pages.accessPage(Role.TRAINER, Pages.TRAINER_NEW_USER_PAGE);
    }


}
