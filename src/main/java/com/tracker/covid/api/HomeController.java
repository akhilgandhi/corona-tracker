package com.tracker.covid.api;

import com.tracker.covid.models.ConfirmedData;
import com.tracker.covid.models.Fatalities;
import com.tracker.covid.models.RecoveredData;
import com.tracker.covid.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.logging.Logger;

@Controller
public class HomeController {

    private final Logger logger = Logger.getLogger(HomeController.class.getName());

    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
    public String home(Model model) {
        List<ConfirmedData> allConfirmed = coronaVirusDataService.getAllConfirmed();
        List<Fatalities> allFatalities = coronaVirusDataService.getAllDeaths();
        List<RecoveredData> allRecovered = coronaVirusDataService.getAllRecovered();
        int totalReportedCases = allConfirmed.stream().mapToInt(ConfirmedData::getLatestConfirmedCases).sum();
        model.addAttribute("confirmedCases", allConfirmed);
        model.addAttribute("fatalities", allFatalities);
        model.addAttribute("recoveredCases", allRecovered);
        model.addAttribute("totalReportedCases", totalReportedCases);
        logger.info("Populating the model for display");
        return "home";
    }
}
