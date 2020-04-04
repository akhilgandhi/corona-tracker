package com.tracker.covid.api;

import com.tracker.covid.models.ConfirmedData;
import com.tracker.covid.models.Fatalities;
import com.tracker.covid.models.RecoveredData;
import com.tracker.covid.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.DecimalFormat;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
        int totalFatalities = allFatalities.stream().mapToInt(Fatalities::getLatestDeaths).sum();
        int totalRecovered = allRecovered.stream().mapToInt(RecoveredData::getLatestRecovered).sum();
        double mortalityRate = (totalFatalities*100.00)/totalReportedCases;
        model.addAttribute("confirmedCases", allConfirmed);
        model.addAttribute("fatalities", allFatalities);
        model.addAttribute("recoveredCases", allRecovered);
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("totalFatalities", totalFatalities);
        model.addAttribute("totalRecovered", totalRecovered);
        model.addAttribute("mortalityRate", new DecimalFormat("00.00").format(mortalityRate));
        logger.info("Populating the model for display");
        return "home";
    }
}
